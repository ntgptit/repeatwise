package com.repeatwise.dto.request.srs;

import com.repeatwise.entity.enums.ForgottenCardAction;
import com.repeatwise.entity.enums.ReviewOrder;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating SRS settings
 *
 * Requirements:
 * - UC-028: Configure SRS Settings
 *
 * Request Body:
 * {
 * "totalBoxes": 7,
 * "reviewOrder": "RANDOM",
 * "newCardsPerDay": 20,
 * "maxReviewsPerDay": 200,
 * "forgottenCardAction": "MOVE_TO_BOX_1",
 * "moveDownBoxes": 1,
 * "notificationEnabled": true,
 * "notificationTime": "09:00"
 * }
 *
 * @author RepeatWise Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateSrsSettingsRequest {

    @Min(value = 3, message = "{error.srs.totalboxes.min}")
    @Max(value = 10, message = "{error.srs.totalboxes.max}")
    private Integer totalBoxes;

    private ReviewOrder reviewOrder;

    @Min(value = 1, message = "{error.srs.newcards.min}")
    @Max(value = 100, message = "{error.srs.newcards.max}")
    private Integer newCardsPerDay;

    @Min(value = 10, message = "{error.srs.maxreviews.min}")
    @Max(value = 500, message = "{error.srs.maxreviews.max}")
    private Integer maxReviewsPerDay;

    private ForgottenCardAction forgottenCardAction;

    @Min(value = 1, message = "{error.srs.movedown.min}")
    @Max(value = 3, message = "{error.srs.movedown.max}")
    private Integer moveDownBoxes;

    private Boolean notificationEnabled;

    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$", message = "{error.srs.notification.time.format}")
    private String notificationTime;
}
