package com.repeatwise.dto.request.deck;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating deck (rename and change description)
 *
 * Requirements:
 * - UC-011: Create Deck (also handles update)
 * - BR-031: Deck naming (1-100 chars)
 * - BR-033: Name unique within folder
 *
 * Validation:
 * - name: Required, 1-100 characters
 * - description: Optional, max 500 characters
 *
 * Note: Only name and description can be changed
 * For moving deck to different folder, use MoveDeckRequest
 *
 * @author RepeatWise Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateDeckRequest {

    /**
     * New deck name (required, 1-100 chars)
     */
    @NotBlank(message = "{deck.name.required}")
    @Size(min = 1, max = 100, message = "{deck.name.size}")
    private String name;

    /**
     * New deck description (optional, max 500 chars)
     */
    @Size(max = 500, message = "{deck.description.size}")
    private String description;
}
