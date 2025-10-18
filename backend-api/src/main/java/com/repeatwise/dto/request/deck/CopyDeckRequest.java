package com.repeatwise.dto.request.deck;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

/**
 * Request DTO for copying a deck (UC-013)
 *
 * Requirements:
 * - UC-013: Copy Deck
 * - BR-048: Copy thresholds (< 100 cards = sync, >= 100 cards = async)
 * - BR-049: Card state reset (all cards start in Box 1)
 * - BR-050: Naming convention (default: "[Original] (Copy)")
 *
 * Fields:
 * - newName: Name for copied deck (required, 1-100 chars)
 * - destinationFolderId: Target folder ID (nullable = root level)
 *
 * Use Cases:
 * 1. Copy deck to same folder with new name
 * 2. Copy deck to different folder
 * 3. Copy deck to root level (destinationFolderId = null)
 *
 * Validation:
 * - newName must be unique in destination folder
 * - newName trimmed automatically
 * - destinationFolderId must exist and belong to user (if not null)
 *
 * @author RepeatWise Team
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CopyDeckRequest {

    /**
     * New name for copied deck
     *
     * Requirements:
     * - BR-031: Deck naming (1-100 chars)
     * - BR-050: Default naming convention "[Original] (Copy)"
     *
     * Validation:
     * - Not blank (required)
     * - 1-100 characters after trim
     * - Unique within destination folder
     *
     * Examples:
     * - "Academic Vocabulary (Copy)"
     * - "IELTS Vocabulary Backup"
     * - "My Deck Copy 2"
     */
    @NotBlank(message = "{deck.name.required}")
    @Size(min = 1, max = 100, message = "{deck.name.size}")
    private String newName;

    /**
     * Destination folder ID (nullable = root level)
     *
     * Requirements:
     * - BR-032: Deck can be at root level (folder_id nullable)
     *
     * Behavior:
     * - null: Copy to root level
     * - UUID: Copy to specified folder
     *
     * Validation:
     * - Folder must exist and belong to user (if not null)
     * - Name must be unique in destination folder
     *
     * Examples:
     * - null: Copy to root
     * - "550e8400-e29b...": Copy to specific folder
     */
    private UUID destinationFolderId;

    @Override
    public String toString() {
        return "CopyDeckRequest{" +
                "newName='" + newName + '\'' +
                ", destinationFolderId=" + destinationFolderId +
                '}';
    }
}
