package com.repeatwise.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PatchMapping;

import com.repeatwise.dto.request.deck.CopyDeckRequest;
import com.repeatwise.dto.request.deck.CreateDeckRequest;
import com.repeatwise.dto.request.deck.MoveDeckRequest;
import com.repeatwise.dto.request.deck.UpdateDeckRequest;
import com.repeatwise.dto.response.deck.DeckCopyResponse;
import com.repeatwise.dto.response.deck.DeckDeletionResponse;
import com.repeatwise.dto.response.deck.DeckResponse;
import com.repeatwise.entity.User;
import com.repeatwise.service.DeckService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller cho các thao tác với deck (UC-013 đến UC-017).
 */
@RestController
@RequestMapping("/v1/decks")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Deck Management", description = "APIs cho quản lý bộ thẻ (decks)")
@SecurityRequirement(name = "bearerAuth")
public class DeckController {

    private final DeckService deckService;

    /**
     * UC-013: Tạo deck mới.
     */
    @PostMapping
    @Operation(summary = "Tạo deck mới", description = "Tạo deck tại thư mục chỉ định hoặc cấp gốc.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tạo deck thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ hoặc trùng tên"),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực"),
            @ApiResponse(responseCode = "404", description = "Thư mục không tồn tại")
    })
    public ResponseEntity<DeckResponse> createDeck(
            @Valid @RequestBody CreateDeckRequest request,
            @AuthenticationPrincipal User user) {
        final var userId = user.getId();
        log.info("User {} tạo deck '{}'", userId, request.getName());

        final var response = this.deckService.createDeck(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * UC-014: Cập nhật tên/mô tả deck.
     */
    @PatchMapping("/{deckId}")
    @Operation(summary = "Cập nhật deck", description = "Cập nhật tên hoặc mô tả cho deck hiện có.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật deck thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ hoặc trùng tên"),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực"),
            @ApiResponse(responseCode = "404", description = "Deck không tồn tại")
    })
    public ResponseEntity<DeckResponse> updateDeck(
            @PathVariable UUID deckId,
            @Valid @RequestBody UpdateDeckRequest request,
            @AuthenticationPrincipal User user) {
        final var userId = user.getId();
        log.info("User {} cập nhật deck {}", userId, deckId);

        final var response = this.deckService.updateDeck(deckId, request, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * UC-015: Di chuyển deck sang thư mục khác.
     */
    @PostMapping("/{deckId}/move")
    @Operation(summary = "Di chuyển deck", description = "Di chuyển deck sang thư mục khác hoặc cấp gốc.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Di chuyển deck thành công"),
            @ApiResponse(responseCode = "400", description = "Thao tác không hợp lệ hoặc trùng tên"),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực"),
            @ApiResponse(responseCode = "404", description = "Deck hoặc thư mục không tồn tại")
    })
    public ResponseEntity<DeckResponse> moveDeck(
            @PathVariable UUID deckId,
            @Valid @RequestBody MoveDeckRequest request,
            @AuthenticationPrincipal User user) {
        final var userId = user.getId();
        log.info("User {} di chuyển deck {} tới {}", userId, deckId, request.getTargetFolderId());

        final var response = this.deckService.moveDeck(deckId, request, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * UC-016: Sao chép deck.
     */
    @PostMapping("/{deckId}/copy")
    @Operation(summary = "Sao chép deck", description = "Tạo bản sao deck cùng toàn bộ thẻ bên trong.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sao chép deck thành công"),
            @ApiResponse(responseCode = "400", description = "Deck quá lớn hoặc trùng tên"),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực"),
            @ApiResponse(responseCode = "404", description = "Deck hoặc thư mục không tồn tại")
    })
    public ResponseEntity<DeckCopyResponse> copyDeck(
            @PathVariable UUID deckId,
            @Valid @RequestBody CopyDeckRequest request,
            @AuthenticationPrincipal User user) {
        final var userId = user.getId();
        log.info("User {} sao chép deck {} tới {}", userId, deckId, request.getDestinationFolderId());

        final var result = this.deckService.copyDeck(deckId, request, userId);
        final var response = DeckCopyResponse.builder()
                .deck(result.deck())
                .message(result.message())
                .copiedCards(result.copiedCards())
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * UC-017: Xóa (soft delete) deck.
     */
    @DeleteMapping("/{deckId}")
    @Operation(summary = "Xóa deck", description = "Soft delete deck, có thể khôi phục trong 30 ngày.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Xóa deck thành công"),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực"),
            @ApiResponse(responseCode = "404", description = "Deck không tồn tại")
    })
    public ResponseEntity<DeckDeletionResponse> deleteDeck(
            @PathVariable UUID deckId,
            @AuthenticationPrincipal User user) {
        final var userId = user.getId();
        log.info("User {} xóa deck {}", userId, deckId);

        final var result = this.deckService.deleteDeck(deckId, userId);
        final var response = DeckDeletionResponse.builder()
                .message(result.message())
                .deletedAt(result.deletedAt())
                .deckId(result.deckId().toString())
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy danh sách deck theo thư mục (folderId = null => deck ở cấp gốc).
     */
    @GetMapping
    @Operation(summary = "Danh sách deck", description = "Lấy danh sách deck theo thư mục hiện tại hoặc cấp gốc.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công"),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực")
    })
    public ResponseEntity<List<DeckResponse>> getDecks(
            @RequestParam(value = "folderId", required = false) UUID folderId,
            @AuthenticationPrincipal User user) {
        final var userId = user.getId();
        final var decks = this.deckService.getDecks(userId, folderId);
        return ResponseEntity.ok(decks);
    }

    /**
     * Lấy toàn bộ deck của người dùng (mọi thư mục).
     */
    @GetMapping("/all")
    @Operation(summary = "Danh sách toàn bộ deck", description = "Lấy toàn bộ deck của người dùng trên mọi thư mục.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công"),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực")
    })
    public ResponseEntity<List<DeckResponse>> getAllDecks(
            @AuthenticationPrincipal User user) {
        final var userId = user.getId();
        final var decks = this.deckService.getAllDecks(userId);
        return ResponseEntity.ok(decks);
    }

    /**
     * Lấy chi tiết deck.
     */
    @GetMapping("/{deckId}")
    @Operation(summary = "Chi tiết deck", description = "Lấy thông tin chi tiết của một deck.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy deck thành công"),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực"),
            @ApiResponse(responseCode = "404", description = "Deck không tồn tại")
    })
    public ResponseEntity<DeckResponse> getDeck(
            @PathVariable UUID deckId,
            @AuthenticationPrincipal User user) {
        final var userId = user.getId();
        final var deck = this.deckService.getDeckById(deckId, userId);
        return ResponseEntity.ok(deck);
    }
}

