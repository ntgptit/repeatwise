# Learning Set Management Sequence Diagrams

## Tổng quan

Tài liệu này mô tả các luồng sequence cho quá trình quản lý trạng thái học tập của set trong hệ thống RepeatWise, bao gồm tạm dừng và tiếp tục học tập set.

## 1. Pause Learning Set Sequence

### 1.1 Successful Set Pause

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant SetController as Set Controller
    participant SetService as Set Service
    participant CycleService as Cycle Service
    participant ReminderService as Reminder Service
    participant SetRepository as Set Repository
    participant CycleRepository as Cycle Repository
    participant ReminderRepository as Reminder Repository
    participant Database as PostgreSQL

    MobileApp->>APIGateway: POST /api/sets/{setId}/pause
    Note over MobileApp,APIGateway: Authorization: Bearer {accessToken}
    Note over MobileApp,APIGateway: {pauseDuration, pauseReason}
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>SetController: Forward request
    
    SetController->>SetService: pauseSet(setId, userId, pauseData)
    
    SetService->>SetRepository: findById(setId)
    SetRepository->>Database: SELECT * FROM sets WHERE id = ?
    Database-->>SetRepository: Set data
    SetRepository-->>SetService: Set entity
    
    SetService->>SetService: validateSetOwnership(set, userId)
    SetService->>SetService: validateSetStatus(set)
    SetService->>SetService: validatePauseDuration(pauseDuration)
    
    SetService->>CycleRepository: findActiveCycleBySetId(setId, userId)
    CycleRepository->>Database: SELECT * FROM cycles WHERE set_id = ? AND user_id = ? AND status = 'ACTIVE'
    Database-->>CycleRepository: Active cycle data
    CycleRepository-->>SetService: Cycle entity
    
    SetService->>SetService: calculatePauseEndDate(pauseDuration)
    SetService->>SetService: updateSetStatus(set, 'PAUSED')
    SetService->>SetRepository: updateSet(set)
    SetRepository->>Database: UPDATE sets SET status = 'PAUSED', pause_reason = ?, pause_start_date = ?, pause_end_date = ?
    Database-->>SetRepository: Set updated
    
    SetService->>CycleService: pauseCycle(cycle.id)
    CycleService->>CycleService: updateCycleStatus(cycle, 'PAUSED')
    CycleService->>CycleRepository: updateCycle(cycle)
    CycleRepository->>Database: UPDATE cycles SET status = 'PAUSED', next_review_date = ?
    Database-->>CycleRepository: Cycle updated
    
    SetService->>ReminderService: cancelPendingReminders(cycle.id)
    ReminderService->>ReminderRepository: findPendingRemindersByCycleId(cycle.id)
    ReminderRepository->>Database: SELECT * FROM reminders WHERE cycle_id = ? AND status = 'PENDING'
    Database-->>ReminderRepository: Pending reminders
    ReminderRepository-->>ReminderService: List<Reminder> entities
    
    ReminderService->>ReminderService: cancelReminders(reminders)
    ReminderService->>ReminderRepository: updateReminders(reminders)
    ReminderRepository->>Database: UPDATE reminders SET status = 'CANCELLED' WHERE id IN (...)
    Database-->>ReminderRepository: Reminders updated
    
    SetService-->>SetController: PauseResult(success, pauseEndDate)
    SetController-->>APIGateway: 200 OK + Pause confirmation
    APIGateway-->>MobileApp: 200 OK + Pause confirmation
```

### 1.2 Pause Set with Invalid Status

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant SetController as Set Controller
    participant SetService as Set Service
    participant SetRepository as Set Repository
    participant Database as PostgreSQL

    MobileApp->>APIGateway: POST /api/sets/{setId}/pause
    Note over MobileApp,APIGateway: Authorization: Bearer {accessToken}
    Note over MobileApp,APIGateway: {pauseDuration, pauseReason}
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>SetController: Forward request
    
    SetController->>SetService: pauseSet(setId, userId, pauseData)
    
    SetService->>SetRepository: findById(setId)
    SetRepository->>Database: SELECT * FROM sets WHERE id = ?
    Database-->>SetRepository: Set data
    SetRepository-->>SetService: Set entity
    
    SetService->>SetService: validateSetOwnership(set, userId)
    SetService->>SetService: validateSetStatus(set)
    Note over SetService: Set status is 'COMPLETED' - cannot pause
    
    SetService-->>SetController: BusinessRuleError("Cannot pause completed set")
    SetController-->>APIGateway: 400 Bad Request + Error message
    APIGateway-->>MobileApp: 400 Bad Request + Error message
```

## 2. Resume Learning Set Sequence

### 2.1 Successful Set Resume

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant SetController as Set Controller
    participant SetService as Set Service
    participant CycleService as Cycle Service
    participant ReminderService as Reminder Service
    participant SetRepository as Set Repository
    participant CycleRepository as Cycle Repository
    participant ReminderRepository as Reminder Repository
    participant Database as PostgreSQL

    MobileApp->>APIGateway: POST /api/sets/{setId}/resume
    Note over MobileApp,APIGateway: Authorization: Bearer {accessToken}
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>SetController: Forward request
    
    SetController->>SetService: resumeSet(setId, userId)
    
    SetService->>SetRepository: findById(setId)
    SetRepository->>Database: SELECT * FROM sets WHERE id = ?
    Database-->>SetRepository: Set data
    SetRepository-->>SetService: Set entity
    
    SetService->>SetService: validateSetOwnership(set, userId)
    SetService->>SetService: validateSetStatus(set)
    Note over SetService: Set status must be 'PAUSED'
    
    SetService->>CycleRepository: findPausedCycleBySetId(setId, userId)
    CycleRepository->>Database: SELECT * FROM cycles WHERE set_id = ? AND user_id = ? AND status = 'PAUSED'
    Database-->>CycleRepository: Paused cycle data
    CycleRepository-->>SetService: Cycle entity
    
    SetService->>SetService: calculateResumeDate()
    SetService->>SetService: determineResumeStatus(cycle)
    SetService->>SetService: updateSetStatus(set, resumeStatus)
    SetService->>SetRepository: updateSet(set)
    SetRepository->>Database: UPDATE sets SET status = ?, pause_reason = NULL, pause_start_date = NULL, pause_end_date = NULL
    Database-->>SetRepository: Set updated
    
    SetService->>CycleService: resumeCycle(cycle.id)
    CycleService->>CycleService: calculateNextReviewDate(cycle)
    CycleService->>CycleService: updateCycleStatus(cycle, resumeStatus)
    CycleService->>CycleRepository: updateCycle(cycle)
    CycleRepository->>Database: UPDATE cycles SET status = ?, next_review_date = ?
    Database-->>CycleRepository: Cycle updated
    
    SetService->>ReminderService: createResumeReminder(cycle.id, nextReviewDate)
    ReminderService->>ReminderService: createReminderEntity(cycle, nextReviewDate)
    ReminderService->>ReminderRepository: save(reminder)
    ReminderRepository->>Database: INSERT INTO reminders (cycle_id, user_id, reminder_date, status, created_at)
    Database-->>ReminderRepository: Reminder created
    
    SetService-->>SetController: ResumeResult(success, nextReviewDate)
    SetController-->>APIGateway: 200 OK + Resume confirmation
    APIGateway-->>MobileApp: 200 OK + Resume confirmation
```

### 2.2 Resume Set with Overdue Reviews

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant SetController as Set Controller
    participant SetService as Set Service
    participant CycleService as Cycle Service
    participant ReminderService as Reminder Service
    participant SetRepository as Set Repository
    participant CycleRepository as Cycle Repository
    participant Database as PostgreSQL

    MobileApp->>APIGateway: POST /api/sets/{setId}/resume
    Note over MobileApp,APIGateway: Authorization: Bearer {accessToken}
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>SetController: Forward request
    
    SetController->>SetService: resumeSet(setId, userId)
    
    SetService->>SetRepository: findById(setId)
    SetRepository->>Database: SELECT * FROM sets WHERE id = ?
    Database-->>SetRepository: Set data
    SetRepository-->>SetService: Set entity
    
    SetService->>SetService: validateSetOwnership(set, userId)
    SetService->>SetService: validateSetStatus(set)
    
    SetService->>CycleRepository: findPausedCycleBySetId(setId, userId)
    CycleRepository->>Database: SELECT * FROM cycles WHERE set_id = ? AND user_id = ? AND status = 'PAUSED'
    Database-->>CycleRepository: Paused cycle data
    CycleRepository-->>SetService: Cycle entity
    
    SetService->>SetService: checkOverdueReviews(cycle)
    Note over SetService: Cycle has overdue reviews
    
    SetService->>SetService: calculateOverduePenalty(cycle)
    SetService->>SetService: adjustNextReviewDate(cycle, penalty)
    SetService->>SetService: updateSetStatus(set, 'REVIEWING')
    SetService->>SetRepository: updateSet(set)
    SetRepository->>Database: UPDATE sets SET status = 'REVIEWING', pause_reason = NULL
    Database-->>SetRepository: Set updated
    
    SetService->>CycleService: resumeCycleWithOverdue(cycle.id, penalty)
    CycleService->>CycleService: updateCycleStatus(cycle, 'REVIEWING')
    CycleService->>CycleRepository: updateCycle(cycle)
    CycleRepository->>Database: UPDATE cycles SET status = 'REVIEWING', next_review_date = ?
    Database-->>CycleRepository: Cycle updated
    
    SetService-->>SetController: ResumeResult(success, nextReviewDate, overdueWarning)
    SetController-->>APIGateway: 200 OK + Resume confirmation + Warning
    APIGateway-->>MobileApp: 200 OK + Resume confirmation + Warning
```

## 3. Bulk Resume Sets Sequence

### 3.1 Resume Multiple Sets

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant SetController as Set Controller
    participant SetService as Set Service
    participant CycleService as Cycle Service
    participant ReminderService as Reminder Service
    participant SetRepository as Set Repository
    participant CycleRepository as Cycle Repository
    participant Database as PostgreSQL

    MobileApp->>APIGateway: POST /api/sets/bulk-resume
    Note over MobileApp,APIGateway: Authorization: Bearer {accessToken}
    Note over MobileApp,APIGateway: {setIds: [1, 2, 3]}
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>SetController: Forward request
    
    SetController->>SetService: resumeMultipleSets(setIds, userId)
    
    SetService->>SetRepository: findPausedSetsByIds(setIds, userId)
    SetRepository->>Database: SELECT * FROM sets WHERE id IN (?) AND user_id = ? AND status = 'PAUSED'
    Database-->>SetRepository: Paused sets data
    SetRepository-->>SetService: List<Set> entities
    
    loop For each set
        SetService->>SetService: validateSetOwnership(set, userId)
        SetService->>CycleRepository: findPausedCycleBySetId(set.id, userId)
        CycleRepository->>Database: SELECT * FROM cycles WHERE set_id = ? AND user_id = ? AND status = 'PAUSED'
        Database-->>CycleRepository: Cycle data
        CycleRepository-->>SetService: Cycle entity
        
        SetService->>SetService: calculateResumeDate()
        SetService->>SetService: updateSetStatus(set, 'LEARNING')
        SetService->>SetRepository: updateSet(set)
        SetRepository->>Database: UPDATE sets SET status = 'LEARNING', pause_reason = NULL
        Database-->>SetRepository: Set updated
        
        SetService->>CycleService: resumeCycle(cycle.id)
        CycleService->>CycleService: updateCycleStatus(cycle, 'LEARNING')
        CycleService->>CycleRepository: updateCycle(cycle)
        CycleRepository->>Database: UPDATE cycles SET status = 'LEARNING', next_review_date = ?
        Database-->>CycleRepository: Cycle updated
        
        SetService->>ReminderService: createResumeReminder(cycle.id, nextReviewDate)
        ReminderService->>ReminderRepository: save(reminder)
        ReminderRepository->>Database: INSERT INTO reminders (cycle_id, user_id, reminder_date, status)
        Database-->>ReminderRepository: Reminder created
    end
    
    SetService-->>SetController: BulkResumeResult(success, resumedCount, nextReviewDates)
    SetController-->>APIGateway: 200 OK + Bulk resume results
    APIGateway-->>MobileApp: 200 OK + Bulk resume results
```

## Ghi chú kỹ thuật

### 1. Pause Management
- Set có thể pause với duration cụ thể hoặc vô thời hạn
- Khi pause, tất cả reminder pending sẽ bị cancel
- Pause reason được lưu để phân tích sau này
- Pause start/end date được track để tính toán

### 2. Resume Logic
- Resume sẽ khôi phục trạng thái trước khi pause
- Nếu có overdue reviews, sẽ áp dụng penalty
- Next review date được tính toán lại dựa trên SRS algorithm
- Resume có thể bulk cho nhiều set cùng lúc

### 3. Status Transitions
- LEARNING → PAUSED → LEARNING/REVIEWING
- REVIEWING → PAUSED → REVIEWING
- COMPLETED → Không thể pause
- DELETED → Không thể pause/resume

### 4. Error Handling
- Invalid set status trả về 400 Bad Request
- Set not found trả về 404 Not Found
- Unauthorized access trả về 403 Forbidden
- Server errors trả về 500 Internal Server Error

### 5. Performance Considerations
- Bulk operations được xử lý trong transaction
- Reminder creation được batch để tối ưu performance
- Database queries được optimize với indexes
