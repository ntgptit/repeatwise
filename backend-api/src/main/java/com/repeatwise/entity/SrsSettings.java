package com.repeatwise.entity;

import java.time.LocalTime;

import com.repeatwise.entity.base.BaseEntity;
import com.repeatwise.entity.enums.ForgottenCardAction;
import com.repeatwise.entity.enums.ReviewOrder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @NotNull(message = "{error.user.required}")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Builder.Default
    @NotNull(message = "{error.srs.totalboxes.required}")
    @Min(value = 7, message = "{error.srs.totalboxes.exact}")
    @Max(value = 7, message = "{error.srs.totalboxes.exact}")
    @Column(name = "total_boxes", nullable = false)
    private Integer totalBoxes = 7;

    @Builder.Default
    @NotNull(message = "{error.srs.revieworder.required}")
    @Enumerated(EnumType.STRING)
    @Column(name = "review_order", nullable = false, length = 20)
    private ReviewOrder reviewOrder = ReviewOrder.RANDOM;

    @Builder.Default
    @NotNull(message = "{error.srs.notification.enabled.required}")
    @Column(name = "notification_enabled", nullable = false)
    private Boolean notificationEnabled = true;

    @Builder.Default
    @NotNull(message = "{error.srs.notification.time.required}")
    @Column(name = "notification_time", nullable = false)
    private LocalTime notificationTime = LocalTime.of(9, 0);

    @Builder.Default
    @NotNull(message = "{error.srs.forgotten.action.required}")
    @Enumerated(EnumType.STRING)
    @Column(name = "forgotten_card_action", nullable = false, length = 30)
    private ForgottenCardAction forgottenCardAction = ForgottenCardAction.MOVE_TO_BOX_1;

    @Builder.Default
    @NotNull(message = "{error.srs.movedown.required}")
    @Min(value = 1, message = "{error.srs.movedown.min}")
    @Max(value = 3, message = "{error.srs.movedown.max}")
    @Column(name = "move_down_boxes", nullable = false)
    private Integer moveDownBoxes = 1;

    @Builder.Default
    @NotNull(message = "{error.srs.newcards.required}")
    @Min(value = 1, message = "{error.srs.newcards.min}")
    @Max(value = 100, message = "{error.srs.newcards.max}")
    @Column(name = "new_cards_per_day", nullable = false)
    private Integer newCardsPerDay = 20;

    @Builder.Default
    @NotNull(message = "{error.srs.maxreviews.required}")
    @Min(value = 1, message = "{error.srs.maxreviews.min}")
    @Max(value = 500, message = "{error.srs.maxreviews.max}")
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
