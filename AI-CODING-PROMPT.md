# AI Coding Prompt - RepeatWise MVP

## 📋 Tổng quan

Tài liệu này là **Main Index** cho AI Coding Prompts. Vui lòng chọn prompt phù hợp với vai trò của bạn:

### 🎯 Chọn Prompt theo vai trò

#### 1. Backend Developer (Java Spring Boot)
👉 **[AI-CODING-PROMPT-BACKEND.md](AI-CODING-PROMPT-BACKEND.md)**
- Java 17 + Spring Boot 3 + PostgreSQL
- REST APIs, JPA Entities, Business Logic
- SRS Algorithm Implementation
- Authentication (JWT + Refresh Token)

#### 2. Frontend Web Developer (React)
👉 **[AI-CODING-PROMPT-WEB.md](AI-CODING-PROMPT-WEB.md)**
- React 18 + TypeScript + Vite
- TanStack Query, React Router
- Tailwind CSS + Shadcn/ui
- Forms (React Hook Form + Zod)

#### 3. Mobile Developer (React Native)
👉 **[AI-CODING-PROMPT-MOBILE.md](AI-CODING-PROMPT-MOBILE.md)**
- React Native 0.73+ + TypeScript
- React Navigation, React Native Paper
- Platform-specific features (iOS/Android)
- Push Notifications

---

## 🚀 Quick Start

### Bạn là ai?

**Backend Developer?** → Đọc [Backend Prompt](AI-CODING-PROMPT-BACKEND.md)

**Frontend Web Developer?** → Đọc [Web Prompt](AI-CODING-PROMPT-WEB.md)

**Mobile Developer?** → Đọc [Mobile Prompt](AI-CODING-PROMPT-MOBILE.md)

**Full-Stack Developer?** → Đọc cả 3 prompts theo thứ tự: Backend → Web → Mobile

---

## 📚 Document Overview

### RepeatWise MVP
RepeatWise là ứng dụng flashcard learning với SRS (Spaced Repetition System), bao gồm:
- **Backend**: Java 17 + Spring Boot 3 + PostgreSQL
- **Frontend Web**: React 18 + TypeScript + Tailwind CSS
- **Frontend Mobile**: React Native + React Native Paper

## 📚 Tài liệu cần đọc

### 1️⃣ Business & Requirements (Hiểu bài toán)
- **[Business Overview](01-business/product-overview.md)** - Tổng quan sản phẩm
- **[MVP Scope](01-business/mvp-scope.md)** - Phạm vi MVP
- **[System Spec](02-system-analysis/system-spec.md)** - Yêu cầu hệ thống chi tiết
- **[Use Cases](02-system-analysis/use-cases/)** - 24 use cases chi tiết

### 2️⃣ Design Documents (Kiến trúc & thiết kế)
**Đọc theo thứ tự sau:**

#### A. API Design
- **[API Endpoints Summary](03-design/api/api-endpoints-summary.md)** ⭐ **BẮT ĐẦU TỪ ĐÂY**
  - Tất cả API endpoints với request/response examples
  - Authentication flow (JWT + Refresh Token)
  - Pagination, filtering, sorting

#### B. Architecture Patterns
- **[Design Patterns](03-design/architecture/design-patterns.md)** ⭐ **BẮT BUỘC ĐỌC**
  - Composite Pattern (Folder tree)
  - Strategy Pattern (SRS behaviors)
  - Visitor Pattern (Statistics)
  - Repository, DTO, Domain Events
  - **Có code examples đầy đủ**

- **[SRS Algorithm Design](03-design/architecture/srs-algorithm-design.md)** ⭐ **CORE LOGIC**
  - 7-box system với intervals
  - Rating calculations (Again, Hard, Good, Easy)
  - Forgotten card strategies
  - Performance optimizations

#### C. Backend Architecture
- **[Backend Detailed Design](03-design/architecture/backend-detailed-design.md)** ⭐
  - Layered architecture: Controller → Service → Repository
  - Package structure chi tiết
  - Exception handling, validation
  - Transaction management

#### D. Database Design
- **[Database Schema](03-design/database/schema.md)** ⭐
  - PostgreSQL tables with constraints
  - Relationships (1-1, 1-N, N-N)
  - Soft delete strategy

- **[JPA Entity Design](03-design/database/jpa-entity-design.md)** ⭐
  - JPA entities với mappings
  - Cascade strategies
  - Fetch strategies (LAZY loading)
  - N+1 prevention

- **[Indexing Strategy](03-design/database/indexing-strategy.md)** ⭐
  - Critical indexes (đặc biệt: `idx_card_box_user_due`)
  - Performance tuning

#### E. Frontend Architecture
- **[Frontend Architecture](03-design/architecture/frontend-architecture.md)** ⭐
  - React + React Native structure
  - State management (React Query, Context, Zustand)
  - Component patterns
  - Token refresh interceptor

#### F. Security
- **[Authentication Model](03-design/security/authn-authz-model.md)** ⭐
  - JWT with Refresh Token (15min/7 days)
  - Token rotation
  - Security best practices

### 3️⃣ Detail Design (Implementation specs)
**Đọc khi implement từng module:**

#### Backend Detail
- **[Entity Specifications](04-detail-design/01-entity-specifications.md)** - JPA entity specs
- **[API Request/Response Specs](04-detail-design/02-api-request-response-specs.md)** - DTOs & validation
- **[Business Logic Flows](04-detail-design/03-business-logic-flows.md)** - Service methods pseudo-code
- **[SRS Algorithm Implementation](04-detail-design/04-srs-algorithm-implementation.md)** - SRS chi tiết
- **[Validation Rules](04-detail-design/05-validation-rules.md)** - Tất cả validation rules
- **[Error Handling Specs](04-detail-design/06-error-handling-specs.md)** - Error codes & messages

#### Frontend Detail
- **[Frontend Web Specs](04-detail-design/07-frontend-web-specs.md)** - React components specs
- **[Frontend Mobile Specs](04-detail-design/08-frontend-mobile-specs.md)** - React Native screens specs
- **[Wireframes Web](04-detail-design/09-wireframes-web.md)** - Web UI layout
- **[Wireframes Mobile](04-detail-design/10-wireframes-mobile.md)** - Mobile UI layout

### 4️⃣ Coding Conventions (TUÂN THỦ NGHIÊM NGẶT)
- **[Backend Coding Convention](05-quality/coding-convention-backend.md)** ⭐ **BẮT BUỘC**
  - Java 17 + Spring Boot 3 best practices
  - Google Java Style Guide
  - Clean Code principles (tên rõ ràng, method ≤30 lines, no deep nesting)
  - Apache Commons libraries (StringUtils, CollectionUtils, FileUtils)
  - MessageSource cho i18n
  - Lombok annotations
  - Transaction management
  - Exception handling patterns

- **[Web Coding Convention](05-quality/coding-convention-web.md)** ⭐ **BẮT BUỘC**
  - React 18 + TypeScript best practices
  - Airbnb Style Guide
  - Component structure (≤30 lines)
  - TypeScript strict mode
  - TanStack Query patterns
  - Form validation (React Hook Form + Zod)
  - i18next cho multi-language

- **[Mobile Coding Convention](05-quality/coding-convention-mobile.md)** ⭐ **BẮT BUỘC**
  - React Native best practices
  - Platform-specific code (iOS/Android)
  - Performance optimizations
  - FlatList optimizations
  - Navigation patterns

---

## 🎯 Nguyên tắc coding

### 1. Clean Code (BẮT BUỘC TUÂN THỦ)

#### ✅ Tên biến/function/class rõ ràng
```java
// ❌ SAI
public class UsrMgr { }
String n;
int cnt;
void proc() { }

// ✅ ĐÚNG
public class UserManager { }
String userName;
int totalUserCount;
void processUserRegistration() { }
```

#### ✅ Method/Component ≤ 30 lines
- Tách thành methods/components nhỏ hơn
- Mỗi method chỉ làm 1 việc
- Extract helper methods

#### ✅ Tránh deep nesting (>2 levels)
- Sử dụng Early Return
- Guard Clauses
- Extract methods

#### ✅ Dùng `const`/`final` cho biến không đổi
```java
// Java
private final FolderRepository folderRepository;
final String processedName = name.trim();

// TypeScript
const userId = '123';
const MAX_DEPTH = 10;
```

#### ✅ Tối đa 3 parameters
```java
// ❌ SAI
public Folder create(String name, String desc, UUID parentId, UUID userId, int depth)

// ✅ ĐÚNG
public FolderResponse createFolder(CreateFolderRequest request, UUID userId)
```

### 2. Libraries & Utilities (BẮT BUỘC SỬ DỤNG)

#### Backend - Apache Commons
```java
// LUÔN dùng Apache Commons
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;

if (StringUtils.isBlank(name)) { }
if (CollectionUtils.isEmpty(list)) { }
String content = FileUtils.readFileToString(file, UTF_8);
```

#### Frontend Web - Lodash & date-fns
```typescript
import { isEmpty, uniq, debounce } from 'lodash-es';
import { format, formatDistance } from 'date-fns';

if (isEmpty(folders)) { }
const uniqueIds = uniq(ids);
const formattedDate = format(new Date(), 'PPP');
```

#### Frontend Mobile - Lodash & date-fns
```typescript
import { isEmpty, debounce } from 'lodash';
import { format } from 'date-fns';
```

### 3. Internationalization (i18n) (BẮT BUỘC)

#### Backend - MessageSource
```java
// src/main/resources/messages.properties
error.folder.not.found=Không tìm thấy thư mục với ID {0}
error.folder.max.depth=Thư mục không thể vượt quá {0} cấp độ

// Service
throw new ResourceNotFoundException(
    "FOLDER_002",
    getMessage("error.folder.not.found", folderId)
);
```

#### Frontend Web - react-i18next
```typescript
// src/i18n/locales/vi.json
{
  "folder": {
    "title": "Thư mục",
    "deleteConfirm": "Bạn có chắc chắn muốn xóa thư mục này?"
  }
}

// Component
const { t } = useTranslation();
<button>{t('common.delete')}</button>
```

#### Frontend Mobile - i18n-js
```typescript
import i18n from '@/i18n/config';
<Button title={i18n.t('common.save')} />
```

### 4. Design Patterns (BẮT BUỘC ÁP DỤNG)

#### Composite Pattern - Folder Tree
```java
@Entity
public class Folder {
    @ManyToOne
    private Folder parentFolder;

    @OneToMany(mappedBy = "parentFolder")
    private List<Folder> subFolders;

    @OneToMany(mappedBy = "folder")
    private List<Deck> decks;
}
```

#### Strategy Pattern - Review Order
```java
public interface ReviewOrderStrategy {
    List<Card> orderCards(List<Card> cards);
}

public class RandomReviewStrategy implements ReviewOrderStrategy {
    @Override
    public List<Card> orderCards(List<Card> cards) {
        Collections.shuffle(cards);
        return cards;
    }
}
```

#### Repository Pattern - Spring Data JPA
```java
public interface FolderRepository extends JpaRepository<Folder, UUID> {
    List<Folder> findByUserAndParentFolderIsNullAndDeletedAtIsNull(User user);

    @Query("SELECT f FROM Folder f LEFT JOIN FETCH f.decks WHERE f.id = :id")
    Optional<Folder> findByIdWithDecks(@Param("id") UUID id);
}
```

### 5. State Management (Frontend)

#### React Query - Server State
```typescript
// Hooks
export function useFolderTree() {
  return useQuery({
    queryKey: folderKeys.tree(),
    queryFn: () => folderService.getFolderTree(),
    staleTime: 5 * 60 * 1000,
  });
}

export function useCreateFolder() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: CreateFolderRequest) => folderService.createFolder(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: folderKeys.tree() });
    },
  });
}
```

#### Context API - Auth State
```typescript
interface AuthContextType {
  user: User | null;
  isAuthenticated: boolean;
  login: (email: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
}
```

#### Zustand - UI State
```typescript
interface UIState {
  sidebarOpen: boolean;
  toggleSidebar: () => void;
  theme: 'light' | 'dark' | 'system';
  setTheme: (theme: 'light' | 'dark' | 'system') => void;
}

export const useUIStore = create<UIState>()(
  persist(
    (set) => ({
      sidebarOpen: true,
      toggleSidebar: () => set((state) => ({ sidebarOpen: !state.sidebarOpen })),
      theme: 'system',
      setTheme: (theme) => set({ theme }),
    }),
    { name: 'repeatwise-ui' }
  )
);
```

---

## 🔑 Key Implementation Points

### 1. Authentication Flow
- **Access Token**: 15 minutes, JWT in memory
- **Refresh Token**: 7 days, HTTP-only cookie
- **Token Rotation**: New refresh token on each refresh
- **Interceptor**: Auto-refresh expired access token

### 2. SRS Algorithm (Core Logic)
- **7-box system**: Intervals [1, 3, 7, 14, 30, 60, 120] days
- **Rating → Box transition**:
  - Again: Move to Box 1 (or down N boxes, or stay)
  - Hard: Stay in box, half interval
  - Good: Move to next box
  - Easy: Skip 1 box, 4x interval
- **Critical Index**: `idx_card_box_user_due (user_id, due_date, current_box)`

### 3. Performance Optimizations
- **N+1 Prevention**: Use `JOIN FETCH` hoặc `@EntityGraph`
- **Batch Operations**: 1000 items per transaction
- **Async Operations**:
  - Folder copy: >50 items
  - Deck copy: >1000 cards
  - Background jobs: Spring @Async
- **Caching**: Denormalized stats table (TTL: 5 min)

### 4. Error Handling
- **Custom Exceptions**:
  - `ResourceNotFoundException` → 404
  - `ValidationException` → 400
  - `ForbiddenException` → 403
  - `UnauthorizedException` → 401
- **Global Exception Handler**: `@RestControllerAdvice`
- **Error Response Format**:
```json
{
  "timestamp": "2025-01-10T10:00:00Z",
  "status": 404,
  "error": "NOT_FOUND",
  "errorCode": "FOLDER_002",
  "message": "Không tìm thấy thư mục với ID abc-123",
  "path": "/api/folders/abc-123"
}
```

### 5. Validation Rules
- **Backend**: Bean Validation (`@NotBlank`, `@Size`, `@Email`)
- **Frontend**: React Hook Form + Zod
- **Business Rules**:
  - Folder max depth: 10
  - Folder name: 1-100 characters
  - Import file: max 10,000 rows, 50MB
  - Review limit: 200 cards/session

---

## 📝 Coding Checklist

### Trước khi code
- [ ] Đọc use case liên quan
- [ ] Đọc API spec cho endpoint
- [ ] Đọc entity spec cho database
- [ ] Đọc business logic flow
- [ ] Đọc validation rules
- [ ] Hiểu error scenarios

### Khi code Backend
- [ ] Tuân thủ [Backend Coding Convention](05-quality/coding-convention-backend.md)
- [ ] Package structure đúng: `com.repeatwise.{module}`
- [ ] Class naming: `EntityServiceImpl`, `EntityRepository`, `EntityController`
- [ ] Method ≤ 30 lines, extract helpers
- [ ] Dùng Apache Commons (StringUtils, CollectionUtils, etc.)
- [ ] Dùng MessageSource cho i18n
- [ ] Transaction boundaries đúng (`@Transactional`)
- [ ] Exception handling đầy đủ
- [ ] Logging với SLF4J (`@Slf4j`)
- [ ] N+1 prevention (`JOIN FETCH`)

### Khi code Frontend Web
- [ ] Tuân thủ [Web Coding Convention](05-quality/coding-convention-web.md)
- [ ] Component ≤ 30 lines
- [ ] TypeScript strict mode, no `any`
- [ ] Props interface rõ ràng
- [ ] TanStack Query cho API calls
- [ ] Error handling với try-catch
- [ ] Loading & error states
- [ ] i18next cho text
- [ ] Tailwind CSS classes, dùng `cn()` utility
- [ ] Responsive design

### Khi code Frontend Mobile
- [ ] Tuân thủ [Mobile Coding Convention](05-quality/coding-convention-mobile.md)
- [ ] Screen ≤ 30 lines
- [ ] TypeScript strict mode
- [ ] StyleSheet.create (NO inline styles)
- [ ] FlatList cho lists (NOT ScrollView + map)
- [ ] Platform-specific code (`Platform.select()`)
- [ ] SafeAreaView cho screens
- [ ] Type-safe navigation
- [ ] i18n-js cho text
- [ ] Performance optimizations (memo, useMemo, useCallback)

### Testing
- [ ] Unit tests cho service logic (coverage ≥ 70%)
- [ ] Integration tests cho repositories
- [ ] Controller tests với MockMvc
- [ ] Frontend component tests
- [ ] Hook tests
- [ ] Edge case tests

---

## 🚀 Implementation Roadmap

### Phase 1: Database & Entities
1. Create PostgreSQL schema (Flyway migrations)
2. Implement JPA entities với mappings
3. Create repositories với custom queries
4. Add indexes (especially `idx_card_box_user_due`)

### Phase 2: Backend Core
1. Implement service layer (business logic)
2. Implement strategy patterns (ReviewOrder, ForgottenCard)
3. Implement SRS algorithm
4. Add validation & error handling
5. Add transaction management

### Phase 3: REST APIs
1. Implement controllers
2. Create Request/Response DTOs
3. Add MapStruct mappers
4. Test with Postman/MockMvc

### Phase 4: Frontend Web
1. Setup React project (Vite + TypeScript)
2. Setup state management (React Query + Context + Zustand)
3. Implement authentication flow
4. Implement folder tree components
5. Implement review session
6. Add i18n & theme support

### Phase 5: Frontend Mobile
1. Setup React Native project
2. Setup navigation (React Navigation)
3. Implement screens
4. Add gestures & animations
5. Setup push notifications
6. Platform-specific features

### Phase 6: Integration & Testing
1. End-to-end testing
2. Performance testing
3. Security testing
4. Bug fixes

---

## 📖 Quick Reference Links

### Must-Read Documents (Priority Order)
1. [API Endpoints Summary](03-design/api/api-endpoints-summary.md) ⭐
2. [Design Patterns](03-design/architecture/design-patterns.md) ⭐
3. [SRS Algorithm Design](03-design/architecture/srs-algorithm-design.md) ⭐
4. [Backend Coding Convention](05-quality/coding-convention-backend.md) ⭐
5. [Web Coding Convention](05-quality/coding-convention-web.md) ⭐
6. [Database Schema](03-design/database/schema.md) ⭐
7. [JPA Entity Design](03-design/database/jpa-entity-design.md) ⭐

### Implementation Details
- [Business Logic Flows](04-detail-design/03-business-logic-flows.md)
- [Validation Rules](04-detail-design/05-validation-rules.md)
- [Error Handling Specs](04-detail-design/06-error-handling-specs.md)
- [Frontend Web Specs](04-detail-design/07-frontend-web-specs.md)
- [Frontend Mobile Specs](04-detail-design/08-frontend-mobile-specs.md)

### UI/UX Reference
- [Web Wireframes](04-detail-design/09-wireframes-web.md)
- [Mobile Wireframes](04-detail-design/10-wireframes-mobile.md)

---

## ✅ Success Criteria

Code được coi là **PASS** khi:
1. ✅ Tuân thủ 100% coding conventions
2. ✅ Tất cả use cases đã implement
3. ✅ Validation rules đầy đủ
4. ✅ Error handling đầy đủ
5. ✅ Performance đạt yêu cầu (API <500ms p95)
6. ✅ Tests pass với coverage ≥70%
7. ✅ No critical bugs
8. ✅ Security best practices applied
9. ✅ Code review approved

---

---

## 📂 File Structure

```
00_docs/
├── AI-CODING-PROMPT.md                    # ← Main index (you are here)
├── AI-CODING-PROMPT-BACKEND.md            # Backend specific prompt
├── AI-CODING-PROMPT-WEB.md                # Web specific prompt
├── AI-CODING-PROMPT-MOBILE.md             # Mobile specific prompt
│
├── 01-business/                           # Business requirements
├── 02-system-analysis/                    # System specs & use cases
├── 03-design/                             # Design documents
├── 04-detail-design/                      # Implementation specs
└── 05-quality/                            # Coding conventions
    ├── coding-convention-backend.md       # ⭐ Backend rules
    ├── coding-convention-web.md           # ⭐ Web rules
    └── coding-convention-mobile.md        # ⭐ Mobile rules
```

---

## 🎯 Bắt đầu từ đâu?

### 👨‍💻 Backend Developer
👉 **Đọc [AI-CODING-PROMPT-BACKEND.md](AI-CODING-PROMPT-BACKEND.md)**

Bao gồm:
- Java 17 + Spring Boot 3 best practices
- Apache Commons, MessageSource, Lombok
- JPA Entities, Repositories, Services
- REST APIs, Authentication (JWT + Refresh Token)
- SRS Algorithm Implementation
- Database design & indexing strategy

### 👩‍💻 Frontend Web Developer
👉 **Đọc [AI-CODING-PROMPT-WEB.md](AI-CODING-PROMPT-WEB.md)**

Bao gồm:
- React 18 + TypeScript best practices
- TanStack Query, Context API, Zustand
- Tailwind CSS + Shadcn/ui components
- React Hook Form + Zod validation
- Token refresh interceptor
- i18next multi-language support

### 📱 Mobile Developer
👉 **Đọc [AI-CODING-PROMPT-MOBILE.md](AI-CODING-PROMPT-MOBILE.md)**

Bao gồm:
- React Native 0.73+ best practices
- React Navigation, React Native Paper
- StyleSheet.create (NO inline styles)
- FlatList optimizations
- Platform-specific code (iOS/Android)
- Push notifications setup

---

## ✅ Success Criteria

Code được coi là **PASS** khi:
1. ✅ Tuân thủ 100% coding conventions
2. ✅ Tất cả use cases đã implement
3. ✅ Validation rules đầy đủ
4. ✅ Error handling đầy đủ
5. ✅ Performance đạt yêu cầu (API <500ms p95)
6. ✅ Tests pass với coverage ≥70%
7. ✅ No critical bugs
8. ✅ Security best practices applied
9. ✅ Code review approved

---

**Version**: 2.0 (Tách thành 3 prompts riêng)
**Last Updated**: 2025-01-10
**Status**: Ready for AI Coding
**Owner**: Technical Team

**Happy Coding! 🚀**
