package com.repeatwise.entity;

import com.repeatwise.entity.base.BaseEntity;
import com.repeatwise.entity.enums.ForgottenCardAction;
import com.repeatwise.entity.enums.ReviewOrder;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalTime;

/**
 * SRS Settings entity - User's spaced repetition system configuration
 *
 * Requirements:
 * - UC-022: Configure SRS Settings
 * - Database Schema: srs_settings table
 *
 * Business Rules:
 * - One settings per user (1:1 relationship)
 * - total_boxes = 7 (fixed for MVP)
 * - Created automatically on user registration with defaults
 *
 * @author RepeatWise Team
 */
@Entity
@Table(name = "srs_settings", indexes = {
    @Index(name = "idx_srs_settings_user", columnList = "user_id")
})
@EntityListeners(org.springframework.data.jpa.domain.support.AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SrsSettings extends BaseEntity {

    @NotNull(message = "{error.user.required}")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @NotNull(message = "{error.srs.totalboxes.required}")
    @Column(name = "total_boxes", nullable = false)
    @Builder.Default
    private Integer totalBoxes = 7; // Fixed for MVP

    @NotNull(message = "{error.srs.revieworder.required}")
    @Enumerated(EnumType.STRING)
    @Column(name = "review_order", nullable = false, length = 20)
    @Builder.Default
    private ReviewOrder reviewOrder = ReviewOrder.RANDOM;

    @NotNull(message = "{error.srs.notification.enabled.required}")
    @Column(name = "notification_enabled", nullable = false)
    @Builder.Default
    private Boolean notificationEnabled = true;

    @NotNull(message = "{error.srs.notification.time.required}")
    @Column(name = "notification_time", nullable = false)
    @Builder.Default
    private LocalTime notificationTime = LocalTime.of(9, 0);

    @NotNull(message = "{error.srs.forgotten.action.required}")
    @Enumerated(EnumType.STRING)
    @Column(name = "forgotten_card_action", nullable = false, length = 30)
    @Builder.Default
    private ForgottenCardAction forgottenCardAction = ForgottenCardAction.MOVE_TO_BOX_1;

    @NotNull(message = "{error.srs.movedown.required}")
    @Min(value = 1, message = "{error.srs.movedown.min}")
    @Max(value = 3, message = "{error.srs.movedown.max}")
    @Column(name = "move_down_boxes", nullable = false)
    @Builder.Default
    private Integer moveDownBoxes = 1;

    @NotNull(message = "{error.srs.newcards.required}")
    @Min(value = 1, message = "{error.srs.newcards.min}")
    @Max(value = 100, message = "{error.srs.newcards.max}")
    @Column(name = "new_cards_per_day", nullable = false)
    @Builder.Default
    private Integer newCardsPerDay = 20;

    @NotNull(message = "{error.srs.maxreviews.required}")
    @Min(value = 1, message = "{error.srs.maxreviews.min}")
    @Max(value = 500, message = "{error.srs.maxreviews.max}")
    @Column(name = "max_reviews_per_day", nullable = false)
    @Builder.Default
    private Integer maxReviewsPerDay = 200;

    /**
     * Business method: Create default settings for a user
     */
    public static SrsSettings createDefault(final User user) {
        return SrsSettings.builder()
            .user(user)
            .totalBoxes(7)
            .reviewOrder(ReviewOrder.RANDOM)
            .notificationEnabled(true)
            .notificationTime(LocalTime.of(9, 0))
            .forgottenCardAction(ForgottenCardAction.MOVE_TO_BOX_1)
            .moveDownBoxes(1)
            .newCardsPerDay(20)
            .maxReviewsPerDay(200)
            .build();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SrsSettings)) {
            return false;
        }
        final SrsSettings that = (SrsSettings) o;
        return getId() != null && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "SrsSettings{" +
                "id=" + getId() +
                ", userId=" + (user != null ? user.getId() : null) +
                ", totalBoxes=" + totalBoxes +
                ", reviewOrder=" + reviewOrder +
                '}';
    }
}
