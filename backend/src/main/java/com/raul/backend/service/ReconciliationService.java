package com.raul.backend.service;

import com.raul.backend.dto.reconciliation.ReconciliationCreateDTO;
import com.raul.backend.dto.reconciliation.ReconciliationResponseDTO;
import com.raul.backend.dto.reconciliationitem.ReconciliationItemResponseDTO;
import com.raul.backend.entity.*;
import com.raul.backend.enums.ReconciliationStatus;
import com.raul.backend.repository.GatewayTransactionRepository;
import com.raul.backend.repository.PaymentRepository;
import com.raul.backend.repository.ReconciliationRepository;
import com.raul.backend.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReconciliationService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final ReconciliationRepository reconciliationRepository;
    private final GatewayTransactionRepository gatewayTransactionRepository;

    public ReconciliationService(PaymentRepository paymentRepository, UserRepository userRepository, ReconciliationRepository reconciliationRepository, GatewayTransactionRepository gatewayTransactionRepository) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.reconciliationRepository = reconciliationRepository;
        this.gatewayTransactionRepository = gatewayTransactionRepository;
    }

    public ReconciliationResponseDTO execute(ReconciliationCreateDTO dto) {

        LocalDateTime start = dto.getPeriodStart().atStartOfDay();
        LocalDateTime end = dto.getPeriodEnd().atTime(23, 59, 59);

        List<Payment> payments = paymentRepository
                .findByPaymentDateBetween(start, end);

        List<GatewayTransaction> gatewayTransactions = gatewayTransactionRepository
                .findByPayment_PaymentDateBetween(start, end);

        Reconciliation reconciliation = new Reconciliation();
        reconciliation.setPeriodStart(dto.getPeriodStart());
        reconciliation.setPeriodEnd(dto.getPeriodEnd());
        reconciliation.setExecutedAt(LocalDateTime.now());

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        reconciliation.setExecutedBy(user);

        List<ReconciliationItem> items = new ArrayList<>();

        for (Payment payment : payments) {

            GatewayTransaction gateway = gatewayTransactions.stream()
                    .filter(g -> g.getPayment().getId().equals(payment.getId()))
                    .findFirst()
                    .orElse(null);

            BigDecimal systemAmount = payment.getAmount();
            BigDecimal gatewayAmount = gateway != null ? gateway.getAmount() : BigDecimal.ZERO;

            ReconciliationStatus status;

            if (gateway == null) {
                status = ReconciliationStatus.MISSING_IN_GATEWAY;
            } else if (systemAmount.compareTo(gatewayAmount) != 0) {
                status = ReconciliationStatus.DIVERGENT;
            } else {
                status = ReconciliationStatus.MATCHED;
            }

            ReconciliationItem item = new ReconciliationItem();
            item.setReconciliation(reconciliation);
            item.setPayment(payment);
            item.setInvoice(payment.getInvoice());
            item.setGatewayTransaction(gateway);
            item.setSystemAmount(systemAmount);
            item.setGatewayAmount(gatewayAmount);
            item.setStatus(status);

            items.add(item);
        }

        reconciliation.setItems(items);

        Reconciliation saved = reconciliationRepository.save(reconciliation);

        return mapToDTO(saved);
    }

    private ReconciliationResponseDTO mapToDTO(Reconciliation reconciliation) {
        return new ReconciliationResponseDTO(
                reconciliation.getId(),
                reconciliation.getPeriodStart(),
                reconciliation.getPeriodEnd(),
                reconciliation.getExecutedAt(),
                reconciliation.getExecutedBy() != null ? reconciliation.getExecutedBy().getId() : null,
                reconciliation.getItems()
                        .stream()
                        .map(item -> new ReconciliationItemResponseDTO(
                                item.getId(),
                                item.getPayment().getId(),
                                item.getGatewayTransaction() != null ? item.getGatewayTransaction().getId() : null,
                                item.getSystemAmount(),
                                item.getGatewayAmount(),
                                item.getStatus().name()
                        ))
                        .toList()
        );
    }
}
