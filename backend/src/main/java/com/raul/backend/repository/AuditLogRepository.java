package com.raul.backend.repository;

import com.raul.backend.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    Page<AuditLog> findAll(Pageable pageable);
    Page<AuditLog> findByEntityType(String entityType, Pageable pageable);
    Page<AuditLog> findByAction(String action, Pageable pageable);
    Page<AuditLog> findByEntityTypeAndAction(String entityType, String action, Pageable pageable);

    boolean existsByEntityIdAndActionAndCreatedAtBetween(
            Long entityId,
            String action,
            LocalDateTime start,
            LocalDateTime end
    );

    boolean existsByEntityIdAndAction(Long entityId, String action);

}

