# Product Overview - RepeatWise MVP

## 1. Executive Summary

RepeatWise là ứng dụng học tập cá nhân sử dụng thuật toán Spaced Repetition System (SRS) dạng Box System. Ứng dụng giúp người dùng tạo và quản lý flashcards, tổ chức theo cấu trúc folders phân cấp, và hệ thống tự động lên lịch ôn tập dựa trên khoa học nhận thức.

**Phiên bản**: MVP - Sử dụng cá nhân (Personal Use)
**Mục tiêu**: Các chức năng cốt lõi, dễ maintain và mở rộng sau này

## 2. Vision & Mission

### Vision
Trở thành công cụ học tập thông minh, đơn giản và hiệu quả nhất cho người học tự do, giúp tối ưu hóa việc ghi nhớ thông tin dài hạn.

### Mission
- Cung cấp công cụ flashcard đơn giản, dễ sử dụng
- Áp dụng thuật toán khoa học (SRS) để tối ưu hóa việc học
- Hỗ trợ tổ chức kiến thức theo cấu trúc linh hoạt
- Giúp người dùng xây dựng thói quen học tập bền vững

## 3. Target Users

### Primary Users
- **Học sinh, sinh viên**: Học ngoại ngữ, ôn thi, ghi nhớ kiến thức
- **Người đi làm**: Học kỹ năng mới, chứng chỉ nghề nghiệp
- **Self-learners**: Học lập trình, design patterns, kiến thức chuyên môn

### User Characteristics
- Tự học, có động lực cao
- Cần công cụ đơn giản, không phức tạp
- Muốn theo dõi tiến độ học tập
- Sử dụng trên cả web và mobile

## 4. Core Value Propositions

### 4.1 Simple & Focused
- Interface đơn giản, không rườm rà
- Tập trung vào chức năng học tập cốt lõi
- Không có quá nhiều tính năng phức tạp (gamification, social)

### 4.2 Scientific Approach
- Thuật toán SRS dựa trên Box System (Leitner System)
- 7 ô cố định với intervals tối ưu
- Tùy chỉnh chiến lược review (ascending, descending, random)

### 4.3 Flexible Organization
- Cấu trúc folders phân cấp không giới hạn (max 10 levels)
- Tự do tổ chức kiến thức theo domain
- Hỗ trợ copy/move folders và decks

### 4.4 Import/Export Capabilities
- Import hàng loạt từ CSV/Excel
- Export để backup hoặc chia sẻ
- Template file để dễ dàng chuẩn bị dữ liệu

### 4.5 Multi-Platform
- Web app: Responsive design, đầy đủ tính năng
- Mobile app: React Native, sync realtime
- Consistent UX trên cả hai platform

## 5. Key Features (MVP Scope)

### 5.1 User Management
- Đăng ký/Đăng nhập với email + password
- Profile: tên, timezone, ngôn ngữ (VI/EN), theme (Light/Dark/System)
- ❌ OAuth, Premium, Avatar upload → Future

### 5.2 Folder & Deck Management
- **Hierarchical folders**: Unlimited depth (max 10 levels)
- **Folder CRUD**: Create, rename, move, copy, delete (soft delete)
- **Deck CRUD**: Create, move, copy, delete
- **Async operations**: Copy folder/deck với progress tracking
- **Breadcrumb navigation**: Dễ dàng navigate trong folder tree

### 5.3 Flashcard Management
- **Basic card type**: Front/back text only (plain text)
- **CRUD operations**: Tạo, sửa, xóa cards
- **Bulk import/export**: CSV/Excel với validation
- ❌ Rich text, images, audio, cloze deletion → Future

### 5.4 Spaced Repetition System (SRS)
- **7-box system**: Fixed intervals (1, 3, 7, 14, 30, 60, 120 days)
- **Review order**: Ascending, Descending, Random
- **Forgotten card actions**: Move to Box 1, Move down N boxes, Stay in box
- **Daily limits**: New cards, review cards per day
- **Rating options**: Again, Hard, Good, Easy

### 5.5 Study Modes
- **Standard SRS**: Review theo schedule, scope: all/folder/deck
- **Cram Mode**: Học nhanh tất cả cards, không ảnh hưởng schedule
- **Random Mode**: Review ngẫu nhiên số lượng cards nhất định

### 5.6 Statistics & Analytics
- **Streak counter**: Số ngày học liên tục
- **Box distribution**: Số cards trong từng ô
- **Today's stats**: Cards reviewed, new cards learned
- **Folder/Deck stats**: Total cards, due cards

### 5.7 Notifications
- **Push notifications**: Khi có cards due
- **Daily reminder**: Thời gian do user set
- **Toggle ON/OFF**: Bật/tắt thông báo

## 6. MVP Exclusions (Future Features)

### Phase 4: UI/UX Enhancements
- Drag & drop, context menu, color/icon customization
- Search trong folders, bulk operations

### Phase 5: Rich Content
- Rich text editor, images, audio, code snippets
- Cloze deletion, multiple choice, tags

### Phase 6: Analytics & Gamification
- Heatmap, advanced charts, retention rate
- Badges, achievements, leaderboard

### Phase 7: Social Features
- Share decks/folders, community decks
- Collaborative editing, rating & review

### Phase 8: Premium Features
- Offline mode, AI-generated cards
- Advanced analytics, test mode

## 7. Success Metrics (MVP)

### User Engagement
- Daily active users (DAU)
- Average session time
- Cards reviewed per session
- Streak retention rate (% users with 7+ days streak)

### Product Usage
- Total cards created
- Average cards per deck
- Average folders per user
- Import/export usage rate

### Quality Metrics
- App crash rate < 1%
- API response time < 500ms
- User-reported bugs per week

### User Satisfaction
- App store rating > 4.0/5.0
- Feature request volume
- User retention rate (30 days)

## 8. Technical Constraints

### MVP Constraints
- **Single user focus**: Không có multi-user collaboration
- **Online only**: Không có offline mode
- **Basic auth**: JWT 24h, no refresh token
- **Simple UI**: Plain text cards only
- **Performance**: Đủ cho < 100 users, 10,000 cards/deck

### Scalability Considerations
- Database: PostgreSQL với proper indexes
- Async operations: ThreadPool cho copy operations
- Caching: Denormalized folder_stats
- Pagination: Limit queries, lazy loading

## 9. Risk Assessment

### Technical Risks
- **Folder tree performance**: Recursive queries với large datasets
  - Mitigation: Materialized path, indexed queries, denormalized stats
- **Async copy timeout**: Large folders/decks timeout
  - Mitigation: Max 500 items (folder), 10,000 cards (deck), progress tracking
- **Import validation**: Large CSV/Excel files
  - Mitigation: Stream processing, batch insert, timeout protection

### Business Risks
- **User adoption**: Competitive market với nhiều flashcard apps
  - Mitigation: Focus on simplicity, folder organization, import/export
- **Retention**: Users không xây dựng được habit
  - Mitigation: Notifications, streak tracking, simple onboarding

### Security Risks
- **JWT expiry**: 24h token quá dài cho production
  - Mitigation: Upgrade to refresh token in production
- **Rate limiting**: Simple in-memory rate limit
  - Mitigation: Redis-based rate limiting in production

## 10. Development Timeline

### Phase 1: Backend Foundation (2 weeks)
- Database setup, authentication
- Folder/Deck/Card APIs
- Import/Export service

### Phase 2: SRS Algorithm (2 weeks)
- Box-based SRS implementation
- Review session logic, strategies
- Due cards calculation

### Phase 3: Folder Stats & Study Modes (2 weeks)
- Folder statistics (recursive)
- Standard SRS, Cram, Random modes

### Phase 4: Frontend Web (2 weeks)
- React + Tailwind + Shadcn
- Folder tree, breadcrumb, review UI
- Import/Export UI, theme toggle

### Phase 5: Mobile App (4-5 weeks)
- React Native + React Native Paper
- Navigation, screens, components
- Push notifications

### Phase 6: Polish & Deploy (1-2 weeks)
- Bug fixes, edge cases
- Production deployment, backup

**Total**: ~12-15 weeks (3-4 tháng)

## 11. Post-MVP Roadmap

### Q2: UI/UX Enhancements
- Drag & drop, search, bulk operations
- Color/icon customization

### Q3: Rich Content & Analytics
- Rich text, images, audio
- Advanced charts, heatmap

### Q4: Social & Premium Features
- Share decks, community decks
- Offline mode, AI-generated cards

## 12. Conclusion

RepeatWise MVP tập trung vào các chức năng cốt lõi của flashcard learning app với SRS algorithm, folder organization, và import/export capabilities. Thiết kế đơn giản, dễ maintain, và có khả năng mở rộng cho các phase sau.

**Target Launch**: Q2 2025
**Initial Users**: 50-100 beta users
**Success Criteria**: 4.0+ rating, 50% 30-day retention
