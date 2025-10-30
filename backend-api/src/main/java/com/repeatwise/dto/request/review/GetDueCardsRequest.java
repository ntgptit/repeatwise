package com.repeatwise.dto.request.review;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for starting a review session
 *
 * Requirements:
 * - UC-023: Review Cards (SRS)
 *
 * Request Body:
 * {
 *   "scopeType": "DECK",
 *   "scopeId": "uuid"
 * }
 *
 * @author RepeatWise Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetDueCardsRequest {

    /**
     * Scope type: DECK or FOLDER
     */
    @NotNull(message = "{error.review.scopetype.required}")
    private ScopeType scopeType;

    /**
     * Scope ID (deck ID or folder ID)
     */
    @NotNull(message = "{error.review.scopeid.required}")
    private UUID scopeId;

    public enum ScopeType {
        DECK,
        FOLDER
    }
}

