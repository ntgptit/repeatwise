# UC-031: View User Statistics

## 1. Brief Description

User views personal learning statistics including total cards learned, streak days, total study time, reviews today, and activity over the past 7 days. Statistics are displayed in dashboard format with key performance indicators (KPIs) and simple charts.

## 2. Actors

- **Primary Actor:** Authenticated User
- **Secondary Actor:** Statistics Service

## 3. Preconditions

- User is authenticated with valid access token
- User has access to Dashboard or Statistics page
- Stats are available (initialized at registration)

## 4. Postconditions

### Success Postconditions

- Statistics displayed to the user
- KPIs shown: total cards learned, streak days, study time, reviews today
- Activity chart shown (past 7 days)
- User can view progress and track learning activity

### Failure Postconditions

- Error message displayed
- No stats displayed
- User remains on dashboard

## 5. Main Success Scenario (Basic Flow)

1. User navigates to Dashboard > Statistics page
2. System fetches user statistics from database:
   ```sql
   SELECT total_cards_learned, streak_days, last_study_date, 
          total_study_time_minutes
   FROM user_stats
   WHERE user_id = ?
   ```
3. System fetches reviews count for today:
   ```sql
   SELECT COUNT(*) as reviews_today
   FROM review_logs
   WHERE user_id = ?
     AND DATE(created_at) = CURRENT_DATE
     AND undone_at IS NULL
   ```
4. System fetches reviews for past 7 days:
   ```sql
   SELECT DATE(created_at) as date, COUNT(*) as count
   FROM review_logs
   WHERE user_id = ?
     AND created_at >= CURRENT_DATE - INTERVAL '7 days'
     AND undone_at IS NULL
   GROUP BY DATE(created_at)
   ORDER BY date ASC
   ```
5. System aggregates statistics:
   - total_cards_learned: from user_stats
   - streak_days: current streak from user_stats
   - last_study_date: most recent study date
   - total_study_time_minutes: total minutes studied
   - reviews_today: count of reviews today
   - reviews_past_7_days: array of daily counts [day1, day2, ..., day7]
6. System returns 200 OK with statistics:
   ```json
   {
     "totalCardsLearned": 1234,
     "streakDays": 7,
     "lastStudyDate": "2025-01-28",
     "totalStudyTimeMinutes": 456,
     "reviewsToday": 120,
     "reviewsPast7Days": [12, 34, 56, 78, 90, 110, 120],
     "lastUpdated": "2025-01-28T14:45:00Z"
   }
   ```
7. Client receives response
8. Client displays statistics dashboard:
   - KPI cards: Total Cards, Streak Days, Study Time, Reviews Today
   - Activity chart: Bar chart showing reviews per day (past 7 days)
   - Last updated timestamp
9. User views statistics and tracks progress

## 6. Alternative Flows

### 6a. No Activity Yet

**Trigger:** Step 3-4 - User has no review activity

1. System queries for reviews
2. No reviews found (new user or inactive)
3. System returns zeros:
   ```json
   {
     "totalCardsLearned": 0,
     "streakDays": 0,
     "lastStudyDate": null,
     "totalStudyTimeMinutes": 0,
     "reviewsToday": 0,
     "reviewsPast7Days": [0, 0, 0, 0, 0, 0, 0]
   }
   ```
4. Client displays empty state with onboarding tips:
   - "Start your learning journey!"
   - "Review your first card to begin tracking progress"
   - Button: "Start Reviewing"
5. Use case ends (success)

### 6b. Cached vs. Live Data

**Trigger:** Step 6 - Statistics may be cached

1. System checks if statistics are cached
2. If cached and fresh (< 5 minutes old):
   - Return cached data with cached timestamp
3. If cache expired or no cache:
   - Fetch fresh data from database
   - Update cache
   - Return fresh data
4. Client displays data with timestamp
5. Client provides "Refresh" button to force reload
6. If user clicks Refresh:
   - Client sends request with cache-busting parameter
   - System fetches fresh data
   - Client updates display

### 6c. Missing Last Study Date

**Trigger:** Step 5 - last_study_date is NULL

1. System checks last_study_date
2. Value is NULL (user never studied)
3. System sets lastStudyDate to null in response
4. Client displays "Never" or "N/A" for last study date
5. Continue to Step 8 (Main Flow)

### 6d. Session Expired During View

**Trigger:** Step 2 - Access token expired

1. User's access token expires while viewing statistics
2. System returns 401 Unauthorized
3. Client attempts token refresh (UC-003)
4. If refresh succeeds:
   - Retry statistics request
   - Continue to Step 6 (Main Flow)
5. If refresh fails:
   - Client redirects to login
   - Use case ends (failure)

### 6e. Database Error

**Trigger:** Step 2-4 - Database query fails

1. System encounters database error
2. System logs error details
3. System returns 500 Internal Server Error:
   ```json
   {
     "error": "Internal server error",
     "message": "Failed to load statistics. Please try again."
   }
   ```
4. Client displays error message
5. Client shows retry button
6. User can retry or navigate away
7. Use case ends (failure)

### 6f. Partial Data Available

**Trigger:** Step 4 - Some data missing

1. System queries for reviews
2. Some days have no reviews (normal)
3. System fills missing days with 0:
   ```json
   {
     "reviewsPast7Days": [12, 0, 34, 56, 0, 90, 120]
   }
   ```
4. Client displays chart with zeros for missing days
5. Continue to Step 8 (Main Flow)

## 7. Special Requirements

### 7.1 Performance

- Response time < 1 second for fetching statistics
- Consider caching aggregated statistics (5-minute cache)
- Efficient queries with proper indexes
- Pre-aggregated daily rollups (optional optimization)

### 7.2 Data Accuracy

- **Real-time for today:** Reviews today should be current
- **Cached for historical:** Past 7 days can be cached briefly
- **Timezone awareness:** Dates respect user's timezone
- **Undone reviews:** Exclude undone reviews from counts

### 7.3 Display Format

- **KPIs:** Large, readable numbers with labels
- **Chart:** Simple bar chart or line chart (past 7 days)
- **Time formatting:** Format study time as hours and minutes
- **Date formatting:** Format dates according to user's locale

### 7.4 Refresh Capability

- **Auto-refresh:** Refresh every 5 minutes (optional)
- **Manual refresh:** Refresh button to force reload
- **Loading state:** Show loading indicator during fetch

## 8. Technology and Data Variations

### 8.1 Statistics Aggregation

```typescript
interface UserStatistics {
  totalCardsLearned: number;
  streakDays: number;
  lastStudyDate: string | null; // ISO date string
  totalStudyTimeMinutes: number;
  reviewsToday: number;
  reviewsPast7Days: number[]; // Array of 7 daily counts
  lastUpdated: string; // ISO timestamp
}

const formatStudyTime = (minutes: number): string => {
  const hours = Math.floor(minutes / 60);
  const mins = minutes % 60;
  return `${hours}h ${mins}m`;
};
```

### 8.2 Past 7 Days Query

```sql
-- Get reviews for past 7 days
WITH date_series AS (
  SELECT generate_series(
    CURRENT_DATE - INTERVAL '6 days',
    CURRENT_DATE,
    '1 day'::interval
  )::date AS date
)
SELECT 
  ds.date,
  COALESCE(COUNT(rl.id), 0) as count
FROM date_series ds
LEFT JOIN review_logs rl ON DATE(rl.created_at) = ds.date
  AND rl.user_id = ?
  AND rl.undone_at IS NULL
GROUP BY ds.date
ORDER BY ds.date ASC;
```

### 8.3 Caching Strategy

```typescript
const getStatistics = async (userId: string, forceRefresh: boolean) => {
  const cacheKey = `stats:${userId}`;
  
  if (!forceRefresh) {
    const cached = await cache.get(cacheKey);
    if (cached && Date.now() - cached.timestamp < 5 * 60 * 1000) {
      return cached.data; // Return cached if < 5 minutes old
    }
  }

  // Fetch fresh data
  const stats = await fetchStatisticsFromDB(userId);
  
  // Cache for 5 minutes
  await cache.set(cacheKey, {
    data: stats,
    timestamp: Date.now()
  }, 300); // 5 minutes TTL

  return stats;
};
```

### 8.4 Streak Calculation

```sql
-- Calculate current streak
SELECT COUNT(*) as streak_days
FROM (
  SELECT DISTINCT DATE(created_at) as study_date
  FROM review_logs
  WHERE user_id = ?
    AND undone_at IS NULL
    AND created_at >= (
      SELECT MAX(created_at) - INTERVAL '30 days'
      FROM review_logs
      WHERE user_id = ?
    )
  ORDER BY study_date DESC
) consecutive_days
WHERE study_date >= CURRENT_DATE - (ROW_NUMBER() OVER (ORDER BY study_date DESC) - 1) || ' days'::interval;
```

## 9. Frequency of Occurrence

- **Frequent:** Daily checks by users
- **Per user:** 1-3 views per day
- **Total:** 100-500 views/day (MVP phase)
- Peak: Morning and evening (after review sessions)

## 10. Open Issues

- **Goals and achievements:** Add learning goals and achievement badges (future)
- **Detailed analytics:** More charts and insights (future)
- **Export statistics:** Allow users to export stats as CSV/PDF (future)
- **Comparative statistics:** Compare with previous periods (future)
- **Leaderboards:** Compare with other users (future, privacy concerns)

## 11. Related Use Cases

- [UC-023: Review Cards (SRS)](UC-023-review-cards-srs.md) - Reviews contribute to statistics
- [UC-024: Rate Card](UC-024-rate-card.md) - Rating actions tracked in statistics
- [UC-032: View Box Distribution](UC-032-view-box-distribution.md) - Another statistics view

## 12. Business Rules References

- **BR-STATS-01:** Statistics updated in real-time during reviews
- **BR-STATS-02:** Undone reviews excluded from statistics
- **BR-STATS-03:** last_study_date is user timezone-aware
- **BR-STATS-04:** Streak counts consecutive days with at least one review

## 13. UI Mockup Notes

### Statistics Dashboard

```
┌─────────────────────────────────────────┐
│ Your Statistics                         │
├─────────────────────────────────────────┤
│                                         │
│ ┌──────────┐  ┌──────────┐            │
│ │ 1,234    │  │   7      │            │
│ │ Cards    │  │ Streak   │            │
│ │ Learned  │  │ Days     │            │
│ └──────────┘  └──────────┘            │
│                                         │
│ ┌──────────┐  ┌──────────┐            │
│ │ 7h 36m   │  │  120     │            │
│ │ Study    │  │ Reviews  │            │
│ │ Time     │  │ Today    │            │
│ └──────────┘  └──────────┘            │
│                                         │
│ Activity (Past 7 Days)                │
│ ▁▃▅▇█▆▇                                 │
│ 12 34 56 78 90 110 120                 │
│ Mon Tue Wed Thu Fri Sat Sun            │
│                                         │
│ Last updated: 2 minutes ago [Refresh] │
└─────────────────────────────────────────┘
```

### Empty State

```
┌─────────────────────────────────────────┐
│ 📊 Your Statistics                       │
├─────────────────────────────────────────┤
│                                         │
│ No activity yet                         │
│                                         │
│ Start your learning journey by         │
│ reviewing your first card!              │
│                                         │
│ [Start Reviewing]                      │
└─────────────────────────────────────────┘
```

## 14. API Endpoint

```http
GET /api/users/me/stats
```

**Request Headers:**

```http
Authorization: Bearer <access-token>
```

**Query Parameters (optional):**

```
?refresh=true  # Force refresh, bypass cache
```

**Success Response (200 OK):**

```json
{
  "totalCardsLearned": 1234,
  "streakDays": 7,
  "lastStudyDate": "2025-01-28",
  "totalStudyTimeMinutes": 456,
  "reviewsToday": 120,
  "reviewsPast7Days": [12, 34, 56, 78, 90, 110, 120],
  "lastUpdated": "2025-01-28T14:45:00Z"
}
```

**Success Response (200 OK) - No Activity:**

```json
{
  "totalCardsLearned": 0,
  "streakDays": 0,
  "lastStudyDate": null,
  "totalStudyTimeMinutes": 0,
  "reviewsToday": 0,
  "reviewsPast7Days": [0, 0, 0, 0, 0, 0, 0],
  "lastUpdated": "2025-01-28T14:45:00Z"
}
```

**Error Responses:**

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
  "message": "Failed to load statistics. Please try again."
}
```

## 15. Test Cases

### TC-031-001: View Statistics Successfully

- **Given:** User has review activity
- **When:** User views statistics page
- **Then:** All KPIs displayed correctly, chart shows past 7 days

### TC-031-002: No Activity Yet

- **Given:** New user with no reviews
- **When:** User views statistics page
- **Then:** Empty state shown with onboarding message

### TC-031-003: Refresh Statistics

- **Given:** User viewing cached statistics
- **When:** User clicks Refresh button
- **Then:** Fresh data loaded, timestamp updated

### TC-031-004: Reviews Today Count

- **Given:** User reviewed 50 cards today
- **When:** User views statistics
- **Then:** Reviews Today shows 50

### TC-031-005: Past 7 Days Chart

- **Given:** User reviewed cards on different days
- **When:** User views statistics
- **Then:** Chart shows correct counts for each day, zeros for days with no reviews

### TC-031-006: Streak Days Calculation

- **Given:** User reviewed cards for 5 consecutive days
- **When:** User views statistics
- **Then:** Streak Days shows 5

### TC-031-007: Study Time Formatting

- **Given:** totalStudyTimeMinutes = 456
- **When:** User views statistics
- **Then:** Display shows "7h 36m"

### TC-031-008: Session Expired

- **Given:** User's access token expired
- **When:** User views statistics
- **Then:** Token refreshed automatically, statistics loaded

### TC-031-009: Undone Reviews Excluded

- **Given:** User rated 10 cards, then undid 2
- **When:** User views statistics
- **Then:** Reviews Today shows 8 (not 10)

### TC-031-010: Last Study Date Null

- **Given:** User never studied
- **When:** User views statistics
- **Then:** Last Study Date shows "Never" or "N/A"

## 16. Database Schema Reference

### user_stats table

```sql
CREATE TABLE user_stats (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
  total_cards_learned INTEGER NOT NULL DEFAULT 0,
  streak_days INTEGER NOT NULL DEFAULT 0,
  last_study_date DATE,
  total_study_time_minutes INTEGER NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_user_stats_user_id ON user_stats(user_id);
```

### review_logs table (for statistics)

```sql
-- Query reviews for statistics
-- Exclude undone reviews: WHERE undone_at IS NULL
-- Group by date: GROUP BY DATE(created_at)
CREATE INDEX idx_review_logs_user_date ON review_logs(user_id, DATE(created_at));
```
