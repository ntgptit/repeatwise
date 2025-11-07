package com.repeatwise.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Folder statistics entity (denormalized cache with TTL = 5 minutes)
 */
@Entity
@Table(name = "folder_stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FolderStats {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotNull(message = "Folder is required")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id", nullable = false)
    private Folder folder;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder.Default
    @Min(value = 0, message = "Total cards count must be at least 0")
    @Column(name = "total_cards_count", nullable = false)
    private Integer totalCardsCount = 0;

    @Builder.Default
    @Min(value = 0, message = "Due cards count must be at least 0")
    @Column(name = "due_cards_count", nullable = false)
    private Integer dueCardsCount = 0;

    @Builder.Default
    @Min(value = 0, message = "New cards count must be at least 0")
    @Column(name = "new_cards_count", nullable = false)
    private Integer newCardsCount = 0;

    @Builder.Default
    @Min(value = 0, message = "Mature cards count must be at least 0")
    @Column(name = "mature_cards_count", nullable = false)
    private Integer matureCardsCount = 0;

    @CreationTimestamp
    @Column(name = "last_computed_at", nullable = false)
    private LocalDateTime lastComputedAt;

    /**
     * Check if the statistics are stale (older than TTL)
     * TTL = 5 minutes
     */
    public boolean isStale() {
        return lastComputedAt == null ||
               LocalDateTime.now().isAfter(lastComputedAt.plusMinutes(5));
    }

    /**
     * Refresh the computation timestamp
     */
    public void refreshTimestamp() {
        this.lastComputedAt = LocalDateTime.now();
    }
}
