package com.raul.backend.config.initializer;

import com.raul.backend.entity.Contract;
import com.raul.backend.entity.Invoice;
import com.raul.backend.enums.InvoiceStatus;
import com.raul.backend.repository.ContractRepository;
import com.raul.backend.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

//@Component
@RequiredArgsConstructor
@Profile("dev")
@Order(5)
public class InvoiceInitializer implements CommandLineRunner {

    private final InvoiceRepository repository;
    private final ContractRepository contractRepository;

    @Override
    public void run(String... args) {

        System.out.println("[FaturasInitializer] Iniciando criação de faturas...");

        if (repository.count() > 0) return;

        List<Contract> contracts = contractRepository.findAll();

        for (Contract contract : contracts) {

            Invoice invoice = new Invoice();

            invoice.setContract(contract);

            invoice.setStatus(
                    contract.getId() % 5 == 0
                            ? InvoiceStatus.PAID
                            : InvoiceStatus.PENDING
            );

            invoice.setIssueDate(LocalDate.now());

            invoice.setDueDate(
                    LocalDate.now().plusDays((contract.getId() % 30) + 1)
            );

            BigDecimal amount = BigDecimal.valueOf(
                    100 + (contract.getId() % 20) * 50
            );

            invoice.setAmount(amount);

            invoice.setOriginalAmount(amount);

            invoice.setLateFreeAmount(BigDecimal.ZERO);

            invoice.setInterestAmount(BigDecimal.ZERO);

            invoice.setCreatedAt(LocalDateTime.now());

            repository.save(invoice);
        }

        System.out.println("1000 invoices criadas com sucesso.");
    }
}
