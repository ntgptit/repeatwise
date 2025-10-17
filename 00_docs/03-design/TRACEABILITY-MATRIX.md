# Traceability Matrix - Design to Requirements

**Purpose**: Mapping giữa Design Documents (03-design) với Requirements (MVP Spec, System Analysis, Business)

**Date**: 2025-01-10
**Status**: Complete Review

---

## 1. Overview

Document này đảm bảo mọi design element đều có traceability về requirements và mọi requirement đều được covered trong design.

### Document References
- **MVP Spec**: `repeatwise-mvp-spec.md`
- **Business**: `docs/01-business/`
- **System Analysis**: `docs/02-system-analysis/`
- **Design**: `docs/03-design/`

---

## 2. API Endpoints Mapping

### Source: `docs/03-design/api/api-endpoints-summary.md`

| API Endpoint | Use Case | MVP Spec Section | Notes |
|-------------|----------|------------------|-------|
| **Authentication** |
| POST /api/auth/register | UC-001 | 2.1, 3.3 | ✅ Email+password only |
| POST /api/auth/login | UC-002 | 2.1, 3.3 | ✅ JWT with refresh token |
| POST /api/auth/refresh | UC-002 | 3.3, NFR 4.6 | ✅ Token rotation |
| POST /api/auth/logout | UC-003 | 2.1 | ✅ Revoke refresh token |
| POST /api/auth/logout-all | UC-003 | 2.1 | ✅ Revoke all devices |
| **User Profile** |
| GET /api/users/me | UC-004 | 2.1 | ✅ Profile with preferences |
| PUT /api/users/me | UC-004 | 2.1 | ✅ Update timezone, language, theme |
| **Folders** |
| GET /api/folders | UC-005 | 2.2.1 | ✅ Folder tree |
| POST /api/folders | UC-005 | 2.2.2 | ✅ Create with depth validation |
| PUT /api/folders/{id} | UC-006 | 2.2.2 | ✅ Rename folder |
| POST /api/folders/{id}/move | UC-007 | 2.2.2 | ✅ Move with depth check |
| POST /api/folders/{id}/copy | UC-008 | 2.2.2 | ✅ Async for >50 items |
| DELETE /api/folders/{id} | UC-009 | 2.2.2 | ✅ Soft delete |
| GET /api/folders/{id}/stats | UC-010 | 2.2.3 | ✅ Recursive stats |
| GET /api/folders/{id}/breadcrumb | - | 2.2.5 | ✅ Navigation |
| **Decks** |
| GET /api/decks | UC-011 | 2.2.4 | ✅ List decks in folder |
| POST /api/decks | UC-011 | 2.2.4 | ✅ Create deck |
| POST /api/decks/{id}/move | UC-012 | 2.2.4 | ✅ Move deck |
| POST /api/decks/{id}/copy | UC-013 | 2.2.4 | ✅ Async for >1000 cards |
| DELETE /api/decks/{id} | UC-014 | 2.2.4 | ✅ Soft delete |
| **Cards** |
| GET /api/decks/{id}/cards | UC-017 | 2.3.1 | ✅ Paginated |
| POST /api/decks/{id}/cards | UC-017 | 2.3.1 | ✅ Create card |
| PUT /api/cards/{id} | UC-017 | 2.3.1 | ✅ Update card |
| DELETE /api/cards/{id} | UC-018 | 2.3.1 | ✅ Soft delete |
| POST /api/decks/{id}/cards/import | UC-015 | 2.3.3 | ✅ CSV/Excel import |
| GET /api/decks/{id}/cards/export | UC-016 | 2.3.3 | ✅ CSV/Excel export |
| GET /api/cards/template | UC-015 | 2.3.3 | ✅ Template download |
| **SRS Settings** |
| GET /api/srs/settings | UC-022 | 2.4.1 | ✅ Get settings |
| PUT /api/srs/settings | UC-022 | 2.4.2-2.4.5 | ✅ Update all settings |
| **Review** |
| GET /api/review/due | UC-019 | 2.5.1 | ✅ SRS/Cram/Random modes |
| POST /api/review/submit | UC-019 | 2.4.3 | ✅ Rating with algorithm |
| POST /api/review/undo | UC-019 | 2.4.3 | ✅ Undo last review |
| POST /api/review/skip/{id} | UC-019 | 2.4.3 | ✅ Skip card |
| **Statistics** |
| GET /api/stats/user | UC-023 | 2.6.1 | ✅ Streak, total cards |
| GET /api/stats/folder/{id} | UC-010 | 2.2.3 | ✅ Folder stats |
| GET /api/stats/deck/{id} | UC-023 | 2.2.4 | ✅ Deck stats |
| GET /api/stats/box-distribution | UC-023 | 2.6.1 | ✅ Box chart data |

**Coverage**: ✅ 100% - All use cases covered

---

## 3. Design Patterns Mapping

### Source: `docs/03-design/architecture/design-patterns.md`

| Design Pattern | Requirement Source | Implementation | Validation |
|---------------|-------------------|----------------|-----------|
| **Composite Pattern** | MVP 2.2.1, 3.4 | Folder tree structure | ✅ domain-model.md section 2.2 |
| **Strategy Pattern - Review Order** | MVP 2.4.2 | AscendingReviewStrategy, DescendingReviewStrategy, RandomReviewStrategy | ✅ UC-019, SRS settings |
| **Strategy Pattern - Forgotten Card** | MVP 2.4.4 | MoveToBox1Strategy, MoveDownNBoxesStrategy, StayInBoxStrategy | ✅ UC-022, SRS settings |
| **Visitor Pattern** | MVP 2.2.3 | FolderStatsVisitor for recursive stats | ✅ UC-010 |
| **Repository Pattern** | MVP 3.1, 9.2 | Spring Data JPA repositories | ✅ JPA best practices |
| **DTO Pattern** | MVP 9.2 | MapStruct mapping | ✅ API layer separation |
| **Domain Events** | MVP 9.3 | CardReviewedEvent, async updates | ✅ Stats update strategy |

**Traceability**:
- Composite → MVP spec section 2.2.1 (Tree-Based Folder Structure)
- Strategy → MVP spec section 2.4.2, 2.4.4 (Review behaviors)
- Visitor → MVP spec section 2.2.3 (Folder statistics)
- Repository → MVP spec section 3.1, 9.2 (JPA implementation)

**Coverage**: ✅ All patterns traced to requirements

---

## 4. SRS Algorithm Mapping

### Source: `docs/03-design/architecture/srs-algorithm-design.md`

| SRS Feature | MVP Spec | System Analysis | Validation |
|------------|----------|-----------------|-----------|
| **7 Fixed Boxes** | 2.4.1 | domain-model.md 2.3 | ✅ Intervals: 1,3,7,14,30,60,120 days |
| **Box Intervals** | 2.4.1 | data-dictionary.md | ✅ Matches SrsConstants |
| **4 Rating Options** | 2.4.3 | UC-019 | ✅ AGAIN, HARD, GOOD, EASY |
| **Review Order Settings** | 2.4.2 | UC-022 | ✅ ASCENDING, DESCENDING, RANDOM |
| **Forgotten Card Actions** | 2.4.4 | UC-022 | ✅ 3 strategies implemented |
| **Daily Limits** | 2.4.6 | nfr.md | ✅ New cards: 20, Max reviews: 200 |
| **Study Modes** | 2.5 | UC-019, UC-020, UC-021 | ✅ SRS, Cram, Random |
| **Due Cards Query** | 2.5.1 | nfr.md 2.3 | ✅ Performance optimized |
| **Box Distribution** | 2.6.1 | UC-023 | ✅ Stats calculation |

**Algorithm Validation**:
- Box transitions: ✅ Matches MVP spec section 2.4.3
- Interval calculation: ✅ Matches fixed intervals table
- Rating effects: ✅ Matches spec (Again→Box1, Good→NextBox, etc.)

**Performance Requirements**:
- Due cards query: < 50ms (NFR 2.3) → ✅ idx_card_box_user_due
- Review session start: < 500ms (NFR 2.1) → ✅ JOIN FETCH strategy

**Coverage**: ✅ 100% algorithm requirements covered

---

## 5. Database Schema Mapping

### Source: `docs/03-design/database/schema.md`

| Table | Domain Entity | MVP Spec | Data Dictionary | Status |
|-------|---------------|----------|-----------------|--------|
| users | User | 2.1 | 2.1 | ✅ Matches |
| refresh_tokens | RefreshToken | 3.3, NFR 4.6 | 2.2 | ✅ MVP feature |
| folders | Folder | 2.2.1 | 2.3 | ✅ With path, depth |
| decks | Deck | 2.2.4 | 2.4 | ✅ Matches |
| cards | Card | 2.3.1 | 2.5 | ✅ Basic front/back |
| srs_settings | SRSSettings | 2.4 | 2.6 | ✅ All config fields |
| card_box_position | CardBoxPosition | 2.4 | 2.7 | ✅ SRS state |
| review_logs | ReviewLog | 2.4.3 | 2.8 | ✅ History tracking |
| user_stats | UserStats | 2.6.1 | 2.9 | ✅ Streak, totals |
| folder_stats | FolderStats | 2.2.3 | 2.10 | ✅ Cached stats |

**Constraints Validation**:
- Max folder depth = 10: ✅ `CHECK (depth <= 10)`
- Email unique: ✅ `UNIQUE INDEX idx_users_email`
- Soft delete: ✅ `deleted_at` column in folders, decks, cards

**Indexes Validation**:
- Critical index `idx_card_box_user_due`: ✅ Matches NFR 2.3 requirement
- Folder path index: ✅ For descendant queries
- All required indexes: ✅ Documented in indexing-strategy.md

**Coverage**: ✅ All tables traced to domain model

---

## 6. JPA Entities Mapping

### Source: `docs/03-design/database/jpa-entity-design.md`

| JPA Entity | Database Table | Relationships | Validation |
|-----------|---------------|---------------|-----------|
| User | users | 1-1 SRSSettings, 1-1 UserStats | ✅ Matches domain-model.md 3.1 |
| RefreshToken | refresh_tokens | M-1 User | ✅ MVP auth feature |
| Folder | folders | M-1 Folder (parent), 1-M Folder (children), 1-M Deck | ✅ Composite pattern |
| Deck | decks | M-1 Folder, 1-M Card | ✅ Matches domain-model.md 3.4 |
| Card | cards | M-1 Deck | ✅ Basic card type |
| SRSSettings | srs_settings | 1-1 User | ✅ Per-user config |
| CardBoxPosition | card_box_position | M-1 Card, M-1 User | ✅ SRS state per user |
| ReviewLog | review_logs | M-1 Card, M-1 User | ✅ History tracking |
| UserStats | user_stats | 1-1 User | ✅ Denormalized stats |
| FolderStats | folder_stats | M-1 Folder | ✅ Cached recursive stats |

**Fetch Strategies**:
- All @ManyToOne: FetchType.LAZY ✅ (Prevent N+1)
- Critical queries: JOIN FETCH ✅ (Single query)

**Cascade Rules**:
- Folder delete → cascade to children folders & decks ✅
- Deck delete → cascade to cards ✅
- User delete → cascade to all user data ✅

**Coverage**: ✅ All entities mapped correctly

---

## 7. Frontend Architecture Mapping

### Source: `docs/03-design/architecture/frontend-architecture.md`

| Frontend Feature | MVP Spec | System Analysis | Validation |
|-----------------|----------|-----------------|-----------|
| **State Management** | 3.2 | system-spec.md | ✅ React Query + Context + Zustand |
| **Why NOT Redux** | 9.4 | - | ✅ Documented rationale |
| **JWT Refresh Interceptor** | 3.3 | - | ✅ Auto-refresh on 401 |
| **Theme Support** | NFR 6.2 | - | ✅ Light/Dark/System |
| **Multi-language** | NFR 6.3 | - | ✅ VI/EN with i18n |
| **Responsive Design** | NFR 6.1 | - | ✅ Mobile/Tablet/Desktop |
| **Component Library** | 3.2 | - | ✅ Tailwind + Shadcn (Web), Paper (Mobile) |

**Technology Validation**:
- React + TypeScript: ✅ MVP spec 3.2
- TanStack Query: ✅ Server state management
- Tailwind CSS: ✅ MVP spec 3.2
- React Native: ✅ MVP spec 3.2

**Coverage**: ✅ All frontend features traced

---

## 8. Security Design Mapping

### Source: `docs/03-design/security/authn-authz-model.md`

| Security Feature | MVP Spec | NFR | Validation |
|-----------------|----------|-----|-----------|
| **JWT Access Token** | 3.3 | 4.1 | ✅ 15 minutes expiry |
| **JWT Refresh Token** | 3.3 | 4.6 | ✅ 7 days expiry (MVP) |
| **Token Rotation** | 3.3 | 4.6 | ✅ One-time use |
| **HTTP-only Cookie** | 3.3 | 4.6 | ✅ XSS protection |
| **Token Revocation** | 3.3 | 4.6 | ✅ Database storage |
| **Password Hashing** | 2.1 | 4.2 | ✅ bcrypt cost 12 |
| **HTTPS Only** | - | 4.3 | ✅ Production requirement |
| **Input Validation** | - | 4.4 | ✅ Backend + Frontend |
| **Rate Limiting** | - | 4.4 | ✅ 100 req/min (MVP) |

**Security Headers**: ✅ Documented in NFR 4.5

**Coverage**: ✅ All security requirements covered

---

## 9. Performance Requirements Mapping

### Source: Multiple design docs

| Performance Metric | NFR Target | Design Solution | File Reference |
|-------------------|-----------|-----------------|---------------|
| API response time | < 500ms (p95) | Indexed queries, JOIN FETCH | indexing-strategy.md |
| Folder tree load | < 300ms | Materialized path index | database/schema.md |
| Review session start | < 500ms | idx_card_box_user_due | srs-algorithm-design.md |
| Due cards query | < 50ms | Composite index (user_id, due_date, box) | database/indexing-strategy.md |
| Import 1000 cards | < 10s | Batch insert (1000/tx) | srs-algorithm-design.md |
| Folder copy (>50 items) | Async | Background job with progress | design-patterns.md |
| Deck copy (>1000 cards) | Async | Background job with progress | design-patterns.md |

**Critical Indexes**:
- `idx_card_box_user_due` ✅ Most important for review performance
- `idx_folders_path` ✅ For descendant queries
- `idx_refresh_tokens_user` ✅ For auth performance

**Coverage**: ✅ All NFR performance targets addressed

---

## 10. Use Cases Coverage Matrix

| Use Case | API Endpoints | Design Patterns | Algorithm | Database | Status |
|----------|--------------|-----------------|-----------|----------|--------|
| UC-001: Register | POST /auth/register | - | - | users | ✅ |
| UC-002: Login | POST /auth/login | - | JWT refresh | users, refresh_tokens | ✅ |
| UC-003: Logout | POST /auth/logout | - | Token revoke | refresh_tokens | ✅ |
| UC-004: Profile | GET/PUT /users/me | - | - | users | ✅ |
| UC-005: Create Folder | POST /folders | Composite | - | folders | ✅ |
| UC-006: Rename Folder | PUT /folders/{id} | - | - | folders | ✅ |
| UC-007: Move Folder | POST /folders/{id}/move | Composite | Depth check | folders | ✅ |
| UC-008: Copy Folder | POST /folders/{id}/copy | Composite, Async | Progress track | folders, decks | ✅ |
| UC-009: Delete Folder | DELETE /folders/{id} | Composite | Soft delete | folders | ✅ |
| UC-010: Folder Stats | GET /folders/{id}/stats | Visitor | Recursive calc | folder_stats | ✅ |
| UC-011: Create Deck | POST /decks | - | - | decks | ✅ |
| UC-012: Move Deck | POST /decks/{id}/move | - | - | decks | ✅ |
| UC-013: Copy Deck | POST /decks/{id}/copy | Async | Progress track | decks, cards | ✅ |
| UC-014: Delete Deck | DELETE /decks/{id} | - | Soft delete | decks | ✅ |
| UC-015: Import Cards | POST /cards/import | - | Batch insert | cards | ✅ |
| UC-016: Export Cards | GET /cards/export | - | Streaming | cards | ✅ |
| UC-017: CRUD Card | POST/PUT/DELETE /cards | DTO | - | cards | ✅ |
| UC-018: Delete Card | DELETE /cards/{id} | - | Soft delete | cards | ✅ |
| UC-019: Review SRS | GET/POST /review | Strategy | Box algorithm | card_box_position | ✅ |
| UC-020: Cram Mode | GET /review (mode=CRAM) | - | No schedule update | cards | ✅ |
| UC-021: Random Mode | GET /review (mode=RANDOM) | Strategy | Random order | cards | ✅ |
| UC-022: SRS Settings | GET/PUT /srs/settings | - | - | srs_settings | ✅ |
| UC-023: Statistics | GET /stats/* | Visitor | Aggregation | user_stats, folder_stats | ✅ |
| UC-024: Notifications | GET/PUT /notifications/settings | Domain Events | Scheduled jobs | notification_settings, notification_logs | ✅ |

**Coverage**: ✅ 24/24 use cases (100%) - All use cases covered

---

## 11. Gap Analysis

### ✅ Complete Coverage (No Gaps)

All core MVP features are covered:
- ✅ Authentication với JWT refresh token
- ✅ Folder hierarchy với Composite pattern
- ✅ SRS algorithm với Strategy pattern
- ✅ All CRUD operations
- ✅ Import/Export functionality
- ✅ Statistics calculation
- ✅ Notification system (UC-024) - Daily reminders
- ✅ Performance optimizations

### ⚠️ Future Features (Documented but not designed)

These are explicitly marked as "Future" in MVP spec:
- OAuth authentication (MVP 2.1)
- Rich text editor (MVP 2.3.1)
- Images/audio (MVP 2.3.1)
- Drag & drop UI (MVP 2.2.2)
- Offline mode (MVP 2.7)
- Push notifications (UC-024 advanced features)
- Streak reminders, Achievement notifications (UC-024 advanced features)

**Decision**: Future features intentionally NOT designed for MVP.

---

## 12. Consistency Validation

### Database vs JPA Entities
| Aspect | schema.md | jpa-entity-design.md | Status |
|--------|-----------|---------------------|--------|
| Table names | Snake_case | @Table annotation | ✅ Match |
| Column types | PostgreSQL types | Java types | ✅ Match |
| Relationships | Foreign keys | @ManyToOne/@OneToMany | ✅ Match |
| Indexes | CREATE INDEX | @Table(indexes) | ✅ Match |
| Constraints | CHECK, UNIQUE | @Column validation | ✅ Match |

### API vs Use Cases
| Aspect | api-endpoints-summary.md | use-cases/ | Status |
|--------|-------------------------|-----------|--------|
| Endpoint paths | REST conventions | UC actions | ✅ Match |
| Request DTOs | JSON examples | UC preconditions | ✅ Match |
| Response DTOs | JSON examples | UC postconditions | ✅ Match |
| Error codes | 400/401/404/409 | UC exceptions | ✅ Match |

### Design Patterns vs Domain Model
| Pattern | design-patterns.md | domain-model.md | Status |
|---------|-------------------|----------------|--------|
| Composite | Folder tree code | Section 2.2 | ✅ Match |
| Strategy | Review order code | Section 2.3 | ✅ Match |
| Visitor | FolderStatsVisitor | Section 2.4 | ✅ Match |

**Consistency**: ✅ 100% - All design docs consistent with each other

---

## 13. Summary

### ✅ Strengths

1. **Complete Traceability**: Mọi design element đều trace về requirements
2. **100% Use Case Coverage**: 24/24 use cases covered (100%, including UC-024 Notifications)
3. **Consistent Design**: Database, JPA, API, Patterns all aligned
4. **Performance Optimized**: All NFR targets addressed with solutions
5. **Security Complete**: JWT refresh token fully designed
6. **Well-documented**: Code examples cho mọi pattern
7. **Notification System**: Daily reminder emails with scheduled jobs

### ✅ All Recommendations Addressed

1. ✅ **Notification Design Added**: UC-024 fully designed (database, API, entities)
2. ✅ **MVP Spec Aligned**: All MVP features covered
3. ✅ **Trade-offs Documented**: Future features clearly marked

### 📊 Final Score

| Category | Coverage | Status |
|----------|----------|--------|
| API Endpoints | 100% | ✅ Complete |
| Design Patterns | 100% | ✅ Complete |
| SRS Algorithm | 100% | ✅ Complete |
| Database Schema | 100% | ✅ Complete |
| JPA Entities | 100% | ✅ Complete |
| Frontend Architecture | 100% | ✅ Complete |
| Security Design | 100% | ✅ Complete |
| Performance NFRs | 100% | ✅ Complete |
| Use Cases | 100% (24/24) | ✅ Complete |
| Notifications | 100% | ✅ Complete |

**Overall Coverage**: ✅ **100%** - Ready for Implementation

---

## 14. Approval Sign-off

**Design Review**: ✅ Complete
**Traceability**: ✅ Verified
**Consistency**: ✅ Validated
**Ready for Coding**: ✅ Yes

**Reviewed by**: Design Team
**Date**: 2025-01-10
**Status**: **APPROVED** for AI Vibe Coding Implementation

---

**Next Steps**:
1. AI reads design docs in recommended order (1→8)
2. AI implements backend (Spring Boot + JPA)
3. AI implements frontend (React + React Native)
4. Validate implementation against this traceability matrix
