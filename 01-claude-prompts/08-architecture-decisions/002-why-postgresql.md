# ADR – Why PostgreSQL

**Decision**: PostgreSQL 15 as primary database.

**Rationale**
- Strong support for JSON, indexing strategies, and recursive queries needed for folder hierarchy.
- Reliable transactions for SRS updates; supports `uuid` types natively.
- Team familiarity + hosting options (Docker, managed services).

**Alternatives**: MySQL (poorer CTE support), MongoDB (document model complicates transactions).

**Claude tips**
- Use this to justify SQL-based approach when Claude suggests NoSQL.
