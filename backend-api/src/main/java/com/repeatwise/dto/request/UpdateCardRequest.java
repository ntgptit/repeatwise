package com.repeatwise.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request DTO for updating an existing card
 *
 * Requirements:
 * - UC-017: Create/Edit Card (Edit scenario)
 * - API: PUT /api/cards/{cardId}
 *
 * Validation Rules:
 * - front: required, 1-5000 characters
 * - back: required, 1-5000 characters
 * - Both fields trimmed automatically
 *
 * Business Rules:
 * - BR-CARD-005: Editing card preserves SRS state
 *
 * @author RepeatWise Team
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCardRequest {

    @NotBlank(message = "{card.front.required}")
    @Size(min = 1, max = 5000, message = "{card.front.size}")
    private String front;

    @NotBlank(message = "{card.back.required}")
    @Size(min = 1, max = 5000, message = "{card.back.size}")
    private String back;
}
