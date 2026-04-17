package com.raul.backend.service;

import com.raul.backend.dto.financialparameter.FinancialParameterCreateDTO;
import com.raul.backend.dto.financialparameter.FinancialParameterResponseDTO;
import com.raul.backend.dto.financialparameter.FinancialParameterUpdateDTO;
import com.raul.backend.entity.FinancialParameter;
import com.raul.backend.entity.User;
import com.raul.backend.repository.FinancialParameterRepository;
import com.raul.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class FinancialParameterService {

    private final FinancialParameterRepository repository;
    private final UserRepository userRepository;

    public FinancialParameterService(FinancialParameterRepository repository,
                                     UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Transactional
    public FinancialParameterResponseDTO create(FinancialParameterCreateDTO dto, User user) {
        FinancialParameter parameter = new FinancialParameter();
        parameter.setName(dto.getName());
        parameter.setValue(dto.getValue());
        parameter.setType(dto.getType());
        parameter.setCategory(dto.getCategory());
        parameter.setDescription(dto.getDescription());
        parameter.setActive(dto.getActive() != null ? dto.getActive() : true);

        validateByCategory(parameter);

        parameter.setCreatedBy(user);
        parameter.setUpdatedBy(user);

        parameter = repository.save(parameter);
        return toDTO(parameter);
    }

    public List<FinancialParameterResponseDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public FinancialParameterResponseDTO findById(Long id) {
        FinancialParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parâmetro financeiro não encontrado"));
        return toDTO(parameter);
    }

    @Transactional
    public FinancialParameterResponseDTO update(Long id, FinancialParameterUpdateDTO dto) {
        FinancialParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parâmetro financeiro não encontrado"));

        if (dto.getName() != null) parameter.setName(dto.getName());
        if (dto.getValue() != null) parameter.setValue(dto.getValue());
        if (dto.getType() != null) parameter.setType(dto.getType());
        if (dto.getCategory() != null) parameter.setCategory(dto.getCategory()); // 👈 ADD
        if (dto.getDescription() != null) parameter.setDescription(dto.getDescription());
        if (dto.getActive() != null) parameter.setActive(dto.getActive());

        if (dto.getUpdatedById() != null) {
            User updatedBy = userRepository.findById(dto.getUpdatedById())
                    .orElseThrow(() -> new RuntimeException("Usuário updatedBy não encontrado"));
            parameter.setUpdatedBy(updatedBy);
        }

        validateByCategory(parameter);

        parameter = repository.save(parameter);
        return toDTO(parameter);
    }

    @Transactional
    public void deactivate(Long id) {
        FinancialParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parâmetro financeiro não encontrado"));

        parameter.setActive(false);
        repository.save(parameter);
    }

    public BigDecimal getActiveValueByName(String name) {
        FinancialParameter parameter = repository.findByNameIgnoreCaseAndActiveTrue(name)
                .orElseThrow(() -> new RuntimeException("Parâmetro financeiro não encontrado: " + name));

        return parameter.getValue();
    }

    public FinancialParameter getActiveByName(String name) {
        return repository.findByNameIgnoreCaseAndActiveTrue(name)
                .orElseThrow(() -> new RuntimeException("Parâmetro financeiro não encontrado: " + name));
    }

    private void validateByCategory(FinancialParameter parameter) {
        if (parameter.getCategory() == null) return;

        switch (parameter.getCategory()) {

            case PERCENTAGE -> {
                if (parameter.getValue().compareTo(new BigDecimal("100")) > 0) {
                    throw new RuntimeException("Percentual não pode ser maior que 100");
                }
            }

            case DAYS -> {
                if (parameter.getValue().compareTo(BigDecimal.ZERO) < 0) {
                    throw new RuntimeException("Dias não pode ser negativo");
                }
            }

            case MONETARY -> {
                if (parameter.getValue().compareTo(BigDecimal.ZERO) < 0) {
                    throw new RuntimeException("Valor monetário não pode ser negativo");
                }
            }

            case FLAG -> {
            }
        }
    }

    private FinancialParameterResponseDTO toDTO(FinancialParameter parameter) {
        return new FinancialParameterResponseDTO(
                parameter.getId(),
                parameter.getName(),
                parameter.getValue(),
                parameter.getType(),
                parameter.getCategory(),
                parameter.getDescription(),
                parameter.getActive(),
                parameter.getUpdatedBy() != null ? parameter.getUpdatedBy().getId() : null,
                parameter.getCreatedBy() != null ? parameter.getCreatedBy().getId() : null
        );
    }
}