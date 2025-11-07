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

    @Size(min = 1, max = 5000, message = "Front text must be between 1 and 5000 characters")
    private String front;

    @Size(min = 1, max = 5000, message = "Back text must be between 1 and 5000 characters")
    private String back;
}
