package com.repeatwise.entity;

import com.repeatwise.entity.base.SoftDeletableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Card Box Position entity - SRS state per user per card
 *
 * Requirements:
 * - UC-023: Review Cards with SRS
 * - UC-024: Rate Card
 * - Database Schema: card_box_position table (section 2.7)
 *
 * Business Rules:
 * - BR-041: One SRS state per user-card pair (unique constraint)
 * - BR-042: Current box range: 1-7
 * - BR-043: Interval days >= 1
 * - BR-044: New cards start at box 1, review_count = 0
 * - BR-045: Due date calculated from interval_days
 *
 * Performance:
 * - Critical index: (user_id, due_date, current_box) for review queries
 * - Most critical table for review session performance
 *
 * @author RepeatWise Team
 */
@Entity
@Table(name = "card_box_position", indexes = {
        @Index(name = "idx_card_box_position_user_card", columnList = "user_id, card_id"),
        @Index(name = "idx_card_box_user_due", columnList = "user_id, due_date, current_box"),
        @Index(name = "idx_card_box_user_box", columnList = "user_id, current_box"),
        @Index(name = "idx_card_box_new", columnList = "user_id, card_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_card_box_user_card", columnNames = { "user_id", "card_id" })
})
@EntityListeners(org.springframework.data.jpa.domain.support.AuditingEntityListener.class)
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CardBoxPosition extends SoftDeletableEntity {

    // ==================== Relationships ====================

    @NotNull(message = "{error.card.required}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @NotNull(message = "{error.user.required}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ==================== SRS State Fields ====================

    /**
     * Current box (1-7)
     * BR-042: Range 1-7
     */
    @NotNull(message = "{error.cardbox.currentbox.required}")
    @Min(value = 1, message = "{error.cardbox.currentbox.min}")
    @Max(value = 7, message = "{error.cardbox.currentbox.max}")
    @Column(name = "current_box", nullable = false)
    @Builder.Default
    private Integer currentBox = 1;

    /**
     * Days until next review
     * BR-043: >= 1
     */
    @NotNull(message = "{error.cardbox.interval.required}")
    @Min(value = 1, message = "{error.cardbox.interval.min}")
    @Column(name = "interval_days", nullable = false)
    @Builder.Default
    private Integer intervalDays = 1;

    /**
     * Next review due date
     * Calculated from interval_days
     */
    @NotNull(message = "{error.cardbox.duedate.required}")
    @Column(name = "due_date", nullable = false)
    @Builder.Default
    private LocalDate dueDate = LocalDate.now();

    /**
     * Total number of reviews
     * BR-044: Starts at 0 for new cards
     */
    @NotNull(message = "{error.cardbox.reviewcount.required}")
    @Min(value = 0, message = "{error.cardbox.reviewcount.non.negative}")
    @Column(name = "review_count", nullable = false)
    @Builder.Default
    private Integer reviewCount = 0;

    /**
     * Number of times forgotten (AGAIN rating)
     */
    @NotNull(message = "{error.cardbox.lapsecount.required}")
    @Min(value = 0, message = "{error.cardbox.lapsecount.non.negative}")
    @Column(name = "lapse_count", nullable = false)
    @Builder.Default
    private Integer lapseCount = 0;

    /**
     * Last review timestamp
     * NULL for new cards
     */
    @Column(name = "last_reviewed_at")
    private Instant lastReviewedAt;

    // ==================== Business Methods ====================

    /**
     * Check if this is a new card (never reviewed)
     */
    public boolean isNew() {
        return reviewCount == 0;
    }

    /**
     * Check if card is due for review
     */
    public boolean isDue() {
        final LocalDate today = LocalDate.now();
        return dueDate.isBefore(today) || dueDate.isEqual(today);
    }

    /**
     * Check if card is mature (box >= 5)
     */
    public boolean isMature() {
        return currentBox >= 5;
    }

    /**
     * Update due date based on interval days
     */
    public void updateDueDate() {
        this.dueDate = LocalDate.now().plusDays(intervalDays);
    }

    /**
     * Move to a new box
     */
    public void moveToBox(final Integer newBox) {
        this.currentBox = newBox;
        updateDueDate();
    }

    /**
     * Record a review
     */
    public void recordReview() {
        this.reviewCount++;
        this.lastReviewedAt = Instant.now();
    }

    /**
     * Record a lapse (forgot card)
     */
    public void recordLapse() {
        this.lapseCount++;
    }

    /**
     * Create default position for a new card
     */
    public static CardBoxPosition createDefault(final Card card, final User user) {
        return CardBoxPosition.builder()
                .card(card)
                .user(user)
                .currentBox(1)
                .intervalDays(1)
                .dueDate(LocalDate.now())
                .reviewCount(0)
                .lapseCount(0)
                .lastReviewedAt(null)
                .build();
    }

    // ==================== Lifecycle Methods ====================

    @PrePersist
    @PreUpdate
    public void validateState() {
        if (currentBox < 1 || currentBox > 7) {
            throw new IllegalArgumentException("Current box must be between 1 and 7");
        }
        if (intervalDays < 1) {
            throw new IllegalArgumentException("Interval days must be >= 1");
        }
        if (dueDate == null) {
            updateDueDate();
        }
    }

    // ==================== Equals & HashCode ====================

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final CardBoxPosition that)) {
            return false;
        }
        return (getId() != null) && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "CardBoxPosition{" +
                "id=" + getId() +
                ", cardId=" + (card != null ? card.getId() : null) +
                ", userId=" + (user != null ? user.getId() : null) +
                ", currentBox=" + currentBox +
                ", intervalDays=" + intervalDays +
                ", dueDate=" + dueDate +
                ", reviewCount=" + reviewCount +
                '}';
    }
}

