package com.repeatwise.dto.response.srs;

import java.time.LocalTime;
import java.util.UUID;

import com.repeatwise.entity.enums.ForgottenCardAction;
import com.repeatwise.entity.enums.ReviewOrder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for SRS settings response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SrsSettingsResponse {

    private UUID id;
    private Integer totalBoxes;
    private ReviewOrder reviewOrder;
    private Boolean notificationEnabled;
    private LocalTime notificationTime;
    private ForgottenCardAction forgottenCardAction;
    private Integer moveDownBoxes;
    private Integer newCardsPerDay;
    private Integer maxReviewsPerDay;
}
