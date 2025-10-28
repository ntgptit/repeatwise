# Requirements Document: RepeatWise MVP - Personal Flashcard Learning App

## 1. Tổng quan hệ thống

Ứng dụng học tập cá nhân sử dụng thuật toán Spaced Repetition (SRS) dạng Box System. Người dùng tạo flashcard, tổ chức theo cấu trúc folders đơn giản, hệ thống tự động lên lịch ôn tập.

**Phiên bản**: MVP - Sử dụng cá nhân (Personal Use)
**Mục tiêu**: Các chức năng cốt lõi, dễ maintain và mở rộng sau này

---

## 2. Core Features - MVP

### 2.1 User Management
- ✅ Đăng ký/Đăng nhập với email + password
- ✅ Quản lý profile cơ bản:
  - Tên, timezone
  - Ngôn ngữ: Vietnamese (mặc định), English
  - Theme: Light/Dark/System (mặc định: System)
- ❌ ~~OAuth Google/Facebook~~ → Future
- ❌ ~~Phân quyền Premium/Admin~~ → Future
- ❌ ~~Avatar upload~~ → Future

### 2.2 Folder & Deck Management ⭐

#### 2.2.1 Tree-Based Folder Structure
- ✅ **Hierarchical organization**: Folders có thể lồng vô hạn cấp độ (unlimited depth)
- ✅ **Root level**: User có thể tạo nhiều folders ở root
- ✅ **Parent-Child relationship**: Mỗi folder có thể chứa:
  - Sub-folders (folders con)
  - Decks (bộ thẻ)
- ✅ **Visual representation**: Simple tree view với expand/collapse nodes

**Example Structure:**
```
📁 Root
├── 📁 English Learning
│   ├── 📁 IELTS Preparation
│   │   ├── 📁 Vocabulary
│   │   │   ├── 📂 Academic Words (deck)
│   │   │   └── 📂 Collocations (deck)
│   │   ├── 📁 Grammar
│   │   │   └── 📂 Complex Sentences (deck)
│   │   └── 📂 Speaking Topics (deck)
│   └── 📁 Business English
│       ├── 📂 Meeting Phrases (deck)
│       └── 📂 Email Writing (deck)
├── 📁 Programming
│   ├── 📁 Java
│   │   ├── 📂 Design Patterns (deck)
│   │   └── 📂 Spring Boot (deck)
│   └── 📁 JavaScript
│       └── 📂 ES6 Features (deck)
└── 📂 Standalone Deck (deck without folder)
```

#### 2.2.2 Folder CRUD Operations
- ✅ **Create folder**: Tạo folder mới ở bất kỳ level nào
  - Max depth = 10 levels (constraint)
  - Validation: không thể tạo nếu parent đã ở depth 10
- ✅ **Rename folder**: Đổi tên folder
- ✅ **Move folder**: Di chuyển folder (select destination)
  - Move folder A vào folder B → A trở thành child của B
  - Move folder ra root level
  - Validation: depth sau khi move không vượt quá 10
- ✅ **Copy folder**: ⭐ **Async operation cho large folders**
  - Copy folder kèm toàn bộ sub-folders và decks bên trong
  - Deep copy: tạo bản sao hoàn toàn độc lập
  - User chọn destination folder để paste
  - **Limits**:
    - Sync copy: ≤ 50 total items (folders + decks)
    - Async copy: 51-500 items → background job với notification
    - Hard limit: 500 items max (prevent abuse)
  - **Progress tracking**: notification khi copy hoàn thành
- ✅ **Delete folder**:
  - Soft delete để có thể khôi phục
  - Confirm dialog khi delete để tránh mất dữ liệu
  - Delete cả sub-folders và decks bên trong
- ❌ ~~Drag & drop UI~~ → Future (nhưng logic move/copy đã có)

#### 2.2.3 Folder Features
- ✅ **Folder metadata**:
  - Name, description
  - Created date, last modified date
  - Total decks count (recursive - tính cả decks trong sub-folders)
  - Total cards count (recursive)
- ✅ **Folder permissions**: Private only (chỉ owner)
- ✅ **Folder actions**:
  - Collapse/Expand sub-folders
  - Sort folders/decks by: name, date
- ✅ **Folder statistics**:
  - Total cards, due cards count trong folder (recursive)
- ❌ ~~Color/icon customization~~ → Future
- ❌ ~~Shared/Public folders~~ → Future
- ❌ ~~Bulk actions~~ → Future
- ❌ ~~Search trong folders~~ → Future

#### 2.2.4 Deck Management
- ✅ **Create deck**: Tạo deck trong folder hoặc root level
- ✅ **CRUD decks**: Tạo, xem, sửa, xóa (soft delete)
- ✅ **Move deck**: Di chuyển deck giữa các folders
- ✅ **Copy deck**: Copy deck sang folder khác ⭐
  - **Limits**:
    - Sync copy: ≤ 1000 cards
    - Async copy: 1001-10,000 cards → background job
    - Hard limit: 10,000 cards max per deck
  - **Progress tracking**: notification khi copy hoàn thành
- ✅ **Deck metadata**:
  - Name, description
  - Parent folder path
  - Card count, due cards count
  - Last studied date
- ✅ **Import/Export**: Import số lượng lớn cards vào deck ⭐
  - Import formats: CSV, Excel (.xlsx)
  - Export formats: CSV, Excel (.xlsx)
  - Template file download để user điền
  - Bulk import validation
- ❌ ~~Tags~~ → Future
- ❌ ~~Anki format import~~ → Future
- ❌ ~~Share deck~~ → Future

#### 2.2.5 Navigation & UX
- ✅ **Breadcrumb navigation**: Hiển thị folder path hiện tại
  - Example: `Home > English Learning > IELTS Preparation > Vocabulary`
  - Click vào bất kỳ breadcrumb nào để navigate
- ✅ **Tree view sidebar**:
  - Collapsible tree structure
  - Show/hide folder tree
  - Highlight current selected folder
- ✅ **Folder statistics widget**:
  - Hiển thị tổng số decks, cards, due cards trong folder đang chọn (bao gồm sub-folders)
- ❌ ~~Drag & drop~~ → Future
- ❌ ~~Context menu (right-click)~~ → Future
- ❌ ~~List view/Grid view toggle~~ → Future

### 2.3 Flashcard Management (Đơn giản hóa)

#### 2.3.1 Basic Card Features
- ✅ **Basic card type**: Front/back text only
- ✅ CRUD flashcards
- ✅ **Simple text editor**: Plain text with line breaks
- ❌ ~~Rich text (bold, italic)~~ → Future
- ❌ ~~Images, audio~~ → Future
- ❌ ~~Code snippet với syntax highlighting~~ → Future
- ❌ ~~Cloze deletion~~ → Future
- ❌ ~~Multiple choice~~ → Future
- ❌ ~~Type-in answer~~ → Future

#### 2.3.2 Card Metadata (Đơn giản hóa)
- ✅ Front, back text
- ✅ Created date, updated date
- ❌ ~~Tags~~ → Future
- ❌ ~~Difficulty level~~ → Future
- ❌ ~~Notes, source reference~~ → Future
- ❌ ~~Card linking~~ → Future

#### 2.3.3 Bulk Import/Export ⭐

**Import CSV/Excel:**
- ✅ Upload file CSV hoặc Excel (.xlsx)
- ✅ Format: `Front, Back` (2 columns)
- ✅ Validation:
  - Check empty rows → skip
  - Check missing front/back → show error với row number
  - Max 10,000 rows per file
  - Duplicate check (optional): cảnh báo nếu card đã tồn tại
- ✅ Preview trước khi import:
  - Hiển thị 10 rows đầu
  - Show total valid/invalid rows
  - Option to continue or cancel
- ✅ Import progress: progress bar cho large files

**Export CSV/Excel:**
- ✅ Export tất cả cards trong deck
- ✅ Format: `Front, Back, Created Date, Review Count, Current Box`
- ✅ Option: Export only due cards, export all cards
- ✅ File name: `{deck_name}_export_{date}.csv/xlsx`

**Template Download:**
- ✅ Cung cấp template file để user download
- ✅ Template có header + 3 sample rows
- ✅ Instructions trong file (comment hoặc separate sheet cho Excel)

### 2.4 Spaced Repetition System - Box-Based Algorithm ⭐

#### 2.4.1 Box System (Fixed Configuration)
- ✅ **7 ô cố định** (không configurable)
- ✅ **Fixed intervals**:
  - Box 1: 1 day
  - Box 2: 3 days
  - Box 3: 7 days
  - Box 4: 14 days
  - Box 5: 30 days
  - Box 6: 60 days
  - Box 7: 120 days

#### 2.4.2 Review Order Settings
User có thể chọn thứ tự review cards:
- ✅ **Tăng dần (Ascending)**: Review từ ô 1 → ô 7 (từ khó → dễ)
- ✅ **Giảm dần (Descending)**: Review từ ô 7 → ô 1 (từ dễ → khó)
- ✅ **Ngẫu nhiên (Random)**: Xáo trộn thứ tự review

#### 2.4.3 Review Session
- ✅ Hiển thị card với front side
- ✅ User suy nghĩ và lật card để xem answer
- ✅ **Rating options** (4 options):
  - **Again** (< 1 minute): Không nhớ/sai
  - **Hard** (< 6 minutes): Nhớ khó khăn
  - **Good** (next interval): Nhớ tốt
  - **Easy** (4x interval): Nhớ rất dễ
- ✅ **Actions**:
  - Undo: quay lại card trước
  - Skip: postpone card, sẽ review lại cuối session
  - Edit card: sửa nội dung card ngay trong review session
- ❌ ~~Suspend card~~ → Future

#### 2.4.4 Forgotten Card Actions
User cấu hình hành động khi nhấn "Again" (quên card):
- ✅ **Di chuyển vào ô 1**: Reset card về ô đầu tiên (restart from scratch) - Default
- ✅ **Di chuyển xuống N ô**: Lùi lại N ô (configurable: 1, 2, 3 ô)
- ✅ **Giữ nguyên ô**: Card ở lại ô hiện tại, chỉ giảm interval

#### 2.4.5 Notification Settings
- ✅ **Toggle ON/OFF**: Bật/tắt thông báo khi cần lặp lại các từ
- ✅ **Notification triggers**:
  - Khi có cards due trong ngày
  - Daily reminder vào thời gian user set (ví dụ: 9:00 AM, 8:00 PM)
- ✅ **Notification channels**:
  - Push notification trên mobile/web
  - In-app notification badge
- ❌ ~~Email notification~~ → Future
- ❌ ~~Streak reminder~~ → Future
- ❌ ~~Weekly summary~~ → Future

#### 2.4.6 Daily Limits
- ✅ Giới hạn số cards mới mỗi ngày (default: 20, configurable)
- ✅ Giới hạn số cards review tối đa mỗi ngày (default: 200, configurable)
- ✅ Auto-pause khi đạt limit, có option để override

### 2.5 Study Modes ⭐

#### 2.5.1 Chế độ lặp lại giãn cách (Standard SRS)
- ✅ Review cards theo SRS schedule dựa trên box position
- ✅ Áp dụng thuật toán box-based với interval tính theo ô hiện tại
- ✅ **Review scope options**:
  - Review all due cards từ tất cả decks
  - Review due cards từ folder cụ thể (bao gồm sub-folders) ⭐
  - Review due cards từ deck cụ thể

#### 2.5.2 Cram Mode
- ✅ Học nhanh tất cả cards, không theo schedule
- ✅ Không ảnh hưởng đến SRS schedule
- ✅ **Cram scope options**:
  - Cram toàn bộ deck
  - Cram toàn bộ folder (all cards trong folder và sub-folders) ⭐

#### 2.5.3 Random Mode
- ✅ Review ngẫu nhiên cards trong deck/folder
- ✅ Không theo thứ tự hoặc schedule
- ✅ Chọn số lượng cards muốn review

- ❌ ~~Test Mode~~ → Future
- ❌ ~~Custom Study với filters~~ → Future

### 2.6 Statistics & Analytics (Đơn giản hóa)

#### 2.6.1 Personal Statistics (Basic)
- ✅ **Streak counter**: Số ngày học liên tục
- ✅ **Card distribution by box**: Số cards trong từng ô (simple bar chart)
- ✅ **Today's stats**: Cards reviewed hôm nay, new cards learned
- ✅ **Deck stats**: Total cards, due cards, new cards per deck
- ❌ ~~Heatmap activity~~ → Future
- ❌ ~~Accuracy rate~~ → Future
- ❌ ~~Time spent studying~~ → Future
- ❌ ~~Retention rate, lapse rate~~ → Future
- ❌ ~~Advanced charts (line, pie, forecast)~~ → Future

### 2.7 Removed Features (Future)
- ❌ Social Features → Future
- ❌ Gamification → Future
- ❌ Offline Mode → Future (chỉ online)

---

## 3. Technical Stack (Đơn giản hóa)

### 3.1 Backend
- **Language**: Java 17
- **Framework**: Spring Boot 3
- **Database**: PostgreSQL only
- **ORM/Persistence**: Spring Data JPA (Hibernate)
  - JPA Entities với annotations
  - Spring Data JPA Repositories
  - JPQL/Criteria API cho custom queries
- **File Processing**: Apache POI - Excel (.xlsx) processing
  - OpenCSV - CSV file processing
  - Validation framework cho bulk import
- **Background Jobs**: Spring @Async + ThreadPoolTaskExecutor ⭐
  - Async copy operations (folder/deck)
  - Progress tracking với in-memory status store
  - Notification on completion
- **Cache**: ~~Redis~~ → Future (không cần cache cho MVP)
- **Message Queue**: ~~RabbitMQ~~ → Future (phase 2: dùng cho persistent job queue)
- **File Storage**: Local storage (temp upload files)
- **Search**: ~~Elasticsearch~~ → Future

### 3.2 Frontend
- **Web**: React TypeScript
- **Mobile**: React Native
- **State Management**:
  - Server State: TanStack Query (React Query) - caching, refetching, sync
  - Auth State: Context API - user, login, logout
  - UI State: Zustand (optional) - sidebar, theme, temp UI state
  - ❌ ~~Redux~~ → Không cần cho MVP (overhead lớn, app đơn giản)
- **UI Components & Styling**:
  - **Web**: Tailwind CSS + Shadcn/ui
    - Utility-first CSS với Tailwind
    - Pre-built accessible components từ Shadcn/ui
    - Dark mode built-in với Tailwind
    - Customizable theme
  - **Mobile**: React Native Paper
    - Material Design components
    - Built-in theming support
    - Accessibility features

### 3.3 Architecture
- **Pattern**: Layered Architecture với Controller → Service → Repository
- **RESTful API** với proper HTTP methods
- **JWT authentication with Refresh Token** (MVP): ✅
  - **Access token**: 15 minutes expiry (short-lived, secure)
  - **Refresh token**: 7 days expiry (stored in HTTP-only cookie)
  - **Token rotation**: One-time use, new refresh token on each refresh
  - **Revocation**: Logout/password change invalidates all tokens
  - **Storage**: refresh_tokens table with bcrypt hashed tokens
  - **Why in MVP**: Better security from day 1, industry best practice
  - **Implementation**:
    - `/api/auth/login` returns access + refresh tokens
    - `/api/auth/refresh` validates refresh token → new access + refresh tokens
    - `/api/auth/logout` revokes refresh token
  - **See**: `docs/02-system-analysis/nfr.md` section 4.1 for complete details

### 3.4 Design Patterns
- ✅ **Composite Pattern**: Folder tree structure (Folder chứa Folders/Decks) ⭐
- ✅ **Strategy Pattern**: Review order strategies, Forgotten card action strategies
- ✅ **Repository Pattern**: Spring Data JPA Repositories
- ✅ **DTO Pattern**: Data transfer between layers với MapStruct
- ✅ **Visitor Pattern**: Traverse folder tree để tính statistics ⭐
- ❌ ~~Factory Pattern~~ → Future (khi có multiple card types)
- ❌ ~~Observer Pattern~~ → Future (notification phức tạp)

---

## 4. Database Schema (Đơn giản hóa)

### 4.1 Core Tables

**users**

Bảng users chứa các trường:

- id: UUID làm primary key
- email: unique, dùng để đăng nhập
- password_hash: mật khẩu đã được mã hóa
- name: tên người dùng
- timezone: múi giờ
- language: EN hoặc VI, mặc định 'VI'
- theme: LIGHT/DARK/SYSTEM, mặc định 'SYSTEM'
- created_at, updated_at: thời gian tạo và cập nhật

**refresh_tokens** ⭐ (MVP)

Bảng refresh_tokens chứa các trường:

- id: UUID làm primary key
- user_id: Foreign key tới bảng users
- token_hash: VARCHAR(255), token đã được mã hóa bằng bcrypt
- expires_at: thời gian hết hạn (timestamp)
- revoked_at: thời gian thu hồi token (timestamp, nullable)
- created_at, updated_at: thời gian tạo và cập nhật

Indexes quan trọng:

- idx_refresh_tokens_user trên user_id
- idx_refresh_tokens_hash trên token_hash
- idx_refresh_tokens_expires trên expires_at để cleanup job có thể xóa token hết hạn

**folders** ⭐

Bảng folders chứa các trường:

- id: UUID làm primary key
- user_id: Foreign key tới bảng users
- parent_folder_id: Foreign key tới chính bảng folders, nullable (null = root level)
- name: tên folder
- description: mô tả folder
- path: materialized path (ví dụ: /1/5/12) để truy vấn nhanh các ancestor
- depth: int - cấp độ trong tree, có constraint CHECK depth <= 10 (tối đa 10 levels)
- created_at, updated_at: thời gian tạo và cập nhật
- deleted_at: soft delete

Indexes quan trọng:

- idx_folders_path trên path để truy vấn nhanh các descendant
- idx_folders_user_parent trên (user_id, parent_folder_id)
- idx_folders_depth trên depth để validation

Các trường đã loại bỏ cho MVP: color, icon, is_public, shared_with

**decks**

Bảng decks chứa các trường:

- id: UUID làm primary key
- user_id: Foreign key tới bảng users
- folder_id: Foreign key tới bảng folders, nullable (deck có thể không thuộc folder nào)
- name: tên deck
- description: mô tả deck
- created_at, updated_at: thời gian tạo và cập nhật
- deleted_at: soft delete

Các trường đã loại bỏ: is_public, tags

**cards**

Bảng cards chứa các trường:

- id: UUID làm primary key
- deck_id: Foreign key tới bảng decks
- front: text - mặt trước của flashcard
- back: text - mặt sau của flashcard
- created_at, updated_at: thời gian tạo và cập nhật
- deleted_at: soft delete

Các trường đã loại bỏ: card_type, metadata table

### 4.2 SRS-Specific Tables

**srs_settings** (per user)

Bảng srs_settings chứa các trường:

- id: UUID làm primary key
- user_id: Foreign key tới bảng users, unique (mỗi user chỉ có 1 setting)
- total_boxes: int, mặc định 7, cố định
- review_order: ASCENDING/DESCENDING/RANDOM, mặc định RANDOM
- notification_enabled: boolean, mặc định true
- notification_time: time, mặc định '09:00'
- forgotten_card_action: MOVE_TO_BOX_1/MOVE_DOWN_N_BOXES/STAY_IN_BOX, mặc định MOVE_TO_BOX_1
- move_down_boxes: int, mặc định 1
- new_cards_per_day: int, mặc định 20
- max_reviews_per_day: int, mặc định 200
- created_at, updated_at: thời gian tạo và cập nhật

**card_box_position**

Bảng card_box_position chứa các trường:

- id: UUID làm primary key
- card_id: Foreign key tới bảng cards
- user_id: Foreign key tới bảng users
- current_box: int từ 1-7, ô hiện tại của card
- ease_factor: decimal, mặc định 2.5
- interval_days: int - khoảng thời gian đến lần review tiếp theo
- due_date: timestamp - ngày cần review
- last_reviewed_at: timestamp - lần review cuối cùng
- review_count: int - số lần đã review
- lapse_count: int - số lần quên
- created_at, updated_at: thời gian tạo và cập nhật

Composite index quan trọng nhất cho review queries:

- idx_card_box_user_due trên (user_id, due_date, current_box)
- Index này cover cho query: WHERE user_id = ? AND due_date <= ? ORDER BY due_date, current_box

**review_logs**

Bảng review_logs chứa các trường:

- id: UUID làm primary key
- card_id: Foreign key tới bảng cards
- user_id: Foreign key tới bảng users
- rating: AGAIN/HARD/GOOD/EASY - đánh giá của user
- previous_box: ô trước khi review
- new_box: ô sau khi review
- interval_days: khoảng thời gian đến lần review tiếp theo
- reviewed_at: thời gian review

Trường đã loại bỏ: time_taken_seconds

**user_stats** ⭐

Bảng user_stats chứa các trường:

- user_id: Foreign key tới bảng users, unique (mỗi user chỉ có 1 record stats)
- total_cards_learned: int - tổng số cards đã học
- streak_days: int - số ngày học liên tục
- last_study_date: date - ngày học gần nhất
- total_study_time_minutes: int - tổng thời gian học (phút)
- updated_at: timestamp - thời gian cập nhật

Cơ chế cập nhật:

- Triggered by: review_logs insert (sau mỗi lần review submit)
- Strategy: Tăng các counters, tính lại streak
- Frequency: Real-time (cập nhật đồng bộ trong review transaction)

**folder_stats** ⭐ (denormalized cache)

Bảng folder_stats chứa các trường:

- folder_id: Foreign key tới bảng folders
- user_id: Foreign key tới bảng users
- total_cards_count: int - tổng số cards (recursive - tính cả sub-folders)
- due_cards_count: int - số cards cần review (recursive)
- new_cards_count: int - số cards mới
- mature_cards_count: int - số cards đã thuộc
- last_computed_at: timestamp - lần tính toán cuối cùng

Composite primary key: (folder_id, user_id)

Cơ chế cập nhật:

- Triggered by: card CRUD, deck CRUD, review submit
- Strategy: Async batch recalculation (không real-time)
- Frequency:
  - Auto refresh: Mỗi 5 phút (scheduled job)
  - On-demand: Khi user yêu cầu folder stats
  - Invalidation: Set last_computed_at = NULL khi cards/decks thay đổi
- Trade-off: Dữ liệu hơi cũ (tối đa 5 phút) để có performance tốt hơn

### 4.3 Indexes (Performance Critical)

**Folders:**
- `idx_folders_user_parent`: (user_id, parent_folder_id) - for listing folders
- `idx_folders_path`: (path varchar_pattern_ops) - for descendant queries
- `idx_folders_user_deleted`: (user_id, deleted_at) - for soft delete queries
- `idx_folders_depth`: (depth) - for validation

**Decks:**
- `idx_decks_folder_user`: (folder_id, user_id) - for listing decks in folder
- `idx_decks_user_deleted`: (user_id, deleted_at) - for soft delete queries

**Cards:**
- `idx_cards_deck`: (deck_id) - for listing cards in deck
- `idx_cards_deleted`: (deck_id, deleted_at) - for soft delete queries

**Card Box Position (Most Critical for Performance!):**
- `idx_card_box_user_due`: (user_id, due_date, current_box) - **PRIMARY index for review queries**
  - Covers: WHERE user_id = ? AND due_date <= ? ORDER BY due_date, current_box
  - Reduces query time from seconds to milliseconds for large datasets
- `idx_card_box_card_user`: (card_id, user_id) - for single card lookup
- `idx_card_box_user_box`: (user_id, current_box) - for box distribution stats

**Review Logs:**
- `idx_review_logs_user_date`: (user_id, reviewed_at DESC) - for review history
- `idx_review_logs_card`: (card_id) - for card review history

---

## 5. API Endpoints (Đơn giản hóa)

### 5.1 Authentication ⭐
- `POST /api/auth/register` - Register new user
  - Response: User object (no auto-login)
- `POST /api/auth/login` - Login with email + password
  - Request: `{email, password}`
  - Response:
    - Body: `{access_token: "jwt...", expires_in: 900}` (15 minutes)
    - Set-Cookie: `refresh_token=<token>; HttpOnly; Secure; SameSite=Strict; Max-Age=604800` (7 days)
- `POST /api/auth/refresh` - Refresh access token using refresh token (from cookie)
  - Request: No body (refresh token in cookie)
  - Response:
    - Body: `{access_token: "jwt...", expires_in: 900}`
    - Set-Cookie: New refresh token (rotate old one)
  - Error: 401 if refresh token invalid/expired/revoked
- `POST /api/auth/logout` - Logout and revoke refresh token
  - Request: No body (refresh token in cookie)
  - Response: 204 No Content
  - Action: Revoke refresh token in database
- `POST /api/auth/logout-all` - Logout from all devices (revoke all user's refresh tokens)
  - Request: Authorization header with access token
  - Response: 204 No Content

### 5.2 Folders ⭐
- `GET /api/folders` - Lấy folder tree của user (có thể limit depth)
- `GET /api/folders/{id}` - Chi tiết folder
- `GET /api/folders/{id}/children` - Lấy direct children (folders + decks)
- `GET /api/folders/{id}/descendants` - Lấy tất cả descendants (recursive)
- `POST /api/folders` - Tạo folder mới (body: name, description, parent_folder_id)
  - Validation: depth <= 10
- `PUT /api/folders/{id}` - Cập nhật folder (rename)
- `DELETE /api/folders/{id}` - Xóa folder (soft delete)
- `POST /api/folders/{id}/move` - Di chuyển folder (body: new_parent_folder_id)
  - Validation: depth sau move <= 10
- `POST /api/folders/{id}/copy` - Copy folder (body: destination_folder_id, copy_decks: boolean) ⭐
  - Response:
    - Sync (<= 50 items): Immediate success
    - Async (51-500 items): Return job_id, status "PROCESSING"
  - Max 500 items total
- `GET /api/folders/copy-status/{job_id}` - Check async copy status ⭐
  - Response: {status: "PROCESSING/COMPLETED/FAILED", progress: 0-100, message}
- `GET /api/folders/{id}/stats` - Lấy folder statistics (recursive)
- `GET /api/folders/{id}/breadcrumb` - Lấy breadcrumb path

### 5.3 Decks
- `GET /api/decks?folder_id={folderId}` - Lấy decks trong folder
- `POST /api/decks` - Tạo deck (body: name, description, folder_id)
- `GET /api/decks/{id}` - Chi tiết deck + cards
- `PUT /api/decks/{id}` - Update deck
- `DELETE /api/decks/{id}` - Xóa deck
- `POST /api/decks/{id}/move` - Move deck (body: new_folder_id)
- `POST /api/decks/{id}/copy` - Copy deck (body: destination_folder_id) ⭐
  - Response:
    - Sync (<= 1000 cards): Immediate success
    - Async (1001-10,000 cards): Return job_id, status "PROCESSING"
  - Max 10,000 cards
- `GET /api/decks/copy-status/{job_id}` - Check async copy status ⭐

### 5.4 Cards
- `GET /api/decks/{deckId}/cards` - Lấy cards trong deck
- `POST /api/decks/{deckId}/cards` - Tạo card
- `PUT /api/cards/{id}` - Update card
- `DELETE /api/cards/{id}` - Xóa card
- `POST /api/decks/{deckId}/cards/import` - Import cards từ CSV/Excel ⭐
  - Body: multipart/form-data (file upload)
  - Response: Import summary (success count, error count, error details)
- `GET /api/decks/{deckId}/cards/export` - Export cards to CSV/Excel ⭐
  - Query params: format (csv/xlsx), filter (all/due)
  - Response: File download
- `GET /api/cards/template` - Download template file (CSV/Excel) ⭐
  - Query params: format (csv/xlsx)
  - Response: Template file với sample data

### 5.5 SRS Settings
- `GET /api/srs/settings` - Lấy settings
- `PUT /api/srs/settings` - Update settings (review_order, notification_enabled, notification_time, forgotten_card_action, daily limits)
- `POST /api/srs/settings/reset` - Reset về default

### 5.6 Review ⭐
- `GET /api/review/due?mode=SPACED_REPETITION&scope=folder&scope_id={folderId}` - Lấy cards due
  - Query params: mode, scope (all/folder/deck), scope_id, limit (default 100)
  - Response: Paginated results với total count
  - **Performance optimization**:
    - Batch fetch cards với JOIN (cards, decks, card_box_position)
    - Use index: (user_id, due_date, current_box)
    - Limit query results: max 200 cards per request
    - Sort by: due_date ASC, current_box (review order applied after fetch)
- `POST /api/review/submit` - Submit rating (body: card_id, rating)
- `POST /api/review/undo` - Undo last review
- `POST /api/review/skip/{cardId}` - Skip card

### 5.7 Statistics
- `GET /api/stats/user` - User statistics (streak, total cards learned, study time)
- `GET /api/stats/folder/{id}` - Folder statistics (recursive)
- `GET /api/stats/deck/{id}` - Deck statistics
- `GET /api/stats/box-distribution?scope=folder&scope_id={folderId}` - Cards per box
- ❌ ~~heatmap, advanced charts~~ → Future

---

## 6. Non-Functional Requirements (Đơn giản hóa)

### 6.1 Performance
- Response time < 200ms cho CRUD operations
- Load folder tree < 300ms
- Load review session < 500ms
- Hỗ trợ 10,000+ cards/deck, 1,000+ folders/user
- **Database optimization**:
  - Index trên path column cho ancestor queries
  - Materialized path cho quick tree traversal
  - Denormalize folder_stats để tránh recursive queries
- ❌ ~~Redis caching~~ → Future (có thể thêm sau)

### 6.2 Security

**MVP (Acceptable):**
- Password encryption: bcrypt (cost factor 12)
- HTTPS cho production
- JWT authentication: 24h expiry, no refresh token
- Input validation: sanitize user inputs
- Authorization: user chỉ access data của mình
- Rate limiting: 100 requests/minute/user (simple in-memory)

**Production Recommendations:** ⚠️
- **JWT with Refresh Token**:
  - Access token: 15 minutes (short-lived)
  - Refresh token: 7 days, HTTP-only cookie
  - Token rotation on refresh
  - Refresh token stored in DB với revocation support
- **Enhanced Rate Limiting**:
  - Redis-based distributed rate limiting
  - Different limits per endpoint type:
    - Auth: 5 req/min
    - Read: 100 req/min
    - Write: 50 req/min
    - Copy operations: 10 req/hour
- **Security Headers**:
  - CORS: whitelist allowed origins
  - CSP: Content Security Policy
  - X-Frame-Options: DENY
  - X-Content-Type-Options: nosniff
- **Audit Logging**:
  - Log all copy/move/delete operations
  - Failed login attempts tracking
  - Suspicious activity detection

### 6.3 Scalability
- Đủ cho single user hoặc small team (< 100 users)
- ❌ ~~100,000+ concurrent users~~ → Future
- ❌ ~~Database sharding~~ → Future
- ❌ ~~CDN, horizontal scaling~~ → Future

### 6.4 Reliability
- Daily database backup (manual hoặc scheduled job)
- Error logging: console logs (production: file logs)
- ❌ ~~Sentry, CloudWatch~~ → Future

### 6.5 Usability
- Responsive design: mobile, tablet, desktop
- **Theme support**: Light mode + Dark mode ⭐
  - System preference detection (prefers-color-scheme)
  - Manual toggle trong settings
  - Persistent user preference
  - Smooth transition animation
- Language support: Vietnamese (primary), English (secondary)
  - i18n framework: react-i18next (Web), i18n-js (Mobile)
  - User can switch language in settings
- ❌ ~~Keyboard shortcuts~~ → Future
- ❌ ~~Drag & drop~~ → Future
- ❌ ~~WCAG compliance~~ → Future

### 6.6 Testing
- Unit test coverage ≥ 70% (core logic)
- **Critical test cases**:
  - Folder copy với deep nested structure
  - Folder move validation (không thể move folder vào chính nó hoặc children của nó)
  - Folder delete cascade behavior
  - Folder statistics calculation accuracy
  - SRS algorithm correctness
  - Import/Export: CSV/Excel parsing, validation, error handling ⭐
  - Large file import (10,000 rows) performance test ⭐
- Integration tests cho folder operations, review session, import/export
- ❌ ~~E2E tests~~ → Future
- ❌ ~~Load testing~~ → Future

---

## 7. Development Phases

### Phase 1: MVP Core (6-8 weeks)
**Week 1-2: Backend Foundation**
- ✅ Database setup (PostgreSQL)
- ✅ User authentication (register, login) với refresh token ⭐
  - RefreshToken entity, repository, service
  - /api/auth/login → returns access + refresh tokens
  - /api/auth/refresh → validates refresh token, rotates tokens
  - /api/auth/logout → revokes refresh token
  - Flyway migration V2__create_refresh_tokens_table.sql
- ✅ Folder CRUD APIs với tree structure (parent_folder_id, path, depth)
- ✅ Folder move/copy logic
- ✅ Deck CRUD APIs
- ✅ Card CRUD APIs
- ✅ Import/Export service: CSV/Excel processing với Apache POI & OpenCSV ⭐

**Week 3-4: SRS Algorithm**
- ✅ Box-based SRS implementation (7 boxes với configurable intervals)
- ✅ Review session logic (rating, box movement)
- ✅ Review order strategies (Ascending/Descending/Random)
- ✅ Forgotten card action strategies
- ✅ Due cards calculation (support folder scope)
- ✅ Review APIs (undo, skip)

**Week 5-6: Folder Statistics & Study Modes**
- ✅ Folder statistics calculation (recursive)
- ✅ Visitor pattern cho folder tree traversal
- ✅ Standard SRS mode
- ✅ Cram mode
- ✅ Random mode

**Week 7-8: Frontend Web**
- ✅ Login/Register pages
- ✅ Axios interceptor for auto token refresh ⭐
  - Detect 401 → call /api/auth/refresh
  - Retry failed request with new access token
  - Handle refresh token expiration → redirect to login
- ✅ Tree view sidebar cho folders
- ✅ Breadcrumb navigation
- ✅ Folder/Deck CRUD UI
- ✅ Card CRUD UI
- ✅ Import/Export UI: File upload, preview, progress bar ⭐
- ✅ Review session UI với multiple modes
- ✅ SRS settings page
- ✅ Statistics dashboard (streak, box distribution)
- ✅ Theme toggle: Light/Dark mode với smooth transition ⭐
- ✅ Language switcher: VI/EN

### Phase 2: Mobile App (4-5 weeks)
**Week 9-10: React Native Setup**
- ✅ Navigation setup
- ✅ Login/Register screens với token refresh logic ⭐
  - Store access token in memory (secure)
  - Refresh token in HTTP-only cookie (auto-sent)
  - Token refresh interceptor for API calls
- ✅ Tree view component cho folders
- ✅ Folder/Deck CRUD screens
- ✅ Card CRUD screens
- ✅ Import/Export screens: File picker, preview modal ⭐

**Week 11-13: Mobile Review + Notifications**
- ✅ Review session screen với multiple modes
- ✅ SRS settings screen
- ✅ Push notifications setup
- ✅ Statistics screen
- ✅ Theme toggle: Light/Dark mode ⭐
- ✅ Language switcher: VI/EN
- ✅ Testing on iOS/Android

### Phase 3: Polish & Deploy (1-2 weeks)
- ✅ Bug fixes
- ✅ UI/UX refinements
- ✅ Edge case handling (circular folder reference, validation)
- ✅ Production deployment
- ✅ Database backup setup

**Total**: ~12-15 weeks (3-4 tháng)

---

## 8. Future Enhancements (Post-MVP)

### Phase 4: UI/UX Enhancements
- Drag & drop cho folders/decks
- Context menu (right-click)
- Color/icon customization cho folders
- List view/Grid view toggle
- Search trong folders
- Bulk operations

### Phase 5: Rich Content
- Rich text editor (bold, italic, underline)
- Image/audio support
- Code snippets với syntax highlighting
- Cloze deletion, multiple choice
- Tags cho cards/decks

### Phase 6: Analytics & Gamification
- Heatmap activity
- Advanced charts (line, pie, forecast)
- Retention rate, lapse rate tracking
- Badges & achievements
- Leaderboard

### Phase 7: Social Features
- Share decks/folders (public/private)
- Community decks/folders
- Collaborative editing với real-time sync
- Import/Export (CSV, Anki format)
- Rating & review

### Phase 8: Premium Features
- Offline mode với sync
- AI-generated cards
- Advanced analytics
- Suspend card feature
- Test mode với scoring

---

## 9. Architecture for Maintainability ⭐

### 9.1 Code Organization

**Backend (Spring Boot 3 + JPA)**

```
backend/
├── src/main/java/com/repeatwise/
│   ├── config/          # Configuration classes
│   │   ├── SecurityConfig.java
│   │   ├── CorsConfig.java
│   │   └── AsyncConfig.java
│   ├── controller/      # REST endpoints
│   │   ├── AuthController.java
│   │   ├── FolderController.java
│   │   ├── DeckController.java
│   │   ├── CardController.java
│   │   ├── ReviewController.java
│   │   └── StatsController.java
│   ├── service/         # Service interfaces
│   │   ├── IAuthService.java
│   │   ├── IFolderService.java
│   │   ├── IDeckService.java
│   │   ├── ICardService.java
│   │   ├── IReviewService.java
│   │   ├── ISRSService.java
│   │   └── IStatsService.java
│   ├── service/impl/    # Service implementations
│   │   ├── AuthServiceImpl.java
│   │   ├── FolderServiceImpl.java
│   │   ├── DeckServiceImpl.java
│   │   ├── CardServiceImpl.java
│   │   ├── ReviewServiceImpl.java
│   │   ├── SRSServiceImpl.java
│   │   └── StatsServiceImpl.java
│   ├── repository/      # Spring Data JPA Repositories
│   │   ├── UserRepository.java
│   │   ├── FolderRepository.java
│   │   ├── DeckRepository.java
│   │   ├── CardRepository.java
│   │   ├── CardBoxPositionRepository.java
│   │   ├── ReviewLogRepository.java
│   │   ├── SRSSettingsRepository.java
│   │   └── StatsRepository.java
│   ├── entity/          # JPA Entities
│   │   ├── User.java
│   │   ├── Folder.java
│   │   ├── Deck.java
│   │   ├── Card.java
│   │   ├── CardBoxPosition.java
│   │   ├── ReviewLog.java
│   │   ├── SRSSettings.java
│   │   └── UserStats.java
│   ├── dto/             # Data transfer objects
│   │   ├── request/     # Request DTOs
│   │   │   ├── LoginRequest.java
│   │   │   ├── CreateFolderRequest.java
│   │   │   ├── MoveFolderRequest.java
│   │   │   ├── CreateDeckRequest.java
│   │   │   ├── CreateCardRequest.java
│   │   │   └── ReviewSubmitRequest.java
│   │   └── response/    # Response DTOs
│   │       ├── AuthResponse.java
│   │       ├── FolderResponse.java
│   │       ├── DeckResponse.java
│   │       ├── CardResponse.java
│   │       ├── ReviewSessionResponse.java
│   │       └── StatsResponse.java
│   ├── mapper/          # DTO Mappers (MapStruct)
│   │   ├── UserMapper.java
│   │   ├── FolderMapper.java
│   │   ├── DeckMapper.java
│   │   ├── CardMapper.java
│   │   └── ReviewMapper.java
│   ├── strategy/        # Strategy pattern implementations
│   │   ├── ReviewOrderStrategy.java
│   │   ├── AscendingReviewStrategy.java
│   │   ├── DescendingReviewStrategy.java
│   │   ├── RandomReviewStrategy.java
│   │   ├── ForgottenCardActionStrategy.java
│   │   ├── MoveToBox1Strategy.java
│   │   ├── MoveDownNBoxesStrategy.java
│   │   └── StayInBoxStrategy.java
│   ├── visitor/         # Visitor pattern (folder stats)
│   │   ├── FolderVisitor.java
│   │   └── FolderStatsVisitor.java
│   ├── exception/       # Custom exceptions
│   │   ├── GlobalExceptionHandler.java
│   │   ├── ResourceNotFoundException.java
│   │   ├── DuplicateResourceException.java
│   │   └── ValidationException.java
│   └── util/            # Helper classes
│       ├── JwtUtil.java
│       ├── DateUtil.java
│       └── PasswordUtil.java
├── src/main/resources/
│   ├── application.yml           # Main config
│   ├── application-dev.yml       # Dev config
│   ├── application-prod.yml      # Prod config
│   └── db/migration/             # Flyway migrations
│       ├── V1__create_users_table.sql
│       ├── V2__create_folders_table.sql
│       ├── V3__create_decks_table.sql
│       ├── V4__create_cards_table.sql
│       └── V5__create_srs_tables.sql
└── src/test/java/               # Unit & Integration tests
    ├── service/                 # Service tests
    ├── repository/              # Repository tests
    └── controller/              # Controller tests (MockMvc)
```

**Frontend Web (React TypeScript + Tailwind CSS + Shadcn)**

```
frontend-web/
├── src/
│   ├── components/      # Reusable UI components
│   │   ├── ui/          # Shadcn/ui components (Button, Input, Dialog, Card...)
│   │   ├── common/      # Custom common components
│   │   ├── folder/      # FolderTree, FolderCard...
│   │   ├── deck/        # DeckList, DeckCard...
│   │   ├── card/        # CardItem, CardEditor...
│   │   └── review/      # ReviewCard, RatingButtons...
│   ├── pages/           # Page components
│   │   ├── Auth/        # Login, Register
│   │   ├── Dashboard/   # Main dashboard
│   │   ├── Folder/      # Folder management
│   │   ├── Deck/        # Deck management
│   │   ├── Review/      # Review session
│   │   ├── Settings/    # SRS settings
│   │   └── Stats/       # Statistics
│   ├── services/        # API calls
│   │   ├── api.ts       # Axios instance
│   │   ├── authService.ts
│   │   ├── folderService.ts
│   │   ├── deckService.ts
│   │   ├── cardService.ts
│   │   ├── reviewService.ts
│   │   └── statsService.ts
│   ├── contexts/        # React Context API
│   │   ├── AuthContext.tsx      # Auth state (user, login, logout)
│   │   └── SettingsContext.tsx  # SRS settings state
│   ├── store/           # Zustand (optional for UI state)
│   │   └── uiStore.ts   # Sidebar, theme, modal state
│   ├── hooks/           # Custom React Query hooks
│   │   ├── useAuth.ts   # Hook for AuthContext
│   │   ├── useFolder.ts # useQuery, useMutation for folders
│   │   ├── useDeck.ts   # useQuery, useMutation for decks
│   │   ├── useCard.ts   # useQuery, useMutation for cards
│   │   ├── useReview.ts # useQuery, useMutation for review
│   │   └── useStats.ts  # useQuery for statistics
│   ├── lib/             # Utility libraries
│   │   └── utils.ts     # cn() helper, etc.
│   ├── types/           # TypeScript types
│   └── constants/       # Constants
├── tailwind.config.js   # Tailwind configuration
├── components.json      # Shadcn/ui config
└── package.json
```

**Mobile (React Native)**

```
frontend-mobile/
├── src/
│   ├── components/      # Reusable components
│   │   ├── common/      # Button, Input, Card...
│   │   ├── folder/      # FolderTreeView, FolderItem...
│   │   ├── deck/        # DeckList, DeckCard...
│   │   ├── card/        # CardItem, CardEditor...
│   │   └── review/      # ReviewCard, RatingButtons...
│   ├── screens/         # Screen components
│   │   ├── Auth/        # LoginScreen, RegisterScreen
│   │   ├── Home/        # HomeScreen (dashboard)
│   │   ├── Folder/      # FolderScreen, FolderDetailScreen
│   │   ├── Deck/        # DeckScreen, DeckDetailScreen
│   │   ├── Review/      # ReviewScreen
│   │   ├── Settings/    # SettingsScreen (SRS settings)
│   │   └── Stats/       # StatsScreen
│   ├── navigation/      # React Navigation
│   │   ├── AppNavigator.tsx
│   │   ├── AuthNavigator.tsx
│   │   └── MainNavigator.tsx
│   ├── services/        # API calls
│   │   ├── api.ts       # Axios instance
│   │   ├── authService.ts
│   │   ├── folderService.ts
│   │   ├── deckService.ts
│   │   ├── cardService.ts
│   │   ├── reviewService.ts
│   │   └── statsService.ts
│   ├── contexts/        # React Context API
│   │   ├── AuthContext.tsx      # Auth state
│   │   └── SettingsContext.tsx  # SRS settings
│   ├── store/           # Zustand (optional for UI state)
│   │   └── uiStore.ts   # UI state
│   ├── hooks/           # Custom React Query hooks
│   │   ├── useAuth.ts
│   │   ├── useFolder.ts
│   │   ├── useDeck.ts
│   │   ├── useCard.ts
│   │   ├── useReview.ts
│   │   └── useStats.ts
│   ├── types/           # TypeScript types (shared with web)
│   ├── utils/           # Helper functions
│   ├── constants/       # Constants
│   └── notifications/   # Push notification service
├── android/             # Android native code
├── ios/                 # iOS native code
└── package.json
```

### 9.2 Design Principles

#### SOLID Principles
- **Single Responsibility**: Mỗi class chỉ có 1 trách nhiệm duy nhất
- **Open/Closed**: Open for extension, closed for modification
- **Liskov Substitution**: Strategy pattern cho review order, forgotten card actions
- **Interface Segregation**: Service interfaces riêng biệt (IFolderService, IDeckService...)
- **Dependency Inversion**: Depend on abstractions (interfaces), not implementations

#### Other Principles
- **DRY**: Don't repeat yourself
- **KISS**: Keep it simple, stupid
- **Separation of concerns**: Controller → Service → Mapper (MyBatis)
- **Interface-based design**:
  - Service layer: Interface + Implementation
  - Easy to mock for testing
  - Có thể swap implementation dễ dàng

#### JPA Best Practices
- **Entity Design**: Proper use of `@Entity`, `@Table`, `@Column` annotations
- **Relationships**: `@OneToMany`, `@ManyToOne` with proper `fetch` strategies
- **Queries**:
  - Use Spring Data JPA methods for simple queries
  - JPQL for complex queries
  - `@Query` with native SQL khi cần performance
- **Lazy Loading**: Avoid N+1 problems với `@EntityGraph` hoặc JOIN FETCH
- **DTO Projection**: Use interface projections hoặc class-based DTOs
- **Auditing**: `@CreatedDate`, `@LastModifiedDate` với `@EntityListeners`

### 9.3 Extensibility Points
- **Strategy Pattern** cho SRS algorithm → dễ thêm custom algorithms sau
- **DTO Pattern** → dễ thay đổi API contract mà không ảnh hưởng business logic
- **Repository Pattern (JPA)** → dễ thay đổi database hoặc thêm cache layer
- **Interface-based Services** → dễ swap implementation hoặc add decorators
- **Config-driven**: Database settings trong application.yml → dễ config cho dev/prod
- **Visitor Pattern** → Traverse folder tree để tính statistics
- **Composite Pattern** → Folder tree structure

**Note**: Chi tiết implementation sẽ được tài liệu hóa trong các file riêng:
- `docs/backend-architecture.md` - Backend architecture chi tiết
- `docs/jpa-implementation.md` - JPA entities và repositories
- `docs/design-patterns.md` - Design patterns implementation
- `docs/srs-algorithm.md` - SRS algorithm chi tiết

### 9.4 State Management Strategy ⭐

#### Why NOT Redux for MVP:
1. **App complexity chưa cao**: Chỉ 6-7 screens, state sharing không nhiều
2. **Overhead lớn**: Setup store, slices, actions, reducers (~300 lines boilerplate)
3. **React Query tốt hơn cho server state**: Built-in caching, refetch, stale time
4. **Bundle size**: Redux Toolkit ~15KB vs React Query ~5KB + Zustand ~1KB

#### Chosen Architecture:
- **Server State**: TanStack Query (React Query) - folders, decks, cards, reviews
- **Auth State**: Context API - user authentication
- **UI State**: Zustand (optional) - sidebar, modals, theme
- **Benefits**: Less code, better DX, auto caching, dễ migrate sau

**Note**: Chi tiết implementation sẽ được tài liệu hóa trong:
- `docs/frontend-state-management.md` - State management patterns
- `docs/react-query-setup.md` - React Query configuration và hooks
- `docs/tailwind-shadcn-setup.md` - Tailwind CSS + Shadcn/ui setup và theming

### 9.5 Migration Path to Full Version
- **Database schema** đã thiết kế sẵn columns cho future features (comment out)
- **API versioning**: `/api/v1/...` → dễ thêm v2 sau
- **Feature flags**: Có thể thêm feature toggles để enable/disable features
- **Modular frontend**: Components tách biệt → dễ thêm features mới
- **State management**: React Query cho server state giữ nguyên, chỉ migrate Context → Redux nếu cần

---

## 10. Edge Cases & Validation Rules ⭐

### 10.1 Folder Operations
- **Circular reference prevention**: Không thể move folder A vào chính nó hoặc children của nó
- **Max depth limit**:
  - Database constraint: `CHECK depth <= 10`
  - API validation: reject nếu tạo/move vượt depth 10
  - Error message: "Maximum folder depth (10 levels) exceeded"
- **Name uniqueness**: Folder name phải unique trong cùng parent folder
- **Delete validation**: Confirm khi delete folder có chứa decks/sub-folders
- **Copy limits**: ⭐
  - Count total items (recursive): folders + decks
  - <= 50 items: Sync copy (immediate response)
  - 51-500 items: Async copy (return job_id, poll status)
  - > 500 items: Reject với error "Folder too large to copy (max 500 items)"
- **Move validation**:
  - Check depth sau move: `new_depth = destination.depth + folder.depth - current_parent.depth`
  - Reject nếu > 10
- Folder name: max 100 chars, không để trống

### 10.2 Deck Operations
- Deck name: max 100 chars, không để trống
- Deck name unique trong cùng folder
- Xóa deck → confirm dialog nếu có cards bên trong
- **Copy limits**: ⭐
  - <= 1000 cards: Sync copy (immediate)
  - 1001-10,000 cards: Async copy (return job_id)
  - > 10,000 cards: Reject với error "Deck too large to copy (max 10,000 cards)"

### 10.3 Card Operations
- Front/back text: không để trống, max 5000 chars
- Validation khi tạo: ít nhất 1 card trong deck mới có thể review
- **Soft delete**:
  - `deleted_at` field trong cards table
  - Queries exclude deleted: `WHERE deleted_at IS NULL`
  - Restore option: Set `deleted_at = NULL` (trong 30 ngày)
  - Permanent delete: Cleanup job sau 30 ngày

### 10.3.1 Import/Export Validation ⭐
**Import Validation:**
- File size max: 50MB
- Row limit: 10,000 rows per file
- Format validation:
  - CSV: UTF-8 encoding, comma delimiter
  - Excel: .xlsx format only (not .xls)
- Column validation:
  - Minimum 2 columns: Front, Back
  - Missing columns → show clear error message
- Row validation:
  - Empty rows → skip
  - Missing front OR back → mark as error, show row number
  - Front/back > 5000 chars → truncate with warning
- Duplicate handling:
  - Check duplicate front text in same deck
  - Options: Skip duplicates, Replace existing, Keep both
- Error reporting:
  - Show error summary: total errors, error types
  - Download error report file (CSV) với error details

**Export Validation:**
- Max 50,000 cards per export
- Timeout: 30 seconds max
- Async export cho large datasets (>5000 cards)

### 10.4 Review Session
- Không có due cards → hiển thị "No cards to review today"
- Daily limit reached → thông báo "Daily limit reached. Come back tomorrow!"

### 10.5 Performance Considerations

**UI Performance:**
- **Lazy loading**: Chỉ load direct children, expand on demand
- **Pagination**:
  - Folders/Decks list: 50 items per page
  - Cards list: 100 cards per page
  - Review session: Load 100 cards at a time, prefetch next batch
- **Virtual scrolling**: Cho large lists (>100 items) trong UI
- **Debouncing**: Search input với 300ms delay

**Async Operations:** ⭐
- **Folder copy**: Async cho > 50 items
- **Deck copy**: Async cho > 1000 cards
- **ThreadPool config**:
  - Core pool size: 5 threads
  - Max pool size: 10 threads
  - Queue capacity: 100 tasks
  - Rejection policy: CallerRunsPolicy (fallback to sync if queue full)
- **Job status tracking**:
  - In-memory store (ConcurrentHashMap) cho MVP
  - TTL: 1 hour sau completion
  - Cleanup job: run every 30 minutes
- **Progress calculation**:
  - Track: items_processed / total_items * 100
  - Update: every 10 items processed
- **Timeout protection**:
  - Folder copy: max 5 minutes
  - Deck copy: max 10 minutes
  - Auto-cancel with rollback nếu timeout

**Import/Export Performance:**
- Batch insert: 1000 cards per batch transaction
- Progress tracking: update progress every 500 rows
- Memory management: stream processing cho large files
- Timeout handling: cancel operation after 2 minutes

**Database Query Optimization:** ⭐

- **Review queries (Critical!)**:
  - Use composite index: (user_id, due_date, current_box)
  - LIMIT 200 cards per request
  - Batch fetch với single JOIN query instead of N+1
  - Example optimized query: SELECT cards, card_box_position, deck name FROM card_box_position JOIN cards JOIN decks WHERE user_id = ? AND due_date <= CURRENT_DATE ORDER BY due_date ASC, current_box ASC LIMIT 200
- **Folder tree queries**:
  - Materialized path index: CREATE INDEX idx_folders_path ON folders(path varchar_pattern_ops)
  - Avoid N+1: batch fetch all descendants in single query
  - Limit recursive depth: max 10 levels enforced
  - Use CTE (Common Table Expressions) for complex tree operations
- **Statistics queries**:
  - Use denormalized folder_stats table (cached)
  - Update stats async (not real-time)
  - Refresh stats every 5 minutes or on-demand

---

**Version**: 1.0 MVP
**Last Updated**: January 2025
**Focus**: Simple, maintainable, extensible personal learning app
