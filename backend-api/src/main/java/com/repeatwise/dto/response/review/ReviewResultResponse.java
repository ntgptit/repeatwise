package com.repeatwise.dto.response.review;

import java.time.LocalDate;
import java.util.UUID;

import com.repeatwise.entity.enums.Rating;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for review result response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResultResponse {

    private UUID cardId;
    private Rating rating;
    private Integer previousBox;
    private Integer newBox;
    private Integer intervalDays;
    private LocalDate newDueDate;
    private Boolean progressMade;
}
