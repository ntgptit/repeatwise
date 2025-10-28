# UC-031: View User Statistics

## 1. Brief Description

User views personal learning statistics such as total cards learned, streak days, total study time, and recent activity.

## 2. Actors

- Primary Actor: Authenticated User
- Secondary Actor: Statistics Service

## 3. Preconditions

- User is authenticated
- Stats are available (initialized at registration)

## 4. Postconditions

### Success Postconditions

- Statistics displayed to the user

### Failure Postconditions

- Error shown; no stats displayed

## 5. Main Success Scenario (Basic Flow)

1. User navigates to Dashboard > Statistics
2. System fetches user_stats and computed metrics
3. System returns data including:
   - total_cards_learned
   - streak_days
   - last_study_date
   - total_study_time_minutes
   - reviews_today / reviews_past_7_days
4. UI renders KPI cards and simple charts (optional in MVP)

## 6. Alternative Flows

### 6a. No Activity Yet

1. Show zeros and onboarding tips

### 6b. Cached vs. Live

1. Show timestamp; allow refresh

## 7. Special Requirements

- Efficient queries; consider pre-aggregated tables for daily rollups
- Present in user’s locale/timezone

## 8. Business Rules / Constraints

- BR-STATS-03: last_study_date is user timezone-aware

## 9. Frequency of Occurrence

- Frequent; daily checks

## 10. Open Issues

- Add goals and achievements (future)

## 11. Related Use Cases

- UC-023: Review Cards (SRS)
- UC-024: Rate Card

## 12. Business Rules References

- BR-STATS-03

## 13. UI Mockup Notes

- KPI tiles + mini charts; detail drill-down future

## 14. API Endpoint

```
GET /api/users/me/stats
```

Success (200):

```json
{
  "totalCardsLearned": 1234,
  "streakDays": 7,
  "lastStudyDate": "2025-01-01",
  "totalStudyTimeMinutes": 456,
  "reviewsToday": 120,
  "reviewsPast7Days": [12, 34, 56, 78, 90, 21, 34]
}
```

Errors:

- 401 unauthorized (session expired)
- 500 internal error

## 15. Test Cases

- TC-031-001: Stats with recent activity -> correct values
- TC-031-002: No activity -> zeros
- TC-031-003: Refresh -> updates timestamp
