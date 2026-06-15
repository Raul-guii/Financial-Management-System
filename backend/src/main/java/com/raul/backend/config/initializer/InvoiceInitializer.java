package com.raul.backend.config.initializer;

import com.raul.backend.entity.Contract;
import com.raul.backend.repository.ContractRepository;
import com.raul.backend.repository.InvoiceRepository;
import com.raul.backend.service.InvoiceGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

//@Component
@RequiredArgsConstructor
@Profile("dev")
@Order(5)
public class InvoiceInitializer implements CommandLineRunner {

    private final ContractRepository contractRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceGeneratorService invoiceGeneratorService;

    @Override
    public void run(String... args) {
        System.out.println("[InvoiceInitializer] Iniciando criação de faturas...");

        if (invoiceRepository.count() > 0) return;

        List<Long> contractIds = contractRepository.findAll()
                .stream()
                .map(Contract::getId)
                .toList();

        for (Long contractId : contractIds) {
            contractRepository.findByIdWithItems(contractId).ifPresent(contract -> {
                invoiceGeneratorService.generateForContract(contract);
            });
        }

        System.out.println("Faturas geradas para todos os contratos.");
    }
}

