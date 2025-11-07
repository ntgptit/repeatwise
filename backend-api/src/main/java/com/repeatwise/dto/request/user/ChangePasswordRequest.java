package com.repeatwise.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for change password request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {

    @NotBlank(message = "{error.user.password.current.required}")
    private String currentPassword;

    @NotBlank(message = "{error.user.password.required}")
    @Size(min = 8, max = 128, message = "{error.user.password.length}")
    private String newPassword;
}
