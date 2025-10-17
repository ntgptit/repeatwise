# Entity Specifications - RepeatWise MVP

## Document Overview

This document provides comprehensive specifications for all JPA entities in RepeatWise MVP. These specifications serve as the single source of truth for generating JPA entity code.

**Purpose**: Define complete specifications (NOT code) for each entity including table structure, relationships, indexes, validation rules, and business constraints.

**Target Audience**: Backend developers who will implement JPA entities

**Related Documents**:
- [Database Schema](../03-design/database/schema.md) - SQL schema definitions
- [JPA Entity Design](../03-design/database/jpa-entity-design.md) - JPA patterns and examples
- [Domain Model](../02-system-analysis/domain-model.md) - Business logic and domain entities
- [MVP Specification](../../repeatwise-mvp-spec.md) - Complete requirements

---

## Entity: User

### Database Table
- **Table name**: `users`
- **Description**: User accounts with authentication and preferences

### Fields Specifications

| Field | Type | Constraints | Default | Description |
|-------|------|-------------|---------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | gen_random_uuid() | Primary key |
| email | VARCHAR(255) | UNIQUE, NOT NULL | - | User email (login identifier) |
| password_hash | VARCHAR(255) | NOT NULL, LENGTH = 60 | - | Bcrypt hashed password (fixed length) |
| name | VARCHAR(100) | NOT NULL | - | User display name |
| timezone | VARCHAR(50) | NOT NULL | 'Asia/Ho_Chi_Minh' | User timezone for date calculations |
| language | VARCHAR(10) | NOT NULL | 'VI' | UI language (VI, EN) |
| theme | VARCHAR(10) | NOT NULL | 'SYSTEM' | UI theme (LIGHT, DARK, SYSTEM) |
| created_at | TIMESTAMP | NOT NULL | CURRENT_TIMESTAMP | Record creation time |
| updated_at | TIMESTAMP | NOT NULL | CURRENT_TIMESTAMP | Last update time |
| deleted_at | TIMESTAMP | NULL | NULL | Soft delete timestamp |

### Relationships
- **folders**: OneToMany → Folder (LAZY fetch, bidirectional)
  - mappedBy: "user"
  - Cascade: ALL
  - orphanRemoval: true

- **refreshTokens**: OneToMany → RefreshToken (LAZY fetch, bidirectional)
  - mappedBy: "user"
  - Cascade: ALL
  - orphanRemoval: true

- **srsSettings**: OneToOne → SrsSettings (LAZY fetch, bidirectional)
  - mappedBy: "user"
  - Cascade: ALL
  - orphanRemoval: true

- **userStats**: OneToOne → UserStats (LAZY fetch, bidirectional)
  - mappedBy: "user"
  - Cascade: ALL
  - orphanRemoval: true

### Indexes
- **PRIMARY**: id (UUID)
- **UNIQUE**: idx_users_email ON (email)
- **INDEX**: idx_users_deleted_at ON (deleted_at) WHERE deleted_at IS NOT NULL

### Validation Rules
- **email**:
  - NOT NULL
  - Valid email format (regex: `^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$`)
  - Unique across all users
  - Immutable (cannot be changed after registration)

- **password_hash**:
  - NOT NULL
  - Fixed length = 60 (bcrypt output)
  - Bcrypt cost factor = 12

- **name**:
  - NOT NULL
  - Max length 100 characters
  - Not empty after trim

- **language**:
  - NOT NULL
  - Enum values: VI, EN
  - Default: VI

- **theme**:
  - NOT NULL
  - Enum values: LIGHT, DARK, SYSTEM
  - Default: SYSTEM

### Business Rules
- **BR-USER-001**: Email must be unique and immutable
- **BR-USER-002**: Password must be bcrypt hashed with cost factor 12 before storage
- **BR-USER-003**: Default SrsSettings and UserStats must be created on user registration
- **BR-USER-004**: Soft delete cascades to all related entities (folders, decks, cards)
- **BR-USER-005**: Timezone affects due date calculations in SRS system
- **BR-USER-006**: Deleted users retain data for 30 days before permanent deletion

### Audit Fields Strategy
- **created_at**: Auto-set on INSERT via @CreatedDate annotation
- **updated_at**: Auto-updated on UPDATE via @LastModifiedDate annotation
- Uses Spring Data JPA auditing with @EntityListeners(AuditingEntityListener.class)

### Soft Delete Strategy
- **Field**: deleted_at (TIMESTAMP NULL)
- **Implementation**: Set deleted_at = NOW() instead of DELETE
- **Query filtering**: All queries must include WHERE deleted_at IS NULL
- **Cascade**: Soft delete propagates to folders, decks, cards
- **Restoration**: Set deleted_at = NULL within 30-day window
- **Permanent deletion**: Cleanup job runs daily to hard delete users with deleted_at > 30 days

### Performance Considerations
- Index on email for fast login queries
- Partial index on deleted_at to optimize soft delete queries
- Lazy loading for all relationships to avoid N+1 queries

---

## Entity: RefreshToken

### Database Table
- **Table name**: `refresh_tokens`
- **Description**: JWT refresh tokens for authentication with token rotation support

### Fields Specifications

| Field | Type | Constraints | Default | Description |
|-------|------|-------------|---------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | gen_random_uuid() | Primary key |
| user_id | UUID | NOT NULL, FK → users(id) | - | Owner of the token |
| token_hash | VARCHAR(255) | NOT NULL, UNIQUE | - | Bcrypt hashed token |
| expires_at | TIMESTAMP | NOT NULL | NOW() + 7 days | Token expiration time |
| revoked_at | TIMESTAMP | NULL | NULL | Token revocation time |
| created_at | TIMESTAMP | NOT NULL | CURRENT_TIMESTAMP | Token creation time |
| updated_at | TIMESTAMP | NOT NULL | CURRENT_TIMESTAMP | Last update time |

### Relationships
- **user**: ManyToOne → User (LAZY fetch, unidirectional)
  - JoinColumn: user_id
  - Cascade: NONE (don't cascade operations to user)
  - Foreign key: ON DELETE CASCADE (DB level)

### Indexes
- **PRIMARY**: id (UUID)
- **UNIQUE**: idx_refresh_tokens_hash ON (token_hash)
- **INDEX**: idx_refresh_tokens_user ON (user_id) WHERE revoked_at IS NULL AND expires_at > NOW()
- **INDEX**: idx_refresh_tokens_expires ON (expires_at) WHERE revoked_at IS NULL

### Validation Rules
- **token_hash**:
  - NOT NULL
  - Unique across all tokens
  - Bcrypt hashed (never store plain text)
  - Length 60 characters (bcrypt output)

- **expires_at**:
  - NOT NULL
  - Must be > created_at
  - Default: 7 days from creation

- **revoked_at**:
  - NULL by default
  - If set, must be >= created_at
  - Once set, token is permanently invalid

### Business Rules
- **BR-TOKEN-001**: Token rotation - old token revoked when refreshed
- **BR-TOKEN-002**: One-time use - each refresh generates new token pair
- **BR-TOKEN-003**: Logout sets revoked_at = NOW()
- **BR-TOKEN-004**: Logout all devices revokes all user's tokens
- **BR-TOKEN-005**: Token validation checks: not revoked AND not expired
- **BR-TOKEN-006**: Expired tokens (> 7 days) are automatically invalid
- **BR-TOKEN-007**: Cleanup job deletes expired/revoked tokens > 30 days old (runs daily)

### Security Considerations
- Token stored as bcrypt hash, not plaintext
- HTTP-only cookie on client side (JavaScript cannot access)
- Secure flag in production (HTTPS only)
- SameSite=Strict to prevent CSRF
- Partial indexes to exclude expired/revoked tokens for performance

### Performance Considerations
- Partial index on (user_id) WHERE revoked_at IS NULL AND expires_at > NOW()
- Reduces index size by excluding invalid tokens
- Fast lookup for token validation queries

---

## Entity: Folder

### Database Table
- **Table name**: `folders`
- **Description**: Hierarchical folder structure with materialized path for tree traversal

### Fields Specifications

| Field | Type | Constraints | Default | Description |
|-------|------|-------------|---------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | gen_random_uuid() | Primary key |
| user_id | UUID | NOT NULL, FK → users(id) | - | Folder owner |
| parent_folder_id | UUID | NULL, FK → folders(id) | NULL | Parent folder (NULL = root level) |
| name | VARCHAR(100) | NOT NULL | - | Folder name |
| description | VARCHAR(500) | NULL | NULL | Folder description |
| depth | INTEGER | NOT NULL, CHECK >= 0 AND <= 10 | 0 | Tree depth (0 = root, max 10) |
| path | VARCHAR(1000) | NOT NULL | - | Materialized path: /uuid1/uuid2/... |
| created_at | TIMESTAMP | NOT NULL | CURRENT_TIMESTAMP | Creation time |
| updated_at | TIMESTAMP | NOT NULL | CURRENT_TIMESTAMP | Last update time |
| deleted_at | TIMESTAMP | NULL | NULL | Soft delete timestamp |

### Relationships
- **user**: ManyToOne → User (LAZY fetch, required)
  - JoinColumn: user_id
  - Cascade: NONE
  - Foreign key: ON DELETE CASCADE

- **parentFolder**: ManyToOne → Folder (LAZY fetch, optional)
  - JoinColumn: parent_folder_id
  - Cascade: NONE
  - Foreign key: ON DELETE CASCADE
  - Self-referencing relationship

- **subFolders**: OneToMany → Folder (LAZY fetch, bidirectional)
  - mappedBy: "parentFolder"
  - Cascade: ALL
  - orphanRemoval: true

- **decks**: OneToMany → Deck (LAZY fetch, bidirectional)
  - mappedBy: "folder"
  - Cascade: ALL
  - orphanRemoval: true

### Indexes
- **PRIMARY**: id (UUID)
- **INDEX**: idx_folders_user_parent ON (user_id, parent_folder_id) WHERE deleted_at IS NULL
- **INDEX**: idx_folders_path ON (user_id, path) WHERE deleted_at IS NULL
- **INDEX**: idx_folders_depth ON (depth)
- **UNIQUE**: idx_folders_name_parent ON (user_id, parent_folder_id, name) WHERE deleted_at IS NULL

### Validation Rules
- **name**:
  - NOT NULL
  - Max length 100 characters
  - Not empty after trim
  - Unique within same parent folder (per user)

- **description**:
  - Max length 500 characters
  - Nullable

- **depth**:
  - NOT NULL
  - Range: 0 to 10 (inclusive)
  - 0 = root level
  - CHECK constraint: depth >= 0 AND depth <= 10

- **path**:
  - NOT NULL
  - Format: /uuid/uuid/uuid (UUIDs separated by /)
  - Pattern regex: `^(/[0-9a-f-]{36})+$`
  - Max length 1000 characters
  - Auto-calculated based on parent path + self ID

### Business Rules
- **BR-FOLDER-001**: Max depth 10 levels (enforced by CHECK constraint)
- **BR-FOLDER-002**: Folder name must be unique within same parent (per user)
- **BR-FOLDER-003**: Cannot move folder into itself or its descendants (circular reference check)
- **BR-FOLDER-004**: Cannot create folder at depth 10 (already at max)
- **BR-FOLDER-005**: Move validation - new depth after move must not exceed 10
- **BR-FOLDER-006**: Path auto-calculated on insert/update: parent.path + "/" + self.id
- **BR-FOLDER-007**: Root folder has depth=0, path=/self.id, parent_folder_id=NULL
- **BR-FOLDER-008**: Copy limits - sync (≤50 items), async (51-500), reject (>500)
- **BR-FOLDER-009**: Soft delete cascades to sub-folders and decks
- **BR-FOLDER-010**: Recalculate path and depth recursively when folder moved

### Audit Fields Strategy
- **created_at**: Auto-set on INSERT
- **updated_at**: Auto-updated on UPDATE
- Uses @EntityListeners(AuditingEntityListener.class)

### Soft Delete Strategy
- **Field**: deleted_at (TIMESTAMP NULL)
- **Cascade**: Delete propagates to subFolders and decks
- **Implementation**: Recursive soft delete via business method
- **Query filtering**: WHERE deleted_at IS NULL in all queries
- **Restoration**: Set deleted_at = NULL (cascades to children)

### Performance Considerations
- **Materialized path**: Enables fast descendant queries
  - Find all descendants: `WHERE path LIKE '/parent_id/%'`
  - Use GIN index for pattern matching
- **Composite index**: (user_id, parent_folder_id) for listing folders
- **Partial indexes**: Exclude soft-deleted records
- **Denormalized stats**: Use folder_stats table for card counts (cached)
- **Avoid recursive queries**: Use materialized path instead of recursive CTEs

### Design Pattern
- **Composite Pattern**: Folder can contain Folders and Decks
- **Visitor Pattern**: Traverse folder tree for statistics calculation

---

## Entity: Deck

### Database Table
- **Table name**: `decks`
- **Description**: Flashcard decks that can be in folders or at root level

### Fields Specifications

| Field | Type | Constraints | Default | Description |
|-------|------|-------------|---------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | gen_random_uuid() | Primary key |
| user_id | UUID | NOT NULL, FK → users(id) | - | Deck owner |
| folder_id | UUID | NULL, FK → folders(id) | NULL | Parent folder (NULL = root level) |
| name | VARCHAR(100) | NOT NULL | - | Deck name |
| description | VARCHAR(500) | NULL | NULL | Deck description |
| created_at | TIMESTAMP | NOT NULL | CURRENT_TIMESTAMP | Creation time |
| updated_at | TIMESTAMP | NOT NULL | CURRENT_TIMESTAMP | Last update time |
| deleted_at | TIMESTAMP | NULL | NULL | Soft delete timestamp |

### Relationships
- **user**: ManyToOne → User (LAZY fetch, required)
  - JoinColumn: user_id
  - Cascade: NONE
  - Foreign key: ON DELETE CASCADE

- **folder**: ManyToOne → Folder (LAZY fetch, optional)
  - JoinColumn: folder_id
  - Cascade: NONE
  - Foreign key: ON DELETE CASCADE
  - NULL = deck at root level

- **cards**: OneToMany → Card (LAZY fetch, bidirectional)
  - mappedBy: "deck"
  - Cascade: ALL
  - orphanRemoval: true

### Indexes
- **PRIMARY**: id (UUID)
- **INDEX**: idx_decks_folder_user ON (folder_id, user_id) WHERE deleted_at IS NULL
- **INDEX**: idx_decks_user_deleted ON (user_id, deleted_at)
- **UNIQUE**: idx_decks_name_folder ON (user_id, folder_id, name) WHERE deleted_at IS NULL

### Validation Rules
- **name**:
  - NOT NULL
  - Max length 100 characters
  - Not empty after trim
  - Unique within same folder (per user)

- **description**:
  - Max length 500 characters
  - Nullable

- **folder_id**:
  - Nullable (decks can be at root level)
  - If set, must reference existing non-deleted folder

### Business Rules
- **BR-DECK-001**: Deck name must be unique within same folder (per user)
- **BR-DECK-002**: Decks can exist at root level (folder_id = NULL)
- **BR-DECK-003**: Cannot create deck in deleted folder
- **BR-DECK-004**: Soft delete cascades to all cards
- **BR-DECK-005**: Copy limits - sync (≤1000 cards), async (1001-10,000), reject (>10,000)
- **BR-DECK-006**: Cannot delete deck while review session active (optional validation)
- **BR-DECK-007**: Moving deck updates folder_id without affecting cards

### Audit Fields Strategy
- **created_at**: Auto-set on INSERT
- **updated_at**: Auto-updated on UPDATE
- Uses @EntityListeners(AuditingEntityListener.class)

### Soft Delete Strategy
- **Field**: deleted_at (TIMESTAMP NULL)
- **Cascade**: Delete propagates to cards
- **Implementation**: Business method sets deleted_at recursively
- **Query filtering**: WHERE deleted_at IS NULL
- **Restoration**: Set deleted_at = NULL (cascades to cards)

### Performance Considerations
- **Composite index**: (folder_id, user_id) for listing decks in folder
- **Partial index**: Exclude soft-deleted records
- **Eager statistics**: Denormalize card counts for performance
- **Batch operations**: Import/export uses batch processing

---

## Entity: Card

### Database Table
- **Table name**: `cards`
- **Description**: Basic flashcards with front/back text (MVP - plain text only)

### Fields Specifications

| Field | Type | Constraints | Default | Description |
|-------|------|-------------|---------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | gen_random_uuid() | Primary key |
| deck_id | UUID | NOT NULL, FK → decks(id) | - | Parent deck |
| front | TEXT | NOT NULL, LENGTH <= 5000 | - | Question/front side |
| back | TEXT | NOT NULL, LENGTH <= 5000 | - | Answer/back side |
| created_at | TIMESTAMP | NOT NULL | CURRENT_TIMESTAMP | Creation time |
| updated_at | TIMESTAMP | NOT NULL | CURRENT_TIMESTAMP | Last update time |
| deleted_at | TIMESTAMP | NULL | NULL | Soft delete timestamp |

### Relationships
- **deck**: ManyToOne → Deck (LAZY fetch, required)
  - JoinColumn: deck_id
  - Cascade: NONE
  - Foreign key: ON DELETE CASCADE

- **boxPosition**: OneToOne → CardBoxPosition (LAZY fetch, bidirectional)
  - mappedBy: "card"
  - Cascade: ALL
  - orphanRemoval: true

- **reviewLogs**: OneToMany → ReviewLog (LAZY fetch, bidirectional)
  - mappedBy: "card"
  - Cascade: ALL
  - orphanRemoval: true

### Indexes
- **PRIMARY**: id (UUID)
- **INDEX**: idx_cards_deck ON (deck_id) WHERE deleted_at IS NULL
- **INDEX**: idx_cards_deleted ON (deck_id, deleted_at)

### Validation Rules
- **front**:
  - NOT NULL
  - Not empty after trim
  - Max length 5000 characters
  - Plain text only (MVP - no HTML)

- **back**:
  - NOT NULL
  - Not empty after trim
  - Max length 5000 characters
  - Plain text only (MVP - no HTML)

- **deck_id**:
  - NOT NULL
  - Must reference existing non-deleted deck

### Business Rules
- **BR-CARD-001**: Front and back text cannot be empty
- **BR-CARD-002**: Max length 5000 characters per side (reasonable for flashcards)
- **BR-CARD-003**: Cards created without CardBoxPosition are auto-initialized on first access
- **BR-CARD-004**: Soft delete removes card from review schedule
- **BR-CARD-005**: Editing card during review session updates content but preserves SRS state
- **BR-CARD-006**: Import validation - duplicate detection by front text
- **BR-CARD-007**: Export includes SRS metadata (current box, review count)

### Audit Fields Strategy
- **created_at**: Auto-set on INSERT
- **updated_at**: Auto-updated on UPDATE
- Uses @EntityListeners(AuditingEntityListener.class)

### Soft Delete Strategy
- **Field**: deleted_at (TIMESTAMP NULL)
- **Implementation**: Set deleted_at = NOW()
- **Query filtering**: WHERE deleted_at IS NULL
- **Cascade**: Does NOT cascade (CardBoxPosition, ReviewLog kept for audit)
- **Restoration**: Set deleted_at = NULL within 30-day window

### Performance Considerations
- **Index**: (deck_id) for fast deck card listing
- **Partial index**: Exclude soft-deleted cards
- **Text search**: Future enhancement - add GIN index for full-text search
- **Batch operations**: Import uses JDBC batch insert (1000 cards/batch)

### Future Enhancements (Post-MVP)
- Rich text support (HTML)
- Image/audio attachments
- Cloze deletion type
- Multiple choice type
- Tags and categories

---

## Entity: CardBoxPosition

### Database Table
- **Table name**: `card_box_position`
- **Description**: SRS state per user per card - tracks current box, interval, due date

### Fields Specifications

| Field | Type | Constraints | Default | Description |
|-------|------|-------------|---------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | gen_random_uuid() | Primary key |
| card_id | UUID | NOT NULL, FK → cards(id) | - | Card reference |
| user_id | UUID | NOT NULL, FK → users(id) | - | User reference |
| current_box | INTEGER | NOT NULL, CHECK 1-7 | 1 | Current box (1-7) |
| interval_days | INTEGER | NOT NULL, CHECK >= 1 | 1 | Current interval in days |
| due_date | DATE | NOT NULL | CURRENT_DATE | Next review due date |
| review_count | INTEGER | NOT NULL, CHECK >= 0 | 0 | Total review count |
| lapse_count | INTEGER | NOT NULL, CHECK >= 0 | 0 | Times card forgotten |
| last_reviewed_at | TIMESTAMP | NULL | NULL | Last review timestamp |
| created_at | TIMESTAMP | NOT NULL | CURRENT_TIMESTAMP | Creation time |
| updated_at | TIMESTAMP | NOT NULL | CURRENT_TIMESTAMP | Last update time |
| deleted_at | TIMESTAMP | NULL | NULL | Soft delete timestamp |

### Relationships
- **card**: OneToOne → Card (LAZY fetch, required)
  - JoinColumn: card_id
  - Cascade: NONE
  - Foreign key: ON DELETE CASCADE

- **user**: ManyToOne → User (LAZY fetch, required)
  - JoinColumn: user_id
  - Cascade: NONE
  - Foreign key: ON DELETE CASCADE

### Indexes
- **PRIMARY**: id (UUID)
- **UNIQUE**: idx_card_box_position_user_card ON (user_id, card_id)
- **INDEX**: idx_card_box_user_due ON (user_id, due_date, current_box) WHERE deleted_at IS NULL
- **INDEX**: idx_card_box_user_box ON (user_id, current_box)
- **INDEX**: idx_card_box_new ON (user_id, card_id) WHERE review_count = 0

### Validation Rules
- **current_box**:
  - NOT NULL
  - Range: 1 to 7 (7-box system)
  - CHECK constraint: current_box BETWEEN 1 AND 7

- **interval_days**:
  - NOT NULL
  - Minimum: 1 day
  - CHECK constraint: interval_days >= 1
  - Calculated based on current_box

- **due_date**:
  - NOT NULL
  - Calculated: CURRENT_DATE + interval_days
  - Can be in past (overdue cards)

- **review_count**:
  - NOT NULL
  - Minimum: 0
  - Incremented on each review

- **lapse_count**:
  - NOT NULL
  - Minimum: 0
  - Incremented when rating = AGAIN

### Business Rules
- **BR-BOX-001**: One row per user per card (unique constraint)
- **BR-BOX-002**: Fixed intervals per box:
  - Box 1: 1 day
  - Box 2: 3 days
  - Box 3: 7 days
  - Box 4: 14 days
  - Box 5: 30 days (mature cards)
  - Box 6: 60 days
  - Box 7: 120 days

- **BR-BOX-003**: Rating effects on box position:
  - AGAIN: Apply forgotten_card_action (MOVE_TO_BOX_1 | MOVE_DOWN_N_BOXES | STAY_IN_BOX)
  - HARD: Stay in box, interval ÷ 2
  - GOOD: Move to next box, standard interval
  - EASY: Move to next box, interval × 2

- **BR-BOX-004**: New cards start at Box 1, interval 1 day, due_date = today
- **BR-BOX-005**: Mature cards are in Box 5+ (interval >= 30 days)
- **BR-BOX-006**: Due cards: due_date <= CURRENT_DATE
- **BR-BOX-007**: Review count = 0 indicates new card
- **BR-BOX-008**: Lapse count tracks forgotten frequency (for analytics)

### Audit Fields Strategy
- **created_at**: Auto-set on INSERT
- **updated_at**: Auto-updated on each review
- Uses @EntityListeners(AuditingEntityListener.class)

### Soft Delete Strategy
- **Field**: deleted_at (TIMESTAMP NULL)
- **Usage**: Set when card is deleted (keeps SRS history)
- **Query filtering**: WHERE deleted_at IS NULL
- **Cascade**: Does NOT cascade (preserves history)

### Performance Considerations
- **CRITICAL INDEX**: (user_id, due_date, current_box) - primary review query
  - Query: `WHERE user_id = ? AND due_date <= ? ORDER BY due_date, current_box`
  - Reduces query time from seconds to milliseconds
- **Partial index**: Only active records (deleted_at IS NULL)
- **Composite unique**: (user_id, card_id) ensures one position per user per card
- **Batch updates**: Review session updates in single transaction
- **Query optimization**: Use LIMIT to fetch cards in batches (100-200 at a time)

### SRS Algorithm Implementation
Implemented via business methods (NOT in database):
- calculateNextBox(rating, settings)
- calculateInterval(box)
- updateAfterReview(rating, settings)
- isDue()
- isNew()
- isMature()

---

## Entity: ReviewLog

### Database Table
- **Table name**: `review_logs`
- **Description**: Immutable audit log of all card reviews for analytics and undo

### Fields Specifications

| Field | Type | Constraints | Default | Description |
|-------|------|-------------|---------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | gen_random_uuid() | Primary key |
| card_id | UUID | NOT NULL, FK → cards(id) | - | Reviewed card |
| user_id | UUID | NOT NULL, FK → users(id) | - | Reviewer |
| rating | VARCHAR(10) | NOT NULL | - | Rating (AGAIN/HARD/GOOD/EASY) |
| previous_box | INTEGER | NOT NULL, CHECK 1-7 | - | Box before review |
| new_box | INTEGER | NOT NULL, CHECK 1-7 | - | Box after review |
| interval_days | INTEGER | NOT NULL, CHECK >= 1 | - | New interval assigned |
| reviewed_at | TIMESTAMP | NOT NULL | CURRENT_TIMESTAMP | Review timestamp |

### Relationships
- **card**: ManyToOne → Card (LAZY fetch, required)
  - JoinColumn: card_id
  - Cascade: NONE
  - Foreign key: ON DELETE CASCADE

- **user**: ManyToOne → User (LAZY fetch, required)
  - JoinColumn: user_id
  - Cascade: NONE
  - Foreign key: ON DELETE CASCADE

### Indexes
- **PRIMARY**: id (UUID)
- **INDEX**: idx_review_logs_user_date ON (user_id, reviewed_at DESC)
- **INDEX**: idx_review_logs_card ON (card_id)
- **INDEX**: idx_review_logs_user_today ON (user_id, reviewed_at) WHERE reviewed_at >= CURRENT_DATE

### Validation Rules
- **rating**:
  - NOT NULL
  - Enum values: AGAIN, HARD, GOOD, EASY
  - Cannot be changed after insert (immutable)

- **previous_box**:
  - NOT NULL
  - Range: 1 to 7
  - CHECK constraint: BETWEEN 1 AND 7

- **new_box**:
  - NOT NULL
  - Range: 1 to 7
  - CHECK constraint: BETWEEN 1 AND 7

- **interval_days**:
  - NOT NULL
  - Minimum: 1
  - CHECK constraint: >= 1

- **reviewed_at**:
  - NOT NULL
  - Defaults to CURRENT_TIMESTAMP
  - Cannot be in future

### Business Rules
- **BR-LOG-001**: Immutable - never update or delete (audit trail)
- **BR-LOG-002**: Created on each review submit
- **BR-LOG-003**: Used for undo last review functionality
- **BR-LOG-004**: Enables analytics: review count per day, rating distribution
- **BR-LOG-005**: Today's reviews: reviewed_at >= CURRENT_DATE
- **BR-LOG-006**: Review history: all logs for a card ordered by reviewed_at DESC
- **BR-LOG-007**: Streak calculation: consecutive days with at least 1 review
- **BR-LOG-008**: Archive old logs (>1 year) to cold storage (future)

### Audit Fields Strategy
- **reviewed_at**: Auto-set on INSERT (immutable)
- No updated_at field (records never updated)
- No @EntityListeners needed (single timestamp)

### Soft Delete Strategy
- **NOT APPLICABLE**: Records are immutable and never deleted
- Keep all logs for audit trail and analytics
- Future: Archive to cold storage after 1 year

### Performance Considerations
- **Composite index**: (user_id, reviewed_at DESC) for recent reviews
- **Partial index**: (user_id, reviewed_at) WHERE reviewed_at >= CURRENT_DATE for today's stats
- **Partition strategy** (future): Partition by reviewed_at monthly for >100K users
- **Archive strategy** (future): Move logs >1 year to cold storage
- **Query optimization**: Use LIMIT for pagination, avoid COUNT(*) on large tables

### Use Cases
1. **Undo last review**: Restore CardBoxPosition to previous_box
2. **Today's stats**: Count reviews WHERE reviewed_at >= CURRENT_DATE
3. **Review history**: List all reviews for a card
4. **Analytics**: Calculate average rating, lapse rate, retention rate
5. **Streak tracking**: Check consecutive days with reviews

---

## Entity: SrsSettings

### Database Table
- **Table name**: `srs_settings`
- **Description**: User-level SRS configuration (1-1 with User)

### Fields Specifications

| Field | Type | Constraints | Default | Description |
|-------|------|-------------|---------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | gen_random_uuid() | Primary key |
| user_id | UUID | UNIQUE, NOT NULL, FK → users(id) | - | Settings owner |
| total_boxes | INTEGER | NOT NULL | 7 | Total boxes (fixed in MVP) |
| review_order | VARCHAR(20) | NOT NULL | 'RANDOM' | Review order strategy |
| notification_enabled | BOOLEAN | NOT NULL | true | Enable notifications |
| notification_time | TIME | NOT NULL | '09:00' | Daily notification time |
| forgotten_card_action | VARCHAR(30) | NOT NULL | 'MOVE_TO_BOX_1' | Action on AGAIN rating |
| move_down_boxes | INTEGER | NOT NULL, CHECK 1-3 | 1 | Boxes to move down on AGAIN |
| new_cards_per_day | INTEGER | NOT NULL, CHECK 1-100 | 20 | Daily new cards limit |
| max_reviews_per_day | INTEGER | NOT NULL, CHECK 1-500 | 200 | Daily reviews limit |
| created_at | TIMESTAMP | NOT NULL | CURRENT_TIMESTAMP | Creation time |
| updated_at | TIMESTAMP | NOT NULL | CURRENT_TIMESTAMP | Last update time |

### Relationships
- **user**: OneToOne → User (LAZY fetch, required)
  - JoinColumn: user_id (UNIQUE)
  - Cascade: NONE
  - Foreign key: ON DELETE CASCADE

### Indexes
- **PRIMARY**: id (UUID)
- **UNIQUE**: user_id (enforces 1-1 relationship)
- **INDEX**: idx_srs_settings_user ON (user_id)

### Validation Rules
- **total_boxes**:
  - NOT NULL
  - Fixed value: 7 (not configurable in MVP)
  - CHECK constraint: total_boxes = 7

- **review_order**:
  - NOT NULL
  - Enum values: ASCENDING, DESCENDING, RANDOM
  - Default: RANDOM
  - ASCENDING: Review Box 1→7
  - DESCENDING: Review Box 7→1
  - RANDOM: Shuffle review order

- **notification_enabled**:
  - NOT NULL
  - Boolean: true/false
  - Default: true

- **notification_time**:
  - NOT NULL
  - TIME format (HH:MM)
  - Default: 09:00
  - Valid range: 00:00 to 23:59

- **forgotten_card_action**:
  - NOT NULL
  - Enum values: MOVE_TO_BOX_1, MOVE_DOWN_N_BOXES, STAY_IN_BOX
  - Default: MOVE_TO_BOX_1
  - Determines behavior when rating = AGAIN

- **move_down_boxes**:
  - NOT NULL
  - Range: 1 to 3
  - CHECK constraint: BETWEEN 1 AND 3
  - Used when forgotten_card_action = MOVE_DOWN_N_BOXES

- **new_cards_per_day**:
  - NOT NULL
  - Range: 1 to 100
  - CHECK constraint: BETWEEN 1 AND 100
  - Default: 20

- **max_reviews_per_day**:
  - NOT NULL
  - Range: 1 to 500
  - CHECK constraint: BETWEEN 1 AND 500
  - Default: 200

### Business Rules
- **BR-SRS-001**: One settings record per user (1-1 relationship)
- **BR-SRS-002**: Auto-created with defaults on user registration
- **BR-SRS-003**: total_boxes = 7 fixed in MVP (future: configurable)
- **BR-SRS-004**: Review order affects display sequence, not SRS algorithm
- **BR-SRS-005**: forgotten_card_action applies to all cards on AGAIN rating
- **BR-SRS-006**: Daily limits prevent user burnout
- **BR-SRS-007**: Notification time respects user timezone
- **BR-SRS-008**: Settings can be reset to defaults via API

### Audit Fields Strategy
- **created_at**: Auto-set on INSERT
- **updated_at**: Auto-updated on UPDATE
- Uses @EntityListeners(AuditingEntityListener.class)

### Soft Delete Strategy
- **NOT APPLICABLE**: Settings deleted when user deleted (CASCADE)
- No soft delete needed (can recreate with defaults)

### Performance Considerations
- **1-1 relationship**: Use UNIQUE constraint on user_id
- **Small table**: ~1 row per user, no indexing needed beyond primary key
- **Caching**: Consider application-level caching (settings rarely change)

### Default Values
Created on user registration with these defaults:
```
total_boxes: 7
review_order: RANDOM
notification_enabled: true
notification_time: 09:00
forgotten_card_action: MOVE_TO_BOX_1
move_down_boxes: 1
new_cards_per_day: 20
max_reviews_per_day: 200
```

---

## Entity: UserStats

### Database Table
- **Table name**: `user_stats`
- **Description**: Denormalized user progress statistics (1-1 with User)

### Fields Specifications

| Field | Type | Constraints | Default | Description |
|-------|------|-------------|---------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | gen_random_uuid() | Primary key |
| user_id | UUID | UNIQUE, NOT NULL, FK → users(id) | - | Stats owner |
| total_cards_learned | INTEGER | NOT NULL, CHECK >= 0 | 0 | Total unique cards reviewed |
| streak_days | INTEGER | NOT NULL, CHECK >= 0 | 0 | Consecutive study days |
| last_study_date | DATE | NULL | NULL | Last review date |
| total_study_time_minutes | INTEGER | NOT NULL, CHECK >= 0 | 0 | Total study time |
| created_at | TIMESTAMP | NOT NULL | CURRENT_TIMESTAMP | Creation time |
| updated_at | TIMESTAMP | NOT NULL | CURRENT_TIMESTAMP | Last update time |

### Relationships
- **user**: OneToOne → User (LAZY fetch, required)
  - JoinColumn: user_id (UNIQUE)
  - Cascade: NONE
  - Foreign key: ON DELETE CASCADE

### Indexes
- **PRIMARY**: id (UUID)
- **UNIQUE**: user_id (enforces 1-1 relationship)
- **INDEX**: idx_user_stats_user ON (user_id)

### Validation Rules
- **total_cards_learned**:
  - NOT NULL
  - Minimum: 0
  - CHECK constraint: >= 0
  - Count of unique cards with review_count > 0

- **streak_days**:
  - NOT NULL
  - Minimum: 0
  - CHECK constraint: >= 0
  - Consecutive days with at least 1 review

- **last_study_date**:
  - Nullable (no reviews yet)
  - DATE format (no time)
  - Updated after each review session

- **total_study_time_minutes**:
  - NOT NULL
  - Minimum: 0
  - CHECK constraint: >= 0
  - Cumulative study time

### Business Rules
- **BR-STATS-001**: One stats record per user (1-1 relationship)
- **BR-STATS-002**: Auto-created with defaults on user registration
- **BR-STATS-003**: Updated synchronously after each review submit
- **BR-STATS-004**: Streak calculation:
  - If last_study_date = today: no change
  - If last_study_date = yesterday: increment streak
  - If last_study_date < yesterday: reset streak to 1
  - If last_study_date = NULL: set streak to 1

- **BR-STATS-005**: total_cards_learned increments only once per card (not per review)
- **BR-STATS-006**: Study time tracked per review session (manual or auto)
- **BR-STATS-007**: Stats can be recalculated from review_logs if corrupted

### Audit Fields Strategy
- **created_at**: Auto-set on INSERT
- **updated_at**: Auto-updated after each review
- Uses @EntityListeners(AuditingEntityListener.class)

### Soft Delete Strategy
- **NOT APPLICABLE**: Stats deleted when user deleted (CASCADE)
- No soft delete needed (can recreate from review_logs)

### Performance Considerations
- **Denormalized**: Avoids expensive COUNT queries on review_logs
- **Real-time updates**: Updated in same transaction as review submit
- **Rollback support**: If review submit fails, stats not updated
- **Recalculation**: Background job can recalculate from review_logs if needed

### Update Mechanism
Triggered by: CardReviewedEvent (domain event after review submit)
Update logic:
1. Increment total_cards_learned (if card.review_count = 1)
2. Update streak based on last_study_date
3. Set last_study_date = today
4. Increment total_study_time_minutes (if tracked)

---

## Entity: FolderStats

### Database Table
- **Table name**: `folder_stats`
- **Description**: Cached folder statistics (recursive) - denormalized for performance

### Fields Specifications

| Field | Type | Constraints | Default | Description |
|-------|------|-------------|---------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | gen_random_uuid() | Primary key |
| folder_id | UUID | NOT NULL, FK → folders(id) | - | Folder reference |
| user_id | UUID | NOT NULL, FK → users(id) | - | User reference |
| total_cards_count | INTEGER | NOT NULL, CHECK >= 0 | 0 | Total cards (recursive) |
| due_cards_count | INTEGER | NOT NULL, CHECK >= 0 | 0 | Due cards (recursive) |
| new_cards_count | INTEGER | NOT NULL, CHECK >= 0 | 0 | New cards (recursive) |
| mature_cards_count | INTEGER | NOT NULL, CHECK >= 0 | 0 | Mature cards (Box >= 5) |
| last_computed_at | TIMESTAMP | NOT NULL | CURRENT_TIMESTAMP | Last calculation time |

### Relationships
- **folder**: ManyToOne → Folder (LAZY fetch, required)
  - JoinColumn: folder_id
  - Cascade: NONE
  - Foreign key: ON DELETE CASCADE

- **user**: ManyToOne → User (LAZY fetch, required)
  - JoinColumn: user_id
  - Cascade: NONE
  - Foreign key: ON DELETE CASCADE

### Indexes
- **PRIMARY**: id (UUID)
- **UNIQUE**: (folder_id, user_id) - one stats per folder per user
- **INDEX**: idx_folder_stats_lookup ON (folder_id, user_id, last_computed_at DESC)

### Validation Rules
- **total_cards_count**:
  - NOT NULL
  - Minimum: 0
  - CHECK constraint: >= 0
  - Includes all cards in folder and sub-folders

- **due_cards_count**:
  - NOT NULL
  - Minimum: 0
  - CHECK constraint: >= 0
  - Cards with due_date <= today

- **new_cards_count**:
  - NOT NULL
  - Minimum: 0
  - CHECK constraint: >= 0
  - Cards with review_count = 0

- **mature_cards_count**:
  - NOT NULL
  - Minimum: 0
  - CHECK constraint: >= 0
  - Cards in Box 5, 6, or 7

- **last_computed_at**:
  - NOT NULL
  - Timestamp of last calculation
  - Used for staleness check (TTL = 5 minutes)

### Business Rules
- **BR-FSTATS-001**: Denormalized cache to avoid expensive recursive queries
- **BR-FSTATS-002**: TTL = 5 minutes (max staleness)
- **BR-FSTATS-003**: Staleness check: last_computed_at < NOW() - 5 minutes
- **BR-FSTATS-004**: Invalidation triggers:
  - Card CRUD (create, update, delete)
  - Deck CRUD
  - Folder move/copy/delete
  - Review submit (changes due_cards_count)

- **BR-FSTATS-005**: Invalidation method: DELETE row (recalculate on next request)
- **BR-FSTATS-006**: Calculation is recursive (folder + all descendants)
- **BR-FSTATS-007**: Calculation uses materialized path for efficient queries
- **BR-FSTATS-008**: Async recalculation: background job every 5 minutes
- **BR-FSTATS-009**: On-demand calculation: when user requests folder stats

### Audit Fields Strategy
- **last_computed_at**: Set on each calculation
- No created_at/updated_at (row deleted and recreated)

### Soft Delete Strategy
- **NOT APPLICABLE**: Row deleted when invalidated
- Recalculated on next access

### Performance Considerations
- **Trade-off**: Slightly stale data (max 5 min) for much better performance
- **Recursive calculation**: Uses materialized path to find all descendants
  ```sql
  SELECT SUM(cards_count), SUM(due_cards_count) ...
  FROM folders f
  JOIN decks d ON d.folder_id = f.id
  WHERE f.path LIKE '/parent_id/%' AND f.deleted_at IS NULL
  ```
- **Invalidation cost**: Cheap (DELETE single row)
- **Recalculation cost**: Moderate (recursive query + aggregation)
- **Caching**: Consider Redis cache for frequently accessed folders (future)

### Update Mechanism
**Triggered by:**
- CardCreatedEvent → DELETE stats for deck's folder
- CardReviewedEvent → DELETE stats for deck's folder
- DeckCreatedEvent → DELETE stats for parent folder
- FolderMovedEvent → DELETE stats for old and new parent

**Recalculation:**
1. Check if stats exist and not stale
2. If stale or missing: calculate recursively
3. INSERT new stats row with current timestamp
4. Return calculated stats

**Query optimization:**
```sql
WITH RECURSIVE folder_tree AS (
  SELECT id FROM folders WHERE id = :folderId
  UNION ALL
  SELECT f.id FROM folders f
  JOIN folder_tree ft ON f.parent_folder_id = ft.id
)
SELECT
  COUNT(DISTINCT c.id) as total_cards_count,
  COUNT(DISTINCT CASE WHEN cbp.due_date <= CURRENT_DATE THEN c.id END) as due_cards_count,
  COUNT(DISTINCT CASE WHEN cbp.review_count = 0 THEN c.id END) as new_cards_count,
  COUNT(DISTINCT CASE WHEN cbp.current_box >= 5 THEN c.id END) as mature_cards_count
FROM folder_tree ft
JOIN decks d ON d.folder_id = ft.id AND d.deleted_at IS NULL
JOIN cards c ON c.deck_id = d.id AND c.deleted_at IS NULL
JOIN card_box_position cbp ON cbp.card_id = c.id AND cbp.user_id = :userId
```

---

## Summary

### Entity Relationships Overview

```
User (1) ──┬── (1) SrsSettings
           ├── (1) UserStats
           ├── (N) RefreshTokens
           └── (N) Folders ──┬── (N) Folders (self-reference)
                             └── (N) Decks ──── (N) Cards ──┬── (1) CardBoxPosition
                                                             └── (N) ReviewLogs
FolderStats (N:1) ──> Folders
```

### Critical Indexes (Performance)

**Must-have indexes:**
1. `idx_card_box_user_due` - Review session query (most critical)
2. `idx_folders_path` - Folder tree traversal
3. `idx_users_email` - User login
4. `idx_folder_stats_lookup` - Cached stats lookup

**Important indexes:**
5. `idx_folders_user_parent` - Listing folders
6. `idx_decks_folder_user` - Listing decks in folder
7. `idx_review_logs_user_date` - Analytics queries

### Database Constraints Summary

**CHECK Constraints:**
- Users: email format, password hash length, language/theme enums
- Folders: depth <= 10, path format, name not empty
- Cards: front/back not empty, length <= 5000
- CardBoxPosition: current_box 1-7, interval_days >= 1
- SrsSettings: total_boxes = 7, daily limits ranges
- All stats: counts >= 0

**UNIQUE Constraints:**
- Users: email
- RefreshTokens: token_hash
- Folders: (user_id, parent_folder_id, name)
- Decks: (user_id, folder_id, name)
- CardBoxPosition: (user_id, card_id)
- SrsSettings: user_id
- UserStats: user_id
- FolderStats: (folder_id, user_id)

### Soft Delete Strategy

**Entities with soft delete:**
- User, Folder, Deck, Card, CardBoxPosition

**Entities without soft delete:**
- RefreshToken (revoked_at instead)
- ReviewLog (immutable audit log)
- SrsSettings, UserStats, FolderStats (deleted on cascade)

### Performance Optimizations

1. **Materialized Path**: Fast folder tree queries
2. **Denormalized Stats**: Avoid expensive recursive queries
3. **Partial Indexes**: Exclude soft-deleted records
4. **Composite Indexes**: Multi-column query optimization
5. **Lazy Loading**: Avoid N+1 queries
6. **Batch Operations**: Import/export, async copy
7. **TTL Cache**: 5-minute staleness acceptable for stats

---

**Document Version**: 1.0
**Last Updated**: 2025-01-10
**Status**: ✅ Ready for Implementation

**Next Steps:**
1. Generate JPA entity classes based on these specifications
2. Create Flyway migration scripts
3. Implement Spring Data JPA repositories
4. Write unit tests for validation rules
5. Write integration tests for complex queries

