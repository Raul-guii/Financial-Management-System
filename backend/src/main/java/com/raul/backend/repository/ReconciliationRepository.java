package com.raul.backend.repository;

import com.raul.backend.entity.Reconciliation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReconciliationRepository extends JpaRepository<Reconciliation, Long> {
}
