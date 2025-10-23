# Performance Testing (Claude Prompt)

## Goals
- Review API P95 latency <500 ms with 1k concurrent users simulated.
- Import job completion <2 minutes for 5k cards.
- Database queries use indexes; no full table scans on due-card retrieval.

## Approach
- Use k6 scripts targeting `/api/reviews/session` and `/api/reviews/{session}/cards/{id}`.
- Monitor DB with pg_stat_statements; ensure query plans use indexes defined in `07-database-schemas/12-indexes-and-constraints.md`.

## Claude tips
- Run performance tests after major SRS changes; attach summary table to PRs when results change.
