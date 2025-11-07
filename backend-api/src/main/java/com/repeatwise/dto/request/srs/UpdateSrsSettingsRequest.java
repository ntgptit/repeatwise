package com.repeatwise.dto.request.srs;

import com.repeatwise.entity.enums.ForgottenCardAction;
import com.repeatwise.entity.enums.ReviewOrder;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * DTO for updating SRS settings
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSrsSettingsRequest {

    private ReviewOrder reviewOrder;

    private Boolean notificationEnabled;

    private LocalTime notificationTime;

    private ForgottenCardAction forgottenCardAction;

    @Min(value = 1, message = "{error.srs.movedown.min}")
    @Max(value = 3, message = "{error.srs.movedown.max}")
    private Integer moveDownBoxes;

    @Min(value = 1, message = "{error.srs.newcards.min}")
    @Max(value = 100, message = "{error.srs.newcards.max}")
    private Integer newCardsPerDay;

    @Min(value = 1, message = "{error.srs.maxreviews.min}")
    @Max(value = 500, message = "{error.srs.maxreviews.max}")
    private Integer maxReviewsPerDay;
}
