package com.repeatwise.dto.request.review;

import java.util.UUID;

import com.repeatwise.entity.enums.Rating;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for reviewing a card (SRS rating)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCardRequest {

    @NotNull(message = "{error.card.id.required}")
    private UUID cardId;

    @NotNull(message = "{error.reviewlog.rating.required}")
    private Rating rating;
}
