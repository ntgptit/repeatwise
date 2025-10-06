package com.spacedlearning.dto.user;

import java.time.LocalTime;

import com.spacedlearning.entity.enums.PreferredLanguage;
import com.spacedlearning.validation.ValidPassword;
import com.spacedlearning.validation.ValidTimezone;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user registration request
 * Contains all necessary fields for creating a new user account
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @ValidPassword(message = "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
    @Size(max = 255, message = "Password must not exceed 255 characters")
    private String password;

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    @Pattern(regexp = "^[a-zA-ZÀ-ỹ\\s]+$", message = "Full name can only contain letters and spaces")
    private String fullName;

    @NotNull(message = "Preferred language is required")
    private PreferredLanguage preferredLanguage;

    @NotBlank(message = "Timezone is required")
    @ValidTimezone(message = "Timezone must be one of: Asia/Ho_Chi_Minh, UTC, America/New_York, Europe/London")
    @Builder.Default
    private String timezone = "Asia/Ho_Chi_Minh";

    @NotNull(message = "Default reminder time is required")
    @Builder.Default
    private LocalTime defaultReminderTime = LocalTime.of(9, 0);
}
