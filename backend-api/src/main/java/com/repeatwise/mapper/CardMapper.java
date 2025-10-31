package com.repeatwise.mapper;

import com.repeatwise.dto.request.card.CreateCardRequest;
import com.repeatwise.dto.request.card.UpdateCardRequest;
import com.repeatwise.dto.response.card.CardResponse;
import com.repeatwise.entity.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Card Mapper - MapStruct mapper for Card entity and DTOs
 *
 * Requirements:
 * - Coding Convention: Use MapStruct for DTO mapping
 * - Clean separation between entity and DTO
 * - UC-017: Create/Edit Card
 * - UC-018: Delete Card
 * - UC-019: Review Cards with SRS
 *
 * @author RepeatWise Team
 */
@Mapper(componentModel = "spring")
public interface CardMapper {

    // ==================== Entity to Response ====================

    /**
     * Convert Card entity to CardResponse DTO
     * Used for card CRUD operations
     *
     * Note: SRS fields (currentBox, dueDate, lapseCount, lastReviewedAt, reviewCount) are ignored
     * as they come from CardBoxPosition entity, not Card entity.
     * Use toResponseWithSrs() for review session responses.
     *
     * @param card Card entity
     * @return CardResponse DTO
     */
    @Mapping(target = "deckId", source = "deck.id")
    @Mapping(target = "currentBox", ignore = true)
    @Mapping(target = "dueDate", ignore = true)
    @Mapping(target = "lapseCount", ignore = true)
    @Mapping(target = "lastReviewedAt", ignore = true)
    @Mapping(target = "reviewCount", ignore = true)
    CardResponse toResponse(Card card);

    /**
     * Convert Card entity to CardResponse DTO with SRS fields
     * Used for review session responses
     *
     * Note: Only currentBox and dueDate are mapped from parameters.
     * Other SRS fields (lapseCount, lastReviewedAt, reviewCount) are ignored
     * as they are not provided in this method signature.
     * Use CardBoxPosition entity directly for full SRS data mapping.
     *
     * @param card Card entity
     * @param currentBox Current box from CardBoxPosition
     * @param dueDate Due date from CardBoxPosition
     * @return CardResponse DTO with SRS fields
     */
    @Mapping(target = "deckId", source = "card.deck.id")
    @Mapping(target = "currentBox", source = "currentBox")
    @Mapping(target = "dueDate", source = "dueDate")
    @Mapping(target = "lapseCount", ignore = true)
    @Mapping(target = "lastReviewedAt", ignore = true)
    @Mapping(target = "reviewCount", ignore = true)
    CardResponse toResponseWithSrs(Card card, Integer currentBox, java.time.LocalDate dueDate);

    /**
     * Convert list of Card entities to CardResponse DTOs
     *
     * @param cards List of Card entities
     * @return List of CardResponse DTOs
     */
    List<CardResponse> toResponseList(List<Card> cards);

    // ==================== Request to Entity ====================

    /**
     * Convert CreateCardRequest to Card entity
     * Note: deck must be set separately in service layer
     *
     * @param request CreateCardRequest DTO
     * @return Card entity (partial)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deck", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Card toEntity(CreateCardRequest request);

    /**
     * Update Card entity from UpdateCardRequest
     * Only updates front and back fields, preserves other fields
     *
     * @param request UpdateCardRequest DTO
     * @param card Existing Card entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deck", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateEntity(UpdateCardRequest request, @org.mapstruct.MappingTarget Card card);
}

