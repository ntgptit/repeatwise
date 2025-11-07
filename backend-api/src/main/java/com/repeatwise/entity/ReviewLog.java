package com.repeatwise.entity;

import com.repeatwise.entity.enums.Rating;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

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

    @NotNull(message = "Card is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "Rating is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "rating", nullable = false, length = 10)
    private Rating rating;

    @NotNull(message = "Previous box is required")
    @Min(value = 1, message = "Previous box must be at least 1")
    @Column(name = "previous_box", nullable = false)
    private Integer previousBox;

    @NotNull(message = "New box is required")
    @Min(value = 1, message = "New box must be at least 1")
    @Column(name = "new_box", nullable = false)
    private Integer newBox;

    @NotNull(message = "Interval days is required")
    @Min(value = 1, message = "Interval days must be at least 1")
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
        return newBox > previousBox;
    }

    /**
     * Check if the card was moved backward (box decreased)
     */
    public boolean isRegression() {
        return newBox < previousBox;
    }

    /**
     * Check if the card stayed in the same box
     */
    public boolean isStagnant() {
        return newBox.equals(previousBox);
    }

    /**
     * Check if this was a forgotten card (AGAIN rating)
     */
    public boolean wasForgotten() {
        return rating == Rating.AGAIN;
    }
}
