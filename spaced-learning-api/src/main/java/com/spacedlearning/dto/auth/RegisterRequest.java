package com.spacedlearning.dto.auth;

import com.spacedlearning.entity.User.PreferredLanguage;
import com.spacedlearning.validation.ValidPassword;
import com.spacedlearning.validation.ValidPasswordMatch;
import com.spacedlearning.validation.ValidTimezone;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * DTO for user registration based on UC-001: User Registration
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ValidPasswordMatch
public class RegisterRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @ValidPassword
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name must not exceed 100 characters")
    private String fullName;

    @NotNull(message = "Preferred language is required")
    private PreferredLanguage preferredLanguage;

    @NotBlank(message = "Timezone is required")
    @ValidTimezone
    private String timezone;

    @NotNull(message = "Default reminder time is required")
    private LocalTime defaultReminderTime;

    /**
     * Validate that password and confirm password match
     */
    public boolean isPasswordMatch() {
        return password != null && password.equals(confirmPassword);
    }
}
