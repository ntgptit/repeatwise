package com.repeatwise.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.repeatwise.entity.enums.Rating;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
 * Review Log entity - immutable review history for analytics and undo
 */
@Entity
@Table(name = "review_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotNull(message = "{error.card.required}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @NotNull(message = "{error.user.required}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "{error.reviewlog.rating.required}")
    @Enumerated(EnumType.STRING)
    @Column(name = "rating", nullable = false, length = 10)
    private Rating rating;

    @NotNull(message = "{error.reviewlog.previousbox.required}")
    @Min(value = 1, message = "{error.reviewlog.previousbox.min}")
    @Column(name = "previous_box", nullable = false)
    private Integer previousBox;

    @NotNull(message = "{error.reviewlog.newbox.required}")
    @Min(value = 1, message = "{error.reviewlog.newbox.min}")
    @Column(name = "new_box", nullable = false)
    private Integer newBox;

    @NotNull(message = "{error.reviewlog.interval.required}")
    @Min(value = 1, message = "{error.reviewlog.interval.min}")
    @Column(name = "interval_days", nullable = false)
    private Integer intervalDays;

    @CreationTimestamp
    @Column(name = "reviewed_at", nullable = false)
    private LocalDateTime reviewedAt;

    /**
     * Create a new review log entry
     */
    public static ReviewLog create(Card card, User user, Rating rating,
            Integer previousBox, Integer newBox, Integer intervalDays) {
        return ReviewLog.builder()
                .card(card)
                .user(user)
                .rating(rating)
                .previousBox(previousBox)
                .newBox(newBox)
                .intervalDays(intervalDays)
                .build();
    }

    /**
     * Check if the card was moved forward (box increased)
     */
    public boolean isProgressMade() {
        return this.newBox > this.previousBox;
    }

    /**
     * Check if the card was moved backward (box decreased)
     */
    public boolean isRegression() {
        return this.newBox < this.previousBox;
    }

    /**
     * Check if the card stayed in the same box
     */
    public boolean isStagnant() {
        return this.newBox.equals(this.previousBox);
    }

    /**
     * Check if this was a forgotten card (AGAIN rating)
     */
    public boolean wasForgotten() {
        return this.rating == Rating.AGAIN;
    }
}
