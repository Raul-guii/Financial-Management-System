package com.raul.backend.service;

import com.raul.backend.dto.client.ClientCreateDTO;
import com.raul.backend.dto.client.ClientResponseDTO;
import com.raul.backend.dto.client.ClientUpdateDTO;
import com.raul.backend.entity.Client;
import com.raul.backend.enums.ClientType;
import com.raul.backend.repository.ClientRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    // CREATE CLIENT --------
    @Transactional
    public ClientResponseDTO create(ClientCreateDTO dto) {

        validateDocument(dto.getType(), dto.getDocument());

        if (clientRepository.existsByDocument(dto.getDocument())) {
            throw new RuntimeException("Documento já cadastrado");
        }

        Client client = new Client();

        client.setName(dto.getName());
        client.setType(dto.getType());
        client.setDocument(dto.getDocument());
        client.setEmail(dto.getEmail());
        client.setPhone(dto.getPhone());

        client.setAddressStreet(dto.getAddressStreet());
        client.setAddressNumber(dto.getAddressNumber());
        client.setAddressNeighborhood(dto.getAddressNeighborhood());
        client.setAddressCity(dto.getAddressCity());
        client.setAddressState(dto.getAddressState());
        client.setAddressPostalCode(dto.getAddressPostalCode());
        client.setAddressCountry(dto.getAddressCountry());

        client = clientRepository.save(client);

        return toDTO(client);
    }

    // UPDATE CLIENT -------------
    @Transactional
    public ClientResponseDTO update(Long id, ClientUpdateDTO dto) {

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        if (dto.getName() != null) {
            client.setName(dto.getName());
        }

        if (dto.getType() != null && dto.getDocument() != null) {
            validateDocument(dto.getType(), dto.getDocument());

            if (clientRepository.existsByDocumentAndIdNot(dto.getDocument(), id)) {
                throw new RuntimeException("Documento já cadastrado");
            }

            client.setType(dto.getType());
            client.setDocument(dto.getDocument());
        }

        if (dto.getEmail() != null) {
            client.setEmail(dto.getEmail());
        }

        if (dto.getPhone() != null) {
            client.setPhone(dto.getPhone());
        }

        // endereço
        if (dto.getAddressStreet() != null) client.setAddressStreet(dto.getAddressStreet());
        if (dto.getAddressNumber() != null) client.setAddressNumber(dto.getAddressNumber());
        if (dto.getAddressNeighborhood() != null) client.setAddressNeighborhood(dto.getAddressNeighborhood());
        if (dto.getAddressCity() != null) client.setAddressCity(dto.getAddressCity());
        if (dto.getAddressState() != null) client.setAddressState(dto.getAddressState());
        if (dto.getAddressPostalCode() != null) client.setAddressPostalCode(dto.getAddressPostalCode());
        if (dto.getAddressCountry() != null) client.setAddressCountry(dto.getAddressCountry());

        client = clientRepository.save(client);

        return toDTO(client);
    }

    // GET ALL --------------
    public List<ClientResponseDTO> findAll() {
        return clientRepository.findAll().stream()
                .filter(c -> c.getDeletedAt() == null)
                .map(this::toDTO)
                .toList();
    }

    // GET BY ID
    public ClientResponseDTO findById(Long id) {
        Client client = clientRepository.findById(id)
                .filter(c -> c.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        return toDTO(client);
    }

    // DELETE (SOFT)
    @Transactional
    public void delete(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        client.setDeletedAt(LocalDateTime.now());

        clientRepository.save(client);
    }

    private ClientResponseDTO toDTO(Client client) {
        return new ClientResponseDTO(
                client.getId(),
                client.getName(),
                client.getType(),
                client.getDocument(),
                client.getEmail(),
                client.getPhone(),
                client.getAddressStreet(),
                client.getAddressNumber(),
                client.getAddressNeighborhood(),
                client.getAddressCity(),
                client.getAddressState(),
                client.getAddressPostalCode(),
                client.getAddressCountry(),
                client.getDeletedAt() == null,
                client.getCreatedAt(),
                client.getUpdatedAt(),
                null
        );
    }

    private void validateDocument(ClientType type, String document) {

        if (type == ClientType.PERSON && document.length() != 11) {
            throw new RuntimeException("CPF inválido");
        }

        if (type == ClientType.COMPANY && document.length() != 14) {
            throw new RuntimeException("CNPJ inválido");
        }
    }
}
