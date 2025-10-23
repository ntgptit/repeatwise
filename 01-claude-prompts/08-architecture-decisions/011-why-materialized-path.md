# ADR – Why Materialized Path

**Decision**: Store folder hierarchy using materialized path strings.

**Rationale**
- Fast reads for breadcrumb + descendant queries without recursive CTE every time.
- Works well with copy/move operations when combined with async updates.
- Simplifies caching for folder stats.

**Alternatives**: Nested sets (complex updates), adjacency list only (slow multi-level queries).

**Claude tips**
- When implementing move/copy, update `materialized_path` for entire subtree based on this strategy.
