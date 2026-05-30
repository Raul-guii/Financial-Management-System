package com.raul.backend.service;

import com.raul.backend.dto.payment.*;
import com.raul.backend.entity.Invoice;
import com.raul.backend.entity.Payment;
import com.raul.backend.entity.User;
import com.raul.backend.enums.AuditAction;
import com.raul.backend.enums.InvoiceStatus;
import com.raul.backend.enums.PaymentStatus;
import com.raul.backend.repository.InvoiceRepository;
import com.raul.backend.repository.PaymentRepository;
import com.raul.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private final GatewayTransactionService gatewayTransactionService;
    private final PaymentRepository repository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceStatusService invoiceStatusService;
    private final InvoiceCalculatorService invoiceCalculatorService;
    private final AuditLogService auditLogService;
    private final UserRepository userRepository;

    // CREATE
    @Transactional
    public PaymentResponseDTO create(PaymentCreateDTO dto) {

        Invoice invoice = invoiceRepository.findById(dto.getInvoiceId())
                .orElseThrow(() -> new RuntimeException("Invoice não encontrada"));

        // Bloqueia pagamento em fatura cancelada
        if (invoice.getStatus() == InvoiceStatus.CANCELLED) {
            throw new RuntimeException("Não é possível registrar pagamento em uma fatura cancelada");
        }

        BigDecimal remaining = invoiceCalculatorService.getRemainingAmount(invoice);

        if (dto.getPayerEmail() == null || dto.getPayerEmail().isBlank()) {
            throw new RuntimeException("payerEmail é obrigatório");
        }

        if (dto.getAmount().compareTo(remaining) > 0) {
            throw new RuntimeException("Valor maior que o restante da fatura");
        }

        Payment payment = new Payment();
        payment.setAmount(dto.getAmount());
        payment.setPaymentDate(dto.getPaymentDate());
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setInvoice(invoice);
        payment.setPayerEmail(dto.getPayerEmail());
        payment.setDateOfExpiration(dto.getDateOfExpiration());

        payment = repository.save(payment);

        User loggedUser = getLoggedUser();

        auditLogService.log(payment.getId(), "PAYMENT", AuditAction.PAYMENT_CREATED,
                loggedUser != null ? loggedUser.getId() : null,
                loggedUser != null ? loggedUser.getName() : null,
                "Pagamento de R$" + payment.getAmount() + " registrado para fatura #" + invoice.getId());


        try {
            gatewayTransactionService.processPayment(payment);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar pagamento no gateway", e);
        }

        return toDTO(payment);
    }

    // UPDATE
    @Transactional
    public PaymentResponseDTO update(Long id, PaymentUpdateDTO dto) {

        Payment payment = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment não encontrado"));

        if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new RuntimeException(
                    "Não é possível editar um pagamento com status " +
                            payment.getPaymentStatus() + ". Apenas pagamentos PENDING podem ser alterados."
            );
        }

        // amount e invoiceId bloqueados mesmo em PENDING — afetam integridade financeira
        if (dto.getAmount() != null) {
            throw new RuntimeException(
                    "O valor do pagamento não pode ser alterado após o registro. " +
                            "Cancele o pagamento e registre um novo se necessário."
            );
        }

        if (dto.getInvoiceId() != null) {
            throw new RuntimeException(
                    "A fatura vinculada ao pagamento não pode ser alterada após o registro."
            );
        }

        if (dto.getPaymentDate() != null) payment.setPaymentDate(dto.getPaymentDate());
        if (dto.getPaymentMethod() != null) payment.setPaymentMethod(dto.getPaymentMethod());

        payment = repository.save(payment);

        try {
            gatewayTransactionService.processPayment(payment);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar pagamento no gateway", e);
        }

        payment = repository.findById(payment.getId())
                .orElseThrow(() -> new RuntimeException("Payment não encontrado"));

        return toDTO(payment);
    }

    // GET ALL
    public List<PaymentResponseDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // GET BY ID
    public PaymentResponseDTO findById(Long id) {
        return repository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Payment não encontrado"));
    }

    // DELETE
    @Transactional
    public void delete(Long id) {

        Payment payment = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment não encontrado"));

        Invoice invoice = payment.getInvoice();

        repository.delete(payment);

        User loggedUser = getLoggedUser();

        auditLogService.log(id, "PAYMENT", AuditAction.PAYMENT_DELETED,
                loggedUser != null ? loggedUser.getId() : null,
                loggedUser != null ? loggedUser.getName() : null,
                "Pagamento removido da fatura #" + invoice.getId());

        invoiceStatusService.recalculateInvoiceStatus(invoice.getId());
    }

    private User getLoggedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        }
        return null;
    }

    // MAPPER
    private PaymentResponseDTO toDTO(Payment payment) {
        return new PaymentResponseDTO(
                payment.getId(),
                payment.getAmount(),
                payment.getPaymentDate(),
                payment.getCreatedAt(),
                payment.getInvoice() != null ? payment.getInvoice().getId() : null,
                payment.getGatewayTransaction() != null ? payment.getGatewayTransaction().getId() : null,
                payment.getRefundRequests() != null
                        ? payment.getRefundRequests().stream().map(r -> r.getId()).toList()
                        : List.of(),
                payment.getPaymentStatus(),
                payment.getGatewayTransaction() != null
                        ? payment.getGatewayTransaction().getExternalId()
                        : null,
                payment.getGatewayTransaction() != null
                        ? payment.getGatewayTransaction().getQrCode()
                        : null,
                payment.getGatewayTransaction() != null
                        ? payment.getGatewayTransaction().getTicketUrl()
                        : null
        );
    }
}