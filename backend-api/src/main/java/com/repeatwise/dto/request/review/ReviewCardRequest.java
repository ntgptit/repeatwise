package com.repeatwise.dto.request.review;

import com.repeatwise.entity.enums.Rating;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for reviewing a card (SRS rating)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCardRequest {

    @NotNull(message = "Card ID is required")
    private UUID cardId;

    @NotNull(message = "Rating is required")
    private Rating rating;
}
