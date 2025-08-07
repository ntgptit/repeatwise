package com.repeatwise.job;

import com.repeatwise.service.ReminderSchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReminderSchedulerJob {

    private final ReminderSchedulerService reminderSchedulerService;

    /**
     * Morning study reminder processing job
     * Runs at 07:00 every day - Good time for morning study
     * Processes all pending reminders for the current day
     */
    @Scheduled(cron = "0 0 7 * * ?") // Every day at 7:00 AM
    public void processMorningReminders() {
        log.info("Starting morning reminder processing job");
        
        try {
            LocalDate today = LocalDate.now();
            var processedReminders = reminderSchedulerService.processDailyReminders(today);
            
            log.info("Morning reminder processing completed. Processed {} reminders", processedReminders.size());
            
        } catch (Exception e) {
            log.error("Error in morning reminder processing job: {}", e.getMessage(), e);
        }
    }

    /**
     * Lunch break reminder processing job
     * Runs at 12:00 every day - Good time during lunch break
     * Processes reminders for users who prefer lunch time study
     */
    @Scheduled(cron = "0 0 12 * * ?") // Every day at 12:00 PM
    public void processLunchReminders() {
        log.info("Starting lunch break reminder processing job");
        
        try {
            LocalDate today = LocalDate.now();
            var processedReminders = reminderSchedulerService.processDailyReminders(today);
            
            log.info("Lunch break reminder processing completed. Processed {} reminders", processedReminders.size());
            
        } catch (Exception e) {
            log.error("Error in lunch break reminder processing job: {}", e.getMessage(), e);
        }
    }

    /**
     * Evening study reminder processing job
     * Runs at 19:00 every day - Good time after work/dinner
     * Processes reminders for evening study sessions
     */
    @Scheduled(cron = "0 0 19 * * ?") // Every day at 7:00 PM
    public void processEveningReminders() {
        log.info("Starting evening reminder processing job");
        
        try {
            LocalDate today = LocalDate.now();
            var processedReminders = reminderSchedulerService.processDailyReminders(today);
            
            log.info("Evening reminder processing completed. Processed {} reminders", processedReminders.size());
            
        } catch (Exception e) {
            log.error("Error in evening reminder processing job: {}", e.getMessage(), e);
        }
    }

    /**
     * Auto-reschedule overflow reminders job
     * Runs at 20:00 every day (after evening processing)
     * Handles reminders that exceed daily limits
     */
    @Scheduled(cron = "0 0 20 * * ?") // Every day at 8:00 PM
    public void autoRescheduleOverflowReminders() {
        log.info("Starting auto-reschedule overflow reminders job");
        
        try {
            LocalDate today = LocalDate.now();
            var rescheduledReminders = reminderSchedulerService.autoRescheduleOverflowReminders(today);
            
            log.info("Auto-reschedule overflow reminders completed. Rescheduled {} reminders", rescheduledReminders.size());
            
        } catch (Exception e) {
            log.error("Error in auto-reschedule overflow reminders job: {}", e.getMessage(), e);
        }
    }

    /**
     * Health check job
     * Runs every hour to check system health
     */
    @Scheduled(fixedRate = 3600000) // Every hour (60 * 60 * 1000 ms)
    public void healthCheck() {
        log.debug("Starting health check job");
        
        try {
            LocalDate today = LocalDate.now();
            var stats = reminderSchedulerService.getDailyReminderStatistics(today);
            
            log.info("Health check - Daily reminder statistics: Pending={}, Sent={}, Completed={}, Users={}", 
                    stats.totalPending(), stats.totalSent(), stats.totalCompleted(), stats.usersWithReminders());
            
        } catch (Exception e) {
            log.error("Error in health check job: {}", e.getMessage(), e);
        }
    }
} 
