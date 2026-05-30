package com.raul.backend.service;

import com.raul.backend.dto.client.ClientCreateDTO;
import com.raul.backend.dto.client.ClientResponseDTO;
import com.raul.backend.dto.client.ClientUpdateDTO;
import com.raul.backend.entity.Client;
import com.raul.backend.entity.Invoice;
import com.raul.backend.entity.User;
import com.raul.backend.enums.AuditAction;
import com.raul.backend.enums.ClientType;
import com.raul.backend.enums.InvoiceStatus;
import com.raul.backend.repository.ClientRepository;
import com.raul.backend.repository.ContractRepository;
import com.raul.backend.repository.InvoiceRepository;
import com.raul.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final InvoiceRepository invoiceRepository;
    private final ContractRepository contractRepository;
    private final AuditLogService auditLogService;
    private final UserService userService;
    private final UserRepository userRepository;

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
        client.setDefaulter(false);
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

        User loggedUser = getLoggedUser();

        auditLogService.log(client.getId(), "CLIENT", AuditAction.CLIENT_CREATED,
                loggedUser != null ? loggedUser.getId() : null,
                loggedUser != null ? loggedUser.getName() : null,
                "Cliente " + client.getName() + " cadastrado");


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

        User loggedUser = getLoggedUser();

        auditLogService.log(client.getId(), "CLIENT", AuditAction.CLIENT_UPDATED,
                loggedUser != null ? loggedUser.getId() : null,
                loggedUser != null ? loggedUser.getName() : null,
                "Cliente " + client.getName() + " atualizado");

        return toDTO(client);
    }

    // GET ALL --------------
    public Page<ClientResponseDTO> findAll(Pageable pageable) {
        return clientRepository.findByDeletedAtIsNull(pageable)
                .map(this::toDTO);
    }

    public Page<ClientResponseDTO> findAll(Pageable pageable, String search) {
        if (search != null && !search.isBlank()) {
            return clientRepository
                    .findByDeletedAtIsNullAndNameContainingIgnoreCaseOrDeletedAtIsNullAndDocumentContainingIgnoreCase(
                            search, search, pageable)
                    .map(this::toDTO);
        }
        return clientRepository.findByDeletedAtIsNull(pageable).map(this::toDTO);
    }

    // GET BY ID
    public ClientResponseDTO findById(Long id) {
        Client client = clientRepository.findById(id)
                .filter(c -> c.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        return toDTO(client);
    }

    public List<ClientResponseDTO> findDefaulters() {
        return clientRepository.findDefaulters()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // DELETE (SOFT)
    @Transactional
    public void delete(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        client.setDeletedAt(LocalDateTime.now());

        clientRepository.save(client);

        User loggedUser = getLoggedUser();

        auditLogService.log(client.getId(), "CLIENT", AuditAction.CLIENT_DEACTIVATED,
                loggedUser != null ? loggedUser.getId() : null,
                loggedUser != null ? loggedUser.getName() : null,
                "Cliente " + client.getName() + " desativado");
    }

    @Transactional
    public void hardDelete(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        boolean hasContracts = contractRepository.existsByClientId(id);
        if (hasContracts) {
            throw new RuntimeException("Não é possível excluir um cliente com contratos vinculados");
        }

        clientRepository.delete(client);
    }

    @Transactional
    public void identifyDefaulters() {

        List<Invoice> overdueInvoices =
                invoiceRepository.findByDueDateBeforeAndStatusNot(
                        LocalDate.now(),
                        InvoiceStatus.PAID
                );

        Set<Client> defaulters = new HashSet<>();

        for (Invoice invoice : overdueInvoices) {
            invoice.setStatus(InvoiceStatus.OVERDUE);

            Client client = invoice.getContract().getClient();
            client.setDefaulter(true);
            defaulters.add(client);
        }

        invoiceRepository.saveAll(overdueInvoices);
        clientRepository.saveAll(defaulters);
    }

    private User getLoggedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        }
        return null;
    }

    private void validateDocument(ClientType type, String document) {
        if (type == ClientType.PERSON) {
            if (!isValidCpf(document)) {
                throw new RuntimeException("CPF inválido");
            }
        }
        if (type == ClientType.COMPANY) {
            if (!isValidCnpj(document)) {
                throw new RuntimeException("CNPJ inválido");
            }
        }
    }

    private boolean isValidCpf(String cpf) {
        if (cpf == null || cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) return false;

        int sum = 0;
        for (int i = 0; i < 9; i++) sum += (cpf.charAt(i) - '0') * (10 - i);
        int first = 11 - (sum % 11);
        if (first >= 10) first = 0;
        if (first != (cpf.charAt(9) - '0')) return false;

        sum = 0;
        for (int i = 0; i < 10; i++) sum += (cpf.charAt(i) - '0') * (11 - i);
        int second = 11 - (sum % 11);
        if (second >= 10) second = 0;
        return second == (cpf.charAt(10) - '0');
    }

    private boolean isValidCnpj(String cnpj) {
        if (cnpj == null || cnpj.length() != 14 || cnpj.matches("(\\d)\\1{13}")) return false;

        int[] weights1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] weights2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

        int sum = 0;
        for (int i = 0; i < 12; i++) sum += (cnpj.charAt(i) - '0') * weights1[i];
        int first = sum % 11 < 2 ? 0 : 11 - (sum % 11);
        if (first != (cnpj.charAt(12) - '0')) return false;

        sum = 0;
        for (int i = 0; i < 13; i++) sum += (cnpj.charAt(i) - '0') * weights2[i];
        int second = sum % 11 < 2 ? 0 : 11 - (sum % 11);
        return second == (cnpj.charAt(13) - '0');
    }

    private ClientResponseDTO toDTO(Client client) {
        return new ClientResponseDTO(
                client.getId(),
                client.getName(),
                client.getType(),
                client.getDefaulter(),
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
}
