package com.repeatwise.dto.request.deck;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for creating a new deck
 *
 * Requirements:
 * - UC-011: Create Deck
 * - BR-031: Deck naming (1-100 chars)
 * - BR-032: Deck can be at root level
 * - BR-033: Name unique within folder
 *
 * Validation:
 * - name: Required, 1-100 characters, trim whitespace
 * - description: Optional, max 500 characters
 * - folderId: Optional (null = root-level deck)
 *
 * @author RepeatWise Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateDeckRequest {

    /**
     * Deck name (required, 1-100 chars)
     * Trimmed before validation
     * No special character restrictions (unlike folders)
     */
    @NotBlank(message = "{deck.name.required}")
    @Size(min = 1, max = 100, message = "{deck.name.size}")
    private String name;

    /**
     * Deck description (optional, max 500 chars)
     */
    @Size(max = 500, message = "{deck.description.size}")
    private String description;

    /**
     * Parent folder ID (optional)
     * - If null: Create root-level deck (no folder)
     * - If not null: Create deck inside folder
     *
     * Validation:
     * - Folder must exist and belong to user
     * - Folder must not be deleted
     * - Deck name must be unique within folder
     */
    private UUID folderId;
}
