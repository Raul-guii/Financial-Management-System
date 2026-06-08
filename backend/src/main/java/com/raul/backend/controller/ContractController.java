package com.raul.backend.controller;

import com.raul.backend.dto.contract.ContractCreateDTO;
import com.raul.backend.dto.contract.ContractResponseDTO;
import com.raul.backend.dto.contract.ContractUpdateDTO;
import com.raul.backend.entity.Contract;
import com.raul.backend.repository.ContractRepository;
import com.raul.backend.service.ContractService;
import com.raul.backend.service.InvoiceGeneratorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/contracts")
public class ContractController {

    private final ContractService contractService;
    private final ContractRepository contractRepository;
    private final InvoiceGeneratorService invoiceGeneratorService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL_ANALYST','FINANCIAL_MANAGER')")
    public ResponseEntity<ContractResponseDTO> create(@RequestBody @Valid ContractCreateDTO dto) {
        return ResponseEntity.ok(contractService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL_ANALYST','FINANCIAL_MANAGER')")
    public ResponseEntity<ContractResponseDTO> update(@PathVariable Long id,
                                                      @RequestBody @Valid ContractUpdateDTO dto) {
        return ResponseEntity.ok(contractService.update(id, dto));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL_ANALYST','FINANCIAL_MANAGER')")
    public ResponseEntity<Page<ContractResponseDTO>> findAll(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(contractService.findAll(pageable, search));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL_ANALYST','FINANCIAL_MANAGER')")
    public ResponseEntity<ContractResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(contractService.findById(id));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL_MANAGER')")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        contractService.cancel(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/generate-invoices")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCIAL_MANAGER')")
    public ResponseEntity<Void> generateInvoices(@PathVariable Long id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contrato não encontrado"));
        invoiceGeneratorService.generateForContract(contract);
        return ResponseEntity.ok().build();
    }
}
