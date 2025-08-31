# Statistics and Analytics Sequence Diagrams

## Tổng quan

Tài liệu này mô tả các luồng sequence cho quá trình tính toán và hiển thị thống kê học tập trong hệ thống RepeatWise, bao gồm overview statistics, set statistics, performance trends và analytics.

## 1. Overview Statistics Sequence

### 1.1 Get Overview Statistics (Cached)

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant StatisticsController as Statistics Controller
    participant StatisticsService as Statistics Service
    participant Cache as Redis Cache

    MobileApp->>APIGateway: GET /api/statistics/overview
    Note over MobileApp,APIGateway: Authorization: Bearer {accessToken}
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>StatisticsController: Forward request
    
    StatisticsController->>StatisticsService: getOverviewStatistics(userId)
    
    StatisticsService->>Cache: getCachedStatistics(userId, "overview")
    Cache-->>StatisticsService: Cached statistics found
    
    StatisticsService->>StatisticsService: validateCacheData(cachedData)
    StatisticsService-->>StatisticsController: OverviewStatistics object
    StatisticsController-->>APIGateway: 200 OK + Statistics data
    APIGateway-->>MobileApp: 200 OK + Statistics data
```

### 1.2 Get Overview Statistics (Cache Miss)

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant StatisticsController as Statistics Controller
    participant StatisticsService as Statistics Service
    participant StatisticsRepository as Statistics Repository
    participant Cache as Redis Cache
    participant Database as PostgreSQL

    MobileApp->>APIGateway: GET /api/statistics/overview
    Note over MobileApp,APIGateway: Authorization: Bearer {accessToken}
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>StatisticsController: Forward request
    
    StatisticsController->>StatisticsService: getOverviewStatistics(userId)
    
    StatisticsService->>Cache: getCachedStatistics(userId, "overview")
    Cache-->>StatisticsService: Cache miss
    
    StatisticsService->>StatisticsRepository: calculateOverviewStatistics(userId)
    
    StatisticsRepository->>Database: SELECT COUNT(*) as total_sets FROM sets WHERE user_id = ?
    Database-->>StatisticsRepository: Total sets count
    StatisticsRepository->>Database: SELECT COUNT(*) as active_cycles FROM cycles WHERE user_id = ? AND status = 'ACTIVE'
    Database-->>StatisticsRepository: Active cycles count
    StatisticsRepository->>Database: SELECT AVG(score) as avg_score FROM reviews r JOIN cycles c ON r.cycle_id = c.id WHERE c.user_id = ?
    Database-->>StatisticsRepository: Average score
    StatisticsRepository->>Database: SELECT COUNT(*) as completed_cycles FROM cycles WHERE user_id = ? AND status = 'COMPLETED'
    Database-->>StatisticsRepository: Completed cycles count
    
    StatisticsRepository-->>StatisticsService: Statistics data
    StatisticsService->>StatisticsService: buildOverviewStatistics(rawData)
    StatisticsService->>Cache: cacheStatistics(userId, "overview", statistics, TTL=3600)
    Cache-->>StatisticsService: Statistics cached
    
    StatisticsService-->>StatisticsController: OverviewStatistics object
    StatisticsController-->>APIGateway: 200 OK + Statistics data
    APIGateway-->>MobileApp: 200 OK + Statistics data
```

## 2. Set Statistics Sequence

### 2.1 Get Set Statistics

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant StatisticsController as Statistics Controller
    participant StatisticsService as Statistics Service
    participant SetRepository as Set Repository
    participant StatisticsRepository as Statistics Repository
    participant Database as PostgreSQL

    MobileApp->>APIGateway: GET /api/sets/{setId}/statistics
    Note over MobileApp,APIGateway: Authorization: Bearer {accessToken}
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>StatisticsController: Forward request
    
    StatisticsController->>StatisticsService: getSetStatistics(setId, userId)
    
    StatisticsService->>SetRepository: findById(setId)
    SetRepository->>Database: SELECT * FROM sets WHERE id = ?
    Database-->>SetRepository: Set data
    SetRepository-->>StatisticsService: Set entity
    
    StatisticsService->>StatisticsService: validateSetOwnership(set, userId)
    StatisticsService->>StatisticsRepository: calculateSetStatistics(setId)
    
    StatisticsRepository->>Database: SELECT COUNT(*) as total_cycles FROM cycles WHERE set_id = ?
    Database-->>StatisticsRepository: Total cycles count
    StatisticsRepository->>Database: SELECT COUNT(*) as completed_cycles FROM cycles WHERE set_id = ? AND status = 'COMPLETED'
    Database-->>StatisticsRepository: Completed cycles count
    StatisticsRepository->>Database: SELECT AVG(score) as avg_score FROM reviews r JOIN cycles c ON r.cycle_id = c.id WHERE c.set_id = ?
    Database-->>StatisticsRepository: Average score
    StatisticsRepository->>Database: SELECT MAX(created_at) as last_review FROM reviews r JOIN cycles c ON r.cycle_id = c.id WHERE c.set_id = ?
    Database-->>StatisticsRepository: Last review date
    
    StatisticsRepository-->>StatisticsService: Set statistics data
    StatisticsService->>StatisticsService: buildSetStatistics(rawData)
    StatisticsService-->>StatisticsController: SetStatistics object
    StatisticsController-->>APIGateway: 200 OK + Statistics data
    APIGateway-->>MobileApp: 200 OK + Statistics data
```

## 3. Performance Trends Sequence

### 3.1 Get Performance Trends

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant StatisticsController as Statistics Controller
    participant StatisticsService as Statistics Service
    participant StatisticsRepository as Statistics Repository
    participant Database as PostgreSQL

    MobileApp->>APIGateway: GET /api/statistics/trends?period=30&metric=score
    Note over MobileApp,APIGateway: Authorization: Bearer {accessToken}
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>StatisticsController: Forward request
    
    StatisticsController->>StatisticsService: getPerformanceTrends(userId, period, metric)
    
    StatisticsService->>StatisticsService: validateTrendParameters(period, metric)
    StatisticsService->>StatisticsRepository: calculatePerformanceTrends(userId, period, metric)
    
    StatisticsRepository->>Database: SELECT DATE(review_date) as date, AVG(score) as avg_score FROM reviews r JOIN cycles c ON r.cycle_id = c.id WHERE c.user_id = ? AND r.review_date >= ? GROUP BY DATE(review_date) ORDER BY date
    Database-->>StatisticsRepository: Daily performance data
    StatisticsRepository->>Database: SELECT DATE(created_at) as date, COUNT(*) as cycles_started FROM cycles WHERE user_id = ? AND created_at >= ? GROUP BY DATE(created_at) ORDER BY date
    Database-->>StatisticsRepository: Daily cycles data
    
    StatisticsRepository-->>StatisticsService: Trends data
    StatisticsService->>StatisticsService: calculateTrendAnalysis(rawData)
    StatisticsService->>StatisticsService: buildTrendChartData(analysisData)
    StatisticsService-->>StatisticsController: PerformanceTrends object
    StatisticsController-->>APIGateway: 200 OK + Trends data
    APIGateway-->>MobileApp: 200 OK + Trends data
```

## 4. Learning Progress Sequence

### 4.1 Get Learning Progress

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant StatisticsController as Statistics Controller
    participant StatisticsService as Statistics Service
    participant StatisticsRepository as Statistics Repository
    participant Database as PostgreSQL

    MobileApp->>APIGateway: GET /api/statistics/progress
    Note over MobileApp,APIGateway: Authorization: Bearer {accessToken}
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>StatisticsController: Forward request
    
    StatisticsController->>StatisticsService: getLearningProgress(userId)
    
    StatisticsService->>StatisticsRepository: calculateLearningProgress(userId)
    
    StatisticsRepository->>Database: SELECT s.category, COUNT(*) as total_sets, COUNT(CASE WHEN c.status = 'COMPLETED' THEN 1 END) as completed_sets FROM sets s LEFT JOIN cycles c ON s.id = c.set_id WHERE s.user_id = ? GROUP BY s.category
    Database-->>StatisticsRepository: Category progress data
    StatisticsRepository->>Database: SELECT DATE_TRUNC('week', c.created_at) as week, COUNT(*) as cycles_started, COUNT(CASE WHEN c.status = 'COMPLETED' THEN 1 END) as cycles_completed FROM cycles c WHERE c.user_id = ? GROUP BY DATE_TRUNC('week', c.created_at) ORDER BY week
    Database-->>StatisticsRepository: Weekly progress data
    StatisticsRepository->>Database: SELECT AVG(score) as avg_score, COUNT(*) as total_reviews FROM reviews r JOIN cycles c ON r.cycle_id = c.id WHERE c.user_id = ? AND r.review_date >= CURRENT_DATE - INTERVAL '30 days'
    Database-->>StatisticsRepository: Recent performance data
    
    StatisticsRepository-->>StatisticsService: Progress data
    StatisticsService->>StatisticsService: calculateProgressMetrics(rawData)
    StatisticsService->>StatisticsService: buildProgressReport(metrics)
    StatisticsService-->>StatisticsController: LearningProgress object
    StatisticsController-->>APIGateway: 200 OK + Progress data
    APIGateway-->>MobileApp: 200 OK + Progress data
```

## 5. Analytics Data Export Sequence

### 5.1 Export Learning Data

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant StatisticsController as Statistics Controller
    participant StatisticsService as Statistics Service
    participant ExportService as Export Service
    participant Database as PostgreSQL

    MobileApp->>APIGateway: POST /api/statistics/export
    Note over MobileApp,APIGateway: Authorization: Bearer {accessToken}
    Note over MobileApp,APIGateway: {format: "csv", dateRange: "last_30_days"}
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>StatisticsController: Forward request
    
    StatisticsController->>StatisticsService: exportLearningData(userId, format, dateRange)
    
    StatisticsService->>StatisticsService: validateExportParameters(format, dateRange)
    StatisticsService->>ExportService: generateLearningDataExport(userId, format, dateRange)
    
    ExportService->>Database: SELECT s.name, s.category, c.status, r.score, r.review_date FROM sets s JOIN cycles c ON s.id = c.set_id JOIN reviews r ON c.id = r.cycle_id WHERE s.user_id = ? AND r.review_date >= ?
    Database-->>ExportService: Learning data
    ExportService->>ExportService: formatDataForExport(rawData, format)
    ExportService->>ExportService: generateExportFile(formattedData, format)
    
    ExportService-->>StatisticsService: Export file URL
    StatisticsService-->>StatisticsController: ExportResult(fileUrl, expiryDate)
    StatisticsController-->>APIGateway: 200 OK + Export data
    APIGateway-->>MobileApp: 200 OK + Export data
```

## 6. Real-time Statistics Update Sequence

### 6.1 Update Statistics After Review

```mermaid
sequenceDiagram
    participant CycleService as Cycle Service
    participant StatisticsService as Statistics Service
    participant Cache as Redis Cache
    participant StatisticsRepository as Statistics Repository
    participant Database as PostgreSQL

    CycleService->>StatisticsService: updateStatisticsAfterReview(userId, cycleId, score)
    
    StatisticsService->>StatisticsService: invalidateUserStatisticsCache(userId)
    StatisticsService->>Cache: deleteCachedStatistics(userId, "overview")
    Cache-->>StatisticsService: Cache invalidated
    StatisticsService->>Cache: deleteCachedStatistics(userId, "progress")
    Cache-->>StatisticsService: Cache invalidated
    
    StatisticsService->>StatisticsRepository: updateUserStatistics(userId, score)
    StatisticsRepository->>Database: UPDATE user_statistics SET total_reviews = total_reviews + 1, avg_score = (avg_score * total_reviews + ?) / (total_reviews + 1) WHERE user_id = ?
    Database-->>StatisticsRepository: Statistics updated
    StatisticsRepository-->>StatisticsService: Update successful
    
    StatisticsService->>StatisticsService: triggerStatisticsRecalculation(userId)
    StatisticsService-->>CycleService: Statistics updated
```

## 7. Dashboard Statistics Sequence

### 7.1 Get Dashboard Statistics

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant StatisticsController as Statistics Controller
    participant StatisticsService as Statistics Service
    participant Cache as Redis Cache
    participant StatisticsRepository as Statistics Repository
    participant Database as PostgreSQL

    MobileApp->>APIGateway: GET /api/statistics/dashboard
    Note over MobileApp,APIGateway: Authorization: Bearer {accessToken}
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>StatisticsController: Forward request
    
    StatisticsController->>StatisticsService: getDashboardStatistics(userId)
    
    StatisticsService->>Cache: getCachedStatistics(userId, "dashboard")
    
    alt Cache hit
        Cache-->>StatisticsService: Cached dashboard data
        StatisticsService->>StatisticsService: validateCacheData(cachedData)
        StatisticsService-->>StatisticsController: DashboardStatistics object
    else Cache miss
        Cache-->>StatisticsService: Cache miss
        StatisticsService->>StatisticsRepository: calculateDashboardStatistics(userId)
        
        StatisticsRepository->>Database: Multiple aggregation queries
        Database-->>StatisticsRepository: Dashboard data
        StatisticsRepository-->>StatisticsService: Raw dashboard data
        
        StatisticsService->>StatisticsService: buildDashboardStatistics(rawData)
        StatisticsService->>Cache: cacheStatistics(userId, "dashboard", statistics, TTL=1800)
        Cache-->>StatisticsService: Dashboard cached
        StatisticsService-->>StatisticsController: DashboardStatistics object
    end
    
    StatisticsController-->>APIGateway: 200 OK + Dashboard data
    APIGateway-->>MobileApp: 200 OK + Dashboard data
```

## Ghi chú kỹ thuật

### 1. Caching Strategy
- **Overview statistics**: Cache 1 giờ
- **Dashboard statistics**: Cache 30 phút
- **Set statistics**: Cache 15 phút
- **Trends data**: Cache 6 giờ
- **Progress data**: Cache 1 giờ

### 2. Performance Optimization
- Aggregation queries được optimize với indexes
- Batch processing cho large datasets
- Lazy loading cho detailed statistics
- Background calculation cho heavy analytics

### 3. Data Accuracy
- Real-time updates sau mỗi review
- Cache invalidation khi data thay đổi
- Data consistency checks
- Historical data preservation

### 4. Export Features
- **Formats**: CSV, JSON, Excel
- **Date ranges**: Last 7/30/90 days, custom range
- **File storage**: Temporary URLs với expiry
- **Data privacy**: User-specific exports only
