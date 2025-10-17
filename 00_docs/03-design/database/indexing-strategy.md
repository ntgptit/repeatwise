# Indexing Strategy - RepeatWise MVP

## 1. Overview

Indexing strategy cho RepeatWise MVP được thiết kế để tối ưu hóa **critical query paths** trong Spaced Repetition System với 7-box algorithm. Chiến lược tập trung vào:

- **Review session queries** (most critical - sub-50ms target)
- **Folder tree traversal** (hierarchical queries)
- **Statistics calculation** (recursive aggregation)
- **Authentication** (JWT + refresh token)

**Database**: PostgreSQL 15+
**Approach**: Index theo query patterns thực tế, không index tất cả foreign keys

---

## 2. Critical Indexes ⭐ (Performance-Critical)

### 2.1 **idx_card_box_user_due** - MOST IMPORTANT INDEX

**Purpose**: Optimize review session queries (most frequent operation)

```sql
CREATE INDEX idx_card_box_user_due ON card_box_position(
    user_id,
    due_date,
    current_box
) WHERE deleted_at IS NULL;
```

**Query Pattern**:
```sql
-- Get due cards for review (executed every review session)
SELECT cbp.*, c.*, d.*
FROM card_box_position cbp
JOIN cards c ON cbp.card_id = c.id
JOIN decks d ON c.deck_id = d.id
WHERE cbp.user_id = :userId
  AND cbp.due_date <= CURRENT_DATE
  AND cbp.deleted_at IS NULL
ORDER BY cbp.due_date ASC, cbp.current_box ASC
LIMIT 200;
```

**Expected Performance**: < 50ms (NFR requirement)
**Index Selectivity**: Very high (user_id + due_date filter ~99%)
**Priority**: **P0 - Must have**

---

### 2.2 **idx_folders_path** - Folder Tree Queries

**Purpose**: Fast descendant folder queries (recursive tree traversal)

```sql
CREATE INDEX idx_folders_path ON folders(user_id, path)
WHERE deleted_at IS NULL;
```

**Query Pattern**:
```sql
-- Get all descendants of a folder
SELECT *
FROM folders
WHERE user_id = :userId
  AND path LIKE '/parent_uuid/%'
  AND deleted_at IS NULL;
```

**Expected Performance**: < 100ms for tree depth ≤ 10
**Use Cases**: Folder statistics, copy operations, breadcrumb navigation

---

### 2.3 **idx_folders_parent** - Folder Hierarchy

**Purpose**: Get immediate children of a folder

```sql
CREATE INDEX idx_folders_parent ON folders(user_id, parent_folder_id)
WHERE deleted_at IS NULL;
```

**Query Pattern**:
```sql
-- Get children folders
SELECT *
FROM folders
WHERE user_id = :userId
  AND parent_folder_id = :parentId
  AND deleted_at IS NULL
ORDER BY name;
```

**Expected Performance**: < 20ms
**Use Cases**: Folder tree UI, navigation

---

### 2.4 **idx_refresh_tokens_user** - JWT Refresh

**Purpose**: Fast refresh token lookup (authentication critical path)

```sql
CREATE INDEX idx_refresh_tokens_user ON refresh_tokens(user_id)
WHERE revoked_at IS NULL AND expires_at > NOW();
```

**Query Pattern**:
```sql
-- Validate refresh token
SELECT *
FROM refresh_tokens
WHERE user_id = :userId
  AND token_hash = :tokenHash
  AND revoked_at IS NULL
  AND expires_at > NOW();
```

**Expected Performance**: < 10ms
**Priority**: **P0 - Authentication critical**

---

## 3. Primary & Unique Indexes

### 3.1 Primary Keys (Automatic)

```sql
-- Auto-created by PostgreSQL for PRIMARY KEY constraints
PRIMARY KEY (id) -- All tables: users, folders, decks, cards, etc.
```

### 3.2 Unique Indexes

```sql
-- User email uniqueness
CREATE UNIQUE INDEX idx_users_email ON users(email);

-- Refresh token hash uniqueness
CREATE UNIQUE INDEX idx_refresh_tokens_hash ON refresh_tokens(token_hash);

-- Folder name uniqueness within parent (business rule)
CREATE UNIQUE INDEX idx_folders_name_parent
ON folders(user_id, parent_folder_id, name)
WHERE deleted_at IS NULL;

-- Deck name uniqueness within folder (business rule)
CREATE UNIQUE INDEX idx_decks_name_folder
ON decks(user_id, folder_id, name)
WHERE deleted_at IS NULL;

-- Card box position uniqueness (one per user per card)
CREATE UNIQUE INDEX idx_card_box_position_user_card
ON card_box_position(user_id, card_id);
```

---

## 4. Foreign Key Indexes

### 4.1 User-Related Indexes

```sql
-- SRS settings per user (1-1 relationship)
CREATE INDEX idx_srs_settings_user ON srs_settings(user_id);

-- User statistics (1-1 relationship)
CREATE INDEX idx_user_stats_user ON user_stats(user_id);

-- Folders owned by user
CREATE INDEX idx_folders_user ON folders(user_id) WHERE deleted_at IS NULL;

-- Decks owned by user
CREATE INDEX idx_decks_user ON decks(user_id) WHERE deleted_at IS NULL;
```

### 4.2 Folder-Related Indexes

```sql
-- Decks in folder
CREATE INDEX idx_decks_folder ON decks(folder_id) WHERE deleted_at IS NULL;

-- Folder stats cache
CREATE INDEX idx_folder_stats_folder ON folder_stats(folder_id, user_id);
```

### 4.3 Deck-Related Indexes

```sql
-- Cards in deck
CREATE INDEX idx_cards_deck ON cards(deck_id) WHERE deleted_at IS NULL;
```

### 4.4 Card-Related Indexes

```sql
-- Card box position (SRS state)
CREATE INDEX idx_card_box_position_card ON card_box_position(card_id);

-- Review logs for card
CREATE INDEX idx_review_logs_card ON review_logs(card_id);
```

---

## 5. Query-Specific Indexes

### 5.1 Review Session Queries

```sql
-- Due cards by folder scope (for review session with folder filter)
CREATE INDEX idx_card_box_deck_folder ON card_box_position(deck_id, due_date)
WHERE deleted_at IS NULL;

-- Due cards count (for statistics)
CREATE INDEX idx_card_box_user_due_count ON card_box_position(user_id, due_date)
WHERE deleted_at IS NULL;
```

**Query Pattern**:
```sql
-- Get due cards in folder scope
SELECT cbp.*, c.*, d.*
FROM card_box_position cbp
JOIN cards c ON cbp.card_id = c.id
JOIN decks d ON c.deck_id = d.id
JOIN folders f ON d.folder_id = f.id
WHERE cbp.user_id = :userId
  AND cbp.due_date <= CURRENT_DATE
  AND (f.id = :folderId OR f.path LIKE :pathPattern)
ORDER BY cbp.due_date, cbp.current_box
LIMIT 200;
```

---

### 5.2 Box Distribution Queries

```sql
-- Box distribution statistics
CREATE INDEX idx_card_box_user_box ON card_box_position(user_id, current_box);
```

**Query Pattern**:
```sql
-- Count cards by box (for statistics chart)
SELECT current_box, COUNT(*)
FROM card_box_position
WHERE user_id = :userId
GROUP BY current_box
ORDER BY current_box;
```

**Expected Performance**: < 100ms

---

### 5.3 Review Logs (Analytics)

```sql
-- Review logs by user and date
CREATE INDEX idx_review_logs_user_date ON review_logs(user_id, reviewed_at DESC);

-- Review logs for today's stats
CREATE INDEX idx_review_logs_user_today ON review_logs(user_id, reviewed_at)
WHERE reviewed_at >= CURRENT_DATE;
```

**Query Pattern**:
```sql
-- Count today's reviews
SELECT COUNT(*)
FROM review_logs
WHERE user_id = :userId
  AND reviewed_at >= CURRENT_DATE;
```

---

### 5.4 Folder Statistics Cache

```sql
-- Folder stats lookup (cache table)
CREATE INDEX idx_folder_stats_lookup ON folder_stats(folder_id, user_id, last_computed_at DESC);
```

**Query Pattern**:
```sql
-- Check cached folder stats (TTL = 5 minutes)
SELECT *
FROM folder_stats
WHERE folder_id = :folderId
  AND user_id = :userId
  AND last_computed_at > NOW() - INTERVAL '5 minutes'
ORDER BY last_computed_at DESC
LIMIT 1;
```

---

## 6. Partial Indexes (Filtered Indexes)

### 6.1 Soft Delete Filters

**Why**: Exclude deleted records from indexes to improve performance

```sql
-- Active folders only
CREATE INDEX idx_folders_active ON folders(user_id, name)
WHERE deleted_at IS NULL;

-- Active decks only
CREATE INDEX idx_decks_active ON decks(user_id, folder_id, name)
WHERE deleted_at IS NULL;

-- Active cards only
CREATE INDEX idx_cards_active ON cards(deck_id)
WHERE deleted_at IS NULL;
```

**Benefit**: ~30% index size reduction + faster queries

---

### 6.2 Refresh Token Status

```sql
-- Active (non-revoked, non-expired) refresh tokens only
CREATE INDEX idx_refresh_tokens_active ON refresh_tokens(user_id, expires_at)
WHERE revoked_at IS NULL AND expires_at > NOW();
```

**Benefit**: Only index valid tokens (cleanup happens daily)

---

### 6.3 New Cards Filter

```sql
-- New cards (never reviewed) for daily limit
CREATE INDEX idx_card_box_new ON card_box_position(user_id, card_id)
WHERE review_count = 0;
```

**Query Pattern**:
```sql
-- Get new cards up to daily limit
SELECT cbp.*, c.*
FROM card_box_position cbp
JOIN cards c ON cbp.card_id = c.id
WHERE cbp.user_id = :userId
  AND cbp.review_count = 0
LIMIT :newCardsPerDay;
```

---

## 7. Composite Indexes (Multi-Column)

### 7.1 Covering Index for Due Cards

```sql
-- Covering index to avoid table lookups
CREATE INDEX idx_card_box_covering ON card_box_position(
    user_id,
    due_date,
    current_box,
    card_id,
    interval_days,
    review_count
) WHERE deleted_at IS NULL;
```

**Benefit**: Index-only scan (no heap access needed)
**Trade-off**: Larger index size vs faster queries

---

### 7.2 Folder Stats Composite

```sql
-- Composite for folder statistics queries
CREATE INDEX idx_folder_stats_composite ON folder_stats(
    folder_id,
    user_id,
    last_computed_at DESC
);
```

---

## 8. Performance Benchmarks

### 8.1 Critical Query Performance Targets

| Query | Target | Index Used | Status |
|-------|--------|------------|--------|
| Get due cards (review session start) | < 50ms | idx_card_box_user_due | ✅ P0 |
| Folder tree load (all children) | < 100ms | idx_folders_path | ✅ P0 |
| Refresh token validation | < 10ms | idx_refresh_tokens_user | ✅ P0 |
| User login (email lookup) | < 10ms | idx_users_email | ✅ P0 |
| Box distribution stats | < 100ms | idx_card_box_user_box | ✅ P1 |
| Folder statistics (cached) | < 20ms | idx_folder_stats_lookup | ✅ P1 |
| Review log count (today) | < 50ms | idx_review_logs_user_today | ✅ P1 |

---

### 8.2 Query Execution Plans

**Example: Due Cards Query**

```sql
EXPLAIN ANALYZE
SELECT cbp.*, c.front, c.back, d.name AS deck_name
FROM card_box_position cbp
JOIN cards c ON cbp.card_id = c.id
JOIN decks d ON c.deck_id = d.id
WHERE cbp.user_id = 'user-uuid'
  AND cbp.due_date <= CURRENT_DATE
  AND cbp.deleted_at IS NULL
ORDER BY cbp.due_date, cbp.current_box
LIMIT 200;

-- Expected plan:
-- Index Scan using idx_card_box_user_due on card_box_position
-- -> Nested Loop (Join with cards)
-- -> Nested Loop (Join with decks)
-- Total cost: ~0.50..5.00 rows=100 (actual time: 0.100..0.500 ms)
```

---

## 9. Index Maintenance Strategy

### 9.1 Regular Maintenance

```sql
-- Update statistics weekly (Sunday 2 AM)
ANALYZE users, folders, decks, cards, card_box_position, review_logs;

-- Reindex monthly (first Sunday 3 AM)
REINDEX TABLE card_box_position;
REINDEX TABLE review_logs;

-- Vacuum weekly (Sunday 4 AM)
VACUUM ANALYZE card_box_position;
VACUUM ANALYZE review_logs;
```

### 9.2 Monitoring Index Usage

```sql
-- Check index usage statistics
SELECT
    schemaname,
    tablename,
    indexname,
    idx_scan AS index_scans,
    idx_tup_read AS tuples_read,
    idx_tup_fetch AS tuples_fetched
FROM pg_stat_user_indexes
WHERE schemaname = 'public'
ORDER BY idx_scan DESC;

-- Check index size
SELECT
    tablename,
    indexname,
    pg_size_pretty(pg_relation_size(indexrelid)) AS index_size
FROM pg_stat_user_indexes
WHERE schemaname = 'public'
ORDER BY pg_relation_size(indexrelid) DESC;
```

### 9.3 Unused Index Detection

```sql
-- Find unused indexes (idx_scan = 0 after 30 days)
SELECT
    schemaname,
    tablename,
    indexname,
    idx_scan,
    pg_size_pretty(pg_relation_size(indexrelid)) AS index_size
FROM pg_stat_user_indexes
WHERE schemaname = 'public'
  AND idx_scan = 0
  AND indexrelid NOT IN (
      SELECT indexrelid FROM pg_index WHERE indisunique
  )
ORDER BY pg_relation_size(indexrelid) DESC;
```

---

## 10. Index Optimization Guidelines

### 10.1 Index Selection Criteria

✅ **Create index when**:
- Column used in WHERE clause frequently (>1000 queries/day)
- Column used in JOIN conditions
- Column used in ORDER BY (sort operations)
- High cardinality column (many distinct values)
- Query execution time > 100ms without index

❌ **Don't create index when**:
- Table has < 1000 rows (full scan faster)
- Low cardinality column (few distinct values)
- Column rarely queried
- Write-heavy table (indexes slow down writes)

### 10.2 Column Order in Composite Indexes

**Rule**: High selectivity columns first

```sql
-- GOOD: user_id (high selectivity) → due_date (medium) → current_box (low)
CREATE INDEX idx_good ON card_box_position(user_id, due_date, current_box);

-- BAD: current_box (low selectivity) first
CREATE INDEX idx_bad ON card_box_position(current_box, due_date, user_id);
```

### 10.3 Index Size Considerations

**Trade-offs**:
- More indexes = faster reads, slower writes
- Larger indexes = more disk space, slower cache
- Covering indexes = faster queries, larger size

**MVP Strategy**: Prioritize read performance (review sessions critical)

---

## 11. Migration Strategy

### 11.1 Initial Index Creation

```sql
-- Phase 1: Create critical indexes (P0) - Deploy with schema
CREATE UNIQUE INDEX idx_users_email ON users(email);
CREATE INDEX idx_card_box_user_due ON card_box_position(user_id, due_date, current_box) WHERE deleted_at IS NULL;
CREATE INDEX idx_folders_path ON folders(user_id, path) WHERE deleted_at IS NULL;
CREATE INDEX idx_refresh_tokens_user ON refresh_tokens(user_id) WHERE revoked_at IS NULL;

-- Phase 2: Create supporting indexes (P1) - Deploy after initial load
CREATE INDEX idx_folders_parent ON folders(user_id, parent_folder_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_decks_folder ON decks(folder_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_cards_deck ON cards(deck_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_review_logs_user_date ON review_logs(user_id, reviewed_at DESC);
```

### 11.2 Monitoring After Deployment

```sql
-- Day 1-7: Monitor index usage
-- Drop unused indexes
-- Add missing indexes based on slow query log
-- Optimize based on actual query patterns
```

---

## 12. Summary

### 12.1 Index Inventory

**Total Indexes**: ~20 indexes

**Breakdown**:
- Primary keys: 10 (automatic)
- Unique indexes: 5
- Foreign key indexes: 8
- Query-specific indexes: 7
- Partial indexes: 6

**Storage Overhead**: Estimated ~30% of table size (acceptable for read-heavy workload)

### 12.2 Critical Success Factors

✅ **idx_card_box_user_due** - Review session performance (< 50ms)
✅ **idx_folders_path** - Folder tree navigation (< 100ms)
✅ **idx_refresh_tokens_user** - Authentication speed (< 10ms)
✅ **Partial indexes with deleted_at IS NULL** - Exclude soft-deleted records

### 12.3 Future Optimizations

**When to consider** (>10,000 active users):
- Partition card_box_position table by user_id range
- Add materialized views for dashboard statistics
- Implement Redis caching for hot data (due cards count)
- Add read replicas for analytics queries

---

**Document Version**: 2.0 (MVP)
**Last Updated**: 2025-01-10
**Status**: Ready for Implementation
