package com.repeatwise.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

/**
 * Base entity with soft delete support
 * Entities that need soft delete extend this class
 *
 * Requirements:
 * - Database Schema: deleted_at field
 * - Soft delete pattern
 *
 * @author RepeatWise Team
 */
@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class SoftDeletableEntity extends BaseEntity {

    @Column(name = "deleted_at")
    private Instant deletedAt;

    /**
     * Soft delete this entity
     */
    public void softDelete() {
        this.deletedAt = Instant.now();
    }

    /**
     * Check if entity is deleted
     */
    public boolean isDeleted() {
        return deletedAt != null;
    }

    /**
     * Restore soft-deleted entity
     */
    public void restore() {
        this.deletedAt = null;
    }
}
