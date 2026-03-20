package com.raul.backend.controller;

import com.raul.backend.dto.contract.ContractCreateDTO;
import com.raul.backend.dto.contract.ContractResponseDTO;
import com.raul.backend.dto.contract.ContractUpdateDTO;
import com.raul.backend.service.ContractService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contracts")
public class ContractController {

    private final ContractService contractService;

    public ContractController(ContractService contractService) {
        this.contractService = contractService;
    }

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
    public ResponseEntity<List<ContractResponseDTO>> findAll() {
        return ResponseEntity.ok(contractService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL_ANALYST','FINANCIAL_MANAGER')")
    public ResponseEntity<ContractResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(contractService.findById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL_MANAGER')")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        contractService.cancel(id);
        return ResponseEntity.noContent().build();
    }
}
