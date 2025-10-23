package com.repeatwise.event.listener;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.repeatwise.log.LogEvent;

/**
 * Notification Event Listener - Responds to domain events and triggers notifications
 *
 * Requirements:
 * - UC-024: Future notification types (streak reminders, achievements)
 * - Event-driven architecture for decoupled notification triggering
 *
 * Events Handled (Future):
 * - StreakAchievedEvent: User maintains study streak (7, 30, 100 days)
 * - MilestoneReachedEvent: User learns 100, 500, 1000 cards
 * - ReviewCompletedEvent: Daily review session completed
 *
 * MVP Status:
 * - Placeholder for future implementation
 * - Daily reminders use scheduled job (NotificationScheduler)
 * - Event-driven notifications planned for post-MVP
 *
 * @author RepeatWise Team
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationListener {

    // TODO: Future implementation
    // private final INotificationService notificationService;

    /**
     * Handle streak achievement events
     *
     * Future Feature: UC-024 BR-076 - STREAK_REMINDER notification type
     * Trigger: User maintains study streak for milestone days (7, 30, 100)
     *
     * Notification:
     * - Type: STREAK_REMINDER
     * - Subject: "=% [RepeatWise] Congratulations on your X-day streak!"
     * - Body: Motivational message with streak stats
     *
     * @param event StreakAchievedEvent containing user ID and streak days
     */
    @Async
    @EventListener
    public void handleStreakAchieved(final Object event) {
        // TODO: Implement when StreakAchievedEvent is defined
        log.debug("event={} Streak achieved event received (future implementation)", LogEvent.START);
    }

    /**
     * Handle milestone achievement events
     *
     * Future Feature: UC-024 BR-076 - ACHIEVEMENT notification type
     * Trigger: User reaches card learning milestones (100, 500, 1000 cards)
     *
     * Notification:
     * - Type: ACHIEVEMENT
     * - Subject: "<‰ [RepeatWise] You've learned X cards!"
     * - Body: Achievement badge with progress stats
     *
     * @param event MilestoneReachedEvent containing user ID and card count
     */
    @Async
    @EventListener
    public void handleMilestoneReached(final Object event) {
        // TODO: Implement when MilestoneReachedEvent is defined
        log.debug("event={} Milestone reached event received (future implementation)", LogEvent.START);
    }

    /**
     * Handle review completion events
     *
     * Future Feature: Optional post-review summary notification
     * Trigger: User completes daily review session
     *
     * Notification:
     * - Type: DAILY_REMINDER (optional variant)
     * - Subject: " [RepeatWise] Today's review complete!"
     * - Body: Session summary with progress stats
     *
     * @param event ReviewCompletedEvent containing user ID and review stats
     */
    @Async
    @EventListener
    public void handleReviewCompleted(final Object event) {
        // TODO: Implement when ReviewCompletedEvent is defined
        log.debug("event={} Review completed event received (future implementation)", LogEvent.START);
    }

    /**
     * Handle system announcement events
     *
     * Future Feature: UC-024 BR-076 - SYSTEM notification type
     * Trigger: Admin creates system-wide announcement
     *
     * Notification:
     * - Type: SYSTEM
     * - Subject: "[RepeatWise] Important Announcement"
     * - Body: Admin-defined message (maintenance, new features, etc.)
     *
     * @param event SystemAnnouncementEvent containing announcement details
     */
    @Async
    @EventListener
    public void handleSystemAnnouncement(final Object event) {
        // TODO: Implement when SystemAnnouncementEvent is defined
        log.debug("event={} System announcement event received (future implementation)", LogEvent.START);
    }
}

