package com.raul.backend.repository;

import com.raul.backend.entity.Contract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRepository extends JpaRepository<Contract, Long> {

    boolean existsByClientId(Long clientId);
    Page<Contract> findByClientNameContainingIgnoreCase(String name, Pageable pageable);
}
