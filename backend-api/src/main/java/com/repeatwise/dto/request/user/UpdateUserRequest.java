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

    @Size(min = 1, max = 100, message = "{error.user.name.length}")
    private String name;

    @Pattern(regexp = "^[a-z0-9_]{3,30}$", message = "{error.user.username.invalid}")
    @Size(min = 3, max = 30, message = "{error.user.username.length}")
    private String username;

    @Size(max = 50, message = "{error.user.timezone.length}")
    private String timezone;

    private Language language;

    private Theme theme;
}
