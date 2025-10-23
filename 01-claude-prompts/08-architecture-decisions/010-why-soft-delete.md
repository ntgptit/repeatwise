# ADR – Why Soft Delete

**Decision**: Use `is_deleted` flags for folders, decks, cards instead of hard delete.

**Rationale**
- Preserves history for statistics and potential restore features.
- Prevents orphaned SRS records while enabling background cleanup.
- Aligns with import/export and async jobs that may reference soon-to-be-deleted items.

**Alternatives**: Hard delete (risk data loss), archive tables (more complexity now).

**Claude tips**
- Ensure queries filter `is_deleted=false`; mention this whenever Claude writes repository methods.
