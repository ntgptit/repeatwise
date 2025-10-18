package com.repeatwise.mapper;

import com.repeatwise.dto.request.deck.CreateDeckRequest;
import com.repeatwise.dto.response.deck.DeckResponse;
import com.repeatwise.entity.Deck;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Deck Mapper - MapStruct mapper for Deck entity and DTOs
 *
 * Requirements:
 * - Coding Convention: Use MapStruct for DTO mapping
 * - Clean separation between entity and DTO
 * - UC-011 to UC-014: Deck management
 *
 * @author RepeatWise Team
 */
@Mapper(componentModel = "spring")
public interface DeckMapper {

    // ==================== Entity to Response ====================

    /**
     * Convert Deck entity to DeckResponse DTO
     * Used for single deck details (GET /api/decks/{id})
     *
     * @param deck Deck entity
     * @return DeckResponse DTO
     */
    @Mapping(target = "folderId", source = "folder.id")
    @Mapping(target = "folderName", source = "folder.name")
    @Mapping(target = "cardCount", expression = "java(deck.getCards().size())")
    @Mapping(target = "dueCards", constant = "0")
    @Mapping(target = "newCards", constant = "0")
    DeckResponse toResponse(Deck deck);

    /**
     * Convert list of Deck entities to DeckResponse DTOs
     *
     * @param decks List of Deck entities
     * @return List of DeckResponse DTOs
     */
    List<DeckResponse> toResponseList(List<Deck> decks);

    // ==================== Request to Entity ====================

    /**
     * Convert CreateDeckRequest to Deck entity
     * Note: user and folder must be set separately in service layer
     *
     * @param request CreateDeckRequest DTO
     * @return Deck entity (partial)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "folder", ignore = true)
    @Mapping(target = "cards", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Deck toEntity(CreateDeckRequest request);
}
