package com.repeatwise.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.repeatwise.entity.base.SoftDeletableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Card Box Position entity - tracks SRS state per user per card
 * CRITICAL TABLE FOR REVIEW PERFORMANCE
 */
@Entity
@Table(name = "card_box_position")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardBoxPosition extends SoftDeletableEntity {

    @NotNull(message = "{error.card.required}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @NotNull(message = "{error.user.required}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder.Default
    @NotNull(message = "{error.cardbox.current.required}")
    @Min(value = 1, message = "{error.cardbox.current.min}")
    @Column(name = "current_box", nullable = false)
    private Integer currentBox = 1;

    @Builder.Default
    @NotNull(message = "{error.cardbox.interval.required}")
    @Min(value = 1, message = "{error.cardbox.interval.min}")
    @Column(name = "interval_days", nullable = false)
    private Integer intervalDays = 1;

    @NotNull(message = "{error.cardbox.due.required}")
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Builder.Default
    @NotNull(message = "{error.cardbox.reviewcount.required}")
    @Min(value = 0, message = "{error.cardbox.reviewcount.min}")
    @Column(name = "review_count", nullable = false)
    private Integer reviewCount = 0;

    @Builder.Default
    @NotNull(message = "{error.cardbox.lapsecount.required}")
    @Min(value = 0, message = "{error.cardbox.lapsecount.min}")
    @Column(name = "lapse_count", nullable = false)
    private Integer lapseCount = 0;

    @Column(name = "last_reviewed_at")
    private LocalDateTime lastReviewedAt;

    /**
     * Check if this card is new (never reviewed)
     */
    public boolean isNew() {
        return this.reviewCount == 0;
    }

    /**
     * Check if this card is due for review
     */
    public boolean isDue() {
        return (this.dueDate != null) && !this.dueDate.isAfter(LocalDate.now());
    }

    /**
     * Check if this card is mature (in box 5 or higher)
     */
    public boolean isMature() {
        return this.currentBox >= 5;
    }

    /**
     * Initialize a new card box position for a user
     */
    public static CardBoxPosition createNew(Card card, User user) {
        return CardBoxPosition.builder()
                .card(card)
                .user(user)
                .currentBox(1)
                .intervalDays(1)
                .dueDate(LocalDate.now())
                .reviewCount(0)
                .lapseCount(0)
                .build();
    }

    /**
     * Update position after review
     */
    public void updateAfterReview(Integer newBox, Integer newInterval) {
        this.currentBox = newBox;
        this.intervalDays = newInterval;
        this.dueDate = LocalDate.now().plusDays(newInterval);
        this.reviewCount++;
        this.lastReviewedAt = LocalDateTime.now();
    }

    /**
     * Increment lapse count when card is forgotten
     */
    public void incrementLapse() {
        this.lapseCount++;
    }
}
