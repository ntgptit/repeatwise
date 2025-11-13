package com.repeatwise.dto.response.deck;

import lombok.Builder;

/**
 * Response DTO cho kết quả sao chép bộ thẻ.
 */
@Builder
public record DeckCopyResponse(
        DeckResponse deck,
        String message,
        int copiedCards) {
}

