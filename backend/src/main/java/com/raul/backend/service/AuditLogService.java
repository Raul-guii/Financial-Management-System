package com.raul.backend.service;

import com.raul.backend.entity.AuditLog;
import com.raul.backend.entity.User;
import com.raul.backend.enums.NotificationType;
import com.raul.backend.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public void log(Long entityId, String entityType, NotificationType type, Long userId) {

        AuditLog log = new AuditLog();
        log.setEntityId(entityId);
        log.setEntityType(entityType);
        log.setAction(type.name());
        log.setUserId(userId);

        auditLogRepository.save(log);
    }
}