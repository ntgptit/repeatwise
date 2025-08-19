package com.spacedlearning.dto.reminder;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RemindScheduleCreateRequest {

    @NotNull(message = "Set ID is required")
    private UUID setId;

    @NotNull(message = "Remind date is required")
    private LocalDate remindDate;
}
