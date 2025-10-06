package com.spacedlearning.dto.user;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import com.spacedlearning.entity.enums.PreferredLanguage;
import com.spacedlearning.entity.enums.UserStatus;
import com.spacedlearning.masking.Masked;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user registration response
 * Contains user data returned after successful registration
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationResponse {

    private UUID id;
    
    @Masked
    private String email;
    
    private String fullName;
    
    private PreferredLanguage preferredLanguage;
    
    private String timezone;
    
    private LocalTime defaultReminderTime;
    
    private UserStatus status;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
