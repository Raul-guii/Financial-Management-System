package com.raul.backend.controller;

import com.raul.backend.dto.client.ClientCreateDTO;
import com.raul.backend.dto.client.ClientResponseDTO;
import com.raul.backend.dto.client.ClientUpdateDTO;
import com.raul.backend.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients")
public class ClientController{

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCIAL_ANALYST', 'FINANCIAL_MANAGER')")
    public ResponseEntity<ClientResponseDTO> create(@RequestBody @Valid ClientCreateDTO dto) {
        return ResponseEntity.ok(clientService.create(dto));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCIAL_ANALYST', 'FINANCIAL_MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> update(@PathVariable Long id,
                                                    @RequestBody @Valid ClientUpdateDTO dto) {
        return ResponseEntity.ok(clientService.update(id, dto));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCIAL_ANALYST', 'FINANCIAL_MANAGER')")
    @GetMapping
    public ResponseEntity<List<ClientResponseDTO>> findAll() {
        return ResponseEntity.ok(clientService.findAll());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCIAL_ANALYST', 'FINANCIAL_MANAGER')")
    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.findById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCIAL_ANALYST', 'FINANCIAL_MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clientService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/defaulters")
    public List<ClientResponseDTO> getDefaulters() {
        return clientService.findDefaulters();
    }

    @PostMapping("/identify-defaulters")
    public void identifyDefaulters() {
        clientService.identifyDefaulters();
    }
}
