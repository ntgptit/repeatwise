package com.repeatwise.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.repeatwise.dto.response.review.ReviewLogResponse;
import com.repeatwise.dto.response.review.ReviewResultResponse;
import com.repeatwise.dto.response.review.ReviewSessionResponse;
import com.repeatwise.entity.CardBoxPosition;
import com.repeatwise.entity.ReviewLog;

/**
 * Review Mapper - MapStruct mapper for Review-related entities and DTOs
 *
 * Requirements:
 * - Coding Convention: Use MapStruct for DTO mapping
 * - Clean separation between entity and DTO
 * - UC-023: Review Cards with SRS
 * - UC-024: Rate Card
 * - UC-025: Undo Review
 *
 * @author RepeatWise Team
 */
@Mapper(componentModel = "spring", uses = { CardMapper.class })
public interface ReviewMapper {

    // ==================== CardBoxPosition to Response ====================

    /**
     * Convert CardBoxPosition to ReviewSessionResponse.CardInfo
     * Used for review session card display
     *
     * @param cardBoxPosition CardBoxPosition entity
     * @return Card info with SRS fields
     */
    @Mapping(target = "id", source = "card.id")
    @Mapping(target = "front", source = "card.front")
    @Mapping(target = "back", source = "card.back")
    @Mapping(target = "deckId", source = "card.deck.id")
    @Mapping(target = "currentBox", source = "currentBox")
    @Mapping(target = "dueDate", source = "dueDate")
    @Mapping(target = "reviewCount", source = "reviewCount")
    @Mapping(target = "lapseCount", source = "lapseCount")
    @Mapping(target = "lastReviewedAt", source = "lastReviewedAt")
    ReviewSessionResponse.CardInfo toCardInfo(CardBoxPosition cardBoxPosition);

    /**
     * Convert list of CardBoxPosition to CardInfo list
     *
     * @param cardBoxPositions List of CardBoxPosition entities
     * @return List of CardInfo DTOs
     */
    List<ReviewSessionResponse.CardInfo> toCardInfoList(List<CardBoxPosition> cardBoxPositions);

    // ==================== ReviewLog to Response ====================

    /**
     * Convert ReviewLog entity to ReviewLogResponse DTO
     * Used for review history endpoints
     *
     * @param reviewLog ReviewLog entity
     * @return ReviewLogResponse DTO
     */
    @Mapping(target = "cardId", source = "card.id")
    @Mapping(target = "cardFront", source = "card.front")
    @Mapping(target = "cardBack", source = "card.back")
    @Mapping(target = "userId", source = "user.id")
    ReviewLogResponse toResponse(ReviewLog reviewLog);

    /**
     * Convert list of ReviewLog entities to ReviewLogResponse DTOs
     *
     * @param reviewLogs List of ReviewLog entities
     * @return List of ReviewLogResponse DTOs
     */
    List<ReviewLogResponse> toResponseList(List<ReviewLog> reviewLogs);

    // ==================== Review Result Mapping ====================

    /**
     * Create ReviewResultResponse from card and remaining count
     * Used after rating a card
     *
     * @param cardBoxPosition  Next card box position (can be null)
     * @param remaining        Remaining cards count
     * @param completed        Total completed count
     * @param total            Total cards count
     * @param sessionCompleted Whether session is completed
     * @return ReviewResultResponse DTO
     */
    default ReviewResultResponse toReviewResult(
            final CardBoxPosition cardBoxPosition,
            final Integer remaining,
            final Integer completed,
            final Integer total,
            final Boolean sessionCompleted) {
        return ReviewResultResponse.builder()
                .nextCard(cardBoxPosition != null ? toCardInfo(cardBoxPosition) : null)
                .remaining(remaining)
                .progress(ReviewResultResponse.Progress.builder()
                        .completed(completed)
                        .total(total)
                        .build())
                .completed(sessionCompleted)
                .build();
    }
}
