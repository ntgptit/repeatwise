# Detail Design - RepeatWise MVP

## Mục đích

Detail Design cung cấp **specifications chi tiết** để chatbot AI có thể:
- Hiểu rõ logic nghiệp vụ
- Implement code chính xác
- Validate input/output đúng quy tắc
- Xử lý errors phù hợp

**QUAN TRỌNG**: Tài liệu này **KHÔNG chứa code thực tế**, chỉ mô tả logic, quy tắc, flow để AI có thể tự viết code.

## Nguyên tắc

### 1. Logical Detail Design
- **Pseudo-code**: Mô tả thuật toán bằng ngôn ngữ tự nhiên + pseudo-code
- **Flow diagrams**: Mô tả luồng xử lý từng bước
- **Validation rules**: Quy tắc kiểm tra input/output
- **Business rules**: Logic nghiệp vụ chi tiết
- **Error scenarios**: Xử lý lỗi và edge cases

### 2. KHÔNG bao gồm
- ❌ Code Java/TypeScript thực tế
- ❌ Framework-specific code (Spring annotations, React hooks)
- ❌ UI/UX mockups
- ❌ Infrastructure details (deployment, CI/CD)

### 3. Dành cho AI Coding
- ✅ Rõ ràng, không mơ hồ
- ✅ Đầy đủ chi tiết để AI implement
- ✅ Có examples cho mọi case
- ✅ Validation rules đầy đủ
- ✅ Error handling chi tiết

## Cấu trúc tài liệu

```
docs/04-detail-design/
├── README.md                                 # ← Bạn đang ở đây
│
├── BACKEND SPECS (6 files):
├── 01-entity-specifications.md               # JPA Entities specs
├── 02-api-request-response-specs.md          # API contracts chi tiết
├── 03-business-logic-flows.md                # Service layer logic flows
├── 04-srs-algorithm-implementation.md        # SRS algorithm chi tiết
├── 05-validation-rules.md                    # Tất cả validation rules
├── 06-error-handling-specs.md                # Error codes & messages
│
├── FRONTEND SPECS (2 files):
├── 07-frontend-web-specs.md                  # React Web app specs
├── 08-frontend-mobile-specs.md               # React Native app specs
│
└── UI/UX WIREFRAMES (2 files):
    ├── 09-wireframes-web.md                  # Web wireframes (ASCII art)
    └── 10-wireframes-mobile.md               # Mobile wireframes (ASCII art)
```

## Hướng dẫn đọc (cho AI)

### Bước 1: Đọc Entity Specifications
**File**: [01-entity-specifications.md](01-entity-specifications.md)

**Nội dung**:
- Database tables specifications
- Entity relationships (1-1, 1-N, N-N)
- Field types, constraints, defaults
- Indexes và performance considerations
- Soft delete strategy
- Audit fields (createdAt, updatedAt)

**Outputs**:
- Tạo JPA entities với annotations phù hợp
- Tạo repositories với custom queries
- Hiểu relationships để tránh N+1

### Bước 2: Đọc API Request/Response Specs
**File**: [02-api-request-response-specs.md](02-api-request-response-specs.md)

**Nội dung**:
- Request DTOs với validation annotations
- Response DTOs với field descriptions
- Error response formats
- Pagination & filtering specs
- Example requests/responses cho mọi endpoint

**Outputs**:
- Tạo Request/Response DTOs
- Implement validation logic
- Map entities ↔ DTOs

### Bước 3: Đọc Business Logic Flows
**File**: [03-business-logic-flows.md](03-business-logic-flows.md)

**Nội dung**:
- Service method flows (step-by-step)
- Decision trees (if-else logic)
- Transaction boundaries
- Domain events publishing
- Async operation triggers

**Outputs**:
- Implement service layer logic
- Handle transactions correctly
- Publish domain events

### Bước 4: Đọc SRS Algorithm
**File**: [04-srs-algorithm-implementation.md](04-srs-algorithm-implementation.md)

**Nội dung**:
- 7-box system logic
- Rating calculations (Again, Hard, Good, Easy)
- Interval calculations
- Forgotten card strategies
- Study modes (SRS, Cram, Random)
- Performance optimizations

**Outputs**:
- Implement SRS algorithm chính xác
- Calculate next review dates
- Handle edge cases

### Bước 5: Đọc Validation Rules
**File**: [05-validation-rules.md](05-validation-rules.md)

**Nội dung**:
- Input validation rules (field level)
- Business validation rules (logic level)
- Cross-field validations
- Custom validators
- Error messages cho mọi validation

**Outputs**:
- Implement validation logic đầy đủ
- Return correct error messages
- Handle edge cases

### Bước 6: Đọc Error Handling Specs
**File**: [06-error-handling-specs.md](06-error-handling-specs.md)

**Nội dung**:
- HTTP status codes mapping
- Error codes & messages
- Error response formats
- Exception hierarchy
- Recovery strategies

**Outputs**:
- Implement exception handling
- Return consistent error responses
- Log errors properly

---

## Frontend Implementation (Web + Mobile)

### Bước 7: Đọc Frontend Web Specs
**File**: [07-frontend-web-specs.md](07-frontend-web-specs.md)

**Nội dung**:
- React 18 + TypeScript architecture
- Component specifications (20+ components)
- State management (TanStack Query + Context + Zustand)
- API integration với token refresh
- Form validation (React Hook Form + Zod)
- Theme implementation (Light/Dark/System)
- Performance optimizations
- Accessibility guidelines

**Outputs**:
- Implement React components
- Setup state management
- Integrate với backend APIs
- Handle authentication flow
- Implement theme switching

### Bước 8: Đọc Frontend Mobile Specs
**File**: [08-frontend-mobile-specs.md](08-frontend-mobile-specs.md)

**Nội dung**:
- React Native architecture
- Screen specifications (15 screens)
- Navigation structure (tabs + modals)
- Platform-specific features (iOS + Android)
- Gestures & animations
- Push notifications
- Performance optimizations
- Accessibility (VoiceOver/TalkBack)

**Outputs**:
- Implement React Native screens
- Setup navigation (React Navigation)
- Handle platform differences
- Implement flashcard animations
- Setup push notifications

---

## UI/UX Design (Wireframes)

### Bước 9: Xem Wireframes Web
**File**: [09-wireframes-web.md](09-wireframes-web.md)

**Nội dung**:
- 25+ screens với ASCII art wireframes
- Responsive layouts (Desktop/Tablet/Mobile)
- All page types: Auth, Dashboard, Folder, Deck, Review, Stats, Settings
- Modal dialogs: Create, Import, Copy, Delete
- Component states: Loading, Empty, Error, Success
- Interactions & animations specifications
- Dark mode design
- Accessibility notes

**Sử dụng**:
- UI/UX designers: Reference cho mockups
- Frontend developers: Layout implementation guide
- QA: Visual testing reference

### Bước 10: Xem Wireframes Mobile
**File**: [10-wireframes-mobile.md](10-wireframes-mobile.md)

**Nội dung**:
- 40+ screens với ASCII art wireframes
- Mobile-optimized layouts (375px width)
- All screens: Login, Home, Folders, Decks, Review, Stats, Settings
- Platform-specific UI (iOS vs Android)
- Gestures: Swipe, long-press, pull-to-refresh
- Animations: Flip cards, slide transitions
- Native components: Action sheets, bottom sheets
- Loading/Empty/Error states

**Sử dụng**:
- Mobile designers: Reference cho native design
- Mobile developers: Screen layout guide
- QA: Mobile testing scenarios

## Đọc song song với docs/03-design

Detail Design **CHI TIẾT HÓA** các design patterns và architecture từ docs/03-design.

**Ví dụ**:
- **docs/03-design/architecture/design-patterns.md**: Mô tả Composite Pattern cho Folder tree
- **docs/04-detail-design/03-business-logic-flows.md**: Chi tiết hóa logic create/move/delete folders với pseudo-code

**Mapping**:

| docs/03-design | docs/04-detail-design | Mục đích |
|----------------|-----------------------|----------|
| database/schema.md | 01-entity-specifications.md | Tables → JPA Entities |
| api/api-endpoints-summary.md | 02-api-request-response-specs.md | Endpoints → DTOs & validation |
| architecture/backend-detailed-design.md | 03-business-logic-flows.md | Layered architecture → Service logic |
| architecture/srs-algorithm-design.md | 04-srs-algorithm-implementation.md | Algorithm design → Implementation specs |
| - | 05-validation-rules.md | All validations |
| - | 06-error-handling-specs.md | All error scenarios |

## Ví dụ minh họa

### Example 1: Create Folder Flow

**docs/03-design**: Mô tả high-level
```
FolderService.createFolder():
1. Validate parent folder exists
2. Check max depth constraint
3. Create folder entity
4. Update path (materialized path)
5. Save to database
6. Publish FolderCreatedEvent
```

**docs/04-detail-design**: Chi tiết hóa với pseudo-code
```
FUNCTION createFolder(request, userId):
  // Step 1: Validate parent
  IF request.parentFolderId IS NOT NULL THEN
    parent = folderRepository.findById(request.parentFolderId)
    IF parent NOT FOUND THEN
      THROW ResourceNotFoundException("Parent folder not found")
    END IF
    IF parent.userId != userId THEN
      THROW ForbiddenException("Cannot access parent folder")
    END IF
    IF parent.depth >= 9 THEN
      THROW MaxDepthExceededException("Max depth 10 exceeded")
    END IF
  END IF

  // Step 2: Create folder
  folder = new Folder()
  folder.name = request.name
  folder.description = request.description
  folder.userId = userId
  folder.parentFolder = parent
  folder.depth = parent ? parent.depth + 1 : 0

  // Step 3: Calculate path (materialized path)
  IF parent IS NULL THEN
    folder.path = "/" + folder.id
  ELSE
    folder.path = parent.path + "/" + folder.id
  END IF

  // Step 4: Save
  savedFolder = folderRepository.save(folder)

  // Step 5: Publish event (async)
  eventPublisher.publish(FolderCreatedEvent(savedFolder.id, userId))

  // Step 6: Return response
  RETURN mapToResponse(savedFolder)
END FUNCTION
```

**AI có thể**: Đọc pseudo-code → Viết Java code với Spring Boot

### Example 2: SRS Rating Calculation

**docs/03-design**: Mô tả algorithm
```
Rating "Good":
- Move card to next box
- Set next review date based on box interval
```

**docs/04-detail-design**: Chi tiết hóa công thức
```
FUNCTION handleGoodRating(card, cardBoxPosition):
  // Step 1: Move to next box
  currentBox = cardBoxPosition.currentBox
  IF currentBox < 7 THEN
    nextBox = currentBox + 1
  ELSE
    nextBox = 7  // Stay in box 7
  END IF

  // Step 2: Calculate interval
  boxIntervals = [1, 3, 7, 14, 30, 60, 120]  // days
  intervalDays = boxIntervals[nextBox - 1]

  // Step 3: Calculate ease factor (SM-2 inspired)
  easeFactor = cardBoxPosition.easeFactor
  easeFactor = easeFactor + 0.1  // Increase ease
  easeFactor = max(1.3, min(2.5, easeFactor))  // Clamp

  // Step 4: Adjust interval with ease
  adjustedInterval = intervalDays * easeFactor
  adjustedInterval = round(adjustedInterval)

  // Step 5: Calculate due date
  dueDate = today + adjustedInterval days

  // Step 6: Update card box position
  cardBoxPosition.currentBox = nextBox
  cardBoxPosition.easeFactor = easeFactor
  cardBoxPosition.intervalDays = adjustedInterval
  cardBoxPosition.dueDate = dueDate
  cardBoxPosition.lastReviewedAt = now()
  cardBoxPosition.reviewCount += 1

  // Step 7: Save
  cardBoxPositionRepository.save(cardBoxPosition)

  // Step 8: Log review
  reviewLog = new ReviewLog()
  reviewLog.cardId = card.id
  reviewLog.userId = userId
  reviewLog.rating = "GOOD"
  reviewLog.previousBox = currentBox
  reviewLog.newBox = nextBox
  reviewLog.intervalDays = adjustedInterval
  reviewLog.reviewedAt = now()
  reviewLogRepository.save(reviewLog)

  RETURN ReviewResult(success=true, nextReviewDate=dueDate)
END FUNCTION
```

**AI có thể**: Đọc pseudo-code → Implement exact logic

## Quality Checklist (cho AI)

Khi implement, AI cần check:

### Functional Requirements
- [ ] Tất cả use cases đã covered
- [ ] Business rules đã implement đúng
- [ ] Edge cases đã handle
- [ ] Validation rules đã apply đầy đủ

### Non-Functional Requirements
- [ ] Performance: Queries đã optimize (indexes, JOIN FETCH)
- [ ] Security: Authentication/Authorization đã check
- [ ] Error Handling: Tất cả exceptions đã handle
- [ ] Logging: Critical operations đã log

### Code Quality
- [ ] Code dễ đọc, dễ maintain
- [ ] Follow SOLID principles
- [ ] Design patterns applied correctly
- [ ] Transaction boundaries đúng
- [ ] No N+1 queries

### Testing
- [ ] Unit tests cho service logic
- [ ] Integration tests cho repositories
- [ ] Controller tests cho APIs
- [ ] Edge case tests

## References

### Related Docs
- [Business Requirements](../01-business/)
- [System Analysis](../02-system-analysis/)
- [Design Documentation](../03-design/)
- [MVP Specification](../../repeatwise-mvp-spec.md)

### Design Patterns
- [Backend Detailed Design](../03-design/architecture/backend-detailed-design.md)
- [Design Patterns](../03-design/architecture/design-patterns.md)
- [SRS Algorithm Design](../03-design/architecture/srs-algorithm-design.md)

### Database
- [Database Schema](../03-design/database/schema.md)
- [JPA Entity Design](../03-design/database/jpa-entity-design.md)
- [Indexing Strategy](../03-design/database/indexing-strategy.md)

### API
- [API Endpoints Summary](../03-design/api/api-endpoints-summary.md)

## Summary

**Detail Design = Input for AI Vibe Coding**

Mục tiêu: AI đọc → Hiểu logic → Viết code chính xác

### Backend (6 documents):
1. **Entity specs** (database → JPA entities)
2. **API specs** (endpoints → DTOs + validation)
3. **Business logic flows** (services → methods + pseudo-code)
4. **SRS algorithm** (core SRS logic → rating calculations)
5. **Validation rules** (all rules → validation logic)
6. **Error handling** (all errors → exception handling)

### Frontend (2 documents):
7. **Web specs** (React app → components + state + API integration)
8. **Mobile specs** (React Native → screens + navigation + animations)

### UI/UX (2 documents):
9. **Web wireframes** (25+ screens → layouts + interactions + responsive)
10. **Mobile wireframes** (40+ screens → gestures + animations + platform-specific)

**Đọc theo thứ tự**:
- **Backend**: 1 → 2 → 3 → 4 → 5 → 6
- **Frontend Web**: 7 + 9 (specs + wireframes)
- **Frontend Mobile**: 8 + 10 (specs + wireframes)

**Kết quả**: AI có thể implement toàn bộ **backend + web + mobile + UI** từ 10 files này

---

**Document Version**: 1.0
**Last Updated**: 2025-01-10
**Next Review**: Before implementation
**Owner**: Technical Lead
**Status**: Ready for AI Coding
