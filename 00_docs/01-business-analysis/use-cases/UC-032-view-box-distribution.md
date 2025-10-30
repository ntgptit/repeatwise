# UC-032: View Box Distribution

## 1. Brief Description

User views the distribution of cards across SRS boxes (e.g., Box 1..7) for a selected scope (overall/all cards, by folder, or by deck). Distribution is displayed as counts per box and optionally as a bar chart or list.

## 2. Actors

- **Primary Actor:** Authenticated User
- **Secondary Actor:** Statistics Service

## 3. Preconditions

- User is authenticated with valid access token
- User has access to Statistics or Dashboard page
- Cards exist in the selected scope (or scope is empty)

## 4. Postconditions

### Success Postconditions

- Box distribution displayed for selected scope
- Counts shown for each box (Box 1 through total_boxes)
- Total count displayed
- User can view progress and backlog distribution

### Failure Postconditions

- Error message displayed
- No distribution data shown
- User remains on statistics page

## 5. Main Success Scenario (Basic Flow)

1. User navigates to Statistics > Box Distribution page
2. System displays scope selector:
   - Options: "All Cards", "Folder", "Deck"
   - Default: "All Cards"
3. User selects scope (e.g., "All Cards")
4. Client sends GET request to box distribution endpoint:
   ```
   GET /api/stats/box-distribution?scopeType=ALL
   ```
5. Backend validates request:
   - User is authenticated
   - Scope type is valid (ALL, FOLDER, DECK)
   - If scopeType is FOLDER or DECK, scopeId must be provided
6. System retrieves user's total_boxes setting:
   ```sql
   SELECT total_boxes
   FROM srs_settings
   WHERE user_id = ?
   ```
7. System queries cards and aggregates by current_box:
   ```sql
   SELECT current_box, COUNT(*) as count
   FROM cards
   WHERE user_id = ?
     AND deleted_at IS NULL
   GROUP BY current_box
   ORDER BY current_box ASC
   ```
8. System builds distribution array:
   - Initialize array with zeros: [0, 0, 0, 0, 0, 0, 0] (for 7 boxes)
   - Fill counts from query results
   - Example: [120, 300, 450, 230, 90, 40, 10]
9. System calculates total:
   ```sql
   SELECT COUNT(*) as total
   FROM cards
   WHERE user_id = ?
     AND deleted_at IS NULL
   ```
10. System returns 200 OK with distribution:
    ```json
    {
      "totalBoxes": 7,
      "counts": [120, 300, 450, 230, 90, 40, 10],
      "total": 1240,
      "scopeType": "ALL",
      "scopeId": null
    }
    ```
11. Client receives response
12. Client displays box distribution:
    - Bar chart: Horizontal bars for each box
    - List view: Box number and count for each box
    - Total count displayed
    - Percentage shown for each box (optional)
13. User views distribution and understands card distribution across boxes

## 6. Alternative Flows

### 6a. Empty Scope

**Trigger:** Step 7 - No cards found in scope

1. System queries for cards
2. No cards found (scope is empty or all deleted)
3. System returns zeros:
   ```json
   {
     "totalBoxes": 7,
     "counts": [0, 0, 0, 0, 0, 0, 0],
     "total": 0,
     "scopeType": "ALL",
     "scopeId": null
   }
   ```
4. Client displays empty state:
   - "No cards in this scope"
   - Message: "Create cards to see distribution"
   - Button: "Create Card"
5. Use case ends (success)

### 6b. Folder Scope Selected

**Trigger:** Step 3 - User selects "Folder" scope

1. User selects "Folder" from dropdown
2. System displays folder selector
3. User selects a folder (e.g., "IELTS Vocabulary")
4. Client sends request with scopeType=FOLDER and scopeId:
   ```
   GET /api/stats/box-distribution?scopeType=FOLDER&scopeId=folder-uuid-123
   ```
5. System queries cards recursively from folder:
   ```sql
   WITH RECURSIVE folder_cards AS (
     SELECT c.id, c.current_box
     FROM cards c
     JOIN decks d ON c.deck_id = d.id
     WHERE d.folder_id = ?
       AND c.user_id = ?
       AND c.deleted_at IS NULL
     
     UNION ALL
     
     SELECT c.id, c.current_box
     FROM cards c
     JOIN decks d ON c.deck_id = d.id
     JOIN folders f ON d.folder_id = f.id
     WHERE f.parent_id = ?
       AND c.user_id = ?
       AND c.deleted_at IS NULL
   )
   SELECT current_box, COUNT(*) as count
   FROM folder_cards
   GROUP BY current_box
   ORDER BY current_box ASC
   ```
6. System aggregates distribution for folder
7. Continue to Step 10 (Main Flow)

### 6c. Deck Scope Selected

**Trigger:** Step 3 - User selects "Deck" scope

1. User selects "Deck" from dropdown
2. System displays deck selector
3. User selects a deck (e.g., "IELTS Academic Words")
4. Client sends request with scopeType=DECK and scopeId:
   ```
   GET /api/stats/box-distribution?scopeType=DECK&scopeId=deck-uuid-456
   ```
5. System queries cards from specific deck:
   ```sql
   SELECT current_box, COUNT(*) as count
   FROM cards
   WHERE deck_id = ?
     AND user_id = ?
     AND deleted_at IS NULL
   GROUP BY current_box
   ORDER BY current_box ASC
   ```
6. System aggregates distribution for deck
7. Continue to Step 10 (Main Flow)

### 6d. Invalid Scope Type

**Trigger:** Step 5 - Invalid scopeType value

1. System validates scopeType
2. Value not in [ALL, FOLDER, DECK]
3. System returns 400 Bad Request:
   ```json
   {
     "error": "Invalid scope type",
     "message": "Scope type must be ALL, FOLDER, or DECK"
   }
   ```
4. Client displays error message
5. User must select valid scope type
6. Use case ends (failure)

### 6e. Scope Not Found

**Trigger:** Step 5 - FOLDER or DECK scope doesn't exist

1. System queries for folder or deck
2. Scope not found or doesn't belong to user
3. System returns 404 Not Found:
   ```json
   {
     "error": "Scope not found",
     "message": "Folder or deck does not exist"
   }
   ```
4. Client displays error
5. User must select different scope
6. Use case ends (failure)

### 6f. Session Expired During View

**Trigger:** Step 4 - Access token expired

1. User's access token expires
2. System returns 401 Unauthorized
3. Client attempts token refresh (UC-003)
4. If refresh succeeds:
   - Retry distribution request
   - Continue to Step 6 (Main Flow)
5. If refresh fails:
   - Client redirects to login
   - Use case ends (failure)

### 6g. Cached vs. Live Data

**Trigger:** Step 7 - Distribution may be cached

1. System checks cache for distribution
2. If cached and fresh (< 1 minute old):
   - Return cached data
3. If cache expired:
   - Fetch fresh data from database
   - Update cache
   - Return fresh data
4. Client displays data
5. Client provides "Refresh" button
6. If user clicks Refresh:
   - Force fresh query
   - Update display

## 7. Special Requirements

### 7.1 Performance

- Response time < 1 second for fetching distribution
- Efficient aggregation queries with GROUP BY
- Consider caching for frequently accessed scopes
- Index on (user_id, deck_id, current_box) for fast queries

### 7.2 Data Accuracy

- **Exclude deleted cards:** Only count non-deleted cards (deleted_at IS NULL)
- **Respect scope:** Filter by selected scope (ALL, FOLDER, DECK)
- **Complete distribution:** All boxes shown even if count is 0
- **Total calculation:** Sum of all box counts

### 7.3 Display Format

- **Bar chart:** Visual representation with bars for each box
- **List view:** Table format with box number and count
- **Total count:** Prominent display of total cards
- **Percentage:** Optional percentage for each box
- **Empty boxes:** Show boxes with 0 count (don't hide)

### 7.4 Scope Handling

- **ALL:** All user's cards across all decks
- **FOLDER:** Recursive - includes all decks in folder and subfolders
- **DECK:** Single deck only

## 8. Technology and Data Variations

### 8.1 Box Distribution Query

```typescript
interface BoxDistribution {
  totalBoxes: number;
  counts: number[]; // Array of counts per box [box1, box2, ..., boxN]
  total: number;
  scopeType: 'ALL' | 'FOLDER' | 'DECK';
  scopeId: string | null;
}

const getBoxDistribution = async (
  userId: string,
  scopeType: string,
  scopeId?: string
): Promise<BoxDistribution> => {
  // Build query based on scope
  let query = `
    SELECT current_box, COUNT(*) as count
    FROM cards
    WHERE user_id = ? AND deleted_at IS NULL
  `;

  if (scopeType === 'DECK') {
    query += ` AND deck_id = ?`;
  } else if (scopeType === 'FOLDER') {
    // Use recursive CTE for folder
    query = buildFolderQuery(scopeId);
  }

  query += ` GROUP BY current_box ORDER BY current_box ASC`;

  // Execute query and build distribution array
};
```

### 8.2 Building Distribution Array

```typescript
const buildDistributionArray = (
  totalBoxes: number,
  queryResults: { current_box: number; count: number }[]
): number[] => {
  // Initialize array with zeros
  const counts = new Array(totalBoxes).fill(0);

  // Fill counts from query results
  queryResults.forEach((row) => {
    const boxIndex = row.current_box - 1; // Convert to 0-based index
    if (boxIndex >= 0 && boxIndex < totalBoxes) {
      counts[boxIndex] = row.count;
    }
  });

  return counts;
};
```

### 8.3 Folder Recursive Query

```sql
-- PostgreSQL recursive query for folder scope
WITH RECURSIVE folder_decks AS (
  -- Direct decks in folder
  SELECT id FROM decks WHERE folder_id = ?
  
  UNION
  
  -- Decks in subfolders
  SELECT d.id
  FROM decks d
  JOIN folders f ON d.folder_id = f.id
  JOIN folder_decks fd ON f.parent_id = fd.id
)
SELECT c.current_box, COUNT(*) as count
FROM cards c
JOIN folder_decks fd ON c.deck_id = fd.id
WHERE c.user_id = ?
  AND c.deleted_at IS NULL
GROUP BY c.current_box
ORDER BY c.current_box ASC;
```

### 8.4 Caching Strategy

```typescript
const getCachedDistribution = async (
  userId: string,
  scopeType: string,
  scopeId?: string
) => {
  const cacheKey = `box-dist:${userId}:${scopeType}:${scopeId || 'all'}`;
  
  const cached = await cache.get(cacheKey);
  if (cached && Date.now() - cached.timestamp < 60 * 1000) {
    return cached.data; // Return cached if < 1 minute old
  }

  const data = await fetchDistributionFromDB(userId, scopeType, scopeId);
  
  await cache.set(cacheKey, {
    data,
    timestamp: Date.now()
  }, 60); // 1 minute TTL

  return data;
};
```

## 9. Frequency of Occurrence

- **Frequent:** Users check distribution regularly
- **Per user:** 2-5 views per week
- **Total:** 50-200 views/day (MVP phase)
- Peak: After review sessions to check progress

## 10. Open Issues

- **Filter by due vs. not-due:** Show distribution of due cards separately (future)
- **Historical distribution:** Show distribution over time (future)
- **Export distribution:** Export as CSV/PDF (future)
- **Comparison:** Compare distributions between folders/decks (future)
- **Box progression:** Show cards moving through boxes over time (future)

## 11. Related Use Cases

- [UC-023: Review Cards (SRS)](UC-023-review-cards-srs.md) - Reviews affect box distribution
- [UC-031: View User Statistics](UC-031-view-user-statistics.md) - Another statistics view
- [UC-028: Configure SRS Settings](UC-028-configure-srs-settings.md) - total_boxes setting affects display

## 12. Business Rules References

- **BR-BOX-01:** Only non-deleted cards are counted
- **BR-SRS-01:** Total boxes from user settings (default 7)
- **BR-BOX-02:** Distribution shows all boxes even if count is 0

## 13. UI Mockup Notes

### Box Distribution Chart

```
┌─────────────────────────────────────────┐
│ Box Distribution: All Cards            │
├─────────────────────────────────────────┤
│                                         │
│ Total: 1,240 cards                      │
│                                         │
│ Box 1: ████████░░░░░░░░░░  120 (10%)  │
│ Box 2: ████████████████████ 300 (24%) │
│ Box 3: ████████████████████ 450 (36%) │
│ Box 4: ████████████░░░░░░░ 230 (19%)  │
│ Box 5: ███████░░░░░░░░░░░░  90 (7%)   │
│ Box 6: ████░░░░░░░░░░░░░░░  40 (3%)   │
│ Box 7: █░░░░░░░░░░░░░░░░░░  10 (1%)   │
│                                         │
│ Scope: [All Cards ▼]                   │
└─────────────────────────────────────────┘
```

### Empty State

```
┌─────────────────────────────────────────┐
│ Box Distribution                        │
├─────────────────────────────────────────┤
│                                         │
│ No cards in this scope                  │
│                                         │
│ Create cards to see distribution        │
│                                         │
│ [Create Card]                          │
└─────────────────────────────────────────┘
```

## 14. API Endpoint

```http
GET /api/stats/box-distribution
```

**Request Headers:**

```http
Authorization: Bearer <access-token>
```

**Query Parameters:**

```
?scopeType=ALL|FOLDER|DECK
&scopeId=<uuid>  # Required if scopeType is FOLDER or DECK
```

**Success Response (200 OK) - All Cards:**

```json
{
  "totalBoxes": 7,
  "counts": [120, 300, 450, 230, 90, 40, 10],
  "total": 1240,
  "scopeType": "ALL",
  "scopeId": null
}
```

**Success Response (200 OK) - Folder Scope:**

```json
{
  "totalBoxes": 7,
  "counts": [50, 120, 180, 90, 30, 15, 5],
  "total": 490,
  "scopeType": "FOLDER",
  "scopeId": "folder-uuid-123"
}
```

**Success Response (200 OK) - Deck Scope:**

```json
{
  "totalBoxes": 7,
  "counts": [20, 50, 80, 40, 15, 5, 2],
  "total": 212,
  "scopeType": "DECK",
  "scopeId": "deck-uuid-456"
}
```

**Success Response (200 OK) - Empty Scope:**

```json
{
  "totalBoxes": 7,
  "counts": [0, 0, 0, 0, 0, 0, 0],
  "total": 0,
  "scopeType": "ALL",
  "scopeId": null
}
```

**Error Responses:**

400 Bad Request - Invalid scope type:

```json
{
  "error": "Invalid scope type",
  "message": "Scope type must be ALL, FOLDER, or DECK"
}
```

400 Bad Request - Missing scopeId:

```json
{
  "error": "Missing scope ID",
  "message": "scopeId is required when scopeType is FOLDER or DECK"
}
```

404 Not Found - Scope not found:

```json
{
  "error": "Scope not found",
  "message": "Folder or deck does not exist"
}
```

401 Unauthorized:

```json
{
  "error": "Unauthorized",
  "message": "Authentication required"
}
```

500 Internal Server Error:

```json
{
  "error": "Internal server error",
  "message": "Failed to load box distribution. Please try again."
}
```

## 15. Test Cases

### TC-032-001: View Overall Distribution

- **Given:** User has cards in multiple boxes
- **When:** User views box distribution with scopeType=ALL
- **Then:** Distribution shows counts for all boxes, total matches sum

### TC-032-002: View Folder Distribution

- **Given:** User selects folder scope
- **When:** User views box distribution for folder
- **Then:** Distribution shows counts for cards in folder and subfolders

### TC-032-003: View Deck Distribution

- **Given:** User selects deck scope
- **When:** User views box distribution for deck
- **Then:** Distribution shows counts for cards in that deck only

### TC-032-004: Empty Scope

- **Given:** Empty deck or folder
- **When:** User views box distribution
- **Then:** All boxes show 0, total is 0, empty state message shown

### TC-032-005: Deleted Cards Excluded

- **Given:** User has deleted cards
- **When:** User views box distribution
- **Then:** Deleted cards not counted in distribution

### TC-032-006: All Boxes Shown

- **Given:** User has cards only in Box 1 and Box 3
- **When:** User views box distribution
- **Then:** All 7 boxes shown, Box 2, 4, 5, 6, 7 show 0

### TC-032-007: Invalid Scope Type

- **Given:** Invalid scopeType parameter
- **When:** User requests box distribution
- **Then:** 400 error "Invalid scope type"

### TC-032-008: Scope Not Found

- **Given:** Invalid folder or deck ID
- **When:** User requests box distribution
- **Then:** 404 error "Scope not found"

### TC-032-009: Different Total Boxes

- **Given:** User configured total_boxes = 5
- **When:** User views box distribution
- **Then:** Distribution shows 5 boxes only (not 7)

### TC-032-010: Refresh Distribution

- **Given:** User viewing cached distribution
- **When:** User clicks Refresh
- **Then:** Fresh data loaded, display updated

## 16. Database Schema Reference

### cards table (for distribution)

```sql
-- Query cards grouped by current_box
SELECT current_box, COUNT(*) as count
FROM cards
WHERE user_id = ?
  AND deleted_at IS NULL
GROUP BY current_box
ORDER BY current_box ASC;

-- Index for efficient queries
CREATE INDEX idx_cards_user_box ON cards(user_id, current_box) 
WHERE deleted_at IS NULL;
CREATE INDEX idx_cards_deck_box ON cards(deck_id, current_box) 
WHERE deleted_at IS NULL;
```

### srs_settings table

```sql
-- Get total_boxes for user
SELECT total_boxes
FROM srs_settings
WHERE user_id = ?;
```

### folders and decks tables

```sql
-- For folder scope, need to query decks recursively
-- See recursive CTE in section 8.3
```
