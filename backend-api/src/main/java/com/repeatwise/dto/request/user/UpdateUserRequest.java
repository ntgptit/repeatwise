package com.repeatwise.dto.request.user;

import com.repeatwise.entity.enums.Language;
import com.repeatwise.entity.enums.Theme;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating user profile
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    private String name;

    @Pattern(regexp = "^[a-z0-9_]{3,30}$", message = "Username must be 3-30 characters, lowercase alphanumeric and underscore only")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    private String username;

    @Size(max = 50, message = "Timezone must not exceed 50 characters")
    private String timezone;

    private Language language;

    private Theme theme;
}
