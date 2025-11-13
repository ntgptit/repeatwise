package com.repeatwise.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.repeatwise.dto.request.deck.CopyDeckRequest;
import com.repeatwise.dto.request.deck.CreateDeckRequest;
import com.repeatwise.dto.request.deck.MoveDeckRequest;
import com.repeatwise.dto.request.deck.UpdateDeckRequest;
import com.repeatwise.dto.response.deck.DeckResponse;

/**
 * Service interface cho các thao tác với bộ thẻ (deck).
 * Bao phủ các use case UC-013 đến UC-017.
 */
public interface DeckService {

    /**
     * UC-013: Tạo bộ thẻ mới.
     */
    DeckResponse createDeck(CreateDeckRequest request, UUID userId);

    /**
     * UC-014: Cập nhật tên/mô tả bộ thẻ.
     */
    DeckResponse updateDeck(UUID deckId, UpdateDeckRequest request, UUID userId);

    /**
     * UC-015: Di chuyển bộ thẻ sang thư mục khác hoặc về gốc.
     */
    DeckResponse moveDeck(UUID deckId, MoveDeckRequest request, UUID userId);

    /**
     * UC-016: Sao chép bộ thẻ và toàn bộ thẻ bên trong.
     */
    DeckCopyResult copyDeck(UUID deckId, CopyDeckRequest request, UUID userId);

    /**
     * UC-017: Xóa (soft delete) bộ thẻ.
     */
    DeckDeletionResult deleteDeck(UUID deckId, UUID userId);

    /**
     * Lấy thông tin một bộ thẻ theo ID.
     */
    DeckResponse getDeckById(UUID deckId, UUID userId);

    /**
     * Lấy danh sách bộ thẻ của người dùng theo thư mục.
     * Nếu folderId = null => danh sách ở cấp gốc.
     */
    List<DeckResponse> getDecks(UUID userId, UUID folderId);

    /**
     * Lấy toàn bộ bộ thẻ (mọi thư mục) của người dùng.
     */
    List<DeckResponse> getAllDecks(UUID userId);

    /**
     * Kết quả sau khi sao chép bộ thẻ.
     */
    record DeckCopyResult(
            DeckResponse deck,
            String message,
            int copiedCards) {
    }

    /**
     * Kết quả sau khi xóa bộ thẻ.
     */
    record DeckDeletionResult(
            UUID deckId,
            String message,
            LocalDateTime deletedAt) {
    }
}

