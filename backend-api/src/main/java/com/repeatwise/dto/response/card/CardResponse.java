package com.repeatwise.dto.response.card;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for card response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardResponse {

    private UUID id;
    private UUID deckId;
    private String front;
    private String back;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
