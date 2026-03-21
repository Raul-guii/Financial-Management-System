package com.raul.backend.service;

import com.raul.backend.dto.contractitem.*;
import com.raul.backend.entity.Contract;
import com.raul.backend.entity.ContractItem;
import com.raul.backend.entity.InvoiceLine;
import com.raul.backend.repository.ContractItemRepository;
import com.raul.backend.repository.ContractRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContractItemService {

    private final ContractItemRepository contractItemRepository;
    private final ContractRepository contractRepository;

    public ContractItemService(ContractItemRepository contractItemRepository,
                               ContractRepository contractRepository) {
        this.contractItemRepository = contractItemRepository;
        this.contractRepository = contractRepository;
    }

    // CREATE
    @Transactional
    public ContractItemResponseDTO create(ContractItemCreateDTO dto) {

        Contract contract = contractRepository.findById(dto.getContractId())
                .orElseThrow(() -> new RuntimeException("Contrato não encontrado"));

        ContractItem item = new ContractItem();

        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setQuantity(dto.getQuantity());
        item.setUnitPrice(dto.getUnitPrice());
        item.setActive(dto.getActive() != null ? dto.getActive() : true);
        item.setContract(contract);

        item = contractItemRepository.save(item);

        return toDTO(item);
    }

    // UPDATE
    @Transactional
    public ContractItemResponseDTO update(Long id, ContractItemUpdateDTO dto) {

        ContractItem item = contractItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item não encontrado"));

        if (dto.getName() != null) item.setName(dto.getName());
        if (dto.getDescription() != null) item.setDescription(dto.getDescription());
        if (dto.getQuantity() != null) item.setQuantity(dto.getQuantity());
        if (dto.getUnitPrice() != null) item.setUnitPrice(dto.getUnitPrice());
        if (dto.getActive() != null) item.setActive(dto.getActive());

        item = contractItemRepository.save(item);

        return toDTO(item);
    }

    // GET ALL
    public List<ContractItemResponseDTO> findAll() {
        return contractItemRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // GET BY ID
    public ContractItemResponseDTO findById(Long id) {
        ContractItem item = contractItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item não encontrado"));

        return toDTO(item);
    }

    // GET BY CONTRACT
    public List<ContractItemResponseDTO> findByContractId(Long contractId) {

        contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Contrato não encontrado"));

        return contractItemRepository.findByContractId(contractId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // DEACTIVATE
    @Transactional
    public void deactivate(Long id) {

        ContractItem item = contractItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item não encontrado"));

        item.setActive(false);

        contractItemRepository.save(item);
    }

    private ContractItemResponseDTO toDTO(ContractItem item) {
        return new ContractItemResponseDTO(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getActive(),
                item.getContract() != null ? item.getContract().getId() : null,
                item.getInvoiceLines() != null
                        ? item.getInvoiceLines().stream().map(InvoiceLine::getId).toList()
                        : List.of()
        );
    }
}