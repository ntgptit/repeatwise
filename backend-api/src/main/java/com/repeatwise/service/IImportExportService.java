package com.repeatwise.service;

import com.repeatwise.dto.request.card.ImportCardsRequest;
import com.repeatwise.dto.response.card.ImportResultResponse;
import org.springframework.core.io.Resource;

import java.util.UUID;

/**
 * Import/Export Service interface
 *
 * Requirements:
 * - UC-021: Import Cards
 * - UC-022: Export Cards
 *
 * @author RepeatWise Team
 */
public interface IImportExportService {

    /**
     * Import cards from CSV/XLSX file
     * UC-021: Import Cards
     *
     * @param deckId Deck UUID
     * @param file Multipart file
     * @param request Import request with duplicate policy
     * @param userId Current user UUID
     * @return Import result response
     */
    ImportResultResponse importCards(UUID deckId, org.springframework.web.multipart.MultipartFile file,
                                     ImportCardsRequest request, UUID userId);

    /**
     * Export cards to CSV/XLSX file
     * UC-022: Export Cards
     *
     * @param deckId Deck UUID
     * @param format Export format (csv or xlsx)
     * @param scope Export scope (ALL or DUE_ONLY)
     * @param userId Current user UUID
     * @return Resource for file download
     */
    Resource exportCards(UUID deckId, String format, String scope, UUID userId);
}

