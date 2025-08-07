package com.repeatwise.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class UserDto extends BaseDto {

    @NotBlank(message = "Username is required")
    @Size(min = 1, max = 64, message = "Username must be between 1 and 64 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 128, message = "Email must not exceed 128 characters")
    private String email;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 128, message = "Name must be between 2 and 128 characters")
    private String name;

    public static class CreateRequest {
        @NotBlank(message = "Username is required")
        @Size(min = 1, max = 64, message = "Username must be between 1 and 64 characters")
        private String username;

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 128, message = "Email must not exceed 128 characters")
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        private String password;

        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class UpdateRequest {
        @Size(min = 1, max = 64, message = "Username must be between 1 and 64 characters")
        private String username;

        @Email(message = "Email must be valid")
        @Size(max = 128, message = "Email must not exceed 128 characters")
        private String email;

        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    @Data
    @SuperBuilder
    @EqualsAndHashCode(callSuper = true)
    public static class Response extends UserDto {
    }

    public static UserDto fromUser(com.repeatwise.model.User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .name(user.getName())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
} 