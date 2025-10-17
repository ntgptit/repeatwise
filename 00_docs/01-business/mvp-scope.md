# MVP Scope - RepeatWise

## 1. MVP Definition

### What is MVP?
MVP (Minimum Viable Product) là phiên bản đầu tiên của sản phẩm với **chỉ các tính năng cốt lõi** cần thiết để:
- Validate giả thuyết sản phẩm
- Thu thập feedback từ early adopters
- Học hỏi và cải thiện product-market fit

### MVP Goals
1. **Validate core value**: Thuật toán SRS + folder organization có giúp user học tốt hơn?
2. **Test usability**: UI/UX có đủ đơn giản để user sử dụng hàng ngày?
3. **Gather feedback**: Features nào user cần nhất? Features nào không cần thiết?
4. **Minimize development time**: Launch trong 3-4 tháng

## 2. MVP Features Checklist

### ✅ INCLUDED in MVP

#### 2.1 User Management (Basic)
- ✅ Email + password registration
- ✅ Login/logout with JWT (24h expiry)
- ✅ Profile management: name, timezone, language (VI/EN), theme (Light/Dark/System)
- ❌ OAuth (Google/Facebook) → Future
- ❌ Premium/Admin roles → Future
- ❌ Avatar upload → Future

#### 2.2 Folder & Deck Management ⭐
- ✅ Hierarchical folder structure (unlimited depth, max 10 levels)
- ✅ Folder CRUD: Create, rename, move, copy, delete (soft delete)
- ✅ Deck CRUD: Create, rename, move, copy, delete
- ✅ Async copy operations: Folder (>50 items), Deck (>1000 cards)
- ✅ Breadcrumb navigation
- ✅ Tree view sidebar
- ✅ Folder statistics (recursive): total cards, due cards
- ❌ Drag & drop UI → Future
- ❌ Color/icon customization → Future
- ❌ Search trong folders → Future
- ❌ Bulk operations → Future

#### 2.3 Flashcard Management (Simple)
- ✅ Basic card type: Front/back text only (plain text)
- ✅ CRUD flashcards
- ✅ Import/Export CSV/Excel với validation
- ✅ Template file download
- ❌ Rich text (bold, italic) → Future
- ❌ Images, audio → Future
- ❌ Code snippet với syntax highlighting → Future
- ❌ Cloze deletion → Future
- ❌ Multiple choice → Future
- ❌ Tags → Future

#### 2.4 Spaced Repetition System ⭐
- ✅ 7-box system với fixed intervals
- ✅ Review order settings: Ascending, Descending, Random
- ✅ Forgotten card actions: Move to Box 1, Move down N boxes, Stay in box
- ✅ Daily limits: New cards, max reviews per day
- ✅ Review session với 4 ratings: Again, Hard, Good, Easy
- ✅ Review actions: Undo, Skip, Edit card
- ❌ Suspend card → Future
- ❌ Custom intervals → Future

#### 2.5 Study Modes
- ✅ Standard SRS: Review theo schedule (all/folder/deck scope)
- ✅ Cram Mode: Học nhanh, không ảnh hưởng schedule
- ✅ Random Mode: Review ngẫu nhiên
- ❌ Test Mode → Future
- ❌ Custom Study filters → Future

#### 2.6 Statistics & Analytics (Basic)
- ✅ Streak counter
- ✅ Box distribution chart (simple bar chart)
- ✅ Today's stats: cards reviewed, new cards learned
- ✅ Folder/Deck stats: total cards, due cards
- ❌ Heatmap activity → Future
- ❌ Accuracy rate → Future
- ❌ Time spent studying → Future
- ❌ Advanced charts (line, pie, forecast) → Future

#### 2.7 Notifications
- ✅ Push notifications (mobile/web)
- ✅ Toggle ON/OFF
- ✅ Daily reminder với custom time
- ✅ In-app notification badge
- ❌ Email notification → Future
- ❌ Streak reminder → Future
- ❌ Weekly summary → Future

### ❌ EXCLUDED from MVP (Future Phases)

#### Phase 4: UI/UX Enhancements
- Drag & drop folders/decks
- Context menu (right-click)
- Color/icon customization
- List/Grid view toggle
- Search trong folders
- Bulk operations (select multiple, batch delete/move)
- Keyboard shortcuts

#### Phase 5: Rich Content
- Rich text editor (bold, italic, underline, highlight)
- Images, audio attachments
- Code snippets với syntax highlighting
- Cloze deletion cards
- Multiple choice cards
- Type-in answer cards
- Tags cho cards/decks
- Notes, source reference

#### Phase 6: Analytics & Gamification
- Heatmap activity calendar
- Advanced charts (line, pie, forecast)
- Retention rate, lapse rate tracking
- Time spent studying
- Accuracy percentage
- Badges & achievements
- Leaderboard
- XP points & levels

#### Phase 7: Social Features
- Share decks/folders (public/private)
- Community decks marketplace
- Collaborative editing với real-time sync
- Import Anki format
- Rating & review decks
- Comments & discussions

#### Phase 8: Premium Features
- Offline mode với sync
- AI-generated flashcards
- Advanced custom SRS algorithms
- Suspend card feature
- Test mode với scoring
- Study timer & focus mode
- Advanced statistics & insights

## 3. Feature Prioritization Framework

### Must-Have (P0) - Core functionality
Không thể thiếu, ứng dụng không hoạt động nếu không có:
- Authentication & user management
- Folder/Deck/Card CRUD
- SRS algorithm (7-box system)
- Review session với ratings
- Import/Export CSV/Excel

### Should-Have (P1) - Important but not critical
Quan trọng cho UX nhưng có thể workaround:
- Folder tree navigation
- Breadcrumb
- Study modes (Cram, Random)
- Notifications
- Statistics dashboard
- Theme toggle

### Nice-to-Have (P2) - Enhances UX
Tốt nhưng không bắt buộc cho MVP:
- Undo review
- Skip card
- Edit card trong review session
- Async copy progress tracking
- Folder statistics (denormalized)

### Won't-Have (P3) - Explicitly excluded
Không làm trong MVP:
- All Phase 4-8 features (listed above)
- Drag & drop, rich text, images, social features, gamification

## 4. MVP Constraints & Limits

### Technical Limits
- **Folder depth**: Max 10 levels
- **Folder copy**: Max 500 items (folders + decks)
- **Deck copy**: Max 10,000 cards
- **Import file**: Max 10,000 rows, 50MB
- **Review query**: Max 200 cards per request
- **Daily limits**: Default 20 new cards, 200 reviews (configurable)

### User Limits
- **Target users**: < 100 users for MVP
- **Concurrent users**: < 50 simultaneous
- **Data per user**: ~10,000 cards, ~1,000 folders/decks

### Platform Constraints
- **Web**: Desktop + mobile responsive (Chrome, Safari, Edge)
- **Mobile**: iOS 13+, Android 8+ (React Native)
- **Online only**: No offline mode
- **Single language**: Vietnamese (primary), English (secondary)

## 5. Acceptance Criteria

### Functional Criteria
- [ ] User có thể đăng ký/đăng nhập thành công
- [ ] User có thể tạo folder tree với max 10 levels
- [ ] User có thể tạo deck và cards trong folder
- [ ] User có thể import CSV với 1000 rows thành công
- [ ] User có thể export deck ra CSV/Excel
- [ ] User có thể review cards theo SRS schedule
- [ ] User có thể chọn review order (Ascending/Descending/Random)
- [ ] User có thể cram mode toàn bộ folder
- [ ] User có thể xem statistics: streak, box distribution
- [ ] User có thể toggle theme (Light/Dark/System)
- [ ] User nhận được notification khi có due cards

### Non-Functional Criteria
- [ ] API response time < 500ms (p95)
- [ ] Folder tree load < 300ms (p95)
- [ ] Review session load < 500ms (p95)
- [ ] Import 1000 rows < 10 seconds
- [ ] App crash rate < 1%
- [ ] UI responsive trên mobile, tablet, desktop
- [ ] Dark mode hoạt động trơn tru
- [ ] Unit test coverage ≥ 70%

## 6. MVP Release Criteria

### Launch Readiness
- [ ] All P0 features implemented và tested
- [ ] All P1 features implemented (có thể skip 1-2 items)
- [ ] Critical bugs fixed (severity: high/critical)
- [ ] Unit tests pass (coverage ≥ 70%)
- [ ] Integration tests pass
- [ ] Database migrations tested
- [ ] Production deployment tested (staging)
- [ ] Backup & restore process documented

### Documentation
- [ ] User guide (Vietnamese)
- [ ] API documentation
- [ ] Developer setup guide
- [ ] Database schema documentation
- [ ] Deployment guide

### Quality Assurance
- [ ] Manual testing checklist completed
- [ ] Cross-browser testing (Chrome, Safari, Edge)
- [ ] Mobile testing (iOS + Android)
- [ ] Edge cases tested (circular folder reference, max depth, etc.)
- [ ] Performance testing (1000 folders, 10,000 cards)
- [ ] Security testing (auth, input validation, rate limiting)

## 7. Post-MVP Iteration Plan

### Week 1-2 (Beta Release)
- Launch to 10-20 beta users
- Monitor app stability, crash reports
- Collect initial feedback

### Week 3-4 (Feedback Analysis)
- Analyze user behavior (most used features, pain points)
- Identify top 3 feature requests
- Identify top 5 bugs/issues

### Week 5-6 (Iteration 1)
- Fix critical bugs
- Implement 1-2 quick wins (high impact, low effort)
- Improve onboarding flow

### Week 7-8 (Public Launch)
- Launch to public (50-100 users)
- Monitor metrics: DAU, retention, session time
- Plan for Phase 4 features

## 8. Success Metrics for MVP

### User Acquisition
- 50-100 registered users trong 2 tháng đầu
- 20% conversion rate (visitor → registered user)

### User Engagement
- 30% DAU/MAU ratio
- Average 3 sessions per week per user
- Average 20 cards reviewed per session

### User Retention
- 50% 7-day retention
- 30% 30-day retention
- 20% 90-day retention

### Product Quality
- App store rating ≥ 4.0/5.0
- < 5 critical bugs per week
- Average API response time < 300ms

### Feature Usage
- 80% users create at least 1 folder
- 60% users import CSV/Excel at least once
- 50% users enable notifications
- 40% users maintain 7+ days streak

## 9. Decision Log

### Why exclude these features from MVP?

**OAuth (Google/Facebook)**
- Reason: Email/password đủ cho MVP, OAuth adds complexity
- Trade-off: Slower signup flow but simpler codebase

**Rich Text Editor**
- Reason: Plain text đủ cho majority use cases, rich text adds UI complexity
- Trade-off: Less flexible but faster development

**Drag & Drop**
- Reason: Move/Copy APIs đã có, drag & drop chỉ là UI sugar
- Trade-off: Slightly less intuitive but functional

**Offline Mode**
- Reason: Requires complex sync logic, data conflicts resolution
- Trade-off: Users cần internet nhưng avoid sync bugs

**Social Features**
- Reason: Single user focus cho MVP, social requires moderation, permission system
- Trade-off: Less viral growth but simpler product

**Gamification**
- Reason: Streak counter đủ cho motivation, badges/leaderboard có thể distract
- Trade-off: Less engaging but more focused

## 10. Conclusion

MVP scope tập trung vào **core learning workflow**: tạo folders/decks, import cards, review với SRS, xem statistics. Tất cả features khác (rich content, social, gamification) postpone đến các phase sau để:
1. **Faster time-to-market**: 3-4 tháng thay vì 6-12 tháng
2. **Validate core value**: SRS + folder organization có giải quyết được pain point?
3. **Learn from users**: Features nào user thực sự cần?
4. **Reduce complexity**: Codebase đơn giản, dễ maintain

**Next Steps**: Implement theo roadmap 12-15 weeks, launch beta, gather feedback, iterate.
