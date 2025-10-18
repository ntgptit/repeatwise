package com.repeatwise.entity;

import com.repeatwise.entity.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

/**
 * FolderStats entity - Cached folder statistics (Denormalized)
 *
 * Requirements:
 * - UC-010: View Folder Statistics
 * - Schema: folder_stats table (section 4.2)
 * - NFR: Performance optimization via denormalized cache (5-min TTL)
 *
 * Business Rules:
 * - BR-020: Stats recalculation can be async (5-min TTL acceptable)
 * - TTL: 5 minutes (recompute if last_computed_at > 5 min ago)
 * - Invalidation: Delete on card CRUD, review submission, folder move
 * - Calculation: Recursive (folder + all descendants)
 *
 * Performance:
 * - Avoids expensive recursive queries
 * - Updated via domain events (async)
 * - Partial index on (folder_id, user_id, last_computed_at DESC)
 *
 * @author RepeatWise Team
 */
@Entity
@Table(name = "folder_stats", indexes = {
    @Index(name = "idx_folder_stats_folder", columnList = "folder_id, user_id"),
    @Index(name = "idx_folder_stats_lookup", columnList = "folder_id, user_id, last_computed_at")
})
@EntityListeners(org.springframework.data.jpa.domain.support.AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class FolderStats extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id", nullable = false)
    private Folder folder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Total cards in folder and all descendants
     */
    @Min(value = 0, message = "{folder.stats.total.cards.min}")
    @Column(name = "total_cards_count", nullable = false)
    @Builder.Default
    private Integer totalCardsCount = 0;

    /**
     * Due cards (due_date <= today) in folder and all descendants
     */
    @Min(value = 0, message = "{folder.stats.due.cards.min}")
    @Column(name = "due_cards_count", nullable = false)
    @Builder.Default
    private Integer dueCardsCount = 0;

    /**
     * New cards (review_count = 0) in folder and all descendants
     */
    @Min(value = 0, message = "{folder.stats.new.cards.min}")
    @Column(name = "new_cards_count", nullable = false)
    @Builder.Default
    private Integer newCardsCount = 0;

    /**
     * Mature cards (current_box >= 5) in folder and all descendants
     */
    @Min(value = 0, message = "{folder.stats.mature.cards.min}")
    @Column(name = "mature_cards_count", nullable = false)
    @Builder.Default
    private Integer matureCardsCount = 0;

    /**
     * Timestamp when stats were last computed
     * Used for TTL check (5 minutes)
     */
    @Column(name = "last_computed_at", nullable = false)
    @Builder.Default
    private Instant lastComputedAt = Instant.now();

    // ==================== Business Methods ====================

    /**
     * Check if stats are stale (older than 5 minutes)
     * If true, should recompute
     */
    public boolean isStale() {
        final Instant fiveMinutesAgo = Instant.now().minusSeconds(300);
        return lastComputedAt.isBefore(fiveMinutesAgo);
    }

    /**
     * Mark stats as recomputed (update timestamp)
     */
    public void markAsRecomputed() {
        this.lastComputedAt = Instant.now();
    }

    /**
     * Update stats with new values
     */
    public void updateStats(final Integer totalCards, final Integer dueCards,
                           final Integer newCards, final Integer matureCards) {
        this.totalCardsCount = totalCards;
        this.dueCardsCount = dueCards;
        this.newCardsCount = newCards;
        this.matureCardsCount = matureCards;
        this.lastComputedAt = Instant.now();
    }

    /**
     * Increment card counts (when adding cards)
     */
    public void incrementCounts(final Integer totalDelta, final Integer dueDelta,
                               final Integer newDelta, final Integer matureDelta) {
        this.totalCardsCount += totalDelta;
        this.dueCardsCount += dueDelta;
        this.newCardsCount += newDelta;
        this.matureCardsCount += matureDelta;
        this.lastComputedAt = Instant.now();
    }

    /**
     * Decrement card counts (when removing cards)
     */
    public void decrementCounts(final Integer totalDelta, final Integer dueDelta,
                               final Integer newDelta, final Integer matureDelta) {
        this.totalCardsCount = Math.max(0, this.totalCardsCount - totalDelta);
        this.dueCardsCount = Math.max(0, this.dueCardsCount - dueDelta);
        this.newCardsCount = Math.max(0, this.newCardsCount - newDelta);
        this.matureCardsCount = Math.max(0, this.matureCardsCount - matureDelta);
        this.lastComputedAt = Instant.now();
    }

    /**
     * Reset all counts to zero
     */
    public void reset() {
        this.totalCardsCount = 0;
        this.dueCardsCount = 0;
        this.newCardsCount = 0;
        this.matureCardsCount = 0;
        this.lastComputedAt = Instant.now();
    }

    // ==================== Equals & HashCode ====================

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FolderStats)) {
            return false;
        }
        final FolderStats that = (FolderStats) o;
        return getId() != null && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "FolderStats{" +
                "id=" + getId() +
                ", folderId=" + (folder != null ? folder.getId() : null) +
                ", totalCards=" + totalCardsCount +
                ", dueCards=" + dueCardsCount +
                ", newCards=" + newCardsCount +
                ", matureCards=" + matureCardsCount +
                ", lastComputedAt=" + lastComputedAt +
                '}';
    }
}
