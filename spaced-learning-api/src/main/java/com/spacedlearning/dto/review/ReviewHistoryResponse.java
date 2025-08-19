package com.spacedlearning.dto.review;

import com.spacedlearning.entity.enums.ReviewStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewHistoryResponse {

    private UUID id;
    private UUID setId;
    private String setName;
    private Integer cycleNo;
    private Integer reviewNo;
    private Integer score;
    private ReviewStatus status;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
