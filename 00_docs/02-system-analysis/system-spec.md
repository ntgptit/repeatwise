# System Specification - RepeatWise MVP

## 1. System Overview

RepeatWise là ứng dụng flashcard learning với Spaced Repetition System (SRS) dựa trên Box-Based Algorithm (Leitner System). Hệ thống cho phép người dùng tạo và quản lý flashcards theo cấu trúc folders phân cấp, import/export CSV/Excel, và tự động lên lịch ôn tập dựa trên khoa học nhận thức.

**Version**: MVP (Minimum Viable Product)
**Target Users**: Personal use, < 100 users initially
**Platform**: Web (React) + Mobile (React Native)

## 2. System Scope

### 2.1 In Scope (MVP Features)

#### User Management
- ✅ Email + password registration and login
- ✅ JWT authentication with refresh token (MVP) ⭐
  - Access token: 15 minutes (secure, short-lived)
  - Refresh token: 7 days (HTTP-only cookie)
  - Token rotation on refresh
- ✅ Profile management: name, timezone, language (VI/EN), theme (Light/Dark/System)
- ❌ OAuth (Google/Facebook) → Future
- ❌ Avatar upload → Future

#### Folder & Deck Management ⭐
- ✅ Hierarchical folder structure (unlimited depth, max 10 levels)
- ✅ Folder CRUD: Create, rename, move, copy, delete (soft delete)
- ✅ Deck CRUD: Create, rename, move, copy, delete
- ✅ Async copy operations with progress tracking:
  - Folder: > 50 items (folders + decks)
  - Deck: > 1000 cards
- ✅ Breadcrumb navigation
- ✅ Tree view sidebar with expand/collapse
- ✅ Folder statistics (recursive): total cards, due cards, new cards

#### Flashcard Management
- ✅ Basic card type: Front/back plain text only
- ✅ CRUD operations for cards
- ✅ Bulk import/export: CSV and Excel (.xlsx) with validation
- ✅ Template file download for import
- ✅ Import validation: empty rows, missing fields, duplicates
- ✅ Export options: all cards or due cards only

#### Spaced Repetition System (SRS) ⭐
- ✅ **7-box system** with fixed intervals:
  - Box 1: 1 day
  - Box 2: 3 days
  - Box 3: 7 days
  - Box 4: 14 days
  - Box 5: 30 days
  - Box 6: 60 days
  - Box 7: 120 days
- ✅ **Review order settings**: Ascending, Descending, Random
- ✅ **Forgotten card actions**:
  - Move to Box 1 (default)
  - Move down N boxes (configurable: 1, 2, 3)
  - Stay in box
- ✅ **Daily limits**: New cards (default 20), max reviews (default 200)
- ✅ **Rating options**: Again (< 1 min), Hard (< 6 min), Good (next interval), Easy (4x interval)
- ✅ **Review actions**: Undo, Skip, Edit card in session

#### Study Modes
- ✅ **Standard SRS**: Review by schedule (all/folder/deck scope)
- ✅ **Cram Mode**: Quick review without affecting schedule
- ✅ **Random Mode**: Review random cards (user sets count)

#### Statistics & Analytics
- ✅ Streak counter (consecutive study days)
- ✅ Box distribution chart (cards per box)
- ✅ Today's stats: cards reviewed, new cards learned
- ✅ Folder/Deck stats: total cards, due cards, new cards

#### Notifications
- ✅ Push notifications (mobile/web)
- ✅ Daily reminder at custom time
- ✅ Toggle ON/OFF for notifications
- ✅ In-app notification badge

### 2.2 Out of Scope (Future Phases)

#### Phase 4: UI/UX Enhancements
- Drag & drop for folders/decks
- Context menu (right-click)
- Color/icon customization
- Search within folders
- Bulk operations

#### Phase 5: Rich Content
- Rich text editor (bold, italic, highlight)
- Images, audio, video attachments
- Code snippets with syntax highlighting
- Cloze deletion cards
- Multiple choice cards
- Tags for cards/decks

#### Phase 6: Analytics & Gamification
- Heatmap activity calendar
- Advanced charts (line, pie, forecast)
- Retention rate, accuracy tracking
- Badges & achievements
- Leaderboard

#### Phase 7: Social Features
- Share decks/folders (public/private)
- Community decks marketplace
- Collaborative editing
- Comments & discussions

#### Phase 8: Premium Features
- Offline mode with sync
- AI-generated flashcards
- Advanced SRS customization
- Test mode with scoring

## 3. System Actors

### 3.1 Primary Actors

#### Student (Learner)
**Description**: Người dùng chính, học tập sử dụng flashcards

**Capabilities**:
- Create và quản lý folder hierarchy
- Create và quản lý decks và cards
- Import cards từ CSV/Excel
- Export cards để backup
- Review cards theo SRS schedule
- Use study modes (SRS, Cram, Random)
- View statistics và progress
- Configure SRS settings

**Preconditions**:
- Registered account
- Logged in
- Has internet connection (online-only MVP)

### 3.2 Secondary Actors

#### System Scheduler
**Description**: Background service tự động tính toán due dates

**Responsibilities**:
- Calculate due dates based on box position and intervals
- Apply review order settings (ascending/descending/random)
- Update card box positions after review
- Trigger notifications for due cards

#### Notification Service
**Description**: Service gửi notifications

**Responsibilities**:
- Send push notifications (mobile/web)
- Send daily reminders at user's preferred time
- Deliver in-app notifications
- Track delivery status

#### Import/Export Service
**Description**: Service xử lý file operations

**Responsibilities**:
- Parse CSV/Excel files
- Validate import data
- Batch insert cards
- Generate export files
- Handle large files (streaming)

#### Background Job Service
**Description**: Service xử lý async operations

**Responsibilities**:
- Execute folder/deck copy operations
- Track progress of async jobs
- Send completion notifications
- Handle timeouts and failures

## 4. System Architecture

### 4.1 High-Level Architecture

```
┌─────────────────────────────────────────────────────┐
│                  Client Layer                        │
├──────────────────────┬──────────────────────────────┤
│   Web App            │   Mobile App                 │
│   (React + Vite)     │   (React Native)             │
│   - Tailwind CSS     │   - React Native Paper       │
│   - Shadcn/ui        │   - Navigation               │
│   - React Query      │   - React Query              │
│   - Zustand          │   - Zustand                  │
└──────────────────────┴──────────────────────────────┘
                       │
                       │ HTTPS / REST API
                       ▼
┌─────────────────────────────────────────────────────┐
│              Backend Layer (Spring Boot 3)           │
├─────────────────────────────────────────────────────┤
│  Controllers (REST API)                             │
│    - AuthController                                 │
│    - FolderController                               │
│    - DeckController                                 │
│    - CardController                                 │
│    - ReviewController                               │
│    - StatsController                                │
├─────────────────────────────────────────────────────┤
│  Services (Business Logic)                          │
│    - IAuthService → AuthServiceImpl                 │
│    - IFolderService → FolderServiceImpl             │
│    - IDeckService → DeckServiceImpl                 │
│    - ICardService → CardServiceImpl                 │
│    - IReviewService → ReviewServiceImpl             │
│    - ISRSService → SRSServiceImpl                   │
│    - IStatsService → StatsServiceImpl               │
│    - IImportExportService → ImportExportServiceImpl │
├─────────────────────────────────────────────────────┤
│  Strategy Patterns                                  │
│    - ReviewOrderStrategy                            │
│      - AscendingReviewStrategy                      │
│      - DescendingReviewStrategy                     │
│      - RandomReviewStrategy                         │
│    - ForgottenCardActionStrategy                    │
│      - MoveToBox1Strategy                           │
│      - MoveDownNBoxesStrategy                       │
│      - StayInBoxStrategy                            │
├─────────────────────────────────────────────────────┤
│  Repositories (Spring Data JPA)                     │
│    - UserRepository                                 │
│    - FolderRepository                               │
│    - DeckRepository                                 │
│    - CardRepository                                 │
│    - CardBoxPositionRepository                      │
│    - ReviewLogRepository                            │
│    - SRSSettingsRepository                          │
│    - UserStatsRepository                            │
│    - FolderStatsRepository                          │
└─────────────────────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────┐
│           Data Layer (PostgreSQL)                    │
├─────────────────────────────────────────────────────┤
│  Core Tables:                                       │
│    - users, folders, decks, cards                   │
│  SRS Tables:                                        │
│    - srs_settings, card_box_position, review_logs   │
│  Stats Tables:                                      │
│    - user_stats, folder_stats                       │
│  Indexes:                                           │
│    - idx_card_box_user_due (CRITICAL)               │
│    - idx_folders_path                               │
│    - idx_folders_user_parent                        │
└─────────────────────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────┐
│   Background Services (Spring @Async + ThreadPool)   │
├─────────────────────────────────────────────────────┤
│  Technology: Spring @Async with ThreadPoolTaskExecutor│
│  - Core pool: 5 threads, Max pool: 10 threads      │
│  - Queue capacity: 100 tasks                        │
│                                                     │
│  Jobs:                                              │
│  - Folder Copy Job (>50 items)                      │
│  - Deck Copy Job (>1000 cards)                      │
│  - Import Job (large CSV/Excel)                     │
│  - Stats Recalculation Job (every 5 min)            │
│  - Notification Sender (push/email)                 │
│  - Domain Event Listeners (async stats update)      │
└─────────────────────────────────────────────────────┘
```

### 4.2 Design Patterns Used

#### Composite Pattern ⭐
- **Use Case**: Folder tree structure
- **Implementation**: Folder can contain Folders (sub-folders) và Decks
- **Benefit**: Unlimited hierarchical depth, easy traversal

#### Strategy Pattern ⭐
- **Use Case 1**: Review order (Ascending, Descending, Random)
- **Use Case 2**: Forgotten card actions (Move to Box 1, Move down N boxes, Stay in box)
- **Benefit**: Easy to add new strategies, testable

#### Visitor Pattern ⭐
- **Use Case**: Traverse folder tree to calculate statistics (recursive)
- **Benefit**: Separate algorithm from folder structure

#### Repository Pattern
- **Use Case**: Data access abstraction
- **Implementation**: Spring Data JPA Repositories
- **Benefit**: Testable, can swap implementation

#### DTO Pattern
- **Use Case**: Data transfer between layers
- **Implementation**: MapStruct for mapping Entity ↔ DTO
- **Benefit**: Decouple API contract from domain model

## 5. Key Use Cases

### 5.1 UC-001: Create Folder Hierarchy
**Actor**: Student
**Goal**: Organize knowledge by creating nested folders

**Main Flow**:
1. User clicks "Create Folder" in sidebar
2. System prompts for folder name and parent folder
3. User enters "English Learning" (root level)
4. System validates depth ≤ 10
5. System creates folder with UUID
6. User creates sub-folder "IELTS" under "English Learning"
7. User creates sub-folder "Vocabulary" under "IELTS"
8. System displays breadcrumb: Home > English Learning > IELTS > Vocabulary

**Business Rules**:
- Max depth = 10 levels
- Folder name unique within same parent
- Max 100 characters for folder name

### 5.2 UC-002: Import Cards from Excel
**Actor**: Student
**Goal**: Bulk import 1000 vocabulary cards

**Main Flow**:
1. User selects deck "IELTS Vocabulary"
2. User clicks "Import Cards"
3. System provides template download option
4. User downloads template, fills 1000 rows (Front, Back)
5. User uploads filled Excel file
6. System validates file:
   - Check file size < 50MB
   - Check row count ≤ 10,000
   - Validate columns (Front, Back required)
   - Check for empty rows (skip)
   - Check for duplicates (warn)
7. System shows preview (first 10 rows)
8. User confirms import
9. System batch inserts 1000 cards (batch size: 1000 per transaction)
10. System shows success: "1000 cards imported successfully"

**Alternative Flow**:
- If validation fails: Show error details with row numbers
- If file too large: Show error "File size exceeds 50MB"

### 5.3 UC-003: Review Cards with SRS
**Actor**: Student
**Goal**: Review 20 due cards today

**Main Flow**:
1. System calculates due cards: WHERE user_id = ? AND due_date <= CURRENT_DATE
2. System applies review order (user setting: Random)
3. System loads 20 cards with box positions
4. User sees card #1 (Front: "ubiquitous", Box: 2, Due: today)
5. User thinks about answer
6. User clicks "Show Answer"
7. System shows Back: "existing everywhere"
8. User rates "Good" (remembers well)
9. System calculates:
   - Current box: 2 (interval: 3 days)
   - Next box: 3 (interval: 7 days)
   - Due date: today + 7 days
10. System updates card_box_position:
    - current_box = 3
    - due_date = today + 7
    - review_count += 1
11. System logs review to review_logs
12. System shows next card
13. Repeat steps 4-12 for 20 cards
14. System shows session summary:
    - 20 cards reviewed
    - Avg time: 15 seconds/card
    - Streak: 7 days

**Rating Actions**:
- **Again** (<1 min): Move according to forgotten_card_action setting
  - Default: Move to Box 1, due_date = tomorrow
- **Hard** (<6 min): Stay in same box, due_date = today + (current_interval / 2)
- **Good** (next interval): Move to next box, due_date = today + next_interval
- **Easy** (4x interval): Skip 1 box, due_date = today + (next_interval * 4)

### 5.4 UC-004: Copy Folder with Async Job
**Actor**: Student
**Goal**: Copy large folder with 100 decks and 5,000 cards

**Main Flow**:
1. User right-clicks folder "English Learning" (contains 100 decks, 5,000 cards)
2. User selects "Copy"
3. User selects destination folder "Archive"
4. System counts total items: 100 decks + 1 parent folder = 101 items
5. System detects 101 > 50 → Async copy required
6. System shows warning: "This folder is large. Copy will run in background."
7. User confirms
8. System creates background job with job_id
9. System returns immediately with response: { job_id: "abc-123", status: "PROCESSING" }
10. User sees notification: "Copy in progress. You'll be notified when done."
11. Background job executes:
    - Copy folder structure
    - For each deck: deep copy with all cards
    - Update progress: 10%, 20%, ... 100%
12. Job completes after 2 minutes
13. System sends notification: "Folder copied successfully to Archive"
14. User clicks notification, navigates to Archive folder

**Business Rules**:
- Sync copy: ≤ 50 items (immediate response)
- Async copy: 51-500 items (background job)
- Hard limit: > 500 items → Reject with error

### 5.5 UC-005: Study with Cram Mode
**Actor**: Student
**Goal**: Review all 100 cards in "IELTS Speaking" deck before exam tomorrow

**Main Flow**:
1. User selects deck "IELTS Speaking"
2. User clicks "Cram Mode"
3. System loads ALL 100 cards (ignore due_date)
4. System shuffles cards randomly
5. User reviews 100 cards quickly (no time pressure)
6. User rates cards (ratings saved but DON'T affect schedule)
7. System shows summary: "100 cards reviewed in Cram Mode"
8. System does NOT update due_dates or box positions
9. Next day, SRS schedule remains unchanged

**Key Difference from SRS Mode**:
- Cram: All cards, no schedule impact
- SRS: Only due cards, updates schedule

## 6. Data Flow

### 6.1 Review Session Data Flow

```
User clicks "Review"
  → Controller: GET /api/review/due?scope=deck&scope_id={deckId}
  → ReviewService.getDueCards(userId, scope, scopeId)
  → CardBoxPositionRepository.findDueCards(userId, currentDate)
    → SQL: SELECT c.*, cbp.* FROM card_box_position cbp
           JOIN cards c ON c.id = cbp.card_id
           WHERE cbp.user_id = ? AND cbp.due_date <= ?
           ORDER BY cbp.due_date ASC, cbp.current_box ASC
           LIMIT 100
  → Apply ReviewOrderStrategy (e.g., RandomReviewStrategy.shuffle())
  → Return List<CardResponse>

User rates card "Good"
  → Controller: POST /api/review/submit { cardId, rating: "GOOD" }
  → ReviewService.submitReview(userId, cardId, rating)
  → SRSService.calculateNextReview(cardId, rating)
    → Get current box_position
    → Apply ForgottenCardActionStrategy if rating = "AGAIN"
    → Calculate next_box, next_interval, next_due_date
    → Update card_box_position (box, due_date, review_count)
  → ReviewLogRepository.save(reviewLog)
  → UserStatsService.updateStats(userId) // Increment cards_reviewed_today
  → Return { success: true, nextCard: {...} }
```

### 6.2 Folder Statistics Data Flow

```
User opens folder "English Learning"
  → Controller: GET /api/folders/{id}/stats
  → FolderService.getFolderStatistics(folderId, userId)
  → Check FolderStatsRepository.findByFolderIdAndUserId()
    → If cached AND last_computed_at < 5 minutes ago:
        → Return cached stats
    → Else:
        → FolderVisitor.visit(folder) // Traverse tree
        → Calculate recursive stats:
          - Total cards (sum of all decks in folder + sub-folders)
          - Due cards (count cards with due_date <= today)
          - New cards (count cards with review_count = 0)
          - Mature cards (count cards in box ≥ 5)
        → Save to folder_stats table
        → Return stats
```

## 7. Non-Functional Requirements

See [nfr.md](./nfr.md) for detailed non-functional requirements.

**Summary**:
- Performance: API response < 500ms (p95), folder tree load < 300ms
- Scalability: Support < 100 users (MVP), 10,000 cards/deck, 1,000 folders/user
- Security: JWT auth, bcrypt password, HTTPS, input validation
- Reliability: Database backup, error logging
- Usability: Responsive design, dark mode, multi-language (VI/EN)

## 8. Technology Stack

### Backend
- Java 17 + Spring Boot 3
- Spring Data JPA (Hibernate)
- PostgreSQL
- Apache POI (Excel processing)
- OpenCSV (CSV processing)
- Spring @Async + ThreadPoolTaskExecutor (background jobs, domain event listeners)

### Frontend
- **Web**: React + TypeScript, Vite, Tailwind CSS, Shadcn/ui
- **Mobile**: React Native, React Native Paper
- **State Management**: TanStack Query (server state), Context API (auth), Zustand (UI state)

### Architecture
- Layered: Controller → Service → Repository
- RESTful API
- JWT authentication (24h expiry, no refresh token in MVP)

## 9. Constraints & Limitations

### Technical Constraints
- **Folder depth**: Max 10 levels
- **Folder copy**: Max 500 items (folders + decks)
- **Deck copy**: Max 10,000 cards
- **Import file**: Max 10,000 rows, 50MB
- **Review query**: Max 200 cards per request

### User Constraints
- **Target users**: < 100 users for MVP
- **Platform**: Online only (no offline mode in MVP)
- **Authentication**: Email/password only (no OAuth in MVP)

### Performance Constraints
- **Response time**: < 500ms for API calls (p95)
- **Database**: Indexed queries, denormalized stats for performance

## 10. Security Considerations

### MVP Security (Acceptable for < 100 users)
- Password: bcrypt (cost factor 12)
- JWT: 24h expiry, no refresh token
- HTTPS in production
- Input validation & sanitization
- Authorization: user-scoped data access
- Rate limiting: 100 req/min/user (in-memory)

### Production Recommendations (Future)
- JWT with refresh token (15 min access, 7 days refresh)
- Redis-based distributed rate limiting
- Security headers (CORS, CSP, X-Frame-Options)
- Audit logging for critical operations
- Failed login attempt tracking

## 11. Deployment Architecture (MVP)

```
┌─────────────────────────────────────┐
│   CloudFlare / CDN (Static Assets)  │
└─────────────────────────────────────┘
                 ▼
┌─────────────────────────────────────┐
│   Load Balancer (Nginx)             │
└─────────────────────────────────────┘
                 ▼
┌─────────────────────────────────────┐
│   Web Server (React App)            │
│   - Vite build                      │
│   - Served by Nginx                 │
└─────────────────────────────────────┘
                 ▼
┌─────────────────────────────────────┐
│   API Server (Spring Boot)          │
│   - Port 8080                       │
│   - Tomcat embedded                 │
└─────────────────────────────────────┘
                 ▼
┌─────────────────────────────────────┐
│   Database (PostgreSQL 15)          │
│   - Single instance (MVP)           │
│   - Daily backups                   │
└─────────────────────────────────────┘
```

**Notes**:
- Single server for MVP (< 100 users)
- Vertical scaling if needed
- Horizontal scaling planned for Phase 2 (> 1000 users)

## 12. Monitoring & Observability (MVP)

### Logging
- **Backend**: SLF4J + Logback
- **Log Levels**: ERROR, WARN, INFO, DEBUG
- **Log Storage**: File logs (rotate daily, keep 30 days)

### Metrics (Future)
- Response times (p50, p95, p99)
- Error rates (4xx, 5xx)
- Database query times
- Background job durations

### Alerting (Future)
- Email alerts for critical errors
- Slack notifications for deployments

## 13. Testing Strategy

### Unit Tests
- Coverage ≥ 70% for core logic
- Focus on: SRS algorithm, strategies, folder operations
- JUnit 5 + Mockito

### Integration Tests
- Test API endpoints with MockMvc
- Test database operations with @DataJpaTest
- Focus on: folder copy, import/export, review flow

### Critical Test Cases
- Folder copy with deep nested structure (10 levels)
- Folder move validation (prevent circular reference)
- Folder delete cascade behavior
- SRS algorithm correctness (box transitions)
- Import 10,000 rows performance test
- Review query performance (1000 due cards)

## 14. Glossary

See [data-dictionary.md](./data-dictionary.md) for full data dictionary.

**Key Terms**:
- **Folder**: Container for sub-folders and decks, hierarchical organization
- **Deck**: Collection of flashcards within a folder
- **Card**: Flashcard with front (question) and back (answer)
- **Box**: Position in SRS algorithm (1-7), determines review interval
- **Due Date**: Date when card should be reviewed
- **Review**: Study session where user rates card recall
- **Interval**: Days between reviews (1, 3, 7, 14, 30, 60, 120)
- **SRS**: Spaced Repetition System, algorithm for optimal review timing
- **Cram Mode**: Study all cards without affecting SRS schedule

## 15. Change Log

| Version | Date | Changes | Author |
|---------|------|---------|--------|
| 1.0 | 2025-01 | Initial MVP specification | Product Team |

---

**Next Steps**:
1. Review and approve system spec
2. Design detailed database schema → [data-dictionary.md](./data-dictionary.md)
3. Create domain model → [domain-model.md](./domain-model.md)
4. Define non-functional requirements → [nfr.md](./nfr.md)
5. Create detailed use cases for key features
