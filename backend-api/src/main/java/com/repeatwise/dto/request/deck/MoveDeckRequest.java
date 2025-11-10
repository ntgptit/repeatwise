package com.repeatwise.dto.request.deck;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
