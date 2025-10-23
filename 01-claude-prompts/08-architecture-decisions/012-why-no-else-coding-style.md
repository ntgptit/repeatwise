# ADR – Why "No Else" Coding Style

**Decision**: Encourage guard clauses and early returns instead of deep nesting.

**Rationale**
- Matches coding conventions in `00_docs/05-quality/coding-convention-backend.md` and web/mobile guides.
- Improves readability, especially in service methods handling validation.
- Facilitates clear error handling (each guard returns specific error code).

**Claude tips**
- Remind Claude to apply guard clauses when translating pseudocode to concrete code.
