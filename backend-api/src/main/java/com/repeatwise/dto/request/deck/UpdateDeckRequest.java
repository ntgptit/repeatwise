package com.repeatwise.dto.request.deck;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating a deck
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDeckRequest {

    @Size(min = 1, max = 100, message = "{error.deck.name.size}")
    private String name;

    @Size(max = 500, message = "{error.deck.description.size}")
    private String description;
}
