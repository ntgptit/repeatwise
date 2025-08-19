package com.spacedlearning.dto.set;

import com.spacedlearning.entity.enums.SetCategory;
import com.spacedlearning.entity.enums.SetStatus;
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
public class LearningSetResponse {

    private UUID id;
    private String name;
    private String description;
    private SetCategory category;
    private Integer wordCount;
    private SetStatus status;
    private Integer currentCycle;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
