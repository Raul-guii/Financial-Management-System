package com.raul.backend.controller;

import com.raul.backend.dto.financialparameter.FinancialParameterCreateDTO;
import com.raul.backend.dto.financialparameter.FinancialParameterResponseDTO;
import com.raul.backend.dto.financialparameter.FinancialParameterUpdateDTO;
import com.raul.backend.entity.User;
import com.raul.backend.repository.UserRepository;
import com.raul.backend.service.FinancialParameterService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/financial-parameters")
public class FinancialParameterController {

    private final FinancialParameterService service;
    private final UserRepository userRepository;

    public FinancialParameterController(FinancialParameterService service, UserRepository userRepository) {
        this.service = service;
        this.userRepository = userRepository;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL_MANAGER')")
    public ResponseEntity<FinancialParameterResponseDTO> create(
            @Valid @RequestBody FinancialParameterCreateDTO dto,
            Authentication authentication) {

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário logado não encontrado"));

        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto, user));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL_MANAGER')")
    public ResponseEntity<List<FinancialParameterResponseDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL_MANAGER')")
    public ResponseEntity<FinancialParameterResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL_MANAGER')")
    public ResponseEntity<FinancialParameterResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody FinancialParameterUpdateDTO dto) {

        return ResponseEntity.ok(service.update(id, dto));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL_MANAGER')")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        service.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/test-parameters")
    public String test() {
        BigDecimal interest = service.getActiveValueByName("DAILY_INTEREST");
        BigDecimal lateFee = service.getActiveValueByName("LATE_FEE");

        return "Interest: " + interest + " | Late Fee: " + lateFee;
    }

    @GetMapping("/test-calculation")
    public BigDecimal testCalculation() {

        BigDecimal amount = new BigDecimal("1000");
        int daysLate = 10;

        BigDecimal interest = service.getActiveValueByName("DAILY_INTEREST");
        BigDecimal lateFee = service.getActiveValueByName("LATE_FEE");

        BigDecimal total = amount;

        // multa
        total = total.add(amount.multiply(lateFee.divide(new BigDecimal("100"))));

        // juros diário
        total = total.add(
                amount.multiply(interest.divide(new BigDecimal("100")))
                        .multiply(new BigDecimal(daysLate))
        );

        return total;
    }
}