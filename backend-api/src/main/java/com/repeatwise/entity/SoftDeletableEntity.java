package com.repeatwise.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * Base entity with soft delete support
 * Includes: id, created_at, updated_at, deleted_at
 *
 * Design: Entity Specifications Section - Soft Delete Strategy
 */
@MappedSuperclass
@Getter
@Setter
public abstract class SoftDeletableEntity extends BaseEntity {

    @Column(name = "deleted_at")
    private Instant deletedAt;

    /**
     * Soft delete this entity by setting deleted_at timestamp
     */
    public void softDelete() {
        this.deletedAt = Instant.now();
    }

    /**
     * Check if this entity is soft deleted
     *
     * @return true if deleted_at is not null
     */
    public boolean isDeleted() {
        return deletedAt != null;
    }

    /**
     * Restore soft deleted entity by clearing deleted_at timestamp
     */
    public void restore() {
        this.deletedAt = null;
    }
}
