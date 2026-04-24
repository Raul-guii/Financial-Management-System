package com.raul.backend.service;

import com.raul.backend.dto.reconciliation.ReconciliationReportDTO;
import com.raul.backend.entity.Reconciliation;
import com.raul.backend.entity.ReconciliationItem;
import com.raul.backend.repository.ReconciliationItemRepository;
import com.raul.backend.repository.ReconciliationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {

    private final ReconciliationItemRepository reconciliationItemRepository;
    private final ReconciliationRepository reconciliationRepository;

    public ReportService(ReconciliationItemRepository reconciliationItemRepository, ReconciliationRepository reconciliationRepository) {
        this.reconciliationItemRepository = reconciliationItemRepository;
        this.reconciliationRepository = reconciliationRepository;
    }

    public String generateReconciliationCsv() {

        Reconciliation reconciliation = reconciliationRepository
                .findTopByOrderByExecutedAtDesc();

        if (reconciliation == null || reconciliation.getItems().isEmpty()) {
            return "Nenhum dado para exportar";
        }

        StringBuilder csv = new StringBuilder();

        csv.append("ReconciliationId,ItemId,PaymentId,InvoiceId,GatewayTransactionId,SystemAmount,GatewayAmount,Status\n");

        for (ReconciliationItem item : reconciliation.getItems()) {

            csv.append(reconciliation.getId()).append(",");
            csv.append(item.getId()).append(",");
            csv.append(item.getPayment().getId()).append(",");
            csv.append(item.getInvoice().getId()).append(",");

            csv.append(item.getGatewayTransaction() != null
                    ? item.getGatewayTransaction().getId()
                    : "").append(",");

            csv.append(item.getSystemAmount()).append(",");
            csv.append(item.getGatewayAmount()).append(",");
            csv.append(item.getStatus().name()).append("\n");
        }

        return csv.toString();
    }
}