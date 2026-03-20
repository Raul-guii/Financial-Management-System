package com.raul.backend.service;

import com.raul.backend.dto.contract.ContractCreateDTO;
import com.raul.backend.dto.contract.ContractResponseDTO;
import com.raul.backend.dto.contract.ContractUpdateDTO;
import com.raul.backend.entity.*;
import com.raul.backend.enums.ContractStatus;
import com.raul.backend.repository.ClientRepository;
import com.raul.backend.repository.ContractRepository;
import com.raul.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContractService {

    private final ContractRepository contractRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;

    public ContractService(ContractRepository contractRepository, ClientRepository clientRepository, UserRepository userRepository) {
        this.contractRepository = contractRepository;
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
    }

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

        return toDTO(contract);
    }

    // LIST ALL CONTRACTS
    public List<ContractResponseDTO> findAll() {
        return contractRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
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

        contract.setStatus(ContractStatus.CANCELLED);

        contractRepository.save(contract);
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
