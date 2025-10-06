# Learning Set Management Module - Detail Design

## 1. Module Overview

### 1.1 Objectives
Learning Set Management Module xử lý tất cả các hoạt động liên quan đến quản lý bộ học tập bao gồm:
- Tạo mới learning set
- Chỉnh sửa thông tin set
- Xóa learning set
- Xem danh sách và chi tiết set
- Quản lý trạng thái set (active, paused, completed)

### 1.2 Scope
- **In Scope**: CRUD operations cho learning sets, set items management, set status management
- **Out of Scope**: Review sessions (handled by Review Session Module), Statistics (handled by Statistics Module)

### 1.3 Dependencies
- **Database**: learning_sets table, set_items table, user_learning_sets table
- **External Services**: File storage service (for set images)
- **Security**: User authorization, data isolation

## 2. API Contracts

### 2.1 Learning Set CRUD Endpoints

#### POST /api/v1/learning-sets
**Request:**
```json
{
  "title": "Từ vựng tiếng Anh cơ bản",
  "description": "Bộ từ vựng tiếng Anh cơ bản cho người mới bắt đầu",
  "category": "language",
  "tags": ["english", "vocabulary", "beginner"],
  "isPublic": false,
  "items": [
    {
      "front": "Hello",
      "back": "Xin chào",
      "type": "text",
      "difficulty": "easy"
    },
    {
      "front": "Goodbye",
      "back": "Tạm biệt",
      "type": "text",
      "difficulty": "easy"
    }
  ]
}
```

**Response (Success - 201):**
```json
{
  "success": true,
  "message": "Learning set đã được tạo thành công",
  "data": {
    "setId": "uuid-here",
    "title": "Từ vựng tiếng Anh cơ bản",
    "description": "Bộ từ vựng tiếng Anh cơ bản cho người mới bắt đầu",
    "category": "language",
    "tags": ["english", "vocabulary", "beginner"],
    "isPublic": false,
    "itemCount": 2,
    "status": "active",
    "createdAt": "2024-12-19T10:00:00Z",
    "updatedAt": "2024-12-19T10:00:00Z"
  }
}
```

#### GET /api/v1/learning-sets
**Query Parameters:**
- `page`: Page number (default: 1)
- `size`: Page size (default: 20, max: 100)
- `category`: Filter by category
- `status`: Filter by status (active, paused, completed)
- `search`: Search in title and description
- `sortBy`: Sort field (title, createdAt, updatedAt)
- `sortOrder`: Sort order (asc, desc)

**Response:**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "setId": "uuid-here",
        "title": "Từ vựng tiếng Anh cơ bản",
        "description": "Bộ từ vựng tiếng Anh cơ bản cho người mới bắt đầu",
        "category": "language",
        "tags": ["english", "vocabulary", "beginner"],
        "isPublic": false,
        "itemCount": 2,
        "status": "active",
        "progress": {
          "totalItems": 2,
          "learnedItems": 0,
          "masteredItems": 0,
          "completionPercentage": 0
        },
        "createdAt": "2024-12-19T10:00:00Z",
        "updatedAt": "2024-12-19T10:00:00Z"
      }
    ],
    "pagination": {
      "page": 1,
      "size": 20,
      "totalElements": 1,
      "totalPages": 1,
      "hasNext": false,
      "hasPrevious": false
    }
  }
}
```

#### GET /api/v1/learning-sets/{setId}
**Response:**
```json
{
  "success": true,
  "data": {
    "setId": "uuid-here",
    "title": "Từ vựng tiếng Anh cơ bản",
    "description": "Bộ từ vựng tiếng Anh cơ bản cho người mới bắt đầu",
    "category": "language",
    "tags": ["english", "vocabulary", "beginner"],
    "isPublic": false,
    "status": "active",
    "items": [
      {
        "itemId": "uuid-here",
        "front": "Hello",
        "back": "Xin chào",
        "type": "text",
        "difficulty": "easy",
        "order": 1,
        "createdAt": "2024-12-19T10:00:00Z"
      },
      {
        "itemId": "uuid-here-2",
        "front": "Goodbye",
        "back": "Tạm biệt",
        "type": "text",
        "difficulty": "easy",
        "order": 2,
        "createdAt": "2024-12-19T10:00:00Z"
      }
    ],
    "progress": {
      "totalItems": 2,
      "learnedItems": 0,
      "masteredItems": 0,
      "completionPercentage": 0
    },
    "createdAt": "2024-12-19T10:00:00Z",
    "updatedAt": "2024-12-19T10:00:00Z"
  }
}
```

#### PUT /api/v1/learning-sets/{setId}
**Request:**
```json
{
  "title": "Từ vựng tiếng Anh cơ bản - Cập nhật",
  "description": "Bộ từ vựng tiếng Anh cơ bản cho người mới bắt đầu - Phiên bản cập nhật",
  "category": "language",
  "tags": ["english", "vocabulary", "beginner", "updated"],
  "isPublic": true
}
```

#### DELETE /api/v1/learning-sets/{setId}
**Response:**
```json
{
  "success": true,
  "message": "Learning set đã được xóa thành công"
}
```

### 2.2 Set Items Management

#### POST /api/v1/learning-sets/{setId}/items
**Request:**
```json
{
  "front": "Thank you",
  "back": "Cảm ơn",
  "type": "text",
  "difficulty": "easy"
}
```

#### PUT /api/v1/learning-sets/{setId}/items/{itemId}
**Request:**
```json
{
  "front": "Thank you very much",
  "back": "Cảm ơn rất nhiều",
  "type": "text",
  "difficulty": "medium"
}
```

#### DELETE /api/v1/learning-sets/{setId}/items/{itemId}
**Response:**
```json
{
  "success": true,
  "message": "Set item đã được xóa thành công"
}
```

### 2.3 Set Status Management

#### PUT /api/v1/learning-sets/{setId}/status
**Request:**
```json
{
  "status": "paused"
}
```

**Valid statuses**: `active`, `paused`, `completed`

## 3. Data Models

### 3.1 Learning Set Entity
```java
@Entity
@Table(name = "learning_sets")
public class LearningSet {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID setId;
    
    @Column(nullable = false)
    private String title;
    
    @Column(length = 1000)
    private String description;
    
    @Enumerated(EnumType.STRING)
    private SetCategory category;
    
    @ElementCollection
    @CollectionTable(name = "set_tags", joinColumns = @JoinColumn(name = "set_id"))
    @Column(name = "tag")
    private Set<String> tags;
    
    @Column(nullable = false)
    private Boolean isPublic;
    
    @Enumerated(EnumType.STRING)
    private SetStatus status;
    
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    
    @OneToMany(mappedBy = "learningSet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("order ASC")
    private List<SetItem> items;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @Version
    private Long version;
}
```

### 3.2 Set Item Entity
```java
@Entity
@Table(name = "set_items")
public class SetItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID itemId;
    
    @ManyToOne
    @JoinColumn(name = "set_id", nullable = false)
    private LearningSet learningSet;
    
    @Column(nullable = false, length = 2000)
    private String front;
    
    @Column(nullable = false, length = 2000)
    private String back;
    
    @Enumerated(EnumType.STRING)
    private ItemType type;
    
    @Enumerated(EnumType.STRING)
    private ItemDifficulty difficulty;
    
    @Column(nullable = false)
    private Integer order;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
```

### 3.3 User Learning Set Entity (for progress tracking)
```java
@Entity
@Table(name = "user_learning_sets")
public class UserLearningSet {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userSetId;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "set_id", nullable = false)
    private LearningSet learningSet;
    
    @Enumerated(EnumType.STRING)
    private SetStatus status;
    
    private LocalDateTime startedAt;
    
    private LocalDateTime lastReviewedAt;
    
    private LocalDateTime completedAt;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
```

## 4. Business Logic

### 4.1 Create Learning Set Logic
```pseudocode
FUNCTION createLearningSet(userId, createRequest):
    // Validate input
    IF NOT validateCreateSetRequest(createRequest):
        RETURN validationError
    
    // Check set limit per user
    userSetCount = learningSetRepository.countByOwnerId(userId)
    IF userSetCount >= MAX_SETS_PER_USER:
        RETURN setLimitExceededError
    
    // Create learning set
    learningSet = new LearningSet()
    learningSet.title = createRequest.title
    learningSet.description = createRequest.description
    learningSet.category = createRequest.category
    learningSet.tags = createRequest.tags
    learningSet.isPublic = createRequest.isPublic
    learningSet.status = ACTIVE
    learningSet.owner = userRepository.findById(userId)
    learningSet.createdAt = now()
    learningSet.updatedAt = now()
    
    // Save learning set
    savedSet = learningSetRepository.save(learningSet)
    
    // Create set items
    FOR EACH item IN createRequest.items:
        setItem = new SetItem()
        setItem.learningSet = savedSet
        setItem.front = item.front
        setItem.back = item.back
        setItem.type = item.type
        setItem.difficulty = item.difficulty
        setItem.order = item.order
        setItem.createdAt = now()
        setItem.updatedAt = now()
        
        setItemRepository.save(setItem)
    
    // Create user learning set record
    userLearningSet = new UserLearningSet()
    userLearningSet.user = userRepository.findById(userId)
    userLearningSet.learningSet = savedSet
    userLearningSet.status = ACTIVE
    userLearningSet.startedAt = now()
    userLearningSet.createdAt = now()
    userLearningSet.updatedAt = now()
    
    userLearningSetRepository.save(userLearningSet)
    
    RETURN successResponse(mapToSetDto(savedSet))
```

### 4.2 Update Learning Set Logic
```pseudocode
FUNCTION updateLearningSet(userId, setId, updateRequest):
    // Find learning set
    learningSet = learningSetRepository.findByIdAndOwnerId(setId, userId)
    IF learningSet IS NULL:
        RETURN setNotFoundError
    
    // Check if set is in review session
    IF reviewSessionService.isSetInActiveReview(setId, userId):
        RETURN setInUseError
    
    // Update fields
    IF updateRequest.title IS NOT NULL:
        learningSet.title = updateRequest.title
    
    IF updateRequest.description IS NOT NULL:
        learningSet.description = updateRequest.description
    
    IF updateRequest.category IS NOT NULL:
        learningSet.category = updateRequest.category
    
    IF updateRequest.tags IS NOT NULL:
        learningSet.tags = updateRequest.tags
    
    IF updateRequest.isPublic IS NOT NULL:
        learningSet.isPublic = updateRequest.isPublic
    
    learningSet.updatedAt = now()
    
    // Save changes
    updatedSet = learningSetRepository.save(learningSet)
    
    RETURN successResponse(mapToSetDto(updatedSet))
```

### 4.3 Delete Learning Set Logic
```pseudocode
FUNCTION deleteLearningSet(userId, setId):
    // Find learning set
    learningSet = learningSetRepository.findByIdAndOwnerId(setId, userId)
    IF learningSet IS NULL:
        RETURN setNotFoundError
    
    // Check if set is in review session
    IF reviewSessionService.isSetInActiveReview(setId, userId):
        RETURN setInUseError
    
    // Check if set has review history
    reviewCount = reviewSessionRepository.countBySetId(setId)
    IF reviewCount > 0:
        // Soft delete - mark as deleted
        learningSet.status = DELETED
        learningSet.updatedAt = now()
        learningSetRepository.save(learningSet)
    ELSE:
        // Hard delete - remove from database
        userLearningSetRepository.deleteBySetId(setId)
        setItemRepository.deleteBySetId(setId)
        learningSetRepository.delete(learningSet)
    
    RETURN successResponse("Learning set đã được xóa thành công")
```

### 4.4 Get Learning Sets with Pagination Logic
```pseudocode
FUNCTION getLearningSets(userId, pageRequest, filters):
    // Build query
    query = learningSetRepository.createQuery()
    query.whereOwnerId(userId)
    
    IF filters.category IS NOT NULL:
        query.whereCategory(filters.category)
    
    IF filters.status IS NOT NULL:
        query.whereStatus(filters.status)
    
    IF filters.search IS NOT NULL:
        query.whereTitleOrDescriptionContaining(filters.search)
    
    // Apply sorting
    IF filters.sortBy IS NOT NULL:
        query.orderBy(filters.sortBy, filters.sortOrder)
    ELSE:
        query.orderByCreatedAtDesc()
    
    // Apply pagination
    query.page(pageRequest.page, pageRequest.size)
    
    // Execute query
    result = query.execute()
    
    // Map to DTOs with progress information
    setDtos = []
    FOR EACH set IN result.content:
        progress = calculateSetProgress(set.setId, userId)
        setDto = mapToSetDto(set, progress)
        setDtos.add(setDto)
    
    RETURN successResponse({
        content: setDtos,
        pagination: mapToPaginationDto(result)
    })
```

## 5. Validation Rules

### 5.1 Learning Set Validation
- **Title**: Required, 1-200 characters
- **Description**: Optional, max 1000 characters
- **Category**: Required, must be valid enum value
- **Tags**: Optional, max 10 tags, each tag 1-50 characters
- **Items**: Required, min 1 item, max 1000 items per set
- **Public**: Boolean value

### 5.2 Set Item Validation
- **Front**: Required, 1-2000 characters
- **Back**: Required, 1-2000 characters
- **Type**: Required, must be valid enum value (text, image, audio)
- **Difficulty**: Required, must be valid enum value (easy, medium, hard)
- **Order**: Auto-generated, unique within set

### 5.3 Business Rules
- Maximum 50 learning sets per user
- Maximum 1000 items per learning set
- Cannot delete set that is currently in active review session
- Cannot modify set items if set has review history
- Public sets can be viewed by all users (read-only)

## 6. Error Handling

### 6.1 Error Codes
- `SET_001`: Learning set not found
- `SET_002`: Set limit exceeded
- `SET_003`: Set in active review session
- `SET_004`: Invalid set category
- `SET_005`: Set item limit exceeded
- `SET_006`: Invalid set item type
- `SET_007`: Set has review history
- `SET_008`: Unauthorized access to set

### 6.2 Error Response Format
```json
{
  "success": false,
  "error": {
    "code": "SET_002",
    "message": "Đã vượt quá giới hạn số lượng learning set",
    "details": [
      {
        "field": "setCount",
        "message": "Mỗi user chỉ được tạo tối đa 50 learning sets"
      }
    ],
    "timestamp": "2024-12-19T10:00:00Z",
    "requestId": "req-uuid-here"
  }
}
```

## 7. Security Considerations

### 7.1 Data Isolation
- Users can only access their own learning sets
- Public sets are read-only for other users
- Validate ownership before any modification operations

### 7.2 Input Validation
- Sanitize all text inputs to prevent XSS
- Validate file uploads for set images
- Limit input sizes to prevent DoS attacks

### 7.3 Rate Limiting
- Limit set creation to 10 per hour per user
- Limit item addition to 100 per hour per set
- Implement request throttling for bulk operations

## 8. Observability

### 8.1 Logging
```java
// Log set creation
log.info("Learning set created", 
    "setId", set.getSetId(), 
    "userId", set.getOwner().getUserId(),
    "itemCount", set.getItems().size());

// Log set deletion
log.warn("Learning set deleted", 
    "setId", setId, 
    "userId", userId,
    "hasReviewHistory", hasReviewHistory);
```

### 8.2 Metrics
- Learning sets created per day
- Average items per set
- Set deletion rate
- Public vs private set ratio
- Category distribution

### 8.3 Alerts
- High set creation rate (>100 per hour)
- Large set creation (>500 items)
- Frequent set deletions
- Database query performance issues

## 9. Testing Strategy

### 9.1 Unit Tests
- Entity validation
- Business logic functions
- Data mapping operations
- Validation rules

### 9.2 Integration Tests
- API endpoint testing
- Database operations
- Pagination functionality
- Search and filtering

### 9.3 Performance Tests
- Large set creation (1000 items)
- Pagination with large datasets
- Search performance
- Concurrent set operations

## 10. Dependencies

### 10.1 Internal Dependencies
- `LearningSetRepository`: Database operations
- `SetItemRepository`: Item management
- `UserLearningSetRepository`: Progress tracking
- `ReviewSessionService`: Check active sessions
- `ValidationService`: Input validation

### 10.2 External Dependencies
- Database (PostgreSQL)
- File Storage Service (for set images)
- Search Service (for advanced search)

### 10.3 Configuration
```yaml
learning-set:
  limits:
    max-sets-per-user: 50
    max-items-per-set: 1000
    max-tags-per-set: 10
  validation:
    title-max-length: 200
    description-max-length: 1000
    item-content-max-length: 2000
  rate-limiting:
    set-creation: 10
    item-addition: 100
    window-minutes: 60
```

---

**Document Version**: 1.0  
**Last Updated**: 2024-12-19  
**Next Review**: 2024-12-26  
**Owner**: Backend Team  
**Stakeholders**: Development Team, QA Team, Product Team
