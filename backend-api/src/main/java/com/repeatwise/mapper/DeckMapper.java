package com.repeatwise.mapper;

import com.repeatwise.dto.request.deck.CreateDeckRequest;
import com.repeatwise.dto.request.deck.UpdateDeckRequest;
import com.repeatwise.dto.response.deck.DeckResponse;
import com.repeatwise.entity.Deck;
import org.mapstruct.*;

/**
 * MapStruct mapper for Deck entity
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        builder = @Builder(disableBuilder = true)
)
public interface DeckMapper {

    /**
     * Convert Deck entity to DeckResponse DTO
     */
    @Mapping(source = "folder.id", target = "folderId")
    @Mapping(expression = "java(deck.getCardCount())", target = "cardCount")
    DeckResponse toResponse(Deck deck);

    /**
     * Convert CreateDeckRequest to Deck entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "folder", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "cards", ignore = true)
    Deck toEntity(CreateDeckRequest request);

    /**
     * Update Deck entity from UpdateDeckRequest
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "folder", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "cards", ignore = true)
    void updateEntityFromRequest(UpdateDeckRequest request, @MappingTarget Deck deck);
}
