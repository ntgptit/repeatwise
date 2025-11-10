package com.repeatwise.entity.base;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

/**
 * Base entity class with soft delete support
 */
@Getter
@Setter
@MappedSuperclass
public abstract class SoftDeletableEntity extends BaseEntity {

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * Check if entity is deleted
     */
    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    /**
     * Soft delete the entity
     */
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    /**
     * Restore the soft-deleted entity
     */
    public void restore() {
        this.deletedAt = null;
    }
}
