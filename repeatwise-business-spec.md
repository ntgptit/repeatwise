# Requirements Document: RepeatWise - Flashcard + Spaced Repetition System (SRS)

## 1. Tổng quan hệ thống

Ứng dụng học tập thông minh sử dụng thuật toán Spaced Repetition (SRS) dạng Box System để tối ưu hóa việc ghi nhớ. Người dùng tạo flashcard, tổ chức theo cấu trúc tree folders, hệ thống tự động lên lịch ôn tập dựa trên khả năng ghi nhớ cá nhân với các tùy chọn cấu hình linh hoạt.

---

## 2. Core Features

### 2.1 User Management
- Đăng ký/Đăng nhập (email, OAuth Google/Facebook)
- Quản lý profile: timezone, ngôn ngữ, avatar
- Phân quyền: Free User, Premium User, Admin

### 2.2 Folder & Deck Management ⭐

#### 2.2.1 Tree-Based Folder Structure
- **Hierarchical organization**: Folders có thể lồng vô hạn cấp độ (unlimited depth)
- **Root level**: User có thể tạo nhiều folders ở root
- **Parent-Child relationship**: Mỗi folder có thể chứa:
  - Sub-folders (folders con)
  - Decks (bộ thẻ)
- **Visual representation**: Tree view với expand/collapse nodes

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
- **Create folder**: Tạo folder mới ở bất kỳ level nào
- **Rename folder**: Đổi tên folder
- **Move folder**: Di chuyển folder (drag & drop hoặc select destination)
  - Move folder A vào folder B → A trở thành child của B
  - Move folder ra root level
  - Kéo thả trong tree view để reorganize
- **Copy folder** ⭐: 
  - Copy folder kèm toàn bộ sub-folders và decks bên trong
  - Deep copy: tạo bản sao hoàn toàn độc lập
  - User chọn destination folder để paste
  - Options: Copy với decks hoặc chỉ copy folder structure
- **Delete folder**: 
  - Soft delete để có thể khôi phục
  - Options: Delete cả sub-folders và decks bên trong, hoặc chỉ delete folder (move contents lên parent)
  - Confirm dialog khi delete để tránh mất dữ liệu

#### 2.2.3 Folder Features
- **Folder metadata**:
  - Name, description, color/icon (để phân biệt)
  - Created date, last modified date
  - Total decks count (recursive - tính cả decks trong sub-folders)
  - Total cards count (recursive)
- **Folder permissions**:
  - Private: chỉ owner có thể view/edit
  - Shared: chia sẻ với specific users với permissions (view only/edit)
  - Public: mọi người có thể xem và clone
- **Folder actions**:
  - Collapse/Expand all sub-folders
  - Sort folders/decks by: name, date, card count
  - Search trong folder và sub-folders
  - Bulk actions: select multiple folders để move/copy/delete

#### 2.2.4 Deck Management
- **Create deck**: Tạo deck trong folder hoặc ở root level
- **CRUD decks**: Tạo, xem, sửa, xóa (soft delete)
- **Move deck**: Di chuyển deck giữa các folders
- **Copy deck**: Copy deck sang folder khác
- **Deck metadata**:
  - Name, description, tags
  - Parent folder path
  - Card count, due cards count
  - Last studied date
- **Import/Export deck**: 
  - CSV, JSON, Anki format
  - Import vào folder cụ thể
  - Export deck hoặc export cả folder (zip file chứa tất cả decks)
- **Share deck**: Public hoặc private với specific users

#### 2.2.5 Folder Navigation & UX
- **Breadcrumb navigation**: Hiển thị folder path hiện tại
  - Example: `Home > English Learning > IELTS Preparation > Vocabulary`
  - Click vào bất kỳ breadcrumb nào để navigate
- **Tree view sidebar**:
  - Collapsible tree structure
  - Show/hide folder tree
  - Highlight current selected folder
  - Context menu (right-click): Copy, Move, Delete, Share...
- **List view/Grid view**: Switch giữa list và grid layout
- **Folder statistics widget**: 
  - Hiển thị tổng số decks, cards, due cards trong folder đang chọn (bao gồm sub-folders)

### 2.3 Flashcard Management
- CRUD flashcards với front (câu hỏi) và back (câu trả lời)
- Hỗ trợ rich text: bold, italic, underline, highlight
- Chèn images, audio, code snippet với syntax highlighting
- **Card types**:
  - Basic: front/back đơn giản
  - Cloze deletion: điền vào chỗ trống
  - Multiple choice: nhiều đáp án
  - Type-in answer: gõ câu trả lời chính xác
- **Metadata**: tags, difficulty level, notes, source reference
- **Bulk import**: cards từ file CSV/Excel
- **Card linking**: Link card với related cards để học theo context

### 2.4 Spaced Repetition System - Box-Based Algorithm ⭐

#### 2.4.1 Box System Configuration
- **Số lượng ô (boxes)**: Configurable, mặc định 7 ô
- Mỗi ô đại diện cho một level ghi nhớ
- Card di chuyển giữa các ô dựa trên performance
- **Interval tăng theo cấp số nhân**: 
  - Box 1: 1 day
  - Box 2: 3 days
  - Box 3: 7 days
  - Box 4: 14 days
  - Box 5: 30 days
  - Box 6: 60 days
  - Box 7: 120 days

#### 2.4.2 Review Order Settings
User có thể chọn thứ tự review cards:
- **Tăng dần (Ascending)**: Review từ ô 1 → ô 7 (từ khó → dễ)
- **Giảm dần (Descending)**: Review từ ô 7 → ô 1 (từ dễ → khó)
- **Ngẫu nhiên (Random)**: Xáo trộn thứ tự review

#### 2.4.3 Review Session
- Hiển thị card với front side
- User suy nghĩ và lật card để xem answer
- **Rating options**:
  - **Again** (< 1 minute): Không nhớ/sai
  - **Hard** (< 6 minutes): Nhớ khó khăn
  - **Good** (next interval): Nhớ tốt
  - **Easy** (4x interval): Nhớ rất dễ
- **Actions**: 
  - Undo: quay lại card trước
  - Skip: postpone card, sẽ review lại cuối session
  - Suspend: tạm ngưng card, không xuất hiện trong review
  - Edit card: sửa nội dung card ngay trong review session

#### 2.4.4 Forgotten Card Actions ⭐
User cấu hình hành động khi nhấn "Again" (quên card):
- **Di chuyển vào ô 1**: Reset card về ô đầu tiên (restart from scratch)
- **Di chuyển xuống N ô**: Lùi lại N ô (configurable: 1, 2, 3 ô)
- **Giữ nguyên ô**: Card ở lại ô hiện tại, chỉ giảm ease factor và interval

#### 2.4.5 Notification Settings ⭐
- **Toggle ON/OFF**: Bật/tắt thông báo khi cần lặp lại các từ
- **Notification triggers**:
  - Khi có cards due trong ngày
  - Daily reminder vào thời gian user set (ví dụ: 9:00 AM, 8:00 PM)
  - Khi streak sắp bị break (chưa học trong 23h)
  - Weekly summary: tổng kết tiến độ tuần
- **Notification channels**:
  - Push notification trên mobile/web
  - Email notification (optional)
  - In-app notification badge

#### 2.4.6 Daily Limits
- Giới hạn số cards mới mỗi ngày (default: 20, configurable)
- Giới hạn số cards review tối đa mỗi ngày (default: 200, configurable)
- Auto-pause khi đạt limit, có option để override

### 2.5 Study Modes ⭐

#### 2.5.1 Chế độ lặp lại giãn cách (Standard SRS)
- Review cards theo SRS schedule dựa trên box position
- Áp dụng thuật toán box-based với interval tính theo ô hiện tại
- **Review scope options**:
  - Review all due cards từ tất cả decks
  - Review due cards từ folder cụ thể (bao gồm sub-folders) ⭐
  - Review due cards từ deck cụ thể
  - Review due cards từ tags cụ thể

#### 2.5.2 Cram Mode
- Học nhanh tất cả cards, không theo schedule
- Không ảnh hưởng đến SRS schedule
- **Cram scope options**:
  - Cram toàn bộ deck
  - Cram toàn bộ folder (all cards trong folder và sub-folders) ⭐
  - Cram cards theo tags
  - Cram cards theo difficulty level

#### 2.5.3 Test Mode
- Kiểm tra kiến thức, không có gợi ý
- Tính điểm và hiển thị kết quả chi tiết
- Không ảnh hưởng đến SRS schedule
- Export kết quả test (PDF report)

#### 2.5.4 Random Mode
- Review ngẫu nhiên cards trong deck/folder
- Không theo thứ tự hoặc schedule
- Chọn số lượng cards muốn review

#### 2.5.5 Custom Study
- Filter cards theo: tags, difficulty, box number, deck, folder
- Chọn số lượng cards cụ thể
- Study ahead: review cards sẽ due trong tương lai (1-7 days)

### 2.6 Statistics & Analytics

#### 2.6.1 Personal Statistics
- **Heatmap activity**: số cards học mỗi ngày trong năm
- **Streak counter**: số ngày học liên tục
- **Card distribution by box**: số cards trong từng ô (Box 1-7)
- **Card distribution by folder**: breakdown theo folder hierarchy ⭐
- **Accuracy rate**: tỷ lệ đúng overall, theo deck, theo folder, theo tag
- **Time spent studying**: tổng thời gian học, average per day/week
- **Cards learned**: new cards, mature cards (box ≥ 5)
- **Retention rate**: % cards nhớ được sau N ngày
- **Lapse rate**: số lần quên trung bình mỗi card

#### 2.6.2 Folder Statistics ⭐
- Total cards trong folder (recursive)
- Due cards count trong folder (recursive)
- New cards count
- Mature cards count
- Average retention rate của folder
- Most difficult cards trong folder
- Least reviewed cards trong folder

#### 2.6.3 Deck Statistics
- Total cards, new, learning, review
- Mature cards (interval > 21 days)
- Average ease factor
- Retention rate
- Hardest cards (most lapsed)

#### 2.6.4 Charts & Graphs
- **Line chart**: Cards learned theo thời gian
- **Pie chart**: Card distribution by box, by folder
- **Bar chart**: Accuracy by deck/folder
- **Forecast chart**: Predicted due cards trong 7-30 ngày tới
- **Heatmap**: Activity calendar

### 2.7 Social Features
- **Community decks**: Browse, search public decks
- **Community folders**: Browse public folder structures, clone cả folder tree ⭐
- **Rating & review**: Đánh giá và comment
- **Share folders/decks**: Public hoặc private với specific users
- **Collaborate mode**: Nhiều người cùng edit folder/deck với real-time sync

### 2.8 Gamification
- **Badges & achievements**: 
  - 100 cards learned, 30-day streak, perfect week...
  - Folder-specific achievements: "Master IELTS Vocabulary" ⭐
- **Level system**: Level up dựa trên total cards mastered
- **Leaderboard**: Xếp hạng theo streak, total cards, accuracy
- **Daily goals**: Set mục tiêu học bao nhiêu cards/ngày với progress bar

### 2.9 Offline Mode
- Study offline với data đã cache
- Sync data tự động khi reconnect
- Conflict resolution khi có changes khác nhau local vs server
- Offline indicator trong UI

---

## 3. Technical Stack

### 3.1 Backend
- **Language**: Java 17
- **Framework**: Spring Boot 3
- **Database**: PostgreSQL (primary), Oracle (enterprise option)
- **Cache**: Redis cho session, SRS settings, folder tree cache
- **Message Queue**: RabbitMQ cho async tasks (notifications, email)
- **File Storage**: AWS S3 hoặc MinIO cho images/audio
- **Search Engine**: Elasticsearch cho full-text search cards/folders (optional)

### 3.2 Frontend
- **Web**: React TypeScript
- **Mobile**: React Native
- **State Management**: Redux Toolkit (React), Redux cho React Native
- **UI Components**: Ant Design (Web), React Native Paper/NativeBase (Mobile) cho tree view

### 3.3 Architecture
- **Pattern**: Layered Architecture với Controller → Service → Repository
- **MVC/MVVM** cho separation of concerns
- **Dependency Injection** với Spring IoC
- **RESTful API** với proper HTTP methods

### 3.4 Design Patterns
- **Composite Pattern**: Folder tree structure (Folder chứa Folders/Decks) ⭐
- **Strategy Pattern**: Review order strategies, Forgotten card action strategies
- **Factory Pattern**: Card types, Notification types
- **Observer Pattern**: Notification system
- **Repository Pattern**: Data access layer
- **DTO Pattern**: Data transfer between layers
- **Visitor Pattern**: Traverse folder tree để tính statistics ⭐

---

## 4. Database Schema

### 4.1 Core Tables

**users**
```sql
id, email, password_hash, timezone, premium_status, created_at, last_login
```

**folders** ⭐
```sql
id, user_id, parent_folder_id (nullable - null = root level), 
name, description, color, icon, 
path (materialized path: /1/5/12), -- for quick ancestor queries
depth (level trong tree), 
is_public, shared_with (JSON array of user IDs),
created_at, updated_at, deleted_at (soft delete)
```

**decks**
```sql
id, user_id, folder_id (nullable - null = root level),
name, description, is_public, 
created_at, updated_at, deleted_at
```

**cards**
```sql
id, deck_id, front, back, card_type, 
created_at, updated_at
```

**card_metadata**
```sql
card_id, tags (JSON array), note, source, difficulty_level
```

### 4.2 SRS-Specific Tables

**srs_settings**
```sql
id, user_id, 
total_boxes (default: 7), 
review_order (ASCENDING/DESCENDING/RANDOM), 
notification_enabled (boolean), 
notification_time (time),
forgotten_card_action (MOVE_TO_BOX_1/MOVE_DOWN_N_BOXES/STAY_IN_BOX), 
move_down_boxes (int), 
default_game_mode,
new_cards_per_day (int),
max_reviews_per_day (int),
created_at, updated_at
```

**card_box_position**
```sql
id, card_id, user_id, 
current_box (1-7), 
ease_factor (decimal), 
interval_days (int), 
due_date (timestamp), 
last_reviewed_at (timestamp), 
review_count (int), 
lapse_count (int),
created_at, updated_at
```

**review_logs**
```sql
id, card_id, user_id, 
rating (AGAIN/HARD/GOOD/EASY), 
previous_box, new_box, 
interval_days, 
time_taken_seconds,
reviewed_at
```

**user_stats**
```sql
user_id, 
total_cards_learned, 
streak_days, 
last_study_date, 
total_study_time_minutes,
updated_at
```

**folder_stats** ⭐ (cached/computed)
```sql
folder_id, user_id,
total_cards_count (recursive),
due_cards_count (recursive),
new_cards_count,
mature_cards_count,
average_retention_rate,
last_computed_at
```

### 4.3 Indexes
- `folders`: (user_id, parent_folder_id), (path), (user_id, deleted_at)
- `decks`: (folder_id, user_id), (user_id, deleted_at)
- `cards`: (deck_id)
- `card_box_position`: (user_id, due_date), (user_id, current_box), (card_id, user_id)
- `review_logs`: (user_id, reviewed_at), (card_id)

---

## 5. API Endpoints

### 5.1 Authentication
- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/logout`
- `POST /api/auth/refresh-token`

### 5.2 Folders ⭐
- `GET /api/folders` - Lấy folder tree của user (có thể limit depth)
- `GET /api/folders/{id}` - Chi tiết folder
- `GET /api/folders/{id}/children` - Lấy direct children (folders + decks)
- `GET /api/folders/{id}/descendants` - Lấy tất cả descendants (recursive)
- `POST /api/folders` - Tạo folder mới (body: name, parent_folder_id)
- `PUT /api/folders/{id}` - Cập nhật folder (rename, change color/icon)
- `DELETE /api/folders/{id}` - Xóa folder (soft delete)
- `POST /api/folders/{id}/move` - Di chuyển folder (body: new_parent_folder_id)
- `POST /api/folders/{id}/copy` - Copy folder (body: destination_folder_id, copy_decks: boolean)
- `GET /api/folders/{id}/stats` - Lấy folder statistics (recursive)
- `GET /api/folders/{id}/breadcrumb` - Lấy breadcrumb path
- `POST /api/folders/{id}/share` - Share folder (body: user_ids, permission)

### 5.3 Decks
- `GET /api/decks?folder_id={folderId}` - Lấy decks trong folder
- `POST /api/decks` - Tạo deck (body: name, folder_id)
- `GET /api/decks/{id}`
- `PUT /api/decks/{id}`
- `DELETE /api/decks/{id}`
- `POST /api/decks/{id}/move` - Move deck (body: new_folder_id)
- `POST /api/decks/{id}/copy` - Copy deck (body: destination_folder_id)
- `POST /api/decks/{id}/import` - Import cards (CSV/JSON)
- `GET /api/decks/{id}/export` - Export deck

### 5.4 Cards
- `GET /api/decks/{deckId}/cards`
- `POST /api/decks/{deckId}/cards`
- `PUT /api/cards/{id}`
- `DELETE /api/cards/{id}`
- `POST /api/cards/bulk-create`

### 5.5 SRS Settings
- `GET /api/srs/settings`
- `PUT /api/srs/settings`
- `POST /api/srs/settings/reset`

### 5.6 Review ⭐
- `GET /api/review/due?mode=SPACED_REPETITION&scope=folder&scope_id={folderId}` - Lấy cards due
  - Query params: mode, scope (all/folder/deck/tag), scope_id
- `POST /api/review/submit` - Submit rating
- `POST /api/review/undo`
- `POST /api/review/skip/{cardId}`
- `POST /api/review/suspend/{cardId}`

### 5.7 Statistics
- `GET /api/stats/user` - User statistics
- `GET /api/stats/folder/{id}` - Folder statistics (recursive)
- `GET /api/stats/deck/{id}` - Deck statistics
- `GET /api/stats/heatmap` - Activity heatmap
- `GET /api/stats/box-distribution?scope=folder&scope_id={folderId}` - Cards per box

### 5.8 Community
- `GET /api/community/folders` - Browse public folders
- `GET /api/community/decks` - Browse public decks
- `POST /api/community/folders/{id}/clone` - Clone folder tree
- `POST /api/community/decks/{id}/clone` - Clone deck
- `POST /api/community/folders/{id}/rate` - Rate folder

---

## 6. Non-Functional Requirements

### 6.1 Performance
- Response time < 200ms cho CRUD operations
- Load folder tree < 300ms (với caching)
- Load review session < 500ms
- Hỗ trợ 10,000+ cards/deck, 1,000+ folders/user
- **Database optimization**:
  - Index trên path column cho ancestor queries
  - Materialized path cho quick tree traversal
  - Cache folder tree trong Redis (TTL: 5 minutes)
  - Denormalize folder_stats để tránh recursive queries

### 6.2 Security
- Password encryption: bcrypt (cost factor ≥ 12)
- HTTPS cho tất cả endpoints
- JWT authentication với refresh token
- Rate limiting: 100 requests/minute/user
- Input validation: sanitize folder/deck names, prevent path traversal attacks
- Authorization: user chỉ có thể access folders/decks của mình hoặc shared với mình

### 6.3 Scalability
- Hỗ trợ 100,000+ concurrent users
- Database sharding strategy (shard by user_id)
- CDN cho static assets
- Horizontal scaling cho backend services
- Async processing cho folder copy operations (dùng message queue)

### 6.4 Reliability
- Uptime ≥ 99.9%
- Daily database backup, retention 30 days
- Transaction management cho folder operations (copy/move phải atomic)
- Error logging và monitoring (Sentry/CloudWatch)
- Health check endpoints

### 6.5 Usability
- **Folder tree UX**:
  - Smooth expand/collapse animations
  - Drag & drop support cho move folders/decks
  - Lazy loading cho large folder trees
  - Virtual scrolling cho performance
- Responsive design: mobile, tablet, desktop
- Dark mode/Light mode
- Keyboard shortcuts: Ctrl+C/V cho copy/paste folders
- Multi-language support (EN, VI)
- WCAG 2.1 Level AA compliance

### 6.6 Testing
- Unit test coverage ≥ 80%
- **Critical test cases**:
  - Folder copy với deep nested structure
  - Folder move validation (không thể move folder vào chính nó hoặc children của nó)
  - Folder delete cascade behavior
  - Folder statistics calculation accuracy
- Integration tests cho folder operations
- E2E tests cho user journeys
- Load testing: 1000 concurrent folder operations

---

## 7. Development Phases

### Phase 1: MVP (2-3 months)
- User authentication
- **Folder tree structure**: CRUD folders, move/copy folders ⭐
- Deck & card CRUD
- Box-based SRS algorithm với 7 ô
- Review session với rating
- Basic statistics
- Web app với React TypeScript

### Phase 2: Enhanced Features (1-2 months)
- SRS settings: review order, forgotten card actions, notifications
- Multiple game modes (Cram, Test, Random)
- **Review scope options**: review by folder/deck/tag ⭐
- Advanced statistics với charts, folder statistics
- Mobile app (React Native)

### Phase 3: Social & Premium (1-2 months)
- Community folders & decks
- Share & collaborate với real-time sync
- Gamification (badges, leaderboard)
- Premium features
- Offline mode với sync

### Phase 4: Optimization (1 month)
- Performance tuning cho folder tree operations
- Advanced analytics
- AI features (auto-generate cards, smart folder suggestions)
- Accessibility improvements

---

## 8. Success Metrics

- **User Engagement**: DAU/MAU ratio ≥ 30%, retention sau 30 ngày ≥ 40%
- **Learning Effectiveness**: Average retention rate ≥ 85%
- **Performance**: P95 API response time < 300ms, folder tree load < 300ms
- **Usability**: Average folders per user ≥ 5, average folder depth ≥ 2 ⭐
- **Stability**: Crash-free rate ≥ 99.5%
- **Conversion**: Free-to-premium ≥ 5%

---

## 9. Key Differentiators ⭐

- **Advanced Folder Organization**: Tree-based structure với unlimited depth, copy/move folders recursively
- **Flexible Scope Review**: Review by folder (recursive), deck, hoặc tag
- **Folder Statistics**: Detailed analytics cho từng folder và sub-folders
- **Modern UX**: Intuitive tree view với drag & drop, smooth animations
- **Collaborative Folders**: Share và collaborate trên folder level
- **Vietnamese Support**: Full localization

---

## 10. Edge Cases & Validation Rules ⭐

### 10.1 Folder Operations
- **Circular reference prevention**: Không thể move folder A vào chính nó hoặc children của nó
- **Max depth limit**: Giới hạn depth = 10 levels để tránh performance issues (configurable)
- **Name uniqueness**: Folder name phải unique trong cùng parent folder
- **Delete validation**: Confirm khi delete folder có chứa decks/sub-folders
- **Copy limits**: Free users chỉ copy được folder depth ≤ 3, Premium unlimited

### 10.2 Performance Considerations
- **Lazy loading**: Chỉ load direct children, expand on demand
- **Pagination**: Limit số folders/decks hiển thị mỗi lần
- **Cache invalidation**: Clear cache khi có folder operations
- **Background jobs**: Copy large folders (>100 decks) chạy async

---

**Version**: 3.0  
**Last Updated**: October 2025  
**Focus**: Tree-based folder organization với copy/move support