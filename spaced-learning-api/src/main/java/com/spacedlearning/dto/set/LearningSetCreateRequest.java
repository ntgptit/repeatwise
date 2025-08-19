package com.spacedlearning.dto.set;

import com.spacedlearning.entity.enums.SetCategory;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningSetCreateRequest {

    @NotBlank(message = "Set name is required")
    @Size(max = 100, message = "Set name must not exceed 100 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Category is required")
    private SetCategory category;

    @NotNull(message = "Word count is required")
    @Min(value = 1, message = "Word count must be greater than 0")
    private Integer wordCount;
}
