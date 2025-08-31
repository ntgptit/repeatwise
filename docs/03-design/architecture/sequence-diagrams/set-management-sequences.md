# Set Management Sequence Diagrams

## Tổng quan

Tài liệu này mô tả các luồng sequence cho quá trình quản lý set học tập trong hệ thống RepeatWise, bao gồm tạo, chỉnh sửa, xóa và xem danh sách set.

## 1. Create Set Sequence

### 1.1 Successful Set Creation

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant SetController as Set Controller
    participant SetService as Set Service
    participant SetRepository as Set Repository
    participant Database as PostgreSQL

    MobileApp->>APIGateway: POST /api/sets
    Note over MobileApp,APIGateway: Authorization: Bearer {accessToken}
    Note over MobileApp,APIGateway: {name, description, category, difficulty}
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>SetController: Forward request
    
    SetController->>SetService: createSet(setData, userId)
    
    SetService->>SetService: validateSetData(setData)
    SetService->>SetService: validateSetName(name)
    SetService->>SetService: validateCategory(category)
    SetService->>SetService: validateDifficulty(difficulty)
    
    SetService->>SetService: createSetEntity(setData, userId)
    SetService->>SetRepository: save(set)
    SetRepository->>Database: INSERT INTO sets (name, description, category, difficulty, user_id, created_at, status)
    Database-->>SetRepository: Set created with ID
    SetRepository-->>SetService: Set entity
    
    SetService-->>SetController: Set entity
    SetController-->>APIGateway: 201 Created + Set data
    APIGateway-->>MobileApp: 201 Created + Set data
```

### 1.2 Set Creation with Validation Error

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant SetController as Set Controller
    participant SetService as Set Service

    MobileApp->>APIGateway: POST /api/sets
    Note over MobileApp,APIGateway: Authorization: Bearer {accessToken}
    Note over MobileApp,APIGateway: {name: "", description, category}
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>SetController: Forward request
    
    SetController->>SetService: createSet(setData, userId)
    
    SetService->>SetService: validateSetData(setData)
    SetService->>SetService: validateSetName(name)
    Note over SetService: Name validation fails (empty name)
    
    SetService-->>SetController: ValidationError("Set name cannot be empty")
    SetController-->>APIGateway: 400 Bad Request + Error message
    APIGateway-->>MobileApp: 400 Bad Request + Error message
```

## 2. Get Set List Sequence

### 2.1 Get All Sets for User

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant SetController as Set Controller
    participant SetService as Set Service
    participant SetRepository as Set Repository
    participant Database as PostgreSQL

    MobileApp->>APIGateway: GET /api/sets
    Note over MobileApp,APIGateway: Authorization: Bearer {accessToken}
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>SetController: Forward request
    
    SetController->>SetService: getSetsByUserId(userId)
    
    SetService->>SetRepository: findByUserId(userId)
    SetRepository->>Database: SELECT * FROM sets WHERE user_id = ? ORDER BY created_at DESC
    Database-->>SetRepository: List of sets
    SetRepository-->>SetService: List<Set> entities
    
    SetService-->>SetController: List<Set> entities
    SetController-->>APIGateway: 200 OK + Sets data
    APIGateway-->>MobileApp: 200 OK + Sets data
```

### 2.2 Get Sets with Filtering

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant SetController as Set Controller
    participant SetService as Set Service
    participant SetRepository as Set Repository
    participant Database as PostgreSQL

    MobileApp->>APIGateway: GET /api/sets?category=vocabulary&status=active
    Note over MobileApp,APIGateway: Authorization: Bearer {accessToken}
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>SetController: Forward request
    
    SetController->>SetService: getSetsByUserIdWithFilters(userId, filters)
    
    SetService->>SetService: validateFilters(filters)
    SetService->>SetRepository: findByUserIdAndFilters(userId, category, status)
    SetRepository->>Database: SELECT * FROM sets WHERE user_id = ? AND category = ? AND status = ?
    Database-->>SetRepository: Filtered sets
    SetRepository-->>SetService: List<Set> entities
    
    SetService-->>SetController: List<Set> entities
    SetController-->>APIGateway: 200 OK + Sets data
    APIGateway-->>MobileApp: 200 OK + Sets data
```

## 3. Get Set Details Sequence

### 3.1 Get Set by ID

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant SetController as Set Controller
    participant SetService as Set Service
    participant SetRepository as Set Repository
    participant Database as PostgreSQL

    MobileApp->>APIGateway: GET /api/sets/{setId}
    Note over MobileApp,APIGateway: Authorization: Bearer {accessToken}
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>SetController: Forward request
    
    SetController->>SetService: getSetById(setId, userId)
    
    SetService->>SetRepository: findById(setId)
    SetRepository->>Database: SELECT * FROM sets WHERE id = ?
    Database-->>SetRepository: Set data
    SetRepository-->>SetService: Set entity
    
    SetService->>SetService: validateSetOwnership(set, userId)
    
    SetService-->>SetController: Set entity
    SetController-->>APIGateway: 200 OK + Set data
    APIGateway-->>MobileApp: 200 OK + Set data
```

### 3.2 Get Set with Unauthorized Access

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant SetController as Set Controller
    participant SetService as Set Service
    participant SetRepository as Set Repository
    participant Database as PostgreSQL

    MobileApp->>APIGateway: GET /api/sets/{setId}
    Note over MobileApp,APIGateway: Authorization: Bearer {accessToken}
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>SetController: Forward request
    
    SetController->>SetService: getSetById(setId, userId)
    
    SetService->>SetRepository: findById(setId)
    SetRepository->>Database: SELECT * FROM sets WHERE id = ?
    Database-->>SetRepository: Set data
    SetRepository-->>SetService: Set entity
    
    SetService->>SetService: validateSetOwnership(set, userId)
    Note over SetService: Ownership validation fails
    
    SetService-->>SetController: AuthorizationError("Access denied")
    SetController-->>APIGateway: 403 Forbidden + Error message
    APIGateway-->>MobileApp: 403 Forbidden + Error message
```

## 4. Update Set Sequence

### 4.1 Successful Set Update

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant SetController as Set Controller
    participant SetService as Set Service
    participant SetRepository as Set Repository
    participant Database as PostgreSQL

    MobileApp->>APIGateway: PUT /api/sets/{setId}
    Note over MobileApp,APIGateway: Authorization: Bearer {accessToken}
    Note over MobileApp,APIGateway: {name, description, category, difficulty}
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>SetController: Forward request
    
    SetController->>SetService: updateSet(setId, setData, userId)
    
    SetService->>SetRepository: findById(setId)
    SetRepository->>Database: SELECT * FROM sets WHERE id = ?
    Database-->>SetRepository: Set data
    SetRepository-->>SetService: Set entity
    
    SetService->>SetService: validateSetOwnership(set, userId)
    SetService->>SetService: validateSetData(setData)
    SetService->>SetService: updateSetEntity(set, setData)
    
    SetService->>SetRepository: save(set)
    SetRepository->>Database: UPDATE sets SET name = ?, description = ?, category = ?, difficulty = ?, updated_at = ?
    Database-->>SetRepository: Set updated
    SetRepository-->>SetService: Updated set entity
    
    SetService-->>SetController: Set entity
    SetController-->>APIGateway: 200 OK + Updated set data
    APIGateway-->>MobileApp: 200 OK + Updated set data
```

### 4.2 Update Non-existent Set

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant SetController as Set Controller
    participant SetService as Set Service
    participant SetRepository as Set Repository
    participant Database as PostgreSQL

    MobileApp->>APIGateway: PUT /api/sets/{setId}
    Note over MobileApp,APIGateway: Authorization: Bearer {accessToken}
    Note over MobileApp,APIGateway: {name, description, category}
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>SetController: Forward request
    
    SetController->>SetService: updateSet(setId, setData, userId)
    
    SetService->>SetRepository: findById(setId)
    SetRepository->>Database: SELECT * FROM sets WHERE id = ?
    Database-->>SetRepository: No set found
    SetRepository-->>SetService: null
    
    SetService-->>SetController: ResourceNotFoundError("Set not found")
    SetController-->>APIGateway: 404 Not Found + Error message
    APIGateway-->>MobileApp: 404 Not Found + Error message
```

## 5. Delete Set Sequence

### 5.1 Successful Set Deletion

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant SetController as Set Controller
    participant SetService as Set Service
    participant SetRepository as Set Repository
    participant Database as PostgreSQL

    MobileApp->>APIGateway: DELETE /api/sets/{setId}
    Note over MobileApp,APIGateway: Authorization: Bearer {accessToken}
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>SetController: Forward request
    
    SetController->>SetService: deleteSet(setId, userId)
    
    SetService->>SetRepository: findById(setId)
    SetRepository->>Database: SELECT * FROM sets WHERE id = ?
    Database-->>SetRepository: Set data
    SetRepository-->>SetService: Set entity
    
    SetService->>SetService: validateSetOwnership(set, userId)
    SetService->>SetService: validateSetDeletion(set)
    SetService->>SetService: checkActiveCycles(setId)
    
    SetService->>SetRepository: delete(set)
    SetRepository->>Database: DELETE FROM sets WHERE id = ?
    Database-->>SetRepository: Set deleted
    SetRepository-->>SetService: Deletion successful
    
    SetService-->>SetController: DeletionResult(success)
    SetController-->>APIGateway: 204 No Content
    APIGateway-->>MobileApp: 204 No Content
```

### 5.2 Delete Set with Active Cycles

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant SetController as Set Controller
    participant SetService as Set Service
    participant SetRepository as Set Repository
    participant Database as PostgreSQL

    MobileApp->>APIGateway: DELETE /api/sets/{setId}
    Note over MobileApp,APIGateway: Authorization: Bearer {accessToken}
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>SetController: Forward request
    
    SetController->>SetService: deleteSet(setId, userId)
    
    SetService->>SetRepository: findById(setId)
    SetRepository->>Database: SELECT * FROM sets WHERE id = ?
    Database-->>SetRepository: Set data
    SetRepository-->>SetService: Set entity
    
    SetService->>SetService: validateSetOwnership(set, userId)
    SetService->>SetService: validateSetDeletion(set)
    SetService->>SetService: checkActiveCycles(setId)
    Note over SetService: Active cycles found
    
    SetService-->>SetController: BusinessRuleError("Cannot delete set with active cycles")
    SetController-->>APIGateway: 400 Bad Request + Error message
    APIGateway-->>MobileApp: 400 Bad Request + Error message
```

## 6. Set Statistics Sequence

### 6.1 Get Set Statistics

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant SetController as Set Controller
    participant SetService as Set Service
    participant SetRepository as Set Repository
    participant StatisticsService as Statistics Service
    participant Database as PostgreSQL

    MobileApp->>APIGateway: GET /api/sets/{setId}/statistics
    Note over MobileApp,APIGateway: Authorization: Bearer {accessToken}
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>SetController: Forward request
    
    SetController->>SetService: getSetStatistics(setId, userId)
    
    SetService->>SetRepository: findById(setId)
    SetRepository->>Database: SELECT * FROM sets WHERE id = ?
    Database-->>SetRepository: Set data
    SetRepository-->>SetService: Set entity
    
    SetService->>SetService: validateSetOwnership(set, userId)
    SetService->>StatisticsService: calculateSetStatistics(setId)
    
    StatisticsService->>Database: SELECT COUNT(*), AVG(score) FROM reviews WHERE set_id = ?
    Database-->>StatisticsService: Statistics data
    StatisticsService-->>SetService: SetStatistics object
    
    SetService-->>SetController: SetStatistics object
    SetController-->>APIGateway: 200 OK + Statistics data
    APIGateway-->>MobileApp: 200 OK + Statistics data
```

## Ghi chú kỹ thuật

### 1. Business Rules
- Set name không được trống và độ dài tối đa 100 ký tự
- Category phải thuộc danh sách cho phép
- Difficulty level từ 1-5
- Không thể xóa set có active cycles
- User chỉ có thể truy cập set của mình

### 2. Validation
- Input validation ở Controller layer
- Business rule validation ở Service layer
- Ownership validation cho mọi operation
- Data integrity validation

### 3. Error Handling
- Validation errors: 400 Bad Request
- Authorization errors: 403 Forbidden
- Resource not found: 404 Not Found
- Business rule violations: 400 Bad Request

### 4. Performance
- Database queries được optimize với indexes
- Pagination cho set list
- Caching cho frequently accessed sets
- Lazy loading cho set details
