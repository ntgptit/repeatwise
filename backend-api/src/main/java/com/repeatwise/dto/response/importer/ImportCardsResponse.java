package com.repeatwise.dto.response.importer;

import lombok.Builder;
import lombok.Getter;

/**
 * Response cho import đồng bộ.
 */
@Getter
@Builder
public class ImportCardsResponse {
    private final int imported;
    private final int skipped;
    private final int failed;
    private final int totalRows;
    private final String duplicatePolicy;
    private final String message;
    private final String errorReportUrl;
    private final String jobId;
}

