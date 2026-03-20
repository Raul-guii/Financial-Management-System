package com.raul.backend.repository;

import com.raul.backend.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContractRepository extends JpaRepository<Contract, Long> {

    List<Contract> findByClientId(Long clientId);
}
