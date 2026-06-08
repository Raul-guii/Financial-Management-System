package com.raul.backend.service;

import com.raul.backend.entity.*;
import com.raul.backend.enums.BillingPeriod;
import com.raul.backend.enums.InvoiceStatus;
import com.raul.backend.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class InvoiceGeneratorService {

    private final InvoiceRepository invoiceRepository;

    public List<Invoice> generateForContract(Contract contract) {
        List<Invoice> generated = new ArrayList<>();

        LocalDate cursor = contract.getStartDate();
        LocalDate contractEnd = contract.getEndDate();
        int intervalMonths = toMonths(contract.getBillingPeriod());

        while (cursor.isBefore(contractEnd)) {
            LocalDate issueDate = cursor;
            LocalDate nextCursor = cursor.plusMonths(intervalMonths);
            LocalDate dueDate = nextCursor.minusDays(1);

            if (dueDate.isAfter(contractEnd)) dueDate = contractEnd;
            if (issueDate.isAfter(contractEnd)) break;

            boolean alreadyExists = invoiceRepository
                    .existsByContractIdAndIssueDateBetweenAndStatusNot(
                            contract.getId(),
                            issueDate,
                            issueDate.plusMonths(intervalMonths).minusDays(1),
                            InvoiceStatus.CANCELLED
                    );

            System.out.println("cursor=" + cursor + " issueDate=" + issueDate + " dueDate=" + dueDate + " contractEnd=" + contractEnd + " alreadyExists=" + alreadyExists);

            if (!alreadyExists) {
                Invoice invoice = buildInvoice(contract, issueDate, dueDate);
                generated.add(invoiceRepository.save(invoice));
            }

            cursor = nextCursor;
        }

        return generated;
    }

    private Invoice buildInvoice(Contract contract, LocalDate issueDate, LocalDate dueDate) {
        Invoice invoice = new Invoice();
        invoice.setStatus(InvoiceStatus.PENDING);
        invoice.setIssueDate(issueDate);
        invoice.setDueDate(dueDate);
        invoice.setLateFreeAmount(BigDecimal.ZERO);
        invoice.setInterestAmount(BigDecimal.ZERO);
        invoice.setContract(contract);

        BigDecimal total = BigDecimal.ZERO;

        for (ContractItem item : contract.getItems()) {
            if (Boolean.FALSE.equals(item.getActive())) continue;

            InvoiceLine line = new InvoiceLine();
            line.setDescription(item.getName());
            line.setQuantity(item.getQuantity());
            line.setUnitPrice(item.getUnitPrice());

            BigDecimal lineTotal = item.getQuantity().multiply(item.getUnitPrice());
            line.setTotal(lineTotal);
            line.setInvoice(invoice);
            line.setContractItem(item);

            invoice.getInvoiceLines().add(line);
            total = total.add(lineTotal);
        }

        invoice.setAmount(total);
        invoice.setOriginalAmount(total);

        return invoice;
    }

    private int toMonths(BillingPeriod period) {
        return switch (period) {
            case MONTHLY -> 1;
            case QUARTERLY -> 3;
            case SEMIANNUAL -> 6;
            case ANNUAL -> 12;
        };
    }
}