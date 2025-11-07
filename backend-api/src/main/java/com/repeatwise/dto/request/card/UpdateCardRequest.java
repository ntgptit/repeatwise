package com.repeatwise.dto.request.card;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating a card
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCardRequest {

    @Size(min = 1, max = 5000, message = "{error.card.front.size}")
    private String front;

    @Size(min = 1, max = 5000, message = "{error.card.back.size}")
    private String back;
}
