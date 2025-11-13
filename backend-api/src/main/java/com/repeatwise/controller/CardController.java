package com.repeatwise.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.repeatwise.dto.request.card.CreateCardRequest;
import com.repeatwise.dto.request.card.UpdateCardRequest;
import com.repeatwise.dto.response.card.CardDeletionResponse;
import com.repeatwise.dto.response.card.CardResponse;
import com.repeatwise.entity.User;
import com.repeatwise.service.CardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller cho các thao tác thẻ: tạo, cập nhật, xóa.
 */
@RestController
@RequestMapping("/v1/cards")
@RequiredArgsConstructor
@Tag(name = "Card Management", description = "APIs cho quản lý thẻ trong RepeatWise")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class CardController {

    private final CardService cardService;

    /**
     * Lấy danh sách thẻ trong một deck.
     */
    @GetMapping("/deck/{deckId}")
    @Operation(summary = "Danh sách thẻ theo deck", description = "Trả về danh sách thẻ thuộc deck của người dùng.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy danh sách thẻ thành công"),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực"),
            @ApiResponse(responseCode = "404", description = "Deck không tồn tại"),
    })
    public ResponseEntity<List<CardResponse>> getCardsByDeck(
            @PathVariable UUID deckId,
            @AuthenticationPrincipal User user) {
        final var userId = user.getId();
        log.info("User {} fetches cards for deck {}", userId, deckId);
        final var responses = this.cardService.getCardsByDeck(deckId, userId);
        return ResponseEntity.ok(responses);
    }

    /**
     * UC-018: Tạo thẻ mới trong deck.
     */
    @PostMapping
    @Operation(summary = "Tạo thẻ", description = "Tạo thẻ mới trong deck của người dùng.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tạo thẻ thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực"),
            @ApiResponse(responseCode = "404", description = "Deck không tồn tại")
    })
    public ResponseEntity<CardResponse> createCard(
            @Valid @RequestBody CreateCardRequest request,
            @AuthenticationPrincipal User user) {
        final var userId = user.getId();
        log.info("User {} tạo thẻ mới trong deck {}", userId, request.getDeckId());

        final var response = this.cardService.createCard(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * UC-019: Cập nhật nội dung thẻ.
     */
    @PatchMapping("/{cardId}")
    @Operation(summary = "Cập nhật thẻ", description = "Cập nhật mặt trước/mặt sau của thẻ.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thẻ thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực"),
            @ApiResponse(responseCode = "404", description = "Thẻ không tồn tại")
    })
    public ResponseEntity<CardResponse> updateCard(
            @PathVariable UUID cardId,
            @Valid @RequestBody UpdateCardRequest request,
            @AuthenticationPrincipal User user) {
        final var userId = user.getId();
        log.info("User {} cập nhật thẻ {}", userId, cardId);

        final var response = this.cardService.updateCard(cardId, request, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * UC-020: Xóa (soft delete) thẻ.
     */
    @DeleteMapping("/{cardId}")
    @Operation(summary = "Xóa thẻ", description = "Soft delete thẻ, có thể khôi phục trong 30 ngày.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Xóa thẻ thành công"),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực"),
            @ApiResponse(responseCode = "404", description = "Thẻ không tồn tại"),
            @ApiResponse(responseCode = "410", description = "Thẻ đã bị xóa trước đó")
    })
    public ResponseEntity<CardDeletionResponse> deleteCard(
            @PathVariable UUID cardId,
            @AuthenticationPrincipal User user) {
        final var userId = user.getId();
        log.info("User {} xóa thẻ {}", userId, cardId);

        final var result = this.cardService.deleteCard(cardId, userId);
        final var response = CardDeletionResponse.builder()
                .cardId(result.cardId().toString())
                .deletedAt(result.deletedAt())
                .message(result.message())
                .build();
        return ResponseEntity.ok(response);
    }
}

