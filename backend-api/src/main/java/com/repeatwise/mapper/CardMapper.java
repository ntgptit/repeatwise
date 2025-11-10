package com.repeatwise.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.repeatwise.dto.request.card.CreateCardRequest;
import com.repeatwise.dto.request.card.UpdateCardRequest;
import com.repeatwise.dto.response.card.CardResponse;
import com.repeatwise.dto.response.card.CardWithProgressResponse;
import com.repeatwise.entity.Card;
import com.repeatwise.entity.CardBoxPosition;

/**
 * MapStruct mapper for Card entity
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, builder = @Builder(disableBuilder = true))
public interface CardMapper {

    /**
     * Convert Card entity to CardResponse DTO
     */
    @Mapping(source = "deck.id", target = "deckId")
    CardResponse toResponse(Card card);

    /**
     * Convert Card entity and CardBoxPosition to CardWithProgressResponse DTO
     */
    @Mapping(source = "card.id", target = "id")
    @Mapping(source = "card.deck.id", target = "deckId")
    @Mapping(source = "card.front", target = "front")
    @Mapping(source = "card.back", target = "back")
    @Mapping(source = "card.createdAt", target = "createdAt")
    @Mapping(source = "card.updatedAt", target = "updatedAt")
    @Mapping(source = "position.currentBox", target = "currentBox")
    @Mapping(source = "position.intervalDays", target = "intervalDays")
    @Mapping(source = "position.dueDate", target = "dueDate")
    @Mapping(source = "position.reviewCount", target = "reviewCount")
    @Mapping(source = "position.lapseCount", target = "lapseCount")
    @Mapping(source = "position.lastReviewedAt", target = "lastReviewedAt")
    @Mapping(expression = "java(position != null && position.isNew())", target = "isNew")
    @Mapping(expression = "java(position != null && position.isDue())", target = "isDue")
    @Mapping(expression = "java(position != null && position.isMature())", target = "isMature")
    CardWithProgressResponse toResponseWithProgress(Card card, CardBoxPosition position);

    /**
     * Convert CreateCardRequest to Card entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deck", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "cardBoxPositions", ignore = true)
    @Mapping(target = "reviewLogs", ignore = true)
    Card toEntity(CreateCardRequest request);

    /**
     * Update Card entity from UpdateCardRequest
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deck", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "cardBoxPositions", ignore = true)
    @Mapping(target = "reviewLogs", ignore = true)
    void updateEntityFromRequest(UpdateCardRequest request, @MappingTarget Card card);
}
