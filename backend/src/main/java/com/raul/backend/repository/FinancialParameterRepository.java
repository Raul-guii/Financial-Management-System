package com.raul.backend.repository;

import com.raul.backend.entity.FinancialParameter;
import com.raul.backend.enums.FinancialParameterType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FinancialParameterRepository extends JpaRepository<FinancialParameter, Long> {

    List<FinancialParameter> findAllByActiveTrue();

    Optional<FinancialParameter> findByNameIgnoreCaseAndActiveTrue(String name);

    Optional<FinancialParameter> findByTypeAndActiveTrue(FinancialParameterType type);
}
