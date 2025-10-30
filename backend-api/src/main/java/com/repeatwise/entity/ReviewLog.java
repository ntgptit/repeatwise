package com.repeatwise.entity;

import com.repeatwise.entity.base.BaseEntity;
import com.repeatwise.entity.enums.CardRating;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

/**
 * Review Log entity - Immutable review history
 *
 * Requirements:
 * - UC-024: Rate Card
 * - UC-025: Undo Review
 * - Database Schema: review_logs table (section 2.8)
 *
 * Business Rules:
 * - BR-051: Immutable table (no updates/deletes in normal flow)
 * - BR-052: Used for undo functionality (windowed)
 * - BR-053: Used for statistics and analytics
 * - BR-054: Rating must be one of: AGAIN, HARD, GOOD, EASY
 *
 * Design Notes:
 * - Immutable table for audit trail
 * - Created after each review
 * - Used for undo operation (latest review can be undone)
 *
 * @author RepeatWise Team
 */
@Entity
@Table(name = "review_logs", indexes = {
        @Index(name = "idx_review_logs_user_date", columnList = "user_id, reviewed_at DESC"),
        @Index(name = "idx_review_logs_card", columnList = "card_id"),
        @Index(name = "idx_review_logs_user_today", columnList = "user_id, reviewed_at")
})
@EntityListeners(org.springframework.data.jpa.domain.support.AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ReviewLog extends BaseEntity {

    // ==================== Relationships ====================

    @NotNull(message = "{error.card.required}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @NotNull(message = "{error.user.required}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ==================== Review Fields ====================

    /**
     * User rating for this review
     * BR-054: AGAIN, HARD, GOOD, EASY
     */
    @NotNull(message = "{error.reviewlog.rating.required}")
    @Enumerated(EnumType.STRING)
    @Column(name = "rating", nullable = false, length = 10)
    private CardRating rating;

    /**
     * Box number before review
     */
    @NotNull(message = "{error.reviewlog.previousbox.required}")
    @Min(value = 1, message = "{error.reviewlog.previousbox.min}")
    @Max(value = 7, message = "{error.reviewlog.previousbox.max}")
    @Column(name = "previous_box", nullable = false)
    private Integer previousBox;

    /**
     * Box number after review
     */
    @NotNull(message = "{error.reviewlog.newbox.required}")
    @Min(value = 1, message = "{error.reviewlog.newbox.min}")
    @Max(value = 7, message = "{error.reviewlog.newbox.max}")
    @Column(name = "new_box", nullable = false)
    private Integer newBox;

    /**
     * Interval days assigned after review
     */
    @NotNull(message = "{error.reviewlog.interval.required}")
    @Min(value = 1, message = "{error.reviewlog.interval.min}")
    @Column(name = "interval_days", nullable = false)
    private Integer intervalDays;

    /**
     * Review timestamp
     */
    @NotNull(message = "{error.reviewlog.reviewedat.required}")
    @Column(name = "reviewed_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant reviewedAt = Instant.now();

    // ==================== Business Methods ====================

    /**
     * Check if this review moved card forward (progress)
     */
    public boolean isProgress() {
        return newBox > previousBox;
    }

    /**
     * Check if this review moved card backward (regression)
     */
    public boolean isRegression() {
        return newBox < previousBox;
    }

    /**
     * Check if this review kept card in same box
     */
    public boolean isSameBox() {
        return newBox.equals(previousBox);
    }

    /**
     * Check if this is an AGAIN rating (forgot card)
     */
    public boolean isAgainRating() {
        return rating == CardRating.AGAIN;
    }

    /**
     * Create review log from card box position change
     */
    public static ReviewLog create(
            final Card card,
            final User user,
            final CardRating rating,
            final Integer previousBox,
            final Integer newBox,
            final Integer intervalDays) {
        return ReviewLog.builder()
                .card(card)
                .user(user)
                .rating(rating)
                .previousBox(previousBox)
                .newBox(newBox)
                .intervalDays(intervalDays)
                .reviewedAt(Instant.now())
                .build();
    }

    // ==================== Equals & HashCode ====================

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final ReviewLog that)) {
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
        return "ReviewLog{" +
                "id=" + getId() +
                ", cardId=" + (card != null ? card.getId() : null) +
                ", userId=" + (user != null ? user.getId() : null) +
                ", rating=" + rating +
                ", previousBox=" + previousBox +
                ", newBox=" + newBox +
                ", reviewedAt=" + reviewedAt +
                '}';
    }
}

