package com.repeatwise.dto.response.card;

import java.time.LocalDateTime;

import lombok.Builder;

/**
 * DTO phản hồi khi xóa thẻ thành công.
 */
@Builder
public record CardDeletionResponse(
        String message,
        LocalDateTime deletedAt,
        String cardId) {
}

