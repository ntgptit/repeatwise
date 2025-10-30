# UC-012: View Folder Statistics

## 1. Brief Description

User views aggregated statistics for a folder including total decks, cards, due cards, and learning progress. Statistics are calculated recursively for the folder and all its descendants to provide comprehensive overview.

## 2. Actors

- **Primary Actor:** Authenticated User
- **Secondary Actor:** Statistics Service, Cache Service

## 3. Preconditions

- User is authenticated with valid access token
- Target folder exists and belongs to user
- Target folder is not soft-deleted
- User can access folder management interface

## 4. Postconditions

### Success Postconditions

- Statistics displayed to user showing:
  - Total folders (recursive count)
  - Total decks (recursive count)
  - Total cards (recursive count)
  - Due cards today (cards needing review)
  - Learning progress metrics
  - Last updated timestamp (if cached)
- User can make informed decisions about study priorities
- Statistics optionally cached for performance

### Failure Postconditions

- Error message displayed
- User remains on current page
- Statistics not displayed

## 5. Main Success Scenario (Basic Flow)

1. User views folder tree
2. User selects folder to view statistics (e.g., "IELTS Preparation")
3. User initiates view statistics action:
   - Clicks "View Stats" from context menu, OR
   - Clicks statistics icon next to folder name, OR
   - Opens folder details panel
4. System displays Statistics panel/modal showing loading indicator
5. Client sends GET request to statistics API:

   ```http
   GET /api/folders/{folderId}/stats
   ```

6. Backend receives request and validates:
   - User is authenticated (valid JWT token)
   - Folder exists and belongs to user
   - Folder is not soft-deleted
7. System checks statistics cache:
   - If cached and fresh (< 5 minutes old): Return cached data
   - If stale or not cached: Calculate fresh statistics
8. **For fresh calculation:**
   - System queries folder and all descendants using path pattern:

     ```sql
     SELECT id FROM folders
     WHERE (id = ? OR path LIKE ?)
       AND user_id = ?
       AND deleted_at IS NULL
     ```

   - System counts total subfolders:

     ```sql
     SELECT COUNT(*) FROM folders
     WHERE path LIKE ?
       AND user_id = ?
       AND deleted_at IS NULL
     ```

   - System counts total decks in folder tree:

     ```sql
     SELECT COUNT(*) FROM decks
     WHERE folder_id IN (folder_ids)
       AND user_id = ?
       AND deleted_at IS NULL
     ```

   - System counts total cards:

     ```sql
     SELECT COUNT(*) FROM cards
     WHERE deck_id IN (
       SELECT id FROM decks
       WHERE folder_id IN (folder_ids)
     )
     AND deleted_at IS NULL
     ```

   - System counts due cards (cards needing review today):

     ```sql
     SELECT COUNT(*) FROM cards
     WHERE deck_id IN (deck_ids)
       AND due_date <= CURRENT_DATE
       AND deleted_at IS NULL
     ```

   - System calculates learning progress:
     - New cards: Cards never reviewed
     - Learning cards: Cards in learning phase
     - Review cards: Cards in review phase
     - Mastered cards: Cards with high retention
9. System caches calculated statistics (5-minute TTL)
10. System returns 200 OK with statistics:

    ```json
    {
      "folderId": "uuid-123",
      "folderName": "IELTS Preparation",
      "totalFolders": 4,
      "totalDecks": 12,
      "totalCards": 3456,
      "dueCards": 123,
      "newCards": 45,
      "learningCards": 67,
      "reviewCards": 89,
      "masteredCards": 3255,
      "lastUpdatedAt": "2025-01-31T14:30:00Z"
    }
    ```

11. Client receives response
12. UI displays statistics in panel/modal:
    - Summary metrics with icons
    - Progress bars for learning phases
    - Due cards highlighted
    - Last updated timestamp
    - Optional: Mini charts (pie chart, bar chart)
13. User views statistics and understands folder content
14. Optional: User clicks "Refresh" to recalculate statistics
15. Use case ends (success)

## 6. Alternative Flows

### 6a. Folder Not Found

**Trigger:** Step 6 - Folder doesn't exist or was deleted

1. User requests statistics for folder
2. System queries folder by ID
3. No matching active folder found
4. System returns 404 Not Found:

   ```json
   {
     "error": "Folder not found",
     "message": "The folder you're trying to view does not exist"
   }
   ```

5. UI displays error message
6. Use case ends (failure)

### 6b. Empty Folder (No Content)

**Trigger:** Step 8 - Folder has no decks or cards

1. System calculates statistics
2. All counts are zero (no decks, no cards)
3. System returns statistics with all zeros:

   ```json
   {
     "folderId": "uuid-123",
     "totalFolders": 0,
     "totalDecks": 0,
     "totalCards": 0,
     "dueCards": 0,
     "newCards": 0
   }
   ```

4. UI displays "This folder is empty" message
5. UI suggests creating decks
6. Use case ends (success with empty data)

### 6c. Cached Statistics (Fast Path)

**Trigger:** Step 7 - Statistics cached and fresh

1. User requests statistics
2. System checks cache
3. Cache hit: Statistics exist and < 5 minutes old
4. System returns cached statistics immediately (< 50ms)
5. UI displays cached data with "Last updated 2 minutes ago"
6. User can click "Refresh" to force recalculation
7. Use case ends (success with cached data)

### 6d. Stale Cache Refresh

**Trigger:** Step 7 - Statistics cached but stale

1. User requests statistics
2. System checks cache
3. Cache hit but stale (> 5 minutes old)
4. System returns stale data immediately
5. System triggers background refresh asynchronously
6. UI displays stale data with "Refreshing..." indicator
7. When refresh completes, UI updates with fresh data
8. Use case ends (success with progressive update)

### 6e. Session Expired During Request

**Trigger:** Step 6 - Access token expired

1. User requests statistics
2. Token expired (> 15 minutes)
3. Backend returns 401 Unauthorized
4. Client axios interceptor catches 401
5. Client automatically refreshes token (UC-003)
6. Token refresh succeeds
7. Client retries statistics request with new token
8. Request succeeds
9. Continue to step 10 (Main Flow)

### 6f. Network Error

**Trigger:** Step 5 - Network request fails

1. Client sends GET request
2. Network error occurs (no connection, timeout)
3. Request fails before reaching server
4. Client catches network error
5. UI displays error: "Failed to load statistics. Please try again."
6. User can retry by clicking "Refresh"
7. Use case ends (failure)

### 6g. Database Query Timeout

**Trigger:** Step 8 - Query takes too long (> 10 seconds)

1. System starts calculating statistics for very large folder
2. Database queries take > 10 seconds
3. Request times out
4. System returns 504 Gateway Timeout:

   ```json
   {
     "error": "Request timeout",
     "message": "Statistics calculation timed out. Try again or view smaller folders."
   }
   ```

5. UI displays timeout error
6. UI suggests viewing statistics for smaller subfolders
7. Use case ends (failure)

## 7. Special Requirements

### 7.1 Performance

- Response time < 500ms for cached statistics
- Response time < 3 seconds for fresh calculation (< 1000 cards)
- Response time < 10 seconds for large folders (< 10,000 cards)
- Timeout after 10 seconds to prevent long-running queries
- Use database indexes on folder_id, due_date, deleted_at

### 7.2 Caching Strategy

- Cache duration: 5 minutes (balance freshness vs performance)
- Cache key: `folder_stats:{userId}:{folderId}`
- Cache invalidation: On deck/card creation, update, deletion
- Stale-while-revalidate: Show stale data while refreshing
- Cache warming: Pre-calculate stats for frequently viewed folders

### 7.3 Accuracy

- Statistics must be recursive (include all descendants)
- Due cards calculation: Use current date in user's timezone
- Exclude soft-deleted items from all counts
- Learning phases calculated based on SRS algorithm state

### 7.4 Usability

- Clear visual representation of metrics
- Progress bars for learning phases
- Highlight due cards count (user's priority)
- "Last updated" timestamp for cached data
- One-click refresh button
- Responsive layout for different screen sizes

## 8. Technology and Data Variations

### 8.1 Recursive Statistics Query

```sql
-- Get all folder IDs in subtree
WITH RECURSIVE folder_tree AS (
  SELECT id, path FROM folders
  WHERE id = ? AND user_id = ? AND deleted_at IS NULL
  UNION ALL
  SELECT f.id, f.path FROM folders f
  INNER JOIN folder_tree ft ON f.path LIKE CONCAT(ft.path, '%')
  WHERE f.user_id = ? AND f.deleted_at IS NULL
)
-- Count statistics
SELECT
  COUNT(DISTINCT f.id) - 1 AS total_folders, -- Exclude root
  COUNT(DISTINCT d.id) AS total_decks,
  COUNT(DISTINCT c.id) AS total_cards,
  COUNT(DISTINCT CASE WHEN c.due_date <= CURRENT_DATE THEN c.id END) AS due_cards,
  COUNT(DISTINCT CASE WHEN c.review_count = 0 THEN c.id END) AS new_cards
FROM folder_tree ft
LEFT JOIN decks d ON d.folder_id = ft.id AND d.deleted_at IS NULL
LEFT JOIN cards c ON c.deck_id = d.id AND c.deleted_at IS NULL;
```

### 8.2 Caching Implementation

```typescript
interface FolderStats {
  folderId: string;
  folderName: string;
  totalFolders: number;
  totalDecks: number;
  totalCards: number;
  dueCards: number;
  newCards: number;
  learningCards: number;
  reviewCards: number;
  masteredCards: number;
  lastUpdatedAt: Date;
}

async function getFolderStats(
  folderId: string,
  userId: string
): Promise<FolderStats> {
  const cacheKey = `folder_stats:${userId}:${folderId}`;

  // Try cache first
  const cached = await redis.get(cacheKey);
  if (cached) {
    const stats = JSON.parse(cached);
    const age = Date.now() - new Date(stats.lastUpdatedAt).getTime();

    if (age < 5 * 60 * 1000) { // 5 minutes
      return stats; // Fresh cache
    }
  }

  // Calculate fresh statistics
  const stats = await calculateFolderStats(folderId, userId);

  // Cache for 5 minutes
  await redis.setex(cacheKey, 5 * 60, JSON.stringify(stats));

  return stats;
}
```

### 8.3 Cache Invalidation

```typescript
// Invalidate cache when deck/card changes
async function invalidateFolderStatsCache(
  folderId: string,
  userId: string
): Promise<void> {
  // Invalidate this folder
  await redis.del(`folder_stats:${userId}:${folderId}`);

  // Invalidate all ancestor folders
  const ancestors = await getAncestorFolders(folderId);
  for (const ancestor of ancestors) {
    await redis.del(`folder_stats:${userId}:${ancestor.id}`);
  }
}

// Call after deck/card operations
await createDeck(deckData);
await invalidateFolderStatsCache(deckData.folderId, userId);
```

## 9. Frequency of Occurrence

- Expected: 50-200 statistics views per day (MVP phase)
- Peak: 300-500 statistics views per day (post-launch)
- Per user: 5-20 statistics views per day (checking progress)
- Cache hit rate: Expected 80%+ (due to 5-minute TTL)

## 10. Open Issues

- **Real-time updates:** WebSocket for live statistics updates - future
- **Historical trends:** Show statistics over time (charts) - future
- **Comparison mode:** Compare stats across folders - future
- **Export statistics:** Download stats as CSV/PDF - future
- **Advanced filters:** Filter statistics by date range, tags - future

## 11. Related Use Cases

- [UC-007: Create Folder](UC-007-create-folder.md) - Create folders to organize content
- [UC-013: Create Deck](UC-013-create-deck.md) - Create decks that contribute to stats
- [UC-018: Create Card](UC-018-create-card.md) - Create cards counted in stats
- [UC-023: Review Cards (SRS)](UC-023-review-cards-srs.md) - Review updates due card counts

## 12. Business Rules References

- **BR-STATS-01:** Statistics calculated recursively for all descendants
- **BR-STATS-02:** Due cards = cards with due_date <= today and not deleted
- **BR-STATS-03:** Exclude soft-deleted items from all counts
- **BR-STATS-04:** Cache statistics for 5 minutes to optimize performance

## 13. UI Mockup Notes

### Statistics Panel

```
┌─────────────────────────────────────────┐
│ 📊 Folder Statistics                 [×]│
├─────────────────────────────────────────┤
│                                         │
│ IELTS Preparation                       │
│                                         │
│ 📁 Folders:  4                          │
│ 📚 Decks:    12                         │
│ 🃏 Cards:    3,456                      │
│                                         │
│ ⏰ Due Today:  123 cards                │
│                                         │
│ Learning Progress:                      │
│ ┌─────────────────────────────────┐    │
│ │ New      45  [█░░░░░░]  1.3%    │    │
│ │ Learning 67  [██░░░░░]  1.9%    │    │
│ │ Review   89  [███░░░░]  2.6%    │    │
│ │ Mastered 3255[████████] 94.2%   │    │
│ └─────────────────────────────────┘    │
│                                         │
│ Last updated: 2 minutes ago             │
│                              [Refresh]  │
└─────────────────────────────────────────┘
```

### Inline Stats Display

```
Folders
├─ 📁 IELTS Preparation (4 folders, 12 decks, 123 due) ← Inline stats
│  ├─ Listening (3 decks, 45 due)
│  └─ Reading (5 decks, 78 due)
└─ 📁 Japanese (2 folders, 8 decks, 34 due)
```

### Stats Card (Dashboard)

```
┌─────────────────────────────────────────┐
│ 📊 IELTS Preparation                    │
├─────────────────────────────────────────┤
│                                         │
│   3,456 cards       123 due today       │
│   12 decks          94% mastered        │
│                                         │
│   [View Details]                        │
└─────────────────────────────────────────┘
```

## 14. API Endpoint

```http
GET /api/folders/{folderId}/stats
```

**Request Headers:**

```http
Authorization: Bearer <access-token>
```

**Query Parameters (Optional):**

```
?refresh=true  // Force cache refresh
```

**Success Response (200 OK):**

```json
{
  "folderId": "550e8400-e29b-41d4-a716-446655440000",
  "folderName": "IELTS Preparation",
  "totalFolders": 4,
  "totalDecks": 12,
  "totalCards": 3456,
  "dueCards": 123,
  "newCards": 45,
  "learningCards": 67,
  "reviewCards": 89,
  "masteredCards": 3255,
  "completionRate": 94.2,
  "lastUpdatedAt": "2025-01-31T14:30:00Z",
  "cached": true
}
```

**Error Responses:**

404 Not Found:

```json
{
  "error": "Folder not found",
  "message": "The folder you're trying to view does not exist"
}
```

403 Forbidden:

```json
{
  "error": "Access denied",
  "message": "You don't have permission to view this folder's statistics"
}
```

401 Unauthorized:

```json
{
  "error": "Unauthorized",
  "message": "Authentication required"
}
```

504 Gateway Timeout:

```json
{
  "error": "Request timeout",
  "message": "Statistics calculation timed out. Try viewing smaller folders."
}
```

## 15. Test Cases

### TC-012-001: View Statistics for Folder with Content

- **Given:** User has folder "IELTS" with 12 decks and 3456 cards
- **When:** User views statistics for "IELTS"
- **Then:** Statistics displayed showing correct counts

### TC-012-002: View Statistics for Empty Folder

- **Given:** User has empty folder "New Folder" with no decks
- **When:** User views statistics
- **Then:** All counts show 0, message "This folder is empty"

### TC-012-003: Recursive Statistics Calculation

- **Given:** User has folder "Languages" with 3 subfolders containing 20 decks total
- **When:** User views statistics for "Languages"
- **Then:** Statistics show 3 subfolders and 20 decks (recursive)

### TC-012-004: Due Cards Count Accurate

- **Given:** User has 123 cards with due_date <= today
- **When:** User views folder statistics
- **Then:** Due cards count shows 123

### TC-012-005: Cached Statistics Returned Fast

- **Given:** User viewed statistics 2 minutes ago (still cached)
- **When:** User views statistics again
- **Then:** Response < 50ms, shows "Last updated 2 minutes ago"

### TC-012-006: Force Refresh Clears Cache

- **Given:** User has cached statistics
- **When:** User clicks "Refresh" button
- **Then:** Fresh statistics calculated, cache updated

### TC-012-007: Stale Cache Auto-Refresh

- **Given:** User has cached statistics 6 minutes old (stale)
- **When:** User views statistics
- **Then:** Stale data shown immediately, fresh data loaded in background

### TC-012-008: Folder Not Found

- **Given:** User attempts to view statistics for deleted folder
- **When:** Request is sent
- **Then:** 404 error with message "Folder does not exist"

### TC-012-009: Session Expired During Request

- **Given:** User's token expires
- **When:** User views statistics
- **Then:** Token auto-refreshed, statistics loaded successfully

### TC-012-010: Exclude Soft-Deleted Items

- **Given:** Folder has 10 active decks and 3 soft-deleted decks
- **When:** User views statistics
- **Then:** Statistics show only 10 active decks (excludes deleted)

### TC-012-011: Learning Progress Percentages

- **Given:** User has 100 cards: 10 new, 20 learning, 30 review, 40 mastered
- **When:** User views statistics
- **Then:** Percentages shown: 10%, 20%, 30%, 40%

### TC-012-012: Large Folder Performance

- **Given:** User has folder with 10,000 cards
- **When:** User views statistics
- **Then:** Response completes within 10 seconds

## 16. Database Queries

### Optimized Statistics Query

```sql
-- Single query to get all statistics
SELECT
  (SELECT COUNT(*) FROM folders
   WHERE path LIKE CONCAT((SELECT path FROM folders WHERE id = ?), '%')
     AND user_id = ? AND deleted_at IS NULL) - 1 AS total_folders,

  (SELECT COUNT(*) FROM decks d
   WHERE d.folder_id IN (
     SELECT id FROM folders
     WHERE (id = ? OR path LIKE CONCAT((SELECT path FROM folders WHERE id = ?), '%'))
       AND user_id = ? AND deleted_at IS NULL
   ) AND d.deleted_at IS NULL) AS total_decks,

  (SELECT COUNT(*) FROM cards c
   INNER JOIN decks d ON c.deck_id = d.id
   WHERE d.folder_id IN (folder_ids) AND c.deleted_at IS NULL) AS total_cards,

  (SELECT COUNT(*) FROM cards c
   INNER JOIN decks d ON c.deck_id = d.id
   WHERE d.folder_id IN (folder_ids)
     AND c.due_date <= CURRENT_DATE
     AND c.deleted_at IS NULL) AS due_cards;
```

### Cache Table (Optional)

```sql
CREATE TABLE folder_stats_cache (
  folder_id UUID PRIMARY KEY REFERENCES folders(id) ON DELETE CASCADE,
  user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  total_folders INT NOT NULL,
  total_decks INT NOT NULL,
  total_cards INT NOT NULL,
  due_cards INT NOT NULL,
  new_cards INT NOT NULL,
  learning_cards INT NOT NULL,
  review_cards INT NOT NULL,
  mastered_cards INT NOT NULL,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Index for fast lookup
CREATE INDEX idx_folder_stats_user ON folder_stats_cache(user_id);
CREATE INDEX idx_folder_stats_updated ON folder_stats_cache(updated_at);
```

## 17. Future Enhancements

- **Historical trends:** Chart showing statistics over time (daily/weekly/monthly)
- **Comparison mode:** Compare multiple folders side-by-side
- **Drill-down:** Click metric to see detailed breakdown
- **Real-time updates:** WebSocket for live statistics updates
- **Export:** Download statistics as CSV, PDF, or Excel
- **Filters:** Filter by date range, card status, tags
- **Alerts:** Notify when due cards exceed threshold
- **Heatmap:** Visual heatmap showing activity by folder
- **Leaderboard:** Compare progress with other users (optional social feature)
- **Forecasting:** Predict when all cards will be mastered
