# Statistics Management Module - Detail Design

## 1. Module Overview

### 1.1 Objectives
Statistics Management Module xử lý tất cả các hoạt động liên quan đến thống kê và phân tích dữ liệu học tập bao gồm:
- Thu thập và tính toán thống kê học tập
- Hiển thị dashboard với các metrics chính
- Phân tích tiến trình học tập theo thời gian
- Báo cáo hiệu suất và thành tích
- So sánh và benchmarking

### 1.2 Scope
- **In Scope**: Learning statistics, progress tracking, performance analytics, reporting
- **Out of Scope**: Learning set management, Review sessions (handled by other modules)

### 1.3 Dependencies
- **Database**: learning_statistics table, user_achievements table, progress_snapshots table
- **External Services**: Analytics service, Reporting service
- **Algorithms**: Statistical calculations, trend analysis

## 2. API Contracts

### 2.1 Dashboard Statistics

#### GET /api/v1/statistics/dashboard
**Query Parameters:**
- `period`: Time period (today, week, month, year, all)
- `setId`: Filter by specific learning set (optional)

**Response:**
```json
{
  "success": true,
  "data": {
    "overview": {
      "totalSets": 5,
      "activeSets": 3,
      "completedSets": 1,
      "totalItems": 150,
      "learnedItems": 75,
      "masteredItems": 30
    },
    "progress": {
      "today": {
        "sessionsCompleted": 2,
        "itemsReviewed": 25,
        "accuracy": 85.5,
        "timeSpent": 1800
      },
      "week": {
        "sessionsCompleted": 12,
        "itemsReviewed": 150,
        "accuracy": 82.3,
        "timeSpent": 10800,
        "streak": 7
      },
      "month": {
        "sessionsCompleted": 45,
        "itemsReviewed": 600,
        "accuracy": 80.1,
        "timeSpent": 36000
      }
    },
    "achievements": [
      {
        "achievementId": "uuid-here",
        "title": "Học tập 7 ngày liên tiếp",
        "description": "Duy trì thói quen học tập trong 7 ngày",
        "icon": "streak_7",
        "earnedAt": "2024-12-19T10:00:00Z",
        "category": "consistency"
      }
    ],
    "trends": {
      "accuracyTrend": [
        {"date": "2024-12-13", "accuracy": 78.5},
        {"date": "2024-12-14", "accuracy": 80.2},
        {"date": "2024-12-15", "accuracy": 82.1}
      ],
      "timeSpentTrend": [
        {"date": "2024-12-13", "minutes": 25},
        {"date": "2024-12-14", "minutes": 30},
        {"date": "2024-12-15", "minutes": 35}
      ]
    }
  }
}
```

### 2.2 Learning Set Statistics

#### GET /api/v1/statistics/sets/{setId}
**Query Parameters:**
- `period`: Time period (week, month, year, all)

**Response:**
```json
{
  "success": true,
  "data": {
    "setId": "uuid-here",
    "setTitle": "Từ vựng tiếng Anh cơ bản",
    "overview": {
      "totalItems": 50,
      "learnedItems": 25,
      "masteredItems": 10,
      "completionPercentage": 50.0,
      "averageAccuracy": 82.5,
      "totalTimeSpent": 3600
    },
    "progress": {
      "itemsByStatus": {
        "new": 15,
        "learning": 10,
        "mastered": 10,
        "review": 15
      },
      "difficultyDistribution": {
        "easy": 20,
        "medium": 20,
        "hard": 10
      }
    },
    "performance": {
      "accuracyByCycle": [
        {"cycle": 1, "accuracy": 75.0, "items": 10},
        {"cycle": 2, "accuracy": 80.0, "items": 15},
        {"cycle": 3, "accuracy": 85.0, "items": 20}
      ],
      "responseTimeByDifficulty": {
        "easy": 2.5,
        "medium": 4.2,
        "hard": 6.8
      }
    },
    "timeline": {
      "sessionsCompleted": [
        {"date": "2024-12-13", "sessions": 2, "accuracy": 80.0},
        {"date": "2024-12-14", "sessions": 1, "accuracy": 85.0},
        {"date": "2024-12-15", "sessions": 3, "accuracy": 82.5}
      ]
    }
  }
}
```

### 2.3 Progress Analytics

#### GET /api/v1/statistics/progress
**Query Parameters:**
- `period`: Time period (week, month, year)
- `metric`: Metric type (accuracy, time, items, sessions)
- `granularity`: Data granularity (day, week, month)

**Response:**
```json
{
  "success": true,
  "data": {
    "metric": "accuracy",
    "period": "month",
    "granularity": "day",
    "data": [
      {
        "date": "2024-12-01",
        "value": 78.5,
        "sessions": 2,
        "items": 25
      },
      {
        "date": "2024-12-02",
        "value": 80.2,
        "sessions": 1,
        "items": 15
      }
    ],
    "summary": {
      "average": 82.1,
      "trend": "increasing",
      "change": 3.6,
      "bestDay": "2024-12-15",
      "bestValue": 88.5
    }
  }
}
```

### 2.4 Achievement System

#### GET /api/v1/statistics/achievements
**Response:**
```json
{
  "success": true,
  "data": {
    "earned": [
      {
        "achievementId": "uuid-here",
        "title": "Học tập 7 ngày liên tiếp",
        "description": "Duy trì thói quen học tập trong 7 ngày",
        "icon": "streak_7",
        "category": "consistency",
        "earnedAt": "2024-12-19T10:00:00Z",
        "progress": 100
      }
    ],
    "available": [
      {
        "achievementId": "uuid-here-2",
        "title": "Học tập 30 ngày liên tiếp",
        "description": "Duy trì thói quen học tập trong 30 ngày",
        "icon": "streak_30",
        "category": "consistency",
        "progress": 23,
        "requirement": 30
      }
    ],
    "categories": {
      "consistency": 1,
      "accuracy": 0,
      "speed": 0,
      "volume": 0
    }
  }
}
```

### 2.5 Comparative Analytics

#### GET /api/v1/statistics/compare
**Query Parameters:**
- `period`: Time period for comparison
- `metric`: Metric to compare

**Response:**
```json
{
  "success": true,
  "data": {
    "current": {
      "period": "this_month",
      "accuracy": 82.1,
      "timeSpent": 3600,
      "sessions": 45,
      "items": 600
    },
    "previous": {
      "period": "last_month",
      "accuracy": 78.5,
      "timeSpent": 3200,
      "sessions": 40,
      "items": 550
    },
    "comparison": {
      "accuracy": {
        "change": 3.6,
        "trend": "increasing",
        "percentage": 4.6
      },
      "timeSpent": {
        "change": 400,
        "trend": "increasing",
        "percentage": 12.5
      }
    }
  }
}
```

## 3. Data Models

### 3.1 Learning Statistics Entity
```java
@Entity
@Table(name = "learning_statistics")
public class LearningStatistics {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID statisticsId;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "set_id")
    private LearningSet learningSet;
    
    @Column(nullable = false)
    private LocalDate date;
    
    @Enumerated(EnumType.STRING)
    private StatisticsPeriod period;
    
    // Session metrics
    @Column(nullable = false)
    private Integer sessionsCompleted;
    
    @Column(nullable = false)
    private Integer itemsReviewed;
    
    @Column(nullable = false)
    private Integer correctAnswers;
    
    @Column(nullable = false)
    private Integer incorrectAnswers;
    
    @Column(nullable = false)
    private Double accuracy;
    
    @Column(nullable = false)
    private Integer timeSpent; // in seconds
    
    @Column(nullable = false)
    private Double averageResponseTime;
    
    // Progress metrics
    @Column(nullable = false)
    private Integer newItems;
    
    @Column(nullable = false)
    private Integer learningItems;
    
    @Column(nullable = false)
    private Integer masteredItems;
    
    @Column(nullable = false)
    private Integer totalItems;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
```

### 3.2 User Achievement Entity
```java
@Entity
@Table(name = "user_achievements")
public class UserAchievement {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userAchievementId;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "achievement_id", nullable = false)
    private Achievement achievement;
    
    @Column(nullable = false)
    private LocalDateTime earnedAt;
    
    @Column(nullable = false)
    private Integer progress; // 0-100
    
    @Column(nullable = false)
    private Boolean isEarned;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
```

### 3.3 Achievement Entity
```java
@Entity
@Table(name = "achievements")
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID achievementId;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false, length = 500)
    private String description;
    
    @Column(nullable = false)
    private String icon;
    
    @Enumerated(EnumType.STRING)
    private AchievementCategory category;
    
    @Column(nullable = false)
    private String criteria; // JSON criteria definition
    
    @Column(nullable = false)
    private Integer requirement; // Numeric requirement
    
    @Column(nullable = false)
    private Boolean isActive;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
```

### 3.4 Progress Snapshot Entity
```java
@Entity
@Table(name = "progress_snapshots")
public class ProgressSnapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID snapshotId;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "set_id")
    private LearningSet learningSet;
    
    @Column(nullable = false)
    private LocalDateTime snapshotDate;
    
    @Enumerated(EnumType.STRING)
    private SnapshotType type;
    
    // Progress data as JSON
    @Column(columnDefinition = "TEXT")
    private String progressData;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
```

## 4. Business Logic

### 4.1 Calculate Daily Statistics Logic
```pseudocode
FUNCTION calculateDailyStatistics(userId, date):
    // Get all review sessions for the day
    sessions = reviewSessionRepository.findByUserIdAndDate(userId, date)
    
    // Initialize statistics
    stats = new LearningStatistics()
    stats.user = userRepository.findById(userId)
    stats.date = date
    stats.period = DAILY
    stats.sessionsCompleted = sessions.size()
    stats.itemsReviewed = 0
    stats.correctAnswers = 0
    stats.incorrectAnswers = 0
    stats.timeSpent = 0
    stats.averageResponseTime = 0.0
    stats.newItems = 0
    stats.learningItems = 0
    stats.masteredItems = 0
    
    // Calculate session metrics
    totalResponseTime = 0.0
    FOR EACH session IN sessions:
        stats.itemsReviewed += session.totalItems
        stats.correctAnswers += session.correctAnswers
        stats.incorrectAnswers += session.incorrectAnswers
        stats.timeSpent += session.sessionDuration
        
        // Calculate average response time
        FOR EACH item IN session.reviewItems:
            totalResponseTime += item.responseTime
    
    // Calculate derived metrics
    IF stats.itemsReviewed > 0:
        stats.accuracy = (stats.correctAnswers / stats.itemsReviewed) * 100
        stats.averageResponseTime = totalResponseTime / stats.itemsReviewed
    
    // Calculate progress metrics
    itemProgresses = itemProgressRepository.findByUserId(userId)
    FOR EACH progress IN itemProgresses:
        SWITCH progress.status:
            CASE NEW:
                stats.newItems++
            CASE LEARNING:
                stats.learningItems++
            CASE MASTERED:
                stats.masteredItems++
        stats.totalItems++
    
    stats.createdAt = now()
    stats.updatedAt = now()
    
    // Save or update statistics
    existingStats = learningStatisticsRepository.findByUserIdAndDate(userId, date)
    IF existingStats IS NOT NULL:
        existingStats = stats
        learningStatisticsRepository.save(existingStats)
    ELSE:
        learningStatisticsRepository.save(stats)
    
    RETURN stats
```

### 4.2 Calculate Dashboard Data Logic
```pseudocode
FUNCTION getDashboardData(userId, period, setId):
    // Get user's learning sets
    IF setId IS NOT NULL:
        learningSets = [learningSetRepository.findByIdAndUserId(setId, userId)]
    ELSE:
        learningSets = learningSetRepository.findByUserId(userId)
    
    // Calculate overview metrics
    overview = calculateOverviewMetrics(userId, learningSets)
    
    // Calculate progress metrics for different periods
    progress = calculateProgressMetrics(userId, period)
    
    // Get recent achievements
    achievements = userAchievementRepository.findRecentByUserId(userId, 5)
    
    // Calculate trends
    trends = calculateTrends(userId, period)
    
    RETURN {
        overview: overview,
        progress: progress,
        achievements: achievements,
        trends: trends
    }
```

### 4.3 Achievement Processing Logic
```pseudocode
FUNCTION processAchievements(userId):
    // Get all active achievements
    achievements = achievementRepository.findActive()
    
    // Get user's current statistics
    userStats = getUserCurrentStatistics(userId)
    
    FOR EACH achievement IN achievements:
        // Check if user already earned this achievement
        userAchievement = userAchievementRepository.findByUserIdAndAchievementId(userId, achievement.achievementId)
        IF userAchievement IS NOT NULL AND userAchievement.isEarned:
            CONTINUE
        
        // Calculate progress based on criteria
        progress = calculateAchievementProgress(achievement, userStats)
        
        // Update or create user achievement
        IF userAchievement IS NULL:
            userAchievement = new UserAchievement()
            userAchievement.user = userRepository.findById(userId)
            userAchievement.achievement = achievement
            userAchievement.progress = progress
            userAchievement.isEarned = progress >= 100
            userAchievement.earnedAt = userAchievement.isEarned ? now() : null
            userAchievement.createdAt = now()
        ELSE:
            userAchievement.progress = progress
            IF progress >= 100 AND NOT userAchievement.isEarned:
                userAchievement.isEarned = true
                userAchievement.earnedAt = now()
        
        userAchievementRepository.save(userAchievement)
        
        // Send notification if achievement earned
        IF userAchievement.isEarned AND userAchievement.earnedAt == now():
            notificationService.sendAchievementNotification(userId, achievement)
```

### 4.4 Trend Analysis Logic
```pseudocode
FUNCTION calculateTrends(userId, period):
    // Get historical data
    startDate = getStartDateForPeriod(period)
    endDate = now().toDate()
    
    statistics = learningStatisticsRepository.findByUserIdAndDateRange(userId, startDate, endDate)
    
    // Calculate accuracy trend
    accuracyTrend = []
    FOR EACH stat IN statistics:
        accuracyTrend.add({
            date: stat.date,
            accuracy: stat.accuracy
        })
    
    // Calculate time spent trend
    timeSpentTrend = []
    FOR EACH stat IN statistics:
        timeSpentTrend.add({
            date: stat.date,
            minutes: stat.timeSpent / 60
        })
    
    // Calculate overall trend direction
    accuracyTrendDirection = calculateTrendDirection(accuracyTrend)
    timeSpentTrendDirection = calculateTrendDirection(timeSpentTrend)
    
    RETURN {
        accuracyTrend: accuracyTrend,
        timeSpentTrend: timeSpentTrend,
        accuracyTrendDirection: accuracyTrendDirection,
        timeSpentTrendDirection: timeSpentTrendDirection
    }
```

## 5. Validation Rules

### 5.1 Statistics Query Validation
- **Period**: Must be valid enum value (today, week, month, year, all)
- **SetId**: Must exist and belong to user (if provided)
- **Date Range**: Start date must be before end date
- **Granularity**: Must be valid enum value (day, week, month)

### 5.2 Achievement Validation
- **Criteria**: Must be valid JSON format
- **Requirement**: Must be positive integer
- **Category**: Must be valid enum value
- **Title/Description**: Required, max length limits

## 6. Error Handling

### 6.1 Error Codes
- `STATS_001`: Statistics not found
- `STATS_002`: Invalid period parameter
- `STATS_003`: Invalid date range
- `STATS_004`: Achievement not found
- `STATS_005`: Insufficient data for calculation
- `STATS_006`: Invalid metric type

## 7. Security Considerations

### 7.1 Data Privacy
- Users can only access their own statistics
- Implement data anonymization for analytics
- Respect user privacy preferences
- Secure storage of sensitive metrics

### 7.2 Performance
- Implement caching for frequently accessed statistics
- Use database indexes for efficient queries
- Implement pagination for large datasets
- Optimize aggregation queries

## 8. Observability

### 8.1 Logging
```java
// Log statistics calculation
log.info("Statistics calculated", 
    "userId", userId, 
    "period", period,
    "calculationTime", calculationTime);

// Log achievement earned
log.info("Achievement earned", 
    "userId", userId, 
    "achievementId", achievement.getAchievementId(),
    "title", achievement.getTitle());
```

### 8.2 Metrics
- Statistics calculation time
- Dashboard load time
- Achievement processing time
- Data aggregation performance

### 8.3 Alerts
- Statistics calculation failures
- Achievement processing errors
- Performance degradation
- Data inconsistency issues

## 9. Testing Strategy

### 9.1 Unit Tests
- Statistics calculation algorithms
- Achievement progress calculation
- Trend analysis functions
- Data aggregation logic

### 9.2 Integration Tests
- API endpoint testing
- Database operations
- Performance with large datasets
- Achievement processing flow

### 9.3 Performance Tests
- Dashboard load time
- Statistics calculation performance
- Large dataset aggregation
- Concurrent user statistics

## 10. Dependencies

### 10.1 Internal Dependencies
- `LearningStatisticsRepository`: Statistics storage
- `UserAchievementRepository`: Achievement tracking
- `AchievementRepository`: Achievement definitions
- `ReviewSessionService`: Session data
- `ItemProgressService`: Progress data

### 10.2 External Dependencies
- Database (PostgreSQL)
- Analytics Service (for advanced analytics)
- Reporting Service (for report generation)

### 10.3 Configuration
```yaml
statistics:
  calculation:
    batch-size: 1000
    cache-ttl: 3600
    aggregation-interval: 300
  achievements:
    check-interval: 3600
    max-achievements: 100
  dashboard:
    max-period: 365
    cache-ttl: 1800
  performance:
    max-query-time: 5000
    enable-caching: true
```

---

**Document Version**: 1.0  
**Last Updated**: 2024-12-19  
**Next Review**: 2024-12-26  
**Owner**: Backend Team  
**Stakeholders**: Development Team, QA Team, Analytics Team
