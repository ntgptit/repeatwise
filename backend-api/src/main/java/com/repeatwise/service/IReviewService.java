package com.repeatwise.service;

import com.repeatwise.dto.request.review.GetDueCardsRequest;
import com.repeatwise.dto.request.review.ReviewSubmitRequest;
import com.repeatwise.dto.request.review.StartCramSessionRequest;
import com.repeatwise.dto.response.review.ReviewResultResponse;
import com.repeatwise.dto.response.review.ReviewSessionResponse;

import java.util.UUID;

/**
 * Review Service interface
 *
 * Requirements:
 * - UC-023: Review Cards (SRS)
 * - UC-024: Rate Card
 * - UC-025: Undo Review
 * - UC-026: Skip Card
 * - UC-029: Cram Mode
 * - UC-030: Random Mode
 *
 * @author RepeatWise Team
 */
public interface IReviewService {

    /**
     * Start a review session
     * UC-023: Review Cards (SRS)
     *
     * @param request Get due cards request with scope
     * @param userId Current user UUID
     * @return Review session response with first card
     */
    ReviewSessionResponse startReviewSession(GetDueCardsRequest request, UUID userId);

    /**
     * Start a cram session
     * UC-029: Cram Mode
     *
     * @param request Cram session request with scope and filters
     * @param userId Current user UUID
     * @return Review session response with first card
     */
    ReviewSessionResponse startCramSession(StartCramSessionRequest request, UUID userId);

    /**
     * Start a random mode session
     * UC-030: Random Mode
     *
     * @param request Get due cards request with scope
     * @param userId Current user UUID
     * @return Review session response with first card
     */
    ReviewSessionResponse startRandomSession(GetDueCardsRequest request, UUID userId);

    /**
     * Rate a card during review
     * UC-024: Rate Card
     *
     * @param sessionId Session UUID
     * @param request Rating request
     * @param userId Current user UUID
     * @return Review result with next card
     */
    ReviewResultResponse rateCard(UUID sessionId, ReviewSubmitRequest request, UUID userId);

    /**
     * Undo last review
     * UC-025: Undo Review
     *
     * @param sessionId Session UUID
     * @param userId Current user UUID
     * @return Restored card info
     */
    ReviewSessionResponse.CardInfo undoReview(UUID sessionId, UUID userId);

    /**
     * Skip current card
     * UC-026: Skip Card
     *
     * @param sessionId Session UUID
     * @param cardId Card UUID to skip
     * @param userId Current user UUID
     * @return Next card info
     */
    ReviewSessionResponse.CardInfo skipCard(UUID sessionId, UUID cardId, UUID userId);
}

