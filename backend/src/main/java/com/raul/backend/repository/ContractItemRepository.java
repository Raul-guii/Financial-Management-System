package com.raul.backend.repository;

import com.raul.backend.entity.ContractItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ContractItemRepository extends JpaRepository<ContractItem, Long> {
    List<ContractItem> findByContractId(Long contractId);
}
