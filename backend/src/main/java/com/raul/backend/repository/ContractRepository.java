package com.raul.backend.repository;

import com.raul.backend.entity.Contract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ContractRepository extends JpaRepository<Contract, Long> {

    boolean existsByClientId(Long clientId);
    Page<Contract> findByClientNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT c FROM Contract c JOIN FETCH c.items WHERE c.id = :id")
    Optional<Contract> findByIdWithItems(Long id);
}
