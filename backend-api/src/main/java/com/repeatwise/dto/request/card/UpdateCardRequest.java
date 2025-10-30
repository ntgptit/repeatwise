package com.repeatwise.dto.request.card;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating an existing card
 *
 * Requirements:
 * - UC-019: Update Card
 * - UC-027: Edit Card During Review
 * - BR-CARD-01: Front and Back are required, maximum 5000 characters each
 * - BR-CARD-04: Editing does not reset SRS state by default
 *
 * Validation:
 * - front: Required, 1-5000 characters, trim whitespace
 * - back: Required, 1-5000 characters, trim whitespace
 *
 * Business Rules:
 * - Only updates front and back content
 * - SRS state (current_box, due_date) remains unchanged
 *
 * @author RepeatWise Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCardRequest {

    /**
     * Card front text (required, 1-5000 chars)
     * Trimmed before validation
     */
    @NotBlank(message = "{card.front.required}")
    @Size(min = 1, max = 5000, message = "{card.front.size}")
    private String front;

    /**
     * Card back text (required, 1-5000 chars)
     * Trimmed before validation
     */
    @NotBlank(message = "{card.back.required}")
    @Size(min = 1, max = 5000, message = "{card.back.size}")
    private String back;
}

