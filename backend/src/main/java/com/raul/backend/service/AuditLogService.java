package com.raul.backend.service;

import com.raul.backend.dto.auditlog.AuditLogResponseDTO;
import com.raul.backend.entity.AuditLog;
import com.raul.backend.entity.User;
import com.raul.backend.enums.AuditAction;
import com.raul.backend.enums.NotificationType;
import com.raul.backend.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public void log(Long entityId, String entityType, AuditAction action, Long userId, String userName, String description) {
        System.out.println("SALVANDO AUDIT LOG: " + entityType + " " + action.name());
        AuditLog log = new AuditLog();
        log.setEntityId(entityId);
        log.setEntityType(entityType);
        log.setAction(action.name());
        log.setUserId(userId);
        log.setUserName(userName);
        log.setDescription(description);
        AuditLog saved = auditLogRepository.save(log);
        System.out.println("AUDIT LOG SALVO ID: " + saved.getId());
    }

    public Page<AuditLogResponseDTO> findAll(Pageable pageable, String entityType, String action) {
        if (entityType != null && action != null) {
            return auditLogRepository.findByEntityTypeAndAction(entityType, action, pageable).map(this::toDTO);
        }
        if (entityType != null) {
            return auditLogRepository.findByEntityType(entityType, pageable).map(this::toDTO);
        }
        if (action != null) {
            return auditLogRepository.findByAction(action, pageable).map(this::toDTO);
        }
        return auditLogRepository.findAll(pageable).map(this::toDTO);
    }

    private AuditLogResponseDTO toDTO(AuditLog log) {
        return new AuditLogResponseDTO(
                log.getId(),
                log.getEntityId(),
                log.getAction(),
                log.getEntityType(),
                log.getUserName(),
                log.getDescription(),
                log.getCreatedAt(),
                log.getUserId()
        );
    }
}