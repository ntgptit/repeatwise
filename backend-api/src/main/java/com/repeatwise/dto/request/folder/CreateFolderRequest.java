package com.repeatwise.dto.request.folder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for creating a folder
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFolderRequest {

    @NotBlank(message = "{error.folder.name.required}")
    @Size(min = 1, max = 100, message = "{error.folder.name.size}")
    private String name;

    @Size(max = 500, message = "{error.folder.description.too.long}")
    private String description;

    private UUID parentFolderId;
}
