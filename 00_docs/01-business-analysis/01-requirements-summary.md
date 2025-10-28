# Requirements Summary - RepeatWise MVP

## Tổng quan yêu cầu

Tài liệu này tóm tắt các yêu cầu chức năng và phi chức năng cho RepeatWise MVP.

## Functional Requirements

### FR-1: User Management

**FR-1.1: User Registration**
- User có thể đăng ký tài khoản với email và password
- Validation: email unique, password >= 8 ký tự
- Không auto-login sau đăng ký

**FR-1.2: User Authentication**
- Login với email + password
- JWT access token (15 phút) + refresh token (7 ngày)
- Refresh token stored in HTTP-only cookie
- Token rotation on refresh

**FR-1.3: User Profile**
- Update profile: name, timezone, language (VI/EN), theme (Light/Dark/System)
- Change password
- Logout (single device)
- Logout all devices

### FR-2: Folder Management

**FR-2.1: Folder CRUD**
- Create folder at any level (max depth = 10)
- Rename folder
- Delete folder (soft delete)
- View folder details

**FR-2.2: Folder Hierarchy**
- Unlimited nesting (max 10 levels)
- Parent-child relationship
- Materialized path for quick queries
- Breadcrumb navigation

**FR-2.3: Folder Operations**
- Move folder to new location
  - Validation: depth after move <= 10
  - Cannot move into itself or descendants
- Copy folder (deep copy)
  - Sync: <= 50 items (folders + decks)
  - Async: 51-500 items with progress tracking
  - Hard limit: 500 items max

**FR-2.4: Folder Statistics**
- Total decks count (recursive)
- Total cards count (recursive)
- Due cards count (recursive)
- New cards count
- Last modified date

### FR-3: Deck Management

**FR-3.1: Deck CRUD**
- Create deck (in folder or root)
- View deck details
- Update deck (name, description)
- Delete deck (soft delete)

**FR-3.2: Deck Operations**
- Move deck between folders
- Copy deck
  - Sync: <= 1000 cards
  - Async: 1001-10,000 cards
  - Hard limit: 10,000 cards max

**FR-3.3: Deck Metadata**
- Name, description
- Parent folder
- Card count, due cards count
- Last studied date
- Created/updated timestamps

### FR-4: Card Management

**FR-4.1: Card CRUD**
- Create card with front/back text
- View card details
- Update card content
- Delete card (soft delete)

**FR-4.2: Card Validation**
- Front and back cannot be empty
- Max 5000 characters each
- Plain text only (MVP)

**FR-4.3: Import/Export**
- Import from CSV/Excel
  - Max 10,000 rows per file
  - Format: Front, Back
  - Validation with preview
  - Progress bar for large files
- Export to CSV/Excel
  - Format: Front, Back, Created Date, Review Count, Current Box
  - Options: All cards or Due cards only
- Download template file

### FR-5: Spaced Repetition System

**FR-5.1: Box System**
- 7 boxes with fixed intervals:
  - Box 1: 1 day
  - Box 2: 3 days
  - Box 3: 7 days
  - Box 4: 14 days
  - Box 5: 30 days
  - Box 6: 60 days
  - Box 7: 120 days

**FR-5.2: Review Session**
- Show front side
- User thinks and flips card
- Rate card: Again (<1min), Hard (<6min), Good (next interval), Easy (4x interval)
- Actions: Undo last review, Skip card, Edit card

**FR-5.3: Review Order**
- User selectable: Ascending, Descending, Random
- Default: Random

**FR-5.4: Forgotten Card Actions**
- Move to Box 1 (default)
- Move down N boxes (configurable: 1-3)
- Stay in current box

**FR-5.5: Daily Limits**
- New cards per day (default: 20, configurable)
- Max reviews per day (default: 200, configurable)
- Auto-pause with override option

**FR-5.6: Notifications**
- Toggle ON/OFF
- Daily reminder at configured time
- Push notification (mobile)
- In-app notification badge

### FR-6: Study Modes

**FR-6.1: Standard SRS Mode**
- Review cards by SRS schedule
- Scope: All decks, Specific folder (recursive), Specific deck

**FR-6.2: Cram Mode**
- Quick study without affecting SRS
- Scope: Entire deck, Entire folder (recursive)

**FR-6.3: Random Mode**
- Review random cards
- Select number of cards
- No SRS impact

### FR-7: Statistics

**FR-7.1: User Statistics**
- Streak counter (consecutive days)
- Cards reviewed today
- New cards learned today
- Total cards learned
- Total study time

**FR-7.2: Card Distribution**
- Cards per box (bar chart)
- Distribution by folder/deck

**FR-7.3: Deck Statistics**
- Total cards
- Due cards
- New cards
- Last studied date

## Non-Functional Requirements

### NFR-1: Performance

**NFR-1.1: Response Time**
- CRUD operations: < 200ms
- Load folder tree: < 300ms
- Load review session: < 500ms

**NFR-1.2: Scalability**
- Support 10,000+ cards per deck
- Support 1,000+ folders per user
- Support 100+ concurrent requests

**NFR-1.3: Database Optimization**
- Proper indexes on frequently queried columns
- Composite index for review queries: (user_id, due_date, current_box)
- Materialized path for folder tree
- Denormalized folder_stats for performance

### NFR-2: Security

**NFR-2.1: Authentication**
- Password hashing: bcrypt (cost factor 12)
- JWT with refresh token
- Access token: 15 minutes
- Refresh token: 7 days, HTTP-only cookie, one-time use

**NFR-2.2: Data Protection**
- HTTPS for production
- Input validation and sanitization
- SQL injection prevention (JPA)
- XSS prevention
- CORS configuration

**NFR-2.3: Authorization**
- Users can only access their own data
- Role-based access control (future)

**NFR-2.4: Rate Limiting**
- 100 requests/minute per user (in-memory)
- Redis-based for production (future)

### NFR-3: Reliability

**NFR-3.1: Data Integrity**
- Foreign key constraints
- Soft delete for recovery
- Daily database backups

**NFR-3.2: Error Handling**
- Graceful error handling
- User-friendly error messages
- Error logging (console for MVP, file for production)

**NFR-3.3: Availability**
- Target uptime: 99% (MVP)
- Target uptime: 99.9% (Production)

### NFR-4: Usability

**NFR-4.1: User Interface**
- Responsive design (mobile, tablet, desktop)
- Intuitive navigation
- Consistent UI patterns

**NFR-4.2: Internationalization**
- Support Vietnamese and English
- User can switch language in settings
- i18n framework: react-i18next (Web), i18n-js (Mobile)

**NFR-4.3: Accessibility**
- Proper contrast ratios
- Keyboard navigation (future)
- Screen reader support (future)

**NFR-4.4: Theme Support**
- Light mode and Dark mode
- System preference detection
- Smooth theme transition
- Persistent user preference

### NFR-5: Maintainability

**NFR-5.1: Code Quality**
- Unit test coverage >= 70%
- Clean code principles (SOLID, DRY, KISS)
- Code review mandatory

**NFR-5.2: Documentation**
- API documentation (OpenAPI)
- Architecture documentation
- Code comments for complex logic
- README for setup

**NFR-5.3: Version Control**
- Git branching strategy
- Conventional commits
- Pull request process

### NFR-6: Compatibility

**NFR-6.1: Browser Support**
- Chrome (latest 2 versions)
- Firefox (latest 2 versions)
- Safari (latest 2 versions)
- Edge (latest 2 versions)

**NFR-6.2: Mobile Support**
- iOS 13+
- Android 8.0+

**NFR-6.3: Database**
- PostgreSQL 15+

## Constraints

### Technical Constraints
- Java 17 for backend
- Spring Boot 3 framework
- PostgreSQL database only (no MySQL, MongoDB)
- React TypeScript for web
- React Native for mobile

### Business Constraints
- MVP timeline: 3-4 months
- Single developer team initially
- No budget for premium tools (MVP)
- Focus on personal use (no team features)

### Scope Constraints
- No rich text editor (plain text only)
- No images/audio support
- No social features
- No offline mode (online only)
- No OAuth providers (email only)

## Assumptions

- Users have stable internet connection
- Users use modern browsers (last 2 versions)
- Users have basic technical literacy
- Average user has < 10,000 cards total
- Average folder tree depth < 5 levels
- Peak concurrent users < 100 (MVP)

## Dependencies

- PostgreSQL database availability
- Java 17 runtime environment
- Node.js for frontend build
- React Native development environment
- Git for version control

## Success Criteria

### MVP Launch
- All core features functional
- Zero critical bugs
- Response time < 500ms
- Mobile app available on TestFlight/Play Store beta

### Post-Launch (1 month)
- User retention > 50%
- Average rating > 4.0/5
- Daily active users > 100
- Zero data loss incidents

### Long-term (6 months)
- User retention > 40%
- Average rating > 4.5/5
- Daily active users > 1000
- Feature requests prioritized

## Out of Scope (Future)

- Rich text editor
- Image/audio flashcards
- Cloze deletion
- Multiple choice
- Type-in answer
- Tags and categories
- Shared/Public folders
- Community decks
- Drag & drop UI
- Advanced analytics
- Gamification
- Offline mode
- OAuth login
- API for third parties
