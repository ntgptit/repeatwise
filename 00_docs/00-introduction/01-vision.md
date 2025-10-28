# Vision Document - RepeatWise

## Tầm nhìn sản phẩm

RepeatWise hướng đến trở thành một công cụ học tập cá nhân hiệu quả, giúp người dùng ghi nhớ kiến thức lâu dài thông qua phương pháp khoa học Spaced Repetition. Ứng dụng tập trung vào sự đơn giản, ổn định và khả năng mở rộng.

### Tuyên bố tầm nhìn

> "Xây dựng ứng dụng học tập cá nhân sử dụng Spaced Repetition đơn giản, ổn định, và dễ mở rộng; giúp người dùng học đều đặn, ghi nhớ bền vững với trải nghiệm tối giản và hiệu quả."

## Vấn đề cần giải quyết

### Thách thức hiện tại

1. **Quên nhanh kiến thức**
   - Con người quên 50-80% thông tin mới sau 24 giờ nếu không ôn tập (Ebbinghaus Forgetting Curve)
   - Không biết khi nào cần ôn lại để ghi nhớ tối ưu
   - Mất thời gian ôn những gì đã biết thay vì tập trung vào điểm yếu

2. **Thiếu tổ chức kiến thức**
   - Khó quản lý hàng nghìn flashcards trên nhiều chủ đề
   - Không có cấu trúc rõ ràng để phân loại kiến thức
   - Công cụ hiện có đôi khi phức tạp, cồng kềnh

3. **Lịch ôn tập không khoa học**
   - Ôn tập theo cảm tính, không dựa trên dữ liệu
   - Không có hệ thống nhắc nhở đúng lúc
   - Thiếu theo dõi tiến độ và hiệu quả học tập

4. **Khó khởi tạo kho thẻ lớn**
   - Tạo từng thẻ một rất tốn thời gian
   - Không có công cụ nhập hàng loạt hoặc khó sử dụng
   - Thiếu validation và báo cáo lỗi rõ ràng

### Giải pháp của RepeatWise

1. **Thuật toán SRS khoa học**
   - Box System 7 ô với intervals cố định: 1, 3, 7, 14, 30, 60, 120 ngày
   - Tự động tính toán thời điểm tối ưu để ôn tập
   - Chỉ hiển thị cards cần ôn (due cards), tránh lãng phí thời gian

2. **Tổ chức linh hoạt**
   - Cấu trúc folders phân cấp tối đa 10 levels
   - Dễ dàng di chuyển, sao chép folders/decks
   - Breadcrumb navigation trực quan

3. **Nhập/Xuất dễ dàng**
   - Hỗ trợ CSV và Excel (.xlsx)
   - Validation chi tiết với báo cáo lỗi theo dòng
   - Template download để khởi tạo nhanh
   - Async processing cho large datasets

4. **Trải nghiệm tối giản**
   - Giao diện sạch, tập trung vào việc học
   - Performance tốt: < 200ms API response, < 1s thao tác
   - Multi-platform: Web + Mobile (React Native)
   - Dark/Light mode, đa ngôn ngữ (VI/EN)

## Mục tiêu dự án

### Phase 1 - MVP (3-4 tháng) ✅ Current Phase

**Timeline**: Q1 2025

**Core Features**:

- ✅ User authentication với JWT + Refresh Token
- ✅ Folder/Deck management với cấu trúc cây (max 10 levels)
- ✅ Flashcard CRUD operations (basic text only)
- ✅ SRS 7-box system với fixed intervals
- ✅ Import/Export CSV/Excel với validation
- ✅ Multi-mode study: Standard SRS, Cram, Random
- ✅ Basic statistics: Streak, box distribution, today's stats
- ✅ Async operations: Folder/Deck copy, Import large files
- ✅ Notification settings với daily reminders

**Platforms**:

- Web: React + TypeScript + Tailwind CSS + Shadcn/ui
- Mobile: React Native + React Native Paper

**Key Deliverables**:

- Backend API (Spring Boot 3 + PostgreSQL)
- Web application (responsive design)
- Mobile apps (iOS + Android)
- Documentation (API docs, user guides)
- Test coverage ≥ 70%

### Phase 2 - Enhanced Features (Q2-Q3 2025)

**UI/UX Enhancements**:

- Drag & drop cho folders/decks
- Context menu (right-click)
- Color/icon customization cho folders
- List view/Grid view toggle
- Search trong folders
- Bulk operations (select multiple, batch actions)
- Keyboard shortcuts

**Rich Content Support**:

- Rich text editor (bold, italic, underline, lists)
- Image/audio support cho flashcards
- Code snippets với syntax highlighting
- Cloze deletion cards
- Multiple choice cards
- Type-in answer cards
- Tags cho cards/decks

**Analytics**:

- Heatmap activity (daily/monthly)
- Advanced charts (line, pie, forecast)
- Retention rate, lapse rate tracking
- Accuracy rate per deck/folder
- Time spent studying
- Detailed review history

### Phase 3 - Social & Community Features (Q4 2025)

**Sharing Features**:

- Share decks/folders (public/private)
- Community decks library
- Browse và import public decks
- Rating & reviews

**Collaboration**:

- Collaborative editing với real-time sync
- Team workspaces
- User permissions (viewer, editor, admin)
- Activity logs

**Import/Export**:

- Anki format support (.apkg)
- Quizlet import
- Export to various formats (PDF, JSON)

### Phase 4 - Premium Features (2026)

**Advanced Learning**:

- AI-generated flashcards từ text/PDF
- Speech recognition cho language learning
- Adaptive algorithm (FSRS, SM-2)
- Spaced repetition customization

**Premium Features**:

- Offline mode với automatic sync
- Advanced analytics với insights
- Unlimited decks/folders
- Priority support
- Remove ads (if any)

**Monetization**:

- Freemium model
- Premium subscription
- Team plans
- API access for developers

## Đối tượng sử dụng

### Primary Users (MVP Focus)

#### 1. Học sinh, sinh viên

**Đặc điểm**:

- Độ tuổi: 15-30
- Technical level: Basic to Intermediate
- Devices: Mobile (primary), Web (secondary)

**Nhu cầu**:

- Học từ vựng, công thức, khái niệm
- Giao diện đơn giản, dễ sử dụng
- Import nhanh từ Excel/CSV
- Ôn tập mọi lúc mọi nơi
- Theo dõi progress rõ ràng

**Use cases**:

- Chuẩn bị kỳ thi (IELTS, SAT, đại học)
- Học từ vựng tiếng Anh
- Ghi nhớ công thức toán, hóa, lý
- Review notes sau mỗi bài giảng

#### 2. Lập trình viên

**Đặc điểm**:

- Độ tuổi: 20-40
- Technical level: Advanced
- Devices: Web (primary), Mobile (secondary)

**Nhu cầu**:

- Học algorithms, design patterns, syntax
- Performance tốt với large datasets
- Keyboard shortcuts (future)
- API integration (future)
- Code snippets support (future)

**Use cases**:

- Chuẩn bị phỏng vấn (LeetCode, System Design)
- Ghi nhớ syntax của languages mới
- Review design patterns
- Continuous learning

#### 3. Người học ngoại ngữ

**Đặc điểm**:

- Độ tuổi: 18-45
- Technical level: Basic
- Devices: Mobile (primary)

**Nhu cầu**:

- Học từ vựng, ngữ pháp
- Audio support (future)
- Image flashcards (future)
- Spaced repetition hiệu quả

**Use cases**:

- Học từ vựng hàng ngày
- Ôn tập ngữ pháp
- Chuẩn bị test (TOEIC, JLPT, HSK)

### Secondary Users (Future)

#### 4. Giáo viên

**Vai trò**: Tạo và chia sẻ flashcard sets

**Nhu cầu**:

- Bulk creation tools
- Template library
- Share với students
- Track student progress

#### 5. Chuyên gia

**Vai trò**: Ghi nhớ kiến thức chuyên môn

**Nhu cầu**:

- Advanced formatting
- Rich media support
- Knowledge management

## Nguyên tắc thiết kế

### 1. Tối giản, rõ ràng

- Ưu tiên đường dẫn thao tác ngắn
- Thông điệp lỗi dễ hiểu
- UI không rối mắt, tập trung vào nội dung
- Mỗi màn hình có 1 mục đích chính

### 2. Ổn định trước, mở rộng sau

- Tính năng cốt lõi chắc chắn
- Test coverage cao (≥ 70%)
- Giới hạn an toàn (depth, file size, limits)
- Soft delete để tránh mất dữ liệu

### 3. Hiệu năng thiết thực

- Lazy loading cho folders
- Pagination cho lists
- Virtual scrolling cho > 100 items
- Database indexes hợp lý (composite index cho review queries)
- Tránh N+1 queries

### 4. Minh bạch

- Mọi thao tác nền có trạng thái rõ ràng
- Giới hạn được thông báo trước
- Progress tracking cho async operations
- Timeout protection

### 5. Accessible & Inclusive

- Responsive design (mobile, tablet, desktop)
- Dark/Light mode
- Multiple languages (VI/EN)
- Clear error messages

## Tiêu chí thành công

### User Engagement Metrics

**Daily Active Users (DAU)**:

- Target: > 70% of total users
- Measurement: Users who review at least 1 card per day

**Average Study Session**:

- Target: > 15 minutes/day
- Measurement: Time from first review to last review

**Cards Reviewed per Day**:

- Target: > 20 cards/user/day
- Measurement: Average review count

### Retention Metrics

**Week 1 Retention**:

- Target: > 80%
- Measurement: Users who return within 7 days after signup

**Month 1 Retention**:

- Target: > 50%
- Measurement: Users who are still active after 30 days

**Streak Maintenance**:

- Target: > 30% users maintain 7+ day streak
- Measurement: Percentage of users with streak ≥ 7

### Performance Metrics

**API Response Time**:

- Target: < 200ms (95th percentile)
- Critical endpoints: /review/due, /folders, /decks

**App Load Time**:

- Target: < 2s (initial load)
- Target: < 500ms (navigation between pages)

**Folder Tree Load**:

- Target: < 300ms
- Even with 1000+ folders

**Review Session Load**:

- Target: < 500ms
- With 100+ due cards

### User Satisfaction

**App Rating**:

- Target: > 4.5/5 on app stores
- Measurement: Average of iOS + Android ratings

**Feature Completion Rate**:

- Target: > 90%
- Measurement: % of users who complete onboarding

**Support Tickets**:

- Target: < 5% of users submit tickets
- Target: < 24h response time

**Net Promoter Score (NPS)**:

- Target: > 50 (Excellent)
- Measurement: Quarterly surveys

### Technical Metrics

**Test Coverage**:

- Target: ≥ 70% (core logic)
- Critical: SRS algorithm, folder operations, import/export

**Code Quality**:

- Target: < 5% code duplication
- Target: < 10 critical issues (SonarQube)

**Bug Density**:

- Target: < 1 critical bug per 1000 lines of code
- Target: Zero critical bugs at launch

**Documentation Coverage**:

- Target: 100% API endpoints documented
- Target: All public methods have JavaDoc

## Lợi ích

### Cho người dùng

**Hiệu quả học tập**:

- Ghi nhớ kiến thức hiệu quả hơn 300% so với học truyền thống
- Tiết kiệm thời gian với lịch ôn tập tối ưu
- Focus vào điểm yếu thay vì ôn lại những gì đã biết

**Quản lý kiến thức**:

- Tổ chức có hệ thống với folders phân cấp
- Dễ dàng tìm kiếm và quản lý hàng nghìn thẻ
- Import/Export linh hoạt

**Trải nghiệm**:

- Học mọi lúc mọi nơi với mobile app
- Giao diện thân thiện, dễ sử dụng
- Dark mode cho học ban đêm
- Notifications nhắc nhở đúng lúc

### Cho dự án

**Kỹ thuật**:

- Codebase sạch, dễ maintain
- Architecture mở rộng tốt (Layered, Design Patterns)
- Test coverage cao (≥ 70%)
- Documentation đầy đủ

**Kinh doanh**:

- MVP nhanh (3-4 tháng)
- Scalable infrastructure
- Clear roadmap cho future phases
- Freemium monetization potential

## Rủi ro và giảm thiểu

### Technical Risks

**1. Performance với large datasets**

- **Risk**: Slow queries với 10,000+ cards, 1,000+ folders
- **Mitigation**:
  - Pagination (50-100 items per page)
  - Composite indexes cho review queries
  - Materialized path cho folder tree
  - Denormalized statistics (folder_stats cache)
  - Lazy loading

**2. Data loss**

- **Risk**: User mất dữ liệu do bugs hoặc accidents
- **Mitigation**:
  - Daily automated backups
  - Soft delete (30 days restore window)
  - Confirm dialogs cho delete operations
  - Transaction management

**3. Async operations timeout**

- **Risk**: Copy/Import lớn bị timeout hoặc hang
- **Mitigation**:
  - Timeout protection (5-10 min max)
  - Progress tracking
  - Rollback on failure
  - Limits (50-500 items, 1000-10,000 cards)

### Business Risks

**1. Low user adoption**

- **Risk**: Users không sử dụng app sau signup
- **Mitigation**:
  - Onboarding tutorials
  - Sample decks để users thử ngay
  - Clear value proposition
  - Easy import từ Excel/CSV

**2. Competition**

- **Risk**: Anki, Quizlet, RemNote đã có user base lớn
- **Mitigation**:
  - Focus on simplicity & performance
  - Better UX than competitors
  - Smooth import process
  - Vietnamese market focus initially

**3. Retention issues**

- **Risk**: Users bỏ app sau vài ngày
- **Mitigation**:
  - Daily notifications
  - Streak tracking với gamification elements
  - Simple onboarding (< 5 minutes)
  - Show value immediately (import + review)

### Security Risks

**1. Authentication vulnerabilities**

- **Risk**: JWT theft, session hijacking
- **Mitigation**:
  - Refresh token rotation
  - HTTP-only cookies
  - Short access token lifetime (15 min)
  - HTTPS only

**2. Data breaches**

- **Risk**: User data exposure
- **Mitigation**:
  - Password hashing (bcrypt cost 12)
  - Input validation & sanitization
  - SQL injection prevention (JPA)
  - Rate limiting

**3. File upload abuse**

- **Risk**: Malicious files, oversized uploads
- **Mitigation**:
  - File size limits (50MB)
  - Format validation (CSV, XLSX only)
  - Virus scanning (future)
  - Rate limiting on uploads

## Roadmap

### Q1 2025 - MVP Launch ✅

- ✅ Backend API completion
- ✅ Web application launch
- ✅ Mobile apps (iOS + Android) launch
- ✅ Documentation
- ✅ Testing (unit, integration)

### Q2 2025 - Enhancement & Optimization

- UI/UX improvements (drag & drop, search)
- Rich text editor
- Image/audio support
- Performance optimization
- Analytics improvements
- Bug fixes based on user feedback

### Q3 2025 - Social Features

- Share decks/folders
- Community library
- Collaborative editing
- Anki import/export
- Advanced analytics

### Q4 2025 - Premium Features

- AI-generated cards
- Offline mode with sync
- Advanced customization
- Team features
- API access
- Monetization launch

### 2026 - Scaling & Growth

- International expansion
- Partnership with schools
- Enterprise features
- Advanced AI features
- Platform integrations

## Kết luận

RepeatWise MVP tập trung vào việc xây dựng nền tảng vững chắc với các tính năng cốt lõi:

- SRS algorithm khoa học và hiệu quả
- Tổ chức kiến thức linh hoạt
- Import/Export dễ dàng
- Trải nghiệm tối giản và nhanh

Với vision dài hạn hướng đến một platform học tập toàn diện, hỗ trợ cả cá nhân và nhóm, với AI và social features. Nhưng MVP giữ mọi thứ đơn giản để validate core value proposition và xây dựng user base.
