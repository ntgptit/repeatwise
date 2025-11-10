package com.repeatwise.dto.response.card;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for card response with SRS progress information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardWithProgressResponse {

    private UUID id;
    private UUID deckId;
    private String front;
    private String back;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // SRS progress info
    private Integer currentBox;
    private Integer intervalDays;
    private LocalDate dueDate;
    private Integer reviewCount;
    private Integer lapseCount;
    private LocalDateTime lastReviewedAt;
    private Boolean isNew;
    private Boolean isDue;
    private Boolean isMature;
}
