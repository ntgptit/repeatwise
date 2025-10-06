# Notification Management Module - Detail Design

## 1. Module Overview

### 1.1 Objectives
Notification Management Module xử lý tất cả các hoạt động liên quan đến thông báo và nhắc nhở bao gồm:
- Quản lý preferences thông báo của user
- Gửi thông báo nhắc nhở ôn tập
- Gửi thông báo email và push notification
- Lên lịch và quản lý reminder
- Theo dõi trạng thái gửi thông báo

### 1.2 Scope
- **In Scope**: Notification preferences, reminder scheduling, email/push notifications, notification history
- **Out of Scope**: Learning set management, Review sessions (handled by other modules)

### 1.3 Dependencies
- **Database**: notification_preferences table, notifications table, reminder_schedules table
- **External Services**: Email Service, Push Notification Service (FCM), Scheduler Service
- **Security**: User authorization, notification permissions

## 2. API Contracts

### 2.1 Notification Preferences Management

#### GET /api/v1/notifications/preferences
**Response:**
```json
{
  "success": true,
  "data": {
    "userId": "uuid-here",
    "emailNotifications": {
      "enabled": true,
      "reviewReminders": true,
      "weeklyProgress": true,
      "systemUpdates": false
    },
    "pushNotifications": {
      "enabled": true,
      "reviewReminders": true,
      "dailyGoals": true,
      "achievements": true
    },
    "reminderSettings": {
      "defaultTime": "09:00",
      "timezone": "Asia/Ho_Chi_Minh",
      "frequency": "daily",
      "weekendReminders": true
    },
    "quietHours": {
      "enabled": true,
      "startTime": "22:00",
      "endTime": "07:00"
    },
    "updatedAt": "2024-12-19T10:00:00Z"
  }
}
```

#### PUT /api/v1/notifications/preferences
**Request:**
```json
{
  "emailNotifications": {
    "enabled": true,
    "reviewReminders": true,
    "weeklyProgress": false,
    "systemUpdates": false
  },
  "pushNotifications": {
    "enabled": true,
    "reviewReminders": true,
    "dailyGoals": true,
    "achievements": false
  },
  "reminderSettings": {
    "defaultTime": "08:00",
    "timezone": "Asia/Ho_Chi_Minh",
    "frequency": "daily",
    "weekendReminders": false
  },
  "quietHours": {
    "enabled": true,
    "startTime": "23:00",
    "endTime": "08:00"
  }
}
```

### 2.2 Reminder Management

#### POST /api/v1/reminders
**Request:**
```json
{
  "cycleId": "uuid-here",
  "reminderType": "review_due",
  "scheduledAt": "2024-12-20T09:00:00Z",
  "timezone": "Asia/Ho_Chi_Minh",
  "channels": ["email", "push"]
}
```

**Response (Success - 201):**
```json
{
  "success": true,
  "message": "Reminder đã được lên lịch",
  "data": {
    "reminderId": "uuid-here",
    "cycleId": "uuid-here",
    "reminderType": "review_due",
    "scheduledAt": "2024-12-20T09:00:00Z",
    "timezone": "Asia/Ho_Chi_Minh",
    "channels": ["email", "push"],
    "status": "scheduled",
    "createdAt": "2024-12-19T10:00:00Z"
  }
}
```

#### GET /api/v1/reminders
**Query Parameters:**
- `status`: Filter by status (scheduled, sent, failed, cancelled)
- `reminderType`: Filter by type
- `from`: Start date
- `to`: End date

**Response:**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "reminderId": "uuid-here",
        "cycleId": "uuid-here",
        "setTitle": "Từ vựng tiếng Anh cơ bản",
        "reminderType": "review_due",
        "scheduledAt": "2024-12-20T09:00:00Z",
        "channels": ["email", "push"],
        "status": "scheduled",
        "createdAt": "2024-12-19T10:00:00Z"
      }
    ],
    "pagination": {
      "page": 1,
      "size": 20,
      "totalElements": 1,
      "totalPages": 1
    }
  }
}
```

#### PUT /api/v1/reminders/{reminderId}/reschedule
**Request:**
```json
{
  "newScheduledAt": "2024-12-20T10:00:00Z",
  "reason": "user_request"
}
```

#### DELETE /api/v1/reminders/{reminderId}
**Response:**
```json
{
  "success": true,
  "message": "Reminder đã được hủy"
}
```

### 2.3 Notification History

#### GET /api/v1/notifications/history
**Query Parameters:**
- `type`: Filter by notification type
- `channel`: Filter by channel (email, push)
- `status`: Filter by status (sent, failed, delivered)
- `from`: Start date
- `to`: End date
- `page`: Page number
- `size`: Page size

**Response:**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "notificationId": "uuid-here",
        "type": "review_reminder",
        "channel": "email",
        "title": "Thời gian ôn tập đã đến",
        "content": "Bạn có 5 items cần ôn tập trong set 'Từ vựng tiếng Anh cơ bản'",
        "status": "delivered",
        "sentAt": "2024-12-19T09:00:00Z",
        "deliveredAt": "2024-12-19T09:00:05Z",
        "openedAt": "2024-12-19T09:15:00Z"
      }
    ],
    "pagination": {
      "page": 1,
      "size": 20,
      "totalElements": 1,
      "totalPages": 1
    }
  }
}
```

## 3. Data Models

### 3.1 Notification Preferences Entity
```java
@Entity
@Table(name = "notification_preferences")
public class NotificationPreferences {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID preferenceId;
    
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    // Email preferences
    @Column(nullable = false)
    private Boolean emailEnabled;
    
    @Column(nullable = false)
    private Boolean emailReviewReminders;
    
    @Column(nullable = false)
    private Boolean emailWeeklyProgress;
    
    @Column(nullable = false)
    private Boolean emailSystemUpdates;
    
    // Push notification preferences
    @Column(nullable = false)
    private Boolean pushEnabled;
    
    @Column(nullable = false)
    private Boolean pushReviewReminders;
    
    @Column(nullable = false)
    private Boolean pushDailyGoals;
    
    @Column(nullable = false)
    private Boolean pushAchievements;
    
    // Reminder settings
    @Column(nullable = false)
    private String defaultReminderTime;
    
    @Column(nullable = false)
    private String timezone;
    
    @Enumerated(EnumType.STRING)
    private ReminderFrequency frequency;
    
    @Column(nullable = false)
    private Boolean weekendReminders;
    
    // Quiet hours
    @Column(nullable = false)
    private Boolean quietHoursEnabled;
    
    private String quietHoursStart;
    
    private String quietHoursEnd;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
```

### 3.2 Reminder Schedule Entity
```java
@Entity
@Table(name = "reminder_schedules")
public class ReminderSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID reminderId;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "cycle_id")
    private LearningCycle learningCycle;
    
    @Enumerated(EnumType.STRING)
    private ReminderType reminderType;
    
    @Column(nullable = false)
    private LocalDateTime scheduledAt;
    
    @Column(nullable = false)
    private String timezone;
    
    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "reminder_channels", joinColumns = @JoinColumn(name = "reminder_id"))
    @Column(name = "channel")
    private Set<NotificationChannel> channels;
    
    @Enumerated(EnumType.STRING)
    private ReminderStatus status;
    
    private String failureReason;
    
    private LocalDateTime sentAt;
    
    private LocalDateTime cancelledAt;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
```

### 3.3 Notification Entity
```java
@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID notificationId;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "reminder_id")
    private ReminderSchedule reminderSchedule;
    
    @Enumerated(EnumType.STRING)
    private NotificationType type;
    
    @Enumerated(EnumType.STRING)
    private NotificationChannel channel;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false, length = 2000)
    private String content;
    
    @Column(length = 1000)
    private String actionUrl;
    
    @Enumerated(EnumType.STRING)
    private NotificationStatus status;
    
    private String externalId; // ID from email/push service
    
    private String failureReason;
    
    private LocalDateTime sentAt;
    
    private LocalDateTime deliveredAt;
    
    private LocalDateTime openedAt;
    
    private LocalDateTime clickedAt;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
```

## 4. Business Logic

### 4.1 Update Notification Preferences Logic
```pseudocode
FUNCTION updateNotificationPreferences(userId, preferencesRequest):
    // Get or create preferences
    preferences = notificationPreferencesRepository.findByUserId(userId)
    IF preferences IS NULL:
        preferences = new NotificationPreferences()
        preferences.user = userRepository.findById(userId)
    
    // Update email preferences
    IF preferencesRequest.emailNotifications IS NOT NULL:
        preferences.emailEnabled = preferencesRequest.emailNotifications.enabled
        preferences.emailReviewReminders = preferencesRequest.emailNotifications.reviewReminders
        preferences.emailWeeklyProgress = preferencesRequest.emailNotifications.weeklyProgress
        preferences.emailSystemUpdates = preferencesRequest.emailNotifications.systemUpdates
    
    // Update push preferences
    IF preferencesRequest.pushNotifications IS NOT NULL:
        preferences.pushEnabled = preferencesRequest.pushNotifications.enabled
        preferences.pushReviewReminders = preferencesRequest.pushNotifications.reviewReminders
        preferences.pushDailyGoals = preferencesRequest.pushNotifications.dailyGoals
        preferences.pushAchievements = preferencesRequest.pushNotifications.achievements
    
    // Update reminder settings
    IF preferencesRequest.reminderSettings IS NOT NULL:
        preferences.defaultReminderTime = preferencesRequest.reminderSettings.defaultTime
        preferences.timezone = preferencesRequest.reminderSettings.timezone
        preferences.frequency = preferencesRequest.reminderSettings.frequency
        preferences.weekendReminders = preferencesRequest.reminderSettings.weekendReminders
    
    // Update quiet hours
    IF preferencesRequest.quietHours IS NOT NULL:
        preferences.quietHoursEnabled = preferencesRequest.quietHours.enabled
        preferences.quietHoursStart = preferencesRequest.quietHours.startTime
        preferences.quietHoursEnd = preferencesRequest.quietHours.endTime
    
    preferences.updatedAt = now()
    
    // Save preferences
    savedPreferences = notificationPreferencesRepository.save(preferences)
    
    // Update existing reminders if settings changed
    updateExistingReminders(userId, savedPreferences)
    
    RETURN successResponse(mapToPreferencesDto(savedPreferences))
```

### 4.2 Schedule Reminder Logic
```pseudocode
FUNCTION scheduleReminder(userId, scheduleRequest):
    // Validate input
    IF NOT validateScheduleRequest(scheduleRequest):
        RETURN validationError
    
    // Get user preferences
    preferences = notificationPreferencesRepository.findByUserId(userId)
    IF preferences IS NULL:
        RETURN preferencesNotFoundError
    
    // Check if reminder is within quiet hours
    IF isWithinQuietHours(scheduleRequest.scheduledAt, preferences):
        RETURN quietHoursError
    
    // Create reminder schedule
    reminder = new ReminderSchedule()
    reminder.user = userRepository.findById(userId)
    reminder.learningCycle = learningCycleRepository.findById(scheduleRequest.cycleId)
    reminder.reminderType = scheduleRequest.reminderType
    reminder.scheduledAt = scheduleRequest.scheduledAt
    reminder.timezone = scheduleRequest.timezone
    reminder.channels = scheduleRequest.channels
    reminder.status = SCHEDULED
    reminder.createdAt = now()
    reminder.updatedAt = now()
    
    // Save reminder
    savedReminder = reminderScheduleRepository.save(reminder)
    
    // Schedule with external scheduler service
    scheduleWithExternalService(savedReminder)
    
    RETURN successResponse(mapToReminderDto(savedReminder))
```

### 4.3 Send Notification Logic
```pseudocode
FUNCTION sendNotification(reminderId):
    // Get reminder
    reminder = reminderScheduleRepository.findById(reminderId)
    IF reminder IS NULL:
        RETURN reminderNotFoundError
    
    // Get user preferences
    preferences = notificationPreferencesRepository.findByUserId(reminder.user.getUserId())
    
    // Check if user has disabled notifications
    IF NOT preferences.emailEnabled AND NOT preferences.pushEnabled:
        reminder.status = CANCELLED
        reminderScheduleRepository.save(reminder)
        RETURN userDisabledNotificationsError
    
    // Create notification records
    FOR EACH channel IN reminder.channels:
        // Check if channel is enabled for user
        IF NOT isChannelEnabled(channel, preferences):
            CONTINUE
        
        // Create notification
        notification = new Notification()
        notification.user = reminder.user
        notification.reminderSchedule = reminder
        notification.type = mapReminderTypeToNotificationType(reminder.reminderType)
        notification.channel = channel
        notification.title = generateNotificationTitle(reminder)
        notification.content = generateNotificationContent(reminder)
        notification.actionUrl = generateActionUrl(reminder)
        notification.status = PENDING
        notification.createdAt = now()
        
        savedNotification = notificationRepository.save(notification)
        
        // Send via appropriate channel
        IF channel == EMAIL:
            sendEmailNotification(savedNotification)
        ELSE IF channel == PUSH:
            sendPushNotification(savedNotification)
    
    // Update reminder status
    reminder.status = SENT
    reminder.sentAt = now()
    reminderScheduleRepository.save(reminder)
    
    RETURN successResponse("Notifications sent successfully")
```

### 4.4 Generate Notification Content Logic
```pseudocode
FUNCTION generateNotificationContent(reminder):
    user = reminder.user
    cycle = reminder.learningCycle
    set = cycle.learningSet
    
    SWITCH reminder.reminderType:
        CASE REVIEW_DUE:
            pendingItems = getPendingReviewItems(user.getUserId(), cycle.getSetId())
            RETURN "Bạn có " + pendingItems.size() + " items cần ôn tập trong set '" + set.getTitle() + "'. Hãy bắt đầu phiên ôn tập ngay!"
        
        CASE DAILY_GOAL:
            todayProgress = getTodayProgress(user.getUserId())
            goal = cycle.getDailyGoal()
            RETURN "Mục tiêu hôm nay: " + todayProgress + "/" + goal + " items. Hãy tiếp tục học tập để đạt mục tiêu!"
        
        CASE WEEKLY_PROGRESS:
            weekProgress = getWeeklyProgress(user.getUserId())
            RETURN "Tuần này bạn đã hoàn thành " + weekProgress.completedSessions + " phiên ôn tập với độ chính xác " + weekProgress.accuracy + "%. Tuyệt vời!"
        
        CASE ACHIEVEMENT:
            achievement = getLatestAchievement(user.getUserId())
            RETURN "Chúc mừng! Bạn đã đạt được thành tựu: " + achievement.title + " - " + achievement.description
        
        DEFAULT:
            RETURN "Thông báo từ RepeatWise"
```

## 5. Validation Rules

### 5.1 Notification Preferences Validation
- **Email Settings**: Boolean values
- **Push Settings**: Boolean values
- **Default Time**: HH:MM format, 24-hour
- **Timezone**: Valid IANA timezone identifier
- **Frequency**: Must be valid enum value
- **Quiet Hours**: Start/end time in HH:MM format

### 5.2 Reminder Validation
- **ScheduledAt**: Must be in the future
- **Timezone**: Valid IANA timezone identifier
- **Channels**: Must be valid enum values
- **ReminderType**: Must be valid enum value

### 5.3 Business Rules
- Users can have maximum 10 active reminders
- Reminders cannot be scheduled during quiet hours
- Email notifications require valid email address
- Push notifications require device token

## 6. Error Handling

### 6.1 Error Codes
- `NOTIF_001`: Notification preferences not found
- `NOTIF_002`: Reminder not found
- `NOTIF_003`: Invalid reminder time
- `NOTIF_004`: Reminder limit exceeded
- `NOTIF_005`: Quiet hours violation
- `NOTIF_006`: Channel not enabled
- `NOTIF_007`: External service error
- `NOTIF_008`: Invalid timezone

## 7. Security Considerations

### 7.1 Data Privacy
- Never log notification content
- Encrypt sensitive notification data
- Implement data retention policies
- Respect user privacy preferences

### 7.2 Rate Limiting
- Limit reminder creation to 10 per hour per user
- Implement notification throttling
- Prevent spam notifications

## 8. Observability

### 8.1 Logging
```java
// Log notification sent
log.info("Notification sent", 
    "notificationId", notification.getNotificationId(), 
    "userId", notification.getUser().getUserId(),
    "channel", notification.getChannel(),
    "type", notification.getType());

// Log notification failure
log.error("Notification failed", 
    "notificationId", notification.getNotificationId(), 
    "error", error.getMessage(),
    "channel", notification.getChannel());
```

### 8.2 Metrics
- Notifications sent per day
- Delivery success rate by channel
- User engagement rates
- Reminder scheduling frequency

### 8.3 Alerts
- High notification failure rate (>5%)
- External service downtime
- Unusual notification patterns
- User complaint spikes

## 9. Testing Strategy

### 9.1 Unit Tests
- Content generation logic
- Scheduling algorithms
- Validation rules
- Business logic functions

### 9.2 Integration Tests
- External service integration
- Database operations
- API endpoint testing
- End-to-end notification flow

### 9.3 Performance Tests
- High-volume notification sending
- Concurrent reminder scheduling
- External service response times
- Database query performance

## 10. Dependencies

### 10.1 Internal Dependencies
- `NotificationPreferencesRepository`: Preferences management
- `ReminderScheduleRepository`: Reminder scheduling
- `NotificationRepository`: Notification tracking
- `UserService`: User validation
- `LearningCycleService`: Cycle information

### 10.2 External Dependencies
- Email Service (SendGrid/AWS SES)
- Push Notification Service (Firebase Cloud Messaging)
- Scheduler Service (Quartz/Spring Scheduler)
- Database (PostgreSQL)

### 10.3 Configuration
```yaml
notification:
  limits:
    max-reminders-per-user: 10
    max-notifications-per-hour: 100
  channels:
    email:
      provider: sendgrid
      rate-limit: 1000
    push:
      provider: fcm
      rate-limit: 500
  scheduling:
    max-future-days: 30
    retry-attempts: 3
    retry-delay: 300
```

---

**Document Version**: 1.0  
**Last Updated**: 2024-12-19  
**Next Review**: 2024-12-26  
**Owner**: Backend Team  
**Stakeholders**: Development Team, QA Team, DevOps Team
