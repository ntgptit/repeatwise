package com.repeatwise.dto.request.card;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a new card
 *
 * Requirements:
 * - UC-018: Create Card
 * - BR-CARD-01: Front and Back are required, maximum 5000 characters each
 *
 * Validation:
 * - front: Required, 1-5000 characters, trim whitespace
 * - back: Required, 1-5000 characters, trim whitespace
 *
 * @author RepeatWise Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCardRequest {

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

