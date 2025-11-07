package com.repeatwise.dto.request.folder;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating a folder
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFolderRequest {

    @Size(min = 1, max = 100, message = "{error.folder.name.size}")
    private String name;

    @Size(max = 500, message = "{error.folder.description.too.long}")
    private String description;
}
