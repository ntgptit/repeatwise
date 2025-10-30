package com.repeatwise.dto.request.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for importing cards
 *
 * Requirements:
 * - UC-021: Import Cards
 *
 * Request Body (multipart form):
 * - file: CSV or XLSX file
 * - duplicatePolicy: SKIP, REPLACE, or KEEP_BOTH
 *
 * @author RepeatWise Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportCardsRequest {

    /**
     * Duplicate handling policy
     * - SKIP: Skip duplicate cards
     * - REPLACE: Update existing cards
     * - KEEP_BOTH: Create new cards even if duplicates exist
     */
    private DuplicatePolicy duplicatePolicy;

    public enum DuplicatePolicy {
        SKIP,
        REPLACE,
        KEEP_BOTH
    }
}

