# API – Pagination & Filtering

**Source**: `00_docs/03-design/api/pagination-filtering.md`.

## Conventions
- Query params: `page` (0-based), `size` (default 20, max 100), `sort` (`field,asc|desc`). Multiple sorts allowed via comma separation.
- Filtering syntax: `filter=field:value` with supported fields per endpoint (e.g., cards: `status:due|new`, `search:<keyword>`).
- Responses embed `meta` object: `{ page, size, totalPages, totalElements }`.

## Claude tips
- When implementing repository queries, mention allowed sort fields to prevent Claude from exposing unsupported columns.
- Encourage using reusable pagination utilities described in backend detail design.
