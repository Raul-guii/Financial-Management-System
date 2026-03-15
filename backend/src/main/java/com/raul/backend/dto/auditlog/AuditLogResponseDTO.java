package com.raul.backend.dto.auditlog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponseDTO {

    private Long id;
    private Long entityId;
    private String action;
    private String entityType;
    private LocalDateTime created_at;
    private Long userId;
}
