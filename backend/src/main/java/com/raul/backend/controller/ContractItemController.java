package com.raul.backend.controller;

import com.raul.backend.dto.contractitem.*;
import com.raul.backend.service.ContractItemService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contract-items")
public class ContractItemController {

    private final ContractItemService contractItemService;

    public ContractItemController(ContractItemService contractItemService) {
        this.contractItemService = contractItemService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL_ANALYST','FINANCIAL_MANAGER')")
    public ResponseEntity<ContractItemResponseDTO> create(
            @RequestBody @Valid ContractItemCreateDTO dto) {

        return ResponseEntity.ok(contractItemService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL_ANALYST','FINANCIAL_MANAGER')")
    public ResponseEntity<ContractItemResponseDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid ContractItemUpdateDTO dto) {

        return ResponseEntity.ok(contractItemService.update(id, dto));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL_ANALYST','FINANCIAL_MANAGER')")
    public ResponseEntity<List<ContractItemResponseDTO>> findAll() {

        return ResponseEntity.ok(contractItemService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL_ANALYST','FINANCIAL_MANAGER')")
    public ResponseEntity<ContractItemResponseDTO> findById(@PathVariable Long id) {

        return ResponseEntity.ok(contractItemService.findById(id));
    }

    @GetMapping("/contract/{contractId}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL_ANALYST','FINANCIAL_MANAGER')")
    public ResponseEntity<List<ContractItemResponseDTO>> findByContractId(
            @PathVariable Long contractId) {

        return ResponseEntity.ok(contractItemService.findByContractId(contractId));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL_MANAGER')")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {

        contractItemService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}