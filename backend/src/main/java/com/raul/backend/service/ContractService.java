package com.raul.backend.service;

import com.raul.backend.dto.contract.ContractCreateDTO;
import com.raul.backend.dto.contract.ContractResponseDTO;
import com.raul.backend.dto.contract.ContractUpdateDTO;
import com.raul.backend.entity.*;
import com.raul.backend.enums.AuditAction;
import com.raul.backend.enums.ContractStatus;
import com.raul.backend.repository.ClientRepository;
import com.raul.backend.repository.ContractRepository;
import com.raul.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ContractService {

    private final ContractRepository contractRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final InvoiceService invoiceService;
    private final AuditLogService auditLogService;
    private final InvoiceGeneratorService invoiceGeneratorService;

    // CREATE CONTRACT -----------
    @Transactional
    public ContractResponseDTO create(ContractCreateDTO dto) {

        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new RuntimeException("Data final não pode ser antes da inicial");
        }

        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        if (client.getDeletedAt() != null) {
            throw new RuntimeException("Cliente está inativo");
        }

        Contract contract = new Contract();

        User loggedUser = getLoggedUser();

        contract.setCreatedBy(loggedUser);
        contract.setStatus(ContractStatus.ACTIVE);
        contract.setBillingPeriod(dto.getBillingPeriod());
        contract.setStartDate(dto.getStartDate());
        contract.setEndDate(dto.getEndDate());
        contract.setClient(client);

        contract = contractRepository.save(contract);

        // gera faturas automaticamente após salvar o contrato
        if (contract.getItems() != null && !contract.getItems().isEmpty()) {
            invoiceGeneratorService.generateForContract(contract);
        }

        auditLogService.log(contract.getId(), "CONTRACT", AuditAction.CONTRACT_CREATED,
                loggedUser.getId(), loggedUser.getName(),
                "Contrato criado para cliente #" + contract.getClient().getId());

        return toDTO(contract);

    }

    // UPDATE CONTRACT ---------------
    @Transactional
    public ContractResponseDTO update(Long id, ContractUpdateDTO dto) {

        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contrato não encontrado"));

        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new RuntimeException("Data final inválida");
        }

        if (contract.getInvoices() != null && !contract.getInvoices().isEmpty()) {
            throw new RuntimeException("Não é possível alterar contrato com faturas geradas");
        }

        User loggedUser = getLoggedUser();

        contract.setCreatedBy(loggedUser);
        contract.setStatus(dto.getStatus());
        contract.setBillingPeriod(dto.getBillingPeriod());
        contract.setStartDate(dto.getStartDate());
        contract.setEndDate(dto.getEndDate());

        contract = contractRepository.save(contract);

        auditLogService.log(contract.getId(), "CONTRACT", AuditAction.CONTRACT_UPDATED,
                loggedUser.getId(), loggedUser.getName(),
                "Contrato atualizado");

        return toDTO(contract);
    }

    // LIST ALL CONTRACTS
    public Page<ContractResponseDTO> findAll(Pageable pageable) {
        return contractRepository.findAll(pageable)
                .map(this::toDTO);
    }

    // GET CONTRACT BY ID
    public ContractResponseDTO findById(Long id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contrato não encontrado"));

        return toDTO(contract);
    }

    //CANCEL CONTRACT
    @Transactional
    public void cancel(Long id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contrato não encontrado"));

        if (contract.getStatus() == ContractStatus.CANCELLED) {
            throw new RuntimeException("Contrato já está cancelado");
        }

        // cancela invoices primeiro (lança exceção se houver pagamento)
        if (contract.getInvoices() != null && !contract.getInvoices().isEmpty()) {
            invoiceService.cancelByContract(contract.getInvoices());
        }

        contract.setStatus(ContractStatus.CANCELLED);

        contractRepository.save(contract);

        auditLogService.log(contract.getId(), "CONTRACT", AuditAction.CONTRACT_CANCELLED,
                null, null,
                "Contrato cancelado");
    }

    public Page<ContractResponseDTO> findAll(Pageable pageable, String search) {
        if (search != null && !search.isBlank()) {
            return contractRepository
                    .findByClientNameContainingIgnoreCase(search, pageable)
                    .map(this::toDTO);
        }
        return contractRepository.findAll(pageable).map(this::toDTO);
    }

    private ContractResponseDTO toDTO(Contract contract) {
        return new ContractResponseDTO(
                contract.getId(),
                contract.getStatus(),
                contract.getBillingPeriod(),
                contract.getStartDate(),
                contract.getEndDate(),
                contract.getCreatedBy() != null ? contract.getCreatedBy().getId() : null,
                contract.getClient() != null ? contract.getClient().getId() : null,
                contract.getInvoices() != null ? contract.getInvoices().stream().map(Invoice::getId).toList() : List.of(),
                contract.getItems() != null ? contract.getItems().stream().map(ContractItem::getId).toList() : List.of()
        );
    }

    private User getLoggedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            String email = userDetails.getUsername();

            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        }

        throw new RuntimeException("Usuário não autenticado");
    }
}
