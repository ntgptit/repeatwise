package com.spacedlearning.service;

import com.spacedlearning.dto.reminder.RemindScheduleCreateRequest;
import com.spacedlearning.dto.reminder.RemindScheduleResponse;
import com.spacedlearning.dto.reminder.RemindScheduleUpdateRequest;
import com.spacedlearning.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface RemindScheduleService {

    // CRUD operations
    RemindScheduleResponse createReminder(RemindScheduleCreateRequest request, User user);
    
    RemindScheduleResponse updateReminder(UUID reminderId, RemindScheduleUpdateRequest request, User user);
    
    void deleteReminder(UUID reminderId, User user);
    
    RemindScheduleResponse getReminder(UUID reminderId, User user);
    
    List<RemindScheduleResponse> getRemindersBySet(UUID setId, User user);
    
    List<RemindScheduleResponse> getRemindersByDate(LocalDate date, User user);
    
    // Business operations
    void markReminderAsSent(UUID reminderId, User user);
    
    void markReminderAsDone(UUID reminderId, User user);
    
    void markReminderAsSkipped(UUID reminderId, User user);
    
    boolean canReschedule(UUID reminderId, User user);
    
    void rescheduleReminder(UUID reminderId, LocalDate newDate, User user);
    
    // Scheduling operations
    List<RemindScheduleResponse> getOverdueReminders(User user);
    
    List<RemindScheduleResponse> getRemindersToSendToday();
    
    void handleOverload(User user, LocalDate date);
}
