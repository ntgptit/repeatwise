package com.spacedlearning.dto.reminder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.spacedlearning.entity.enums.RemindStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for reminder response
 * Contains reminder schedule data returned after successful creation or retrieval
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReminderResponse {

    private UUID id;
    
    private UUID setId;
    
    private UUID userId;
    
    private LocalDate remindDate;
    
    private RemindStatus status;
    
    private Integer rescheduleCount;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
