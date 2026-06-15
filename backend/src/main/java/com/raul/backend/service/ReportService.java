package com.raul.backend.service;

import com.raul.backend.dto.reconciliation.ReconciliationReportDTO;
import com.raul.backend.entity.Reconciliation;
import com.raul.backend.entity.ReconciliationItem;
import com.raul.backend.repository.ReconciliationItemRepository;
import com.raul.backend.repository.ReconciliationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ReportService {

    private final ReconciliationRepository reconciliationRepository;

    public String generateReconciliationCsv() {

        Reconciliation reconciliation = reconciliationRepository
                .findTopByOrderByExecutedAtDesc();

        if (reconciliation == null || reconciliation.getItems().isEmpty()) {
            return "Nenhum dado para exportar";
        }

        StringBuilder csv = new StringBuilder();

        // cabeçalho com período e totais
        csv.append("Período:,")
                .append(reconciliation.getPeriodStart()).append(",até,")
                .append(reconciliation.getPeriodEnd()).append("\n");
        csv.append("Total recebido:,").append(reconciliation.getTotalIn()).append("\n");
        csv.append("Total reembolsado:,").append(reconciliation.getTotalOut()).append("\n");
        csv.append("Saldo líquido:,").append(reconciliation.getNetBalance()).append("\n");
        csv.append("\n");

        // colunas dos itens
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