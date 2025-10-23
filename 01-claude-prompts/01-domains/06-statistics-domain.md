# Statistics & Analytics Domain (Claude Brief)

**Sources**
- `00_docs/02-system-analysis/domain-model.md` §2.4
- Use cases: `UC-010` View folder stats, `UC-023` View statistics
- Design references: `00_docs/03-design/architecture/design-patterns.md` (Visitor pattern), `03-design/database/schema.md` (stats tables), `04-detail-design/03-business-logic-flows.md` (Stats update), `04-detail-design/06-error-handling-specs.md` (metrics errors)

## Responsibilities
- Track user progress (streaks, cards reviewed/new today, box distribution).
- Maintain aggregated statistics per folder/deck for quick dashboards.
- Provide API endpoints for dashboard widgets (daily stats, historical charts).

## Key entities & invariants
- `UserStats`: single row per user; updates synchronously after review; fields include `streakDays`, `longestStreak`, `cardsReviewedToday`, `newCardsToday`, `lastStudyDate`.
- `FolderStats`: cached aggregates per folder; computed using Visitor traversal; TTL 5 minutes (cache invalidated on mutations).
- `ReviewSession`: optional log grouping a study session for analytics (see `04-detail-design/03-business-logic-flows.md`).

## Implementation checkpoints
1. **After review** – update `UserStats`, optionally reset streak if timezone day changed without study.
2. **Folder stats** – recalc total cards/due counts using recursive query or Visitor pattern; store results in table + cache.
3. **Dashboard API** – `/statistics/overview` returns streak, box distribution, due counts; `/statistics/folder/{id}` returns aggregated metrics.
4. **Performance** – use materialized path to limit queries; ensure indexes match `07-database-schemas/11-folder-stats-table.md`.

## Claude usage tips
- Pull only the stat-specific sections from `02-api-request-response-specs.md` when building responses.
- Remind Claude that heatmaps and advanced analytics are future scope—stick to metrics listed above.
- When debugging stale data, consult `12-quick-references/07-troubleshooting-guide.md` for cache invalidation steps.
