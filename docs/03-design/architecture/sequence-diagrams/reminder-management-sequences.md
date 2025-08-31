# Reminder Management Sequence Diagrams

## Tổng quan

Tài liệu này mô tả các luồng sequence cho quá trình quản lý reminder trong hệ thống RepeatWise, bao gồm tạo reminder, reschedule, mark done và overload prevention.

## 1. Create Reminder Sequence

### 1.1 Automatic Reminder Creation

```mermaid
sequenceDiagram
    participant SchedulerService as Scheduler Service
    participant ReminderService as Reminder Service
    participant CycleRepository as Cycle Repository
    participant ReminderRepository as Reminder Repository
    participant Database as PostgreSQL

    SchedulerService->>SchedulerService: Daily reminder calculation job
    
    SchedulerService->>CycleRepository: findActiveCyclesWithDueReviews()
    CycleRepository->>Database: SELECT * FROM cycles WHERE status = 'ACTIVE' AND next_review_date <= ?
    Database-->>CycleRepository: Active cycles with due reviews
    CycleRepository-->>SchedulerService: List<Cycle> entities
    
    loop For each cycle
        SchedulerService->>ReminderService: createReminderForCycle(cycle)
        
        ReminderService->>ReminderService: validateReminderCreation(cycle)
        ReminderService->>ReminderService: checkOverloadPrevention(cycle.userId)
        
        alt Overload detected
            ReminderService->>ReminderService: rescheduleReminder(cycle, nextAvailableDate)
        else No overload
            ReminderService->>ReminderService: createReminderEntity(cycle)
        end
        
        ReminderService->>ReminderRepository: save(reminder)
        ReminderRepository->>Database: INSERT INTO reminders (cycle_id, user_id, reminder_date, status, created_at)
        Database-->>ReminderRepository: Reminder created
        ReminderRepository-->>ReminderService: Reminder entity
    end
    
    SchedulerService->>SchedulerService: Log reminder creation summary
```

### 1.2 Manual Reminder Creation

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant ReminderController as Reminder Controller
    participant ReminderService as Reminder Service
    participant CycleRepository as Cycle Repository
    participant ReminderRepository as Reminder Repository
    participant Database as PostgreSQL

    MobileApp->>APIGateway: POST /api/reminders
    Note over MobileApp,APIGateway: Authorization: Bearer {accessToken}
    Note over MobileApp,APIGateway: {cycleId, reminderDate, priority}
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>ReminderController: Forward request
    
    ReminderController->>ReminderService: createReminder(reminderData, userId)
    
    ReminderService->>CycleRepository: findById(cycleId)
    CycleRepository->>Database: SELECT * FROM cycles WHERE id = ?
    Database-->>CycleRepository: Cycle data
    CycleRepository-->>ReminderService: Cycle entity
    
    ReminderService->>ReminderService: validateCycleOwnership(cycle, userId)
    ReminderService->>ReminderService: validateReminderDate(reminderDate)
    ReminderService->>ReminderService: checkOverloadPrevention(userId, reminderDate)
    
    ReminderService->>ReminderService: createReminderEntity(cycle, reminderDate, priority)
    ReminderService->>ReminderRepository: save(reminder)
    ReminderRepository->>Database: INSERT INTO reminders (cycle_id, user_id, reminder_date, priority, status, created_at)
    Database-->>ReminderRepository: Reminder created
    ReminderRepository-->>ReminderService: Reminder entity
    
    ReminderService-->>ReminderController: Reminder entity
    ReminderController-->>APIGateway: 201 Created + Reminder data
    APIGateway-->>MobileApp: 201 Created + Reminder data
```

## 2. Get Reminder List Sequence

### 2.1 Get User Reminders

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant ReminderController as Reminder Controller
    participant ReminderService as Reminder Service
    participant ReminderRepository as Reminder Repository
    participant Database as PostgreSQL

    MobileApp->>APIGateway: GET /api/reminders
    Note over MobileApp,APIGateway: Authorization: Bearer {accessToken}
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>ReminderController: Forward request
    
    ReminderController->>ReminderService: getRemindersByUserId(userId)
    
    ReminderService->>ReminderRepository: findByUserId(userId)
    ReminderRepository->>Database: SELECT * FROM reminders WHERE user_id = ? ORDER BY reminder_date ASC
    Database-->>ReminderRepository: List of reminders
    ReminderRepository-->>ReminderService: List<Reminder> entities
    
    ReminderService->>ReminderService: enrichReminderData(reminders)
    ReminderService-->>ReminderController: List<Reminder> entities
    ReminderController-->>APIGateway: 200 OK + Reminders data
    APIGateway-->>MobileApp: 200 OK + Reminders data
```

### 2.2 Get Reminders with Filters

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant ReminderController as Reminder Controller
    participant ReminderService as Reminder Service
    participant ReminderRepository as Reminder Repository
    participant Database as PostgreSQL

    MobileApp->>APIGateway: GET /api/reminders?status=pending&date=2024-01-15
    Note over MobileApp,APIGateway: Authorization: Bearer {accessToken}
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>ReminderController: Forward request
    
    ReminderController->>ReminderService: getRemindersByUserIdWithFilters(userId, filters)
    
    ReminderService->>ReminderService: validateFilters(filters)
    ReminderService->>ReminderRepository: findByUserIdAndFilters(userId, status, date)
    ReminderRepository->>Database: SELECT * FROM reminders WHERE user_id = ? AND status = ? AND DATE(reminder_date) = ?
    Database-->>ReminderRepository: Filtered reminders
    ReminderRepository-->>ReminderService: List<Reminder> entities
    
    ReminderService-->>ReminderController: List<Reminder> entities
    ReminderController-->>APIGateway: 200 OK + Reminders data
    APIGateway-->>MobileApp: 200 OK + Reminders data
```

## 3. Reschedule Reminder Sequence

### 3.1 Successful Reminder Reschedule

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant ReminderController as Reminder Controller
    participant ReminderService as Reminder Service
    participant ReminderRepository as Reminder Repository
    participant Database as PostgreSQL

    MobileApp->>APIGateway: POST /api/reminders/{reminderId}/reschedule
    Note over MobileApp,APIGateway: Authorization: Bearer {accessToken}
    Note over MobileApp,APIGateway: {newDateTime: "2024-01-16T10:00:00Z", reason: "busy"}
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>ReminderController: Forward request
    
    ReminderController->>ReminderService: rescheduleReminder(reminderId, newDateTime, reason, userId)
    
    ReminderService->>ReminderRepository: findById(reminderId)
    ReminderRepository->>Database: SELECT * FROM reminders WHERE id = ?
    Database-->>ReminderRepository: Reminder data
    ReminderRepository-->>ReminderService: Reminder entity
    
    ReminderService->>ReminderService: validateReminderOwnership(reminder, userId)
    ReminderService->>ReminderService: validateRescheduleTime(newDateTime)
    ReminderService->>ReminderService: checkOverloadPrevention(userId, newDateTime)
    
    ReminderService->>ReminderService: updateReminderDateTime(reminder, newDateTime)
    ReminderService->>ReminderService: logRescheduleHistory(reminder, reason)
    
    ReminderService->>ReminderRepository: update(reminder)
    ReminderRepository->>Database: UPDATE reminders SET reminder_date = ?, updated_at = ?
    Database-->>ReminderRepository: Reminder updated
    ReminderRepository-->>ReminderService: Updated reminder
    
    ReminderService-->>ReminderController: Reminder entity
    ReminderController-->>APIGateway: 200 OK + Reminder data
    APIGateway-->>MobileApp: 200 OK + Reminder data
```

### 3.2 Reschedule with Overload Prevention

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant ReminderController as Reminder Controller
    participant ReminderService as Reminder Service
    participant ReminderRepository as Reminder Repository
    participant Database as PostgreSQL

    MobileApp->>APIGateway: POST /api/reminders/{reminderId}/reschedule
    Note over MobileApp,APIGateway: Authorization: Bearer {accessToken}
    Note over MobileApp,APIGateway: {newDateTime: "2024-01-15T10:00:00Z"}
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>ReminderController: Forward request
    
    ReminderController->>ReminderService: rescheduleReminder(reminderId, newDateTime, reason, userId)
    
    ReminderService->>ReminderRepository: findById(reminderId)
    ReminderRepository->>Database: SELECT * FROM reminders WHERE id = ?
    Database-->>ReminderRepository: Reminder data
    ReminderRepository-->>ReminderService: Reminder entity
    
    ReminderService->>ReminderService: validateReminderOwnership(reminder, userId)
    ReminderService->>ReminderService: validateRescheduleTime(newDateTime)
    ReminderService->>ReminderService: checkOverloadPrevention(userId, newDateTime)
    Note over ReminderService: Overload detected (3+ reminders on same day)
    
    ReminderService->>ReminderService: findNextAvailableDate(userId, newDateTime)
    ReminderService->>ReminderService: updateReminderDateTime(reminder, nextAvailableDate)
    
    ReminderService->>ReminderRepository: update(reminder)
    ReminderRepository->>Database: UPDATE reminders SET reminder_date = ?, updated_at = ?
    Database-->>ReminderRepository: Reminder updated
    ReminderRepository-->>ReminderService: Updated reminder
    
    ReminderService-->>ReminderController: Reminder entity with overload warning
    ReminderController-->>APIGateway: 200 OK + Reminder data + Warning
    APIGateway-->>MobileApp: 200 OK + Reminder data + Warning
```

## 4. Mark Reminder Done Sequence

### 4.1 Mark Reminder as Done

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant ReminderController as Reminder Controller
    participant ReminderService as Reminder Service
    participant ReminderRepository as Reminder Repository
    participant Database as PostgreSQL

    MobileApp->>APIGateway: POST /api/reminders/{reminderId}/mark-done
    Note over MobileApp,APIGateway: Authorization: Bearer {accessToken}
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>ReminderController: Forward request
    
    ReminderController->>ReminderService: markReminderDone(reminderId, userId)
    
    ReminderService->>ReminderRepository: findById(reminderId)
    ReminderRepository->>Database: SELECT * FROM reminders WHERE id = ?
    Database-->>ReminderRepository: Reminder data
    ReminderRepository-->>ReminderService: Reminder entity
    
    ReminderService->>ReminderService: validateReminderOwnership(reminder, userId)
    ReminderService->>ReminderService: validateReminderStatus(reminder)
    
    ReminderService->>ReminderService: updateReminderStatus(reminder, DONE)
    ReminderService->>ReminderService: logReminderCompletion(reminder)
    
    ReminderService->>ReminderRepository: update(reminder)
    ReminderRepository->>Database: UPDATE reminders SET status = 'DONE', completed_at = ?, updated_at = ?
    Database-->>ReminderRepository: Reminder updated
    ReminderRepository-->>ReminderService: Updated reminder
    
    ReminderService-->>ReminderController: Reminder entity
    ReminderController-->>APIGateway: 200 OK + Reminder data
    APIGateway-->>MobileApp: 200 OK + Reminder data
```

## 5. Delete Reminder Sequence

### 5.1 Delete Reminder

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant ReminderController as Reminder Controller
    participant ReminderService as Reminder Service
    participant ReminderRepository as Reminder Repository
    participant Database as PostgreSQL

    MobileApp->>APIGateway: DELETE /api/reminders/{reminderId}
    Note over MobileApp,APIGateway: Authorization: Bearer {accessToken}
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>ReminderController: Forward request
    
    ReminderController->>ReminderService: deleteReminder(reminderId, userId)
    
    ReminderService->>ReminderRepository: findById(reminderId)
    ReminderRepository->>Database: SELECT * FROM reminders WHERE id = ?
    Database-->>ReminderRepository: Reminder data
    ReminderRepository-->>ReminderService: Reminder entity
    
    ReminderService->>ReminderService: validateReminderOwnership(reminder, userId)
    ReminderService->>ReminderService: validateReminderDeletion(reminder)
    
    ReminderService->>ReminderRepository: delete(reminder)
    ReminderRepository->>Database: DELETE FROM reminders WHERE id = ?
    Database-->>ReminderRepository: Reminder deleted
    ReminderRepository-->>ReminderService: Deletion successful
    
    ReminderService-->>ReminderController: DeletionResult(success)
    ReminderController-->>APIGateway: 204 No Content
    APIGateway-->>MobileApp: 204 No Content
```

## 6. Overload Prevention Sequence

### 6.1 Overload Detection and Prevention

```mermaid
sequenceDiagram
    participant ReminderService as Reminder Service
    participant ReminderRepository as Reminder Repository
    participant Database as PostgreSQL

    ReminderService->>ReminderService: checkOverloadPrevention(userId, targetDate)
    
    ReminderService->>ReminderRepository: countRemindersByDate(userId, targetDate)
    ReminderRepository->>Database: SELECT COUNT(*) FROM reminders WHERE user_id = ? AND DATE(reminder_date) = ?
    Database-->>ReminderRepository: Reminder count
    ReminderRepository-->>ReminderService: Count result
    
    alt Count >= 3 (Overload detected)
        ReminderService->>ReminderService: findNextAvailableDate(userId, targetDate)
        ReminderService->>ReminderRepository: findNextAvailableSlot(userId, targetDate)
        ReminderRepository->>Database: SELECT MIN(reminder_date) FROM reminders WHERE user_id = ? AND reminder_date > ?
        Database-->>ReminderRepository: Next available date
        ReminderRepository-->>ReminderService: Next available date
        
        ReminderService->>ReminderService: calculateOptimalRescheduleDate(targetDate, nextAvailable)
        ReminderService-->>ReminderService: Return rescheduled date
    else Count < 3 (No overload)
        ReminderService-->>ReminderService: Return original date
    end
```

## 7. Reminder Notification Sequence

### 7.1 Send Reminder Notification

```mermaid
sequenceDiagram
    participant SchedulerService as Scheduler Service
    participant ReminderService as Reminder Service
    participant NotificationService as Notification Service
    participant EmailService as Email Service
    participant PushNotification as Push Notification

    SchedulerService->>SchedulerService: Hourly reminder notification job
    
    SchedulerService->>ReminderService: getDueReminders()
    ReminderService->>ReminderService: findRemindersDueInNextHour()
    
    loop For each due reminder
        SchedulerService->>NotificationService: sendReminderNotification(reminder)
        
        NotificationService->>NotificationService: prepareNotificationContent(reminder)
        NotificationService->>NotificationService: determineNotificationChannels(reminder.userId)
        
        alt Email notification enabled
            NotificationService->>EmailService: sendReminderEmail(reminder)
            EmailService-->>NotificationService: Email sent
        end
        
        alt Push notification enabled
            NotificationService->>PushNotification: sendPushNotification(reminder)
            PushNotification-->>NotificationService: Push notification sent
        end
        
        NotificationService->>NotificationService: logNotificationSent(reminder)
    end
    
    SchedulerService->>SchedulerService: Log notification summary
```

## Ghi chú kỹ thuật

### 1. Overload Prevention Rules
- Giới hạn tối đa 3 reminders/ngày cho mỗi user
- Tự động reschedule khi phát hiện overload
- Ưu tiên reminders theo priority và due date
- Tìm slot trống gần nhất để reschedule

### 2. Reminder Status
- **PENDING**: Chưa được xử lý
- **DONE**: Đã hoàn thành
- **CANCELLED**: Đã hủy
- **OVERDUE**: Quá hạn

### 3. Notification Channels
- **Email**: Gửi email reminder
- **Push Notification**: Gửi push notification
- **In-app**: Hiển thị trong app
- **SMS**: Gửi SMS (tương lai)

### 4. Performance
- Batch processing cho reminder creation
- Caching cho user preferences
- Indexing cho reminder queries
- Async processing cho notifications
