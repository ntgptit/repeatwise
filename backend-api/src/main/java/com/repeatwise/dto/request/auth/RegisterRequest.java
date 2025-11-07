package com.repeatwise.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user registration request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @Email(message = "{error.user.email.invalid}")
    @NotBlank(message = "{error.user.email.required}")
    @Size(max = 255, message = "{error.user.email.too.long}")
    private String email;

    @Pattern(regexp = "^[a-z0-9_]{3,30}$", message = "{error.user.username.invalid}")
    @Size(min = 3, max = 30, message = "{error.user.username.length}")
    private String username;

    @NotBlank(message = "{error.user.password.required}")
    @Size(min = 8, max = 128, message = "{error.user.password.length}")
    private String password;

    @Size(max = 100, message = "{error.user.name.too.long}")
    private String name;
}
