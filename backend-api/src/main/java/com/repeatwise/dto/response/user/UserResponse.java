package com.repeatwise.dto.response.user;

import java.time.LocalDateTime;
import java.util.UUID;

import com.repeatwise.entity.enums.Language;
import com.repeatwise.entity.enums.Theme;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user profile response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private UUID id;
    private String email;
    private String username;
    private String name;
    private String timezone;
    private Language language;
    private Theme theme;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
