package com.repeatwise.service;

import com.repeatwise.dto.request.srs.UpdateSrsSettingsRequest;
import com.repeatwise.dto.response.srs.SrsSettingsResponse;

import java.util.UUID;

/**
 * SRS Settings Service interface
 *
 * Requirements:
 * - UC-028: Configure SRS Settings
 *
 * @author RepeatWise Team
 */
public interface ISrsSettingsService {

    /**
     * Get current SRS settings for user
     * UC-028: Configure SRS Settings
     *
     * @param userId Current user UUID
     * @return SRS settings response
     */
    SrsSettingsResponse getSettings(UUID userId);

    /**
     * Update SRS settings for user
     * UC-028: Configure SRS Settings
     *
     * @param request Update request
     * @param userId Current user UUID
     * @return Updated SRS settings response
     */
    SrsSettingsResponse updateSettings(UpdateSrsSettingsRequest request, UUID userId);
}

