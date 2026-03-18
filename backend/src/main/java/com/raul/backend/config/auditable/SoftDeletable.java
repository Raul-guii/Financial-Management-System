package com.raul.backend.config.auditable;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import java.time.LocalDateTime;

@MappedSuperclass
public abstract class SoftDeletable extends Auditable{

    @Column(name = "deleted_at")
    protected LocalDateTime deletedAt;
}
