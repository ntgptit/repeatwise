package com.repeatwise.controller;

import com.repeatwise.dto.request.review.GetDueCardsRequest;
import com.repeatwise.dto.request.review.ReviewSubmitRequest;
import com.repeatwise.dto.request.review.StartCramSessionRequest;
import com.repeatwise.dto.response.review.ReviewResultResponse;
import com.repeatwise.dto.response.review.ReviewSessionResponse;
import com.repeatwise.security.SecurityUtils;
import com.repeatwise.service.IReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.repeatwise.log.LogEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller for Review Management
 *
 * Requirements:
 * - UC-023: Review Cards (SRS)
 * - UC-024: Rate Card
 * - UC-025: Undo Review
 * - UC-026: Skip Card
 * - UC-029: Cram Mode
 * - UC-030: Random Mode
 *
 * Endpoints:
 * - POST   /api/review/sessions                     - Start review session
 * - POST   /api/review/cram/sessions                 - Start cram session
 * - POST   /api/review/random/sessions               - Start random session
 * - GET    /api/review/sessions/{sessionId}/next    - Get next card
 * - POST   /api/review/sessions/{sessionId}/rate    - Rate card
 * - POST   /api/review/sessions/{sessionId}/undo    - Undo last rating
 * - POST   /api/review/sessions/{sessionId}/skip    - Skip card
 *
 * @author RepeatWise Team
 */
@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final IReviewService reviewService;

    // ==================== UC-023: Start Review Session ====================

    /**
     * Start a review session
     * UC-023: Review Cards (SRS)
     *
     * Requirements:
     * - UC-023: Review Cards (SRS)
     * - BR-REV-01: Due card = due_date <= today
     * - BR-REV-02: Respect daily limits
     * - BR-REV-03: Order by due_date ASC, current_box ASC
     *
     * Request Body:
     * {
     *   "scopeType": "DECK",
     *   "scopeId": "uuid"
     * }
     *
     * Response: 201 Created with session info and first card
     *
     * @param request Get due cards request
     * @return Review session response
     */
    @PostMapping("/sessions")
    public ResponseEntity<ReviewSessionResponse> startReviewSession(
            @Valid @RequestBody final GetDueCardsRequest request) {

        final UUID userId = SecurityUtils.getCurrentUserId();

        log.info("event={} POST /api/review/sessions - Starting session: scopeType={}, scopeId={}, userId={}",
            LogEvent.START, request.getScopeType(), request.getScopeId(), userId);

        final ReviewSessionResponse response = reviewService.startReviewSession(request, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ==================== UC-029: Start Cram Session ====================

    /**
     * Start a cram session
     * UC-029: Cram Mode
     *
     * Requirements:
     * - UC-029: Cram Mode
     * - BR-CRAM-01: Ignore due_date when selecting cards
     * - BR-CRAM-02: Shuffle cards randomly
     * - BR-CRAM-03: Card limit: 500 cards
     *
     * Request Body:
     * {
     *   "scopeType": "DECK",
     *   "scopeId": "uuid",
     *   "applyToSrs": false,
     *   "filters": {
     *     "boxRange": { "min": 1, "max": 3 },
     *     "includeLearned": true
     *   }
     * }
     *
     * Response: 201 Created with session info and first card
     *
     * @param request Cram session request
     * @return Review session response
     */
    @PostMapping("/cram/sessions")
    public ResponseEntity<ReviewSessionResponse> startCramSession(
            @Valid @RequestBody final StartCramSessionRequest request) {

        final UUID userId = SecurityUtils.getCurrentUserId();

        log.info("event={} POST /api/review/cram/sessions - Starting cram session: scopeType={}, scopeId={}, userId={}",
            LogEvent.START, request.getScopeType(), request.getScopeId(), userId);

        final ReviewSessionResponse response = reviewService.startCramSession(request, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ==================== UC-030: Start Random Session ====================

    /**
     * Start a random mode session
     * UC-030: Random Mode
     *
     * Requirements:
     * - UC-030: Random Mode
     * - Same as normal review but with randomized order
     * - Only due cards are included
     *
     * Request Body:
     * {
     *   "scopeType": "DECK",
     *   "scopeId": "uuid"
     * }
     *
     * Response: 201 Created with session info and first card
     *
     * @param request Get due cards request
     * @return Review session response
     */
    @PostMapping("/random/sessions")
    public ResponseEntity<ReviewSessionResponse> startRandomSession(
            @Valid @RequestBody final GetDueCardsRequest request) {

        final UUID userId = SecurityUtils.getCurrentUserId();

        log.info("event={} POST /api/review/random/sessions - Starting random session: scopeType={}, scopeId={}, userId={}",
            LogEvent.START, request.getScopeType(), request.getScopeId(), userId);

        final ReviewSessionResponse response = reviewService.startRandomSession(request, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ==================== UC-024: Rate Card ====================

    /**
     * Rate a card during review
     * UC-024: Rate Card
     *
     * Requirements:
     * - UC-024: Rate Card
     * - Applies SRS algorithm based on rating
     * - Updates card box position
     * - Creates review log
     *
     * Request Body:
     * {
     *   "cardId": "uuid",
     *   "rating": "GOOD",
     *   "timeTakenMs": 5000
     * }
     *
     * Response: 200 OK with next card or completion message
     *
     * @param sessionId Session UUID
     * @param request Rating request
     * @return Review result with next card
     */
    @PostMapping("/sessions/{sessionId}/rate")
    public ResponseEntity<ReviewResultResponse> rateCard(
            @PathVariable final UUID sessionId,
            @Valid @RequestBody final ReviewSubmitRequest request) {

        final UUID userId = SecurityUtils.getCurrentUserId();

        log.info("event={} POST /api/review/sessions/{}/rate - Rating card: cardId={}, rating={}, userId={}",
            LogEvent.START, sessionId, request.getCardId(), request.getRating(), userId);

        final ReviewResultResponse response = reviewService.rateCard(sessionId, request, userId);

        return ResponseEntity.ok(response);
    }

    // ==================== UC-025: Undo Review ====================

    /**
     * Undo last review
     * UC-025: Undo Review
     *
     * Requirements:
     * - UC-025: Undo Review
     * - BR-REV-05: Only the most recent rating can be undone
     * - BR-REV-06: Undo window (default: 2 minutes)
     *
     * Response: 200 OK with restored card
     *
     * @param sessionId Session UUID
     * @return Restored card info
     */
    @PostMapping("/sessions/{sessionId}/undo")
    public ResponseEntity<ReviewSessionResponse.CardInfo> undoReview(
            @PathVariable final UUID sessionId) {

        final UUID userId = SecurityUtils.getCurrentUserId();

        log.info("event={} POST /api/review/sessions/{}/undo - Undoing review: userId={}",
            LogEvent.START, sessionId, userId);

        final ReviewSessionResponse.CardInfo response = reviewService.undoReview(sessionId, userId);

        return ResponseEntity.ok(response);
    }

    // ==================== UC-026: Skip Card ====================

    /**
     * Skip current card
     * UC-026: Skip Card
     *
     * Requirements:
     * - UC-026: Skip Card
     * - BR-REV-07: Skip does not change SRS state
     * - BR-REV-08: Skip limit per session (configurable, default: 3)
     *
     * Request Body:
     * {
     *   "cardId": "uuid"
     * }
     *
     * Response: 200 OK with next card
     *
     * @param sessionId Session UUID
     * @param cardId Card UUID to skip
     * @return Next card info
     */
    @PostMapping("/sessions/{sessionId}/skip")
    public ResponseEntity<ReviewSessionResponse.CardInfo> skipCard(
            @PathVariable final UUID sessionId,
            @RequestBody final SkipCardRequest request) {

        final UUID userId = SecurityUtils.getCurrentUserId();

        log.info("event={} POST /api/review/sessions/{}/skip - Skipping card: cardId={}, userId={}",
            LogEvent.START, sessionId, request.getCardId(), userId);

        final ReviewSessionResponse.CardInfo response = reviewService.skipCard(
            sessionId, request.getCardId(), userId);

        return ResponseEntity.ok(response);
    }

    /**
     * Request DTO for skip card
     */
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class SkipCardRequest {
        private UUID cardId;
    }
}

