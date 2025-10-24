package com.repeatwise.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request DTO for creating a new card
 *
 * Requirements:
 * - UC-017: Create/Edit Card (Section 2)
 * - API: POST /api/decks/{deckId}/cards
 *
 * Validation Rules:
 * - front: required, 1-5000 characters
 * - back: required, 1-5000 characters
 * - Both fields trimmed automatically
 *
 * @author RepeatWise Team
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCardRequest {

    @NotBlank(message = "{card.front.required}")
    @Size(min = 1, max = 5000, message = "{card.front.size}")
    private String front;

    @NotBlank(message = "{card.back.required}")
    @Size(min = 1, max = 5000, message = "{card.back.size}")
    private String back;
}
