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

    @Size(min = 1, max = 100, message = "Deck name must be between 1 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
}
