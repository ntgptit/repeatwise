package com.repeatwise.dto.response.deck;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for deck response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeckResponse {

    private UUID id;
    private String name;
    private String description;
    private UUID folderId;
    private Integer cardCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
