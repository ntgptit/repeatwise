package com.repeatwise.dto.request.deck;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for moving a deck to another folder
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoveDeckRequest {

    private UUID targetFolderId; // null = move to root
}
