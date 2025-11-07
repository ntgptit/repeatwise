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

    @Min(value = 1, message = "Move down boxes must be at least 1")
    @Max(value = 3, message = "Move down boxes must not exceed 3")
    private Integer moveDownBoxes;

    @Min(value = 1, message = "New cards per day must be at least 1")
    @Max(value = 100, message = "New cards per day must not exceed 100")
    private Integer newCardsPerDay;

    @Min(value = 1, message = "Max reviews per day must be at least 1")
    @Max(value = 500, message = "Max reviews per day must not exceed 500")
    private Integer maxReviewsPerDay;
}
