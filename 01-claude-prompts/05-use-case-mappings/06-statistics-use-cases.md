# Use Case Map – Statistics

**Use cases**: `UC-010` View folder statistics, `UC-023` View overall statistics.

## Flow → API → UI
- Folder stats → GET `/api/folders/{id}/stats` → Web `FolderSummaryPanel`, Mobile `FolderStatsCard`.
- Dashboard overview → GET `/api/statistics/overview` → Web `DashboardPage`, Mobile `TodayTab`.
- Box distribution → included in overview response; use charts defined in UX specs.

## Data touchpoints
- Entities: `FolderStats`, `UserStats`, `ReviewLog` (for history).
- DTOs: `StatisticsOverviewResponse`, `FolderStatsResponse`.

## Acceptance highlights
- Stats cached (5 min) – backend should include `cacheControl` header for awareness.
- Streak resets automatically after missed day; display both current + longest streak.
- Response includes `cardsDueToday`, `cardsNewToday`, `cardsReviewedToday`, `boxDistribution` array of length 7.

## Claude tips
- Reference `03-design/architecture/design-patterns.md` Visitor pattern snippet when recalculating stats.
- Provide only the response schema from `02-api-request-response-specs.md` to keep tokens low.
