# Overview - RepeatWise

## Giới thiệu dự án

**RepeatWise** là một ứng dụng học tập cá nhân sử dụng phương pháp Spaced Repetition System (SRS) dạng Box System. Ứng dụng giúp người dùng tạo flashcards, tổ chức theo cấu trúc folders phân cấp, và tự động lên lịch ôn tập dựa trên thuật toán khoa học.

## Phạm vi dự án

**Phiên bản**: MVP (Minimum Viable Product) - Personal Use

**Mục tiêu**: Xây dựng các chức năng cốt lõi, dễ maintain và mở rộng trong tương lai.

**Đối tượng người dùng**: Cá nhân (học sinh, sinh viên, lập trình viên, người học ngoại ngữ)

## Mục đích sản phẩm

RepeatWise giúp người dùng:

- **Học đều đặn, đúng lúc**: SRS tối ưu hóa lịch ôn tập, tập trung vào "due cards"
- **Tổ chức kiến thức linh hoạt**: Cấu trúc thư mục lồng nhau (tối đa 10 cấp) và deck rõ ràng
- **Nhập/xuất dễ dàng**: Hỗ trợ CSV/XLSX để tạo và sao lưu số lượng lớn thẻ
- **Trải nghiệm tối giản**: Giao diện nhẹ, nhanh, tập trung vào việc học

## Công nghệ sử dụng

### Backend

- **Language**: Java 17
- **Framework**: Spring Boot 3
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA (Hibernate)
- **Authentication**: JWT với Refresh Token
- **File Processing**: Apache POI (Excel), OpenCSV
- **Background Jobs**: Spring @Async + ThreadPoolTaskExecutor

### Frontend Web

- **Framework**: React TypeScript
- **UI Components**: Tailwind CSS + Shadcn/ui
- **State Management**:
  - Server State: TanStack Query (React Query)
  - Auth State: Context API
  - UI State: Zustand
- **Features**: Dark/Light mode, i18n (VI/EN)

### Frontend Mobile

- **Framework**: React Native
- **UI Components**: React Native Paper
- **State Management**: TanStack Query + Context API
- **Features**: Push notifications, native performance

## Kiến trúc hệ thống

### Architecture Pattern

- **Layered Architecture**: Controller → Service → Repository
- **API**: RESTful với proper HTTP methods
- **Authentication**: JWT (15 min) + Refresh Token (7 days)

### Design Patterns

- **Composite Pattern**: Cấu trúc cây folders (Folder chứa Folders/Decks)
- **Strategy Pattern**: Review order strategies, Forgotten card action strategies
- **Repository Pattern**: Spring Data JPA Repositories
- **DTO Pattern**: Data transfer giữa layers với MapStruct
- **Visitor Pattern**: Traverse folder tree để tính statistics

## Tính năng chính trong MVP

### 1. User Management

- Đăng ký/Đăng nhập với email + password
- Quản lý profile: tên, timezone, ngôn ngữ (VI/EN), theme (Light/Dark/System)
- JWT với Refresh Token (security tốt hơn)

### 2. Folder & Deck Management

**Hierarchical Folder Structure**:

- Folders lồng vô hạn cấp độ (giới hạn 10 levels)
- CRUD operations: Create, Rename, Move, Copy, Delete (soft delete)
- Copy folder với async operation cho large datasets (> 50 items)
- Folder statistics: tổng decks/cards (recursive)
- Breadcrumb navigation

**Deck Management**:

- CRUD decks trong folder hoặc root level
- Move/Copy deck giữa các folders
- Copy deck với async operation (> 1000 cards)
- Import/Export: CSV, Excel (.xlsx) với validation

### 3. Flashcard Management

- **Basic card type**: Front/back text only
- CRUD flashcards với simple text editor
- Bulk Import/Export:
  - Format: CSV, Excel (.xlsx)
  - Validation: empty rows, missing fields, duplicates
  - Preview trước khi import
  - Template download

### 4. Spaced Repetition System

**Box-Based Algorithm**:

- 7 ô cố định với intervals: 1, 3, 7, 14, 30, 60, 120 ngày
- Review order: Ascending, Descending, Random
- Rating options: Again, Hard, Good, Easy
- Forgotten card actions: Move to Box 1, Move down N boxes, Stay in box

**Daily Limits**:

- Giới hạn cards mới mỗi ngày (default: 20)
- Giới hạn reviews mỗi ngày (default: 200)

**Notification Settings**:

- Toggle ON/OFF notifications
- Daily reminder với thời gian tùy chỉnh
- Push notification (mobile) + in-app notification

### 5. Study Modes

- **Standard SRS**: Review due cards theo schedule (all/folder/deck scope)
- **Cram Mode**: Học nhanh tất cả cards, không ảnh hưởng schedule
- **Random Mode**: Review ngẫu nhiên với số lượng tùy chọn

### 6. Statistics & Analytics

- **Streak counter**: Số ngày học liên tục
- **Card distribution by box**: Bar chart đơn giản
- **Today's stats**: Cards reviewed, new cards learned
- **Deck/Folder stats**: Total cards, due cards, new cards

## Đặc điểm nổi bật

1. **Cấu trúc Folder phân cấp**: Tối đa 10 cấp với validation
2. **Thuật toán SRS 7-box system**: Fixed intervals cho đơn giản
3. **Import/Export hàng loạt**: CSV/Excel với validation chi tiết
4. **Async operations**: Copy/Import lớn chạy background với progress tracking
5. **Multi-mode study**: Standard SRS, Cram, Random
6. **Theme support**: Light/Dark mode với smooth transition
7. **Multilingual**: Vietnamese (default), English
8. **Security**: JWT + Refresh Token rotation

## Ràng buộc & giới hạn (MVP)

### Folder Operations

- Độ sâu tối đa: 10 levels (DB constraint + API validation)
- Copy folder: ≤ 50 items (sync), 51-500 (async), > 500 từ chối

### Deck Operations

- Copy deck: ≤ 1000 cards (sync), 1001-10,000 (async), > 10,000 từ chối

### Import/Export

- File size: ≤ 50MB
- Nhập: tối đa 10,000 dòng
- Xuất: tối đa 50,000 thẻ
- Required columns: Front, Back

### Performance

- API response time: < 200ms (CRUD operations)
- Load folder tree: < 300ms
- Load review session: < 500ms
- Hỗ trợ: 10,000+ cards/deck, 1,000+ folders/user

## Ngoài phạm vi MVP (Future)

### Phase 4 - UI/UX Enhancements

- Drag & drop, Context menu, Color/icon customization, Search, Bulk operations

### Phase 5 - Rich Content

- Rich text editor, Images/audio, Code snippets, Cloze deletion, Multiple choice, Tags

### Phase 6 - Analytics & Gamification

- Heatmap, Advanced charts, Badges, Achievements, Leaderboard

### Phase 7 - Social Features

- Share decks/folders, Community decks, Collaborative editing, Anki import

### Phase 8 - Premium Features

- Offline mode, AI-generated cards, Advanced analytics, Test mode

## Chất lượng & hiệu năng

### Performance Optimization

- **Lazy loading**: Chỉ load direct children, expand on demand
- **Pagination**: Folders/Decks ~50, Cards ~100 per page
- **Virtual scrolling**: Large lists > 100 items
- **Database indexes**: Composite index cho review queries
- **Materialized path**: Quick folder tree traversal
- **Denormalized stats**: Folder statistics cache

### Async Operations

- **ThreadPool config**: 5 core, 10 max threads, 100 queue capacity
- **Timeout protection**: Folder copy 5 min, Deck copy 10 min
- **Progress tracking**: In-memory với TTL 1 hour
- **Cleanup job**: Run every 30 minutes

## Chỉ báo thành công (MVP)

### User Experience

- Tạo ≥ 3 cấp thư mục và ≥ 2 deck mượt mà (< 1s thao tác)
- Ôn tập hàng ngày ổn định (due cards chính xác, không trễ)
- Nhập ≥ 1,000 thẻ qua CSV/XLSX thành công với báo cáo lỗi rõ ràng

### Technical Metrics

- Test coverage ≥ 70% (core logic)
- API response time < 200ms
- Zero critical bugs at launch

## Tài liệu tham khảo

- [Vision Document](./01-vision.md) - Tầm nhìn và mục tiêu dự án
- [Glossary](./02-glossary.md) - Thuật ngữ và định nghĩa
- [System Context Diagram](./03-system-context-diagram.md) - Sơ đồ ngữ cảnh hệ thống
- [Stakeholders](./04-stakeholders.md) - Các bên liên quan

## Roadmap

- **Q1 2025**: MVP Backend + Web completion
- **Q2 2025**: Mobile app launch
- **Q3 2025**: Enhanced features + optimization
- **Q4 2025**: Premium features + scaling
