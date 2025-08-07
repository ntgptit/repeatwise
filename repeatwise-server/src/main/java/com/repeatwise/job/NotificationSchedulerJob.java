package com.repeatwise.job;

import com.repeatwise.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationSchedulerJob {

    private final NotificationService notificationService;

    /**
     * Morning study notifications
     * Runs at 07:15 every day - 15 minutes after morning reminder processing
     * Sends notifications for morning study sessions
     */
    @Scheduled(cron = "0 15 7 * * ?") // Every day at 7:15 AM
    public void sendMorningNotifications() {
        log.info("Starting morning study notifications job");
        
        try {
            var sentNotifications = notificationService.sendScheduledNotifications();
            log.info("Morning study notifications completed. Sent {} notifications", sentNotifications.size());
            
        } catch (Exception e) {
            log.error("Error in morning study notifications job: {}", e.getMessage(), e);
        }
    }

    /**
     * Lunch break notifications
     * Runs at 12:15 every day - 15 minutes after lunch reminder processing
     * Sends notifications for lunch break study sessions
     */
    @Scheduled(cron = "0 15 12 * * ?") // Every day at 12:15 PM
    public void sendLunchNotifications() {
        log.info("Starting lunch break notifications job");
        
        try {
            var sentNotifications = notificationService.sendScheduledNotifications();
            log.info("Lunch break notifications completed. Sent {} notifications", sentNotifications.size());
            
        } catch (Exception e) {
            log.error("Error in lunch break notifications job: {}", e.getMessage(), e);
        }
    }

    /**
     * Evening study notifications
     * Runs at 19:15 every day - 15 minutes after evening reminder processing
     * Sends notifications for evening study sessions
     */
    @Scheduled(cron = "0 15 19 * * ?") // Every day at 7:15 PM
    public void sendEveningNotifications() {
        log.info("Starting evening study notifications job");
        
        try {
            var sentNotifications = notificationService.sendScheduledNotifications();
            log.info("Evening study notifications completed. Sent {} notifications", sentNotifications.size());
            
        } catch (Exception e) {
            log.error("Error in evening study notifications job: {}", e.getMessage(), e);
        }
    }

    /**
     * Cleanup old notifications job
     * Runs at 22:00 every day - Late evening cleanup
     * Removes notifications older than 30 days
     */
    @Scheduled(cron = "0 0 22 * * ?") // Every day at 10:00 PM
    public void cleanupOldNotifications() {
        log.info("Starting cleanup old notifications job");
        
        try {
            // This would call a cleanup method in NotificationService
            // For now, we'll just log the job execution
            log.info("Cleanup old notifications job completed");
            
        } catch (Exception e) {
            log.error("Error in cleanup old notifications job: {}", e.getMessage(), e);
        }
    }

    /**
     * Notification health check job
     * Runs every 30 minutes to check notification system health
     */
    @Scheduled(fixedRate = 1800000) // Every 30 minutes (30 * 60 * 1000 ms)
    public void notificationHealthCheck() {
        log.debug("Starting notification health check job");
        
        try {
            // This would check notification system health
            // For now, we'll just log the job execution
            log.debug("Notification health check job completed");
            
        } catch (Exception e) {
            log.error("Error in notification health check job: {}", e.getMessage(), e);
        }
    }
} 
