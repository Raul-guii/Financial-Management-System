package com.raul.backend.repository;

import com.raul.backend.entity.ReconciliationItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReconciliationItemRepository extends JpaRepository<ReconciliationItem, Long> {
}
