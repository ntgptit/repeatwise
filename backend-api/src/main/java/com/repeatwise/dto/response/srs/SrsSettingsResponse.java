package com.repeatwise.dto.response.srs;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.repeatwise.entity.enums.ForgottenCardAction;
import com.repeatwise.entity.enums.ReviewOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.time.Instant;

/**
 * Response DTO for SRS settings
 *
 * Requirements:
 * - UC-028: Configure SRS Settings
 *
 * Response Format:
 * {
 *   "totalBoxes": 7,
 *   "reviewOrder": "RANDOM",
 *   "newCardsPerDay": 20,
 *   "maxReviewsPerDay": 200,
 *   "forgottenCardAction": "MOVE_TO_BOX_1",
 *   "moveDownBoxes": 1,
 *   "notificationEnabled": true,
 *   "notificationTime": "09:00",
 *   "updatedAt": "2025-01-28T14:45:00Z"
 * }
 *
 * @author RepeatWise Team
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SrsSettingsResponse {

    private Integer totalBoxes;

    private ReviewOrder reviewOrder;

    private Integer newCardsPerDay;

    private Integer maxReviewsPerDay;

    private ForgottenCardAction forgottenCardAction;

    private Integer moveDownBoxes;

    private Boolean notificationEnabled;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime notificationTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant updatedAt;
}

