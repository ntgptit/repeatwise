package com.repeatwise.dto.response.deck;

import java.time.LocalDateTime;

import lombok.Builder;

/**
 * Response DTO cho kết quả xóa bộ thẻ.
 */
@Builder
public record DeckDeletionResponse(
        String message,
        LocalDateTime deletedAt,
        String deckId) {
}

