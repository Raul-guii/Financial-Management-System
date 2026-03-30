package com.raul.backend.repository;

import com.raul.backend.entity.GatewayTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GatewayTransactionRepository extends JpaRepository<GatewayTransaction, Long> {

    Optional<GatewayTransaction> findByExternalId(String externalId);
}
