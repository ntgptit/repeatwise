package com.repeatwise.dto.response.deck;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Response DTO for deck deletion
 *
 * Requirements:
 * - UC-017: Delete Deck
 * - Response includes success message and deletedAt timestamp
 *
 * @author RepeatWise Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeckDeleteResponse {

    /**
     * Success message
     */
    private String message;

    /**
     * Timestamp when deck was deleted
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant deletedAt;
}

