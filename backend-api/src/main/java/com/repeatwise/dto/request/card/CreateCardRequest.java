package com.repeatwise.dto.request.card;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for creating a card
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCardRequest {

    @NotNull(message = "{error.card.deck.required}")
    private UUID deckId;

    @NotBlank(message = "{error.card.front.required}")
    @Size(min = 1, max = 5000, message = "{error.card.front.size}")
    private String front;

    @NotBlank(message = "{error.card.back.required}")
    @Size(min = 1, max = 5000, message = "{error.card.back.size}")
    private String back;
}
