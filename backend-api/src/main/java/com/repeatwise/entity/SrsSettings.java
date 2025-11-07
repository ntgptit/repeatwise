package com.repeatwise.entity;

import com.repeatwise.entity.base.BaseEntity;
import com.repeatwise.entity.enums.ForgottenCardAction;
import com.repeatwise.entity.enums.ReviewOrder;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalTime;

/**
 * SRS Settings entity for user spaced repetition configuration
 */
@Entity
@Table(name = "srs_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SrsSettings extends BaseEntity {

    @NotNull(message = "User is required")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Builder.Default
    @NotNull(message = "Total boxes is required")
    @Min(value = 7, message = "Total boxes must be 7 (MVP constraint)")
    @Max(value = 7, message = "Total boxes must be 7 (MVP constraint)")
    @Column(name = "total_boxes", nullable = false)
    private Integer totalBoxes = 7;

    @Builder.Default
    @NotNull(message = "Review order is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "review_order", nullable = false, length = 20)
    private ReviewOrder reviewOrder = ReviewOrder.RANDOM;

    @Builder.Default
    @NotNull(message = "Notification enabled flag is required")
    @Column(name = "notification_enabled", nullable = false)
    private Boolean notificationEnabled = true;

    @Builder.Default
    @NotNull(message = "Notification time is required")
    @Column(name = "notification_time", nullable = false)
    private LocalTime notificationTime = LocalTime.of(9, 0);

    @Builder.Default
    @NotNull(message = "Forgotten card action is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "forgotten_card_action", nullable = false, length = 30)
    private ForgottenCardAction forgottenCardAction = ForgottenCardAction.MOVE_TO_BOX_1;

    @Builder.Default
    @NotNull(message = "Move down boxes is required")
    @Min(value = 1, message = "Move down boxes must be at least 1")
    @Max(value = 3, message = "Move down boxes must not exceed 3")
    @Column(name = "move_down_boxes", nullable = false)
    private Integer moveDownBoxes = 1;

    @Builder.Default
    @NotNull(message = "New cards per day is required")
    @Min(value = 1, message = "New cards per day must be at least 1")
    @Max(value = 100, message = "New cards per day must not exceed 100")
    @Column(name = "new_cards_per_day", nullable = false)
    private Integer newCardsPerDay = 20;

    @Builder.Default
    @NotNull(message = "Max reviews per day is required")
    @Min(value = 1, message = "Max reviews per day must be at least 1")
    @Max(value = 500, message = "Max reviews per day must not exceed 500")
    @Column(name = "max_reviews_per_day", nullable = false)
    private Integer maxReviewsPerDay = 200;

    /**
     * Create default SRS settings for a user
     */
    public static SrsSettings createDefault(User user) {
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
}
