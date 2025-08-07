package com.repeatwise.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
@Data
@SQLDelete(sql = "UPDATE #{entityName} SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        onPrePersist();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        onPreUpdate();
    }

    /**
     * Hook method for subclasses to add custom logic before persist
     */
    protected void onPrePersist() {
        // Override in subclasses if needed
    }

    /**
     * Hook method for subclasses to add custom logic before update
     */
    protected void onPreUpdate() {
        // Override in subclasses if needed
    }
} 