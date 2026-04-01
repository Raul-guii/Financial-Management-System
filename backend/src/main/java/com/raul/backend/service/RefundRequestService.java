package com.raul.backend.service;

import com.raul.backend.dto.refundrequest.RefundRequestCreateDTO;
import com.raul.backend.dto.refundrequest.RefundRequestResponseDTO;
import com.raul.backend.entity.Payment;
import com.raul.backend.entity.RefundRequest;
import com.raul.backend.entity.User;
import com.raul.backend.enums.InvoiceStatus;
import com.raul.backend.enums.PaymentStatus;
import com.raul.backend.enums.RefundStatus;
import com.raul.backend.repository.PaymentRepository;
import com.raul.backend.repository.RefundRequestRepository;
import com.raul.backend.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RefundRequestService {

    private final RefundRequestRepository refundRequestRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    public RefundRequestService(RefundRequestRepository refundRequestRepository, PaymentRepository paymentRepository, UserRepository userRepository) {
        this.refundRequestRepository = refundRequestRepository;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public RefundRequestResponseDTO create(RefundRequestCreateDTO dto) {
        Payment payment = paymentRepository.findById(dto.getPaymentId())
                .orElseThrow(() -> new RuntimeException("Payment não encontrado"));

        if (payment.getPaymentStatus() != PaymentStatus.APPROVED) {
            throw new RuntimeException("Só é possível solicitar reembolso de um pagamento aprovado");
        }

        refundRequestRepository.findByPaymentIdAndStatus(payment.getId(), RefundStatus.PENDING)
                .ifPresent(r -> {
                    throw new RuntimeException("Já existe uma solicitação de reembolso pendente para este pagamento");
                });

        User user = getAuthenticatedUser();

        RefundRequest refundRequest = new RefundRequest();
        refundRequest.setReason(dto.getReason());
        refundRequest.setStatus(RefundStatus.PENDING);
        refundRequest.setRequestedAt(LocalDateTime.now());
        refundRequest.setPayment(payment);
        refundRequest.setRequestedBy(user);

        refundRequest = refundRequestRepository.save(refundRequest);

        return toDTO(refundRequest);
    }

    public List<RefundRequestResponseDTO> findAll() {
        return refundRequestRepository.findAllByOrderByRequestedAtDesc()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public RefundRequestResponseDTO findById(Long id) {
        return refundRequestRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Solicitação de reembolso não encontrada"));
    }

    @Transactional
    public RefundRequestResponseDTO approve(Long id) {
        RefundRequest refundRequest = refundRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitação de reembolso não encontrada"));

        if (refundRequest.getStatus() != RefundStatus.PENDING) {
            throw new RuntimeException("A solicitação não está pendente");
        }

        refundRequest.setStatus(RefundStatus.APPROVED);
        refundRequest.setApprovedAt(LocalDateTime.now());

        Payment payment = refundRequest.getPayment();

        if (payment == null) {
            throw new RuntimeException("Pagamento não encontrado para esta solicitação");
        }

        payment.setPaymentStatus(PaymentStatus.REFUNDED);

        if (payment.getInvoice() != null) {
            payment.getInvoice().setStatus(InvoiceStatus.REFUNDED);
        }

        refundRequest = refundRequestRepository.save(refundRequest);

        return toDTO(refundRequest);
    }

    @Transactional
    public RefundRequestResponseDTO reject(Long id) {
        RefundRequest refundRequest = refundRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitação de reembolso não encontrada"));

        if (refundRequest.getStatus() != RefundStatus.PENDING) {
            throw new RuntimeException("A solicitação não está pendente");
        }

        refundRequest.setStatus(RefundStatus.REJECTED);

        refundRequest = refundRequestRepository.save(refundRequest);
        return toDTO(refundRequest);
    }

    private RefundRequestResponseDTO toDTO(RefundRequest refundRequest) {
        return new RefundRequestResponseDTO(
                refundRequest.getId(),
                refundRequest.getStatus(),
                refundRequest.getReason(),
                refundRequest.getRequestedAt(),
                refundRequest.getApprovedAt(),
                refundRequest.getPayment() != null ? refundRequest.getPayment().getId() : null
        );
    }

    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }
}