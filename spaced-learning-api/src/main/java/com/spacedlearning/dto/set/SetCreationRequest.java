package com.spacedlearning.dto.set;

import com.spacedlearning.entity.enums.SetCategory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for learning set creation request
 * Contains all necessary fields for creating a new learning set
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetCreationRequest {

    @NotBlank(message = "Set name is required")
    @Size(min = 1, max = 100, message = "Set name must be between 1 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Category is required")
    private SetCategory category;
}
