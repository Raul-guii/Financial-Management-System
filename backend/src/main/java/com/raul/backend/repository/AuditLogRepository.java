package com.raul.backend.repository;

import com.raul.backend.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    boolean existsByEntityIdAndActionAndCreatedAtBetween(
            Long entityId,
            String action,
            LocalDateTime start,
            LocalDateTime end
    );

    boolean existsByEntityIdAndAction(Long entityId, String action);

}

