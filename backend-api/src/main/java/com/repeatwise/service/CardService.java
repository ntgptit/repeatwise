package com.repeatwise.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.repeatwise.dto.request.card.CreateCardRequest;
import com.repeatwise.dto.request.card.UpdateCardRequest;
import com.repeatwise.dto.response.card.CardResponse;

/**
 * Service xử lý nghiệp vụ cho thẻ (UC-018 đến UC-020).
 */
public interface CardService {

    /**
     * UC-018: Tạo thẻ mới trong deck.
     */
    CardResponse createCard(CreateCardRequest request, UUID userId);

    /**
     * UC-019: Cập nhật nội dung thẻ.
     */
    CardResponse updateCard(UUID cardId, UpdateCardRequest request, UUID userId);

    /**
     * UC-020: Xóa (soft delete) thẻ.
     */
    CardDeletionResult deleteCard(UUID cardId, UUID userId);

    /**
     * Lấy danh sách thẻ theo deck cho người dùng hiện tại.
     */
    List<CardResponse> getCardsByDeck(UUID deckId, UUID userId);

    /**
     * Thông tin kết quả xóa thẻ.
     */
    record CardDeletionResult(UUID cardId, LocalDateTime deletedAt, String message) {
    }
}

