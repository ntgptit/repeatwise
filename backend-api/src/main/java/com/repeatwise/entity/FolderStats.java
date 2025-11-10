package com.repeatwise.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @NotNull(message = "{error.folder.required}")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id", nullable = false)
    private Folder folder;

    @NotNull(message = "{error.user.required}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder.Default
    @Min(value = 0, message = "{error.folder.stats.totalcards.non.negative}")
    @Column(name = "total_cards_count", nullable = false)
    private Integer totalCardsCount = 0;

    @Builder.Default
    @Min(value = 0, message = "{error.folder.stats.duecards.non.negative}")
    @Column(name = "due_cards_count", nullable = false)
    private Integer dueCardsCount = 0;

    @Builder.Default
    @Min(value = 0, message = "{error.folder.stats.newcards.non.negative}")
    @Column(name = "new_cards_count", nullable = false)
    private Integer newCardsCount = 0;

    @Builder.Default
    @Min(value = 0, message = "{error.folder.stats.maturecards.non.negative}")
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
        return (this.lastComputedAt == null) ||
                LocalDateTime.now().isAfter(this.lastComputedAt.plusMinutes(5));
    }

    /**
     * Refresh the computation timestamp
     */
    public void refreshTimestamp() {
        this.lastComputedAt = LocalDateTime.now();
    }
}
