package com.repeatwise.dto.request.deck;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for creating a deck
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDeckRequest {

    @NotBlank(message = "{error.deck.name.required}")
    @Size(min = 1, max = 100, message = "{error.deck.name.size}")
    private String name;

    @Size(max = 500, message = "{error.deck.description.size}")
    private String description;

    private UUID folderId; // null = root level deck
}
