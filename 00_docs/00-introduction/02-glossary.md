# Glossary - RepeatWise

Tài liệu này định nghĩa các thuật ngữ, khái niệm và abbreviations được sử dụng trong dự án RepeatWise.

## Thuật ngữ nghiệp vụ (Domain Terms)

### A

**Access Token**

- JWT token ngắn hạn (15 phút) để xác thực API requests
- Được lưu trong memory (không persistent)
- Phải có trong Authorization header của mỗi request
- Format: `Bearer <token>`

**Ascending Order** (Thứ tự tăng dần)

- Review order: từ Box 1 → Box 7
- Review từ khó → dễ
- User option trong SRS settings

**Async Operation** (Thao tác bất đồng bộ)

- Thao tác chạy background không block UI
- Ví dụ: Copy folder với > 50 items, Copy deck với > 1000 cards
- Có progress tracking và timeout protection
- Status: PROCESSING, COMPLETED, FAILED

### B

**Box System**

- Hệ thống 7 ô để quản lý SRS
- Mỗi ô có interval khác nhau: 1, 3, 7, 14, 30, 60, 120 ngày
- Card di chuyển giữa các ô dựa trên rating của user
- Fixed configuration (không customizable trong MVP)

**Box Position**

- Vị trí hiện tại của card trong Box System (1-7)
- Stored trong table `card_box_position`
- Quyết định interval và due date

**Breadcrumb Navigation**

- Navigation path hiển thị vị trí hiện tại trong folder tree
- Format: Home > English > IELTS > Vocabulary
- Click vào bất kỳ breadcrumb nào để navigate

### C

**Card (Flashcard)** (Thẻ)

- Thẻ học với 2 mặt: Front (câu hỏi) và Back (câu trả lời)
- Đơn vị cơ bản trong hệ thống học tập
- MVP: Basic text only (không có images/audio)
- Max length: 5000 characters per side

**Composite Pattern**

- Design pattern cho cấu trúc cây folders
- Folder có thể chứa sub-folders và decks
- Implement trong Java: Folder class có List<Folder> và List<Deck>

**Cram Mode** (Chế độ học nhanh)

- Chế độ học nhanh tất cả cards, không theo SRS schedule
- Không ảnh hưởng đến due date và box position
- Scope: deck hoặc folder (recursive)

**Current Box**

- Box hiện tại của card (1-7)
- Stored trong table `card_box_position`
- Used để tính interval và due date

### D

**Daily Limit**

- Giới hạn số cards học mỗi ngày
- New cards per day: default 20, configurable
- Max reviews per day: default 200, configurable
- Auto-pause khi đạt limit, có option override

**Deck**

- Bộ thẻ học, chứa nhiều flashcards cùng chủ đề
- Có thể thuộc folder hoặc standalone (root level)
- Metadata: name, description, card count, due cards count

**Denormalization**

- Lưu duplicate data để optimize read performance
- Ví dụ: `folder_stats` table cache statistics
- Trade-off: dữ liệu có thể hơi cũ (eventual consistency)

**Depth** (Độ sâu)

- Cấp độ của folder trong tree (root = 0)
- Giới hạn tối đa: 10 levels
- DB constraint: CHECK depth <= 10
- Validated khi create/move folder

**Descending Order** (Thứ tự giảm dần)

- Review order: từ Box 7 → Box 1
- Review từ dễ → khó
- User option trong SRS settings

**Due Cards**

- Các thẻ đến hạn cần ôn trong ngày hiện tại theo SRS
- Query condition: `due_date <= CURRENT_DATE`
- Sorted by: due_date ASC, current_box (based on review order)

**Due Date**

- Ngày cần ôn tập card theo SRS schedule
- Được tính tự động: `last_reviewed_at + interval_days`
- Stored trong table `card_box_position`

**DTO (Data Transfer Object)**

- Object truyền data giữa layers
- Request DTO: từ Client → Controller
- Response DTO: từ Controller → Client
- Mapped từ Entity bằng MapStruct

### E

**Ease Factor** (Hệ số độ dễ)

- Hệ số độ dễ (default 2.5)
- Được điều chỉnh dựa trên rating của user
- MVP: fixed, not customizable

**Entity**

- JPA entity mapping với database table
- Annotations: @Entity, @Table, @Column, @ManyToOne, @OneToMany
- Managed by Hibernate

### F

**Folder**

- Thư mục tổ chức decks và sub-folders
- Có thể lồng nhau không giới hạn (max 10 levels)
- Support CRUD, Move, Copy (async cho > 50 items)

**Folder Statistics**

- Thống kê tổng số decks, cards trong folder (recursive)
- Cached trong table `folder_stats`
- Refresh: every 5 minutes hoặc on-demand

**Foreign Key (FK)**

- Ràng buộc tham chiếu giữa tables
- Đảm bảo data integrity
- Ví dụ: `deck.folder_id` references `folder.id`

**Forgotten Card Action**

- Hành động khi user rate "Again" (quên card)
- Options:
  - Move to Box 1: Reset về ô đầu (default)
  - Move down N boxes: Lùi lại N ô (1-3)
  - Stay in box: Giữ nguyên ô, giảm interval
- Configurable trong SRS settings

### I

**Index** (Database Index)

- Cấu trúc giúp tăng tốc query
- Critical: composite index cho review queries
- Ví dụ: `idx_card_box_user_due` trên `(user_id, due_date, current_box)`

**Interval** (Khoảng thời gian)

- Khoảng thời gian giữa 2 lần review (days)
- Được tính dựa trên box position và rating
- Fixed intervals trong MVP: 1, 3, 7, 14, 30, 60, 120 ngày

**Import/Export**

- Nhập/Xuất dữ liệu thẻ qua file CSV/XLSX
- Import: validation, preview, error report
- Export: CSV/XLSX format với metadata
- Template download available

### J

**JPA (Java Persistence API)**

- ORM specification cho Java
- Implementation: Hibernate
- Used với Spring Data JPA

**JWT (JSON Web Token)**

- Token format để authentication
- Gồm header, payload, signature
- Access token: 15 min, Refresh token: 7 days

### L

**Lapse** (Lỗi/Quên)

- Khi user quên card (rate "Again")
- Lapse count được tracking trong `card_box_position`
- Used để phân tích hiệu quả học tập

**Lazy Loading**

- Chỉ load direct children, expand on demand
- Giảm initial load time
- Applied cho folder tree

### M

**MapStruct**

- Java annotation processor cho DTO mapping
- Generate boilerplate code at compile time
- Used để map Entity ↔ DTO

**Materialized Path**

- Đường dẫn đầy đủ của folder được lưu trong database
- Format: `/parent_id/child_id/grandchild_id`
- Giúp query nhanh descendants
- Indexed với `varchar_pattern_ops`

**MVP (Minimum Viable Product)**

- Phiên bản sản phẩm với tính năng cốt lõi
- Focus: Simple, maintainable, extensible
- Timeline: 3-4 tháng (Q1 2025)

### N

**Notification**

- Thông báo nhắc nhở học tập
- Types: Push notification (mobile), In-app notification
- Settings: ON/OFF, daily reminder time
- Triggers: due cards available, daily reminder

### P

**Pagination**

- Chia data thành nhiều pages
- Folders/Decks: ~50 items per page
- Cards: ~100 items per page
- Reduce memory usage và improve performance

**Primary Key (PK)**

- Unique identifier cho table record
- RepeatWise: UUID format
- Auto-generated

### R

**Random Order**

- Review order: xáo trộn ngẫu nhiên
- User option trong SRS settings
- Applied khi fetch due cards

**Rating** (Đánh giá)

- Đánh giá của user sau khi review card
- 4 options:
  - **Again** (< 1 min): Không nhớ/sai → action based on forgotten_card_action
  - **Hard** (< 6 min): Nhớ khó khăn → stay in box, reduce interval
  - **Good** (next interval): Nhớ tốt → move to next box
  - **Easy** (4x interval): Nhớ rất dễ → skip 1-2 boxes

**Refresh Token**

- Token dài hạn (7 ngày) để refresh access token
- Được lưu trong HTTP-only cookie
- One-time use: rotate sau mỗi lần refresh
- Revoked on logout/password change

**Repository Pattern**

- Data access layer
- Spring Data JPA repositories
- Interface extends JpaRepository<Entity, ID>

**Review** (Ôn tập)

- Quá trình ôn tập flashcard
- Steps: xem front → suy nghĩ → lật xem back → đánh giá (rating)
- Actions: Submit, Undo, Skip, Edit card

**Review Log**

- Lịch sử review của card
- Stored trong table `review_logs`
- Fields: card_id, user_id, rating, previous_box, new_box, reviewed_at

**Review Order**

- Thứ tự review cards
- Options: Ascending, Descending, Random
- Configurable trong SRS settings

**Review Session**

- Phiên ôn tập
- Load due cards based on scope (all/folder/deck)
- Limit: 100-200 cards per session
- Support undo, skip, edit

### S

**Soft Delete**

- Xóa logic bằng cách set `deleted_at` timestamp
- Cho phép restore data trong 30 ngày
- Hard delete: cleanup job sau 30 ngày
- Applied cho: folders, decks, cards

**Spaced Repetition System (SRS)**

- Hệ thống lặp lại giãn cách dựa trên khoa học
- Giúp ghi nhớ lâu dài hiệu quả
- RepeatWise: Box System với 7 ô

**Spring Boot**

- Java framework cho web applications
- Version 3.x (requires Java 17+)
- Features: auto-configuration, embedded server, dependency injection

**Spring Data JPA**

- Spring module cho JPA
- Repository pattern implementation
- Query methods, JPQL, native queries

**Strategy Pattern**

- Design pattern cho algorithms
- Used cho: review order, forgotten card action
- Dễ mở rộng thêm strategies mới

**Streak** (Chuỗi ngày học)

- Số ngày học liên tục không gián đoạn
- Reset về 0 nếu bỏ qua 1 ngày
- Tracked trong table `user_stats`
- Used để gamification

### T

**TanStack Query (React Query)**

- Library quản lý server state cho React
- Features: auto caching, refetch, sync, pagination
- Used thay cho Redux trong MVP

**Template**

- File mẫu để user download và điền data
- Format: CSV, Excel (.xlsx)
- Include: header + sample rows + instructions

**ThreadPool**

- Pool of threads cho async operations
- Config: 5 core, 10 max, 100 queue capacity
- Rejection policy: CallerRunsPolicy

**Tree View**

- Giao diện hiển thị folder structure dạng cây
- Features: expand/collapse nodes, highlight selected
- Support keyboard navigation (future)

### U

**User Stats**

- Thống kê cá nhân của user
- Fields: total_cards_learned, streak_days, last_study_date
- Stored trong table `user_stats`
- Updated real-time sau mỗi review

**UUID (Universally Unique Identifier)**

- Format: 8-4-4-4-12 hex characters
- Used làm primary key trong RepeatWise
- Example: `550e8400-e29b-41d4-a716-446655440000`

### V

**Validation**

- Kiểm tra dữ liệu input
- Levels: client-side, server-side, database constraint
- Import validation: format, required fields, data types, duplicates

**Virtual Scrolling**

- Chỉ render items visible trên screen
- Optimize performance cho large lists (> 100 items)
- Used trong folder tree, card list

**Visitor Pattern**

- Design pattern để traverse folder tree
- Dùng để tính toán statistics recursive
- Separate algorithm from data structure

## Thuật ngữ kỹ thuật (Technical Terms)

### Backend

**Controller**

- Layer xử lý HTTP requests
- Responsibilities: validate input, call service, return response
- Annotations: @RestController, @RequestMapping, @GetMapping, @PostMapping

**Service**

- Business logic layer
- Pattern: Interface + Implementation
- Annotations: @Service, @Transactional

**Repository**

- Data access layer
- Spring Data JPA repositories
- Interface extends JpaRepository<Entity, ID>

**Entity**

- JPA entity mapping với database table
- Annotations: @Entity, @Table, @Column, @Id, @GeneratedValue

**Mapper**

- Convert giữa Entity và DTO
- Library: MapStruct
- Auto-generated at compile time

### Frontend

**Context API**

- React context cho global state
- Used cho: Auth (user, login, logout), Settings (theme, language)
- Avoid prop drilling

**Shadcn/ui**

- Component library cho React
- Built với Tailwind CSS + Radix UI
- Copy-paste components (not npm package)

**Zustand**

- Lightweight state management
- Used cho UI state: sidebar, modals, temp state
- Alternative to Redux (simpler)

### Database

**Composite Index**

- Index gồm nhiều columns
- Critical: `(user_id, due_date, current_box)` cho review queries
- Order matters!

**Composite Primary Key**

- Primary key gồm nhiều columns
- Example: `(folder_id, user_id)` trong `folder_stats`
- Annotations: @EmbeddedId hoặc @IdClass

**Flyway**

- Database migration tool
- Versioned SQL scripts: V1__create_users_table.sql
- Auto-run on application startup

**Foreign Key Constraint**

- Ràng buộc tham chiếu giữa tables
- Ensure referential integrity
- Annotations: @ManyToOne, @JoinColumn

### Security

**bcrypt**

- Password hashing algorithm
- Cost factor: 12 (balance security vs performance)
- One-way hash: không thể decrypt

**CORS (Cross-Origin Resource Sharing)**

- HTTP header-based mechanism
- Allow frontend (different origin) call backend API
- Config: allowed origins, methods, headers

**HTTPS**

- HTTP over TLS/SSL
- Encrypt data in transit
- Required cho production

**Rate Limiting**

- Giới hạn số requests per time window
- MVP: 100 requests/minute/user (in-memory)
- Future: Redis-based distributed rate limiting

## Abbreviations & Acronyms

**API** - Application Programming Interface

**CORS** - Cross-Origin Resource Sharing

**CRUD** - Create, Read, Update, Delete

**CSV** - Comma-Separated Values

**DTO** - Data Transfer Object

**FK** - Foreign Key

**HTTP** - Hypertext Transfer Protocol

**HTTPS** - HTTP Secure

**JPA** - Java Persistence API

**JSON** - JavaScript Object Notation

**JWT** - JSON Web Token

**MVP** - Minimum Viable Product

**ORM** - Object-Relational Mapping

**PK** - Primary Key

**REST** - Representational State Transfer

**SPA** - Single Page Application

**SQL** - Structured Query Language

**SRS** - Spaced Repetition System

**UI** - User Interface

**UUID** - Universally Unique Identifier

**UX** - User Experience

**XLSX** - Excel Open XML Spreadsheet

## Domain-Specific Terms

**Box 1-7**

- 7 ô trong Box System
- Box 1: New cards, interval 1 day
- Box 2-6: Progressing cards, increasing intervals
- Box 7: Mature cards, interval 120 days

**Due Cards Query**

- Query để lấy cards cần review
- Condition: `user_id = ? AND due_date <= CURRENT_DATE AND deleted_at IS NULL`
- Order by: `due_date ASC, current_box` (based on review_order setting)
- Index: `(user_id, due_date, current_box)`

**Folder Path**

- Materialized path của folder
- Format: `/parent_id/child_id/`
- Used để query descendants: `WHERE path LIKE '/parent_id/%'`
- Indexed với `varchar_pattern_ops`

**Review Scope**

- Phạm vi review cards
- Options:
  - **all**: All due cards từ tất cả decks
  - **folder**: Due cards từ folder (recursive)
  - **deck**: Due cards từ deck cụ thể

**Sync vs Async Copy**

- **Sync Copy**: Immediate response (≤ 50 items folder, ≤ 1000 cards deck)
- **Async Copy**: Background job (51-500 items, 1001-10,000 cards)
- Hard limit: 500 items folder, 10,000 cards deck

## Notes

- Thuật ngữ trong docs này là consistent across toàn bộ dự án
- Backend sử dụng English naming (Java convention)
- Frontend có thể dùng Vietnamese labels cho UI
- Database tables và columns dùng snake_case
- Java classes và methods dùng camelCase
