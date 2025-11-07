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

    @NotNull(message = "Deck ID is required")
    private UUID deckId;

    @NotBlank(message = "Front text is required")
    @Size(min = 1, max = 5000, message = "Front text must be between 1 and 5000 characters")
    private String front;

    @NotBlank(message = "Back text is required")
    @Size(min = 1, max = 5000, message = "Back text must be between 1 and 5000 characters")
    private String back;
}
