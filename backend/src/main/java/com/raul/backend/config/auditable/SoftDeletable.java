package com.raul.backend.config.auditable;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public abstract class SoftDeletable extends Auditable{

    @Column(name = "deleted_at")
    protected LocalDateTime deletedAt;
}
