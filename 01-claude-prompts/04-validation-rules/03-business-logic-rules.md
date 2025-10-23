# Business Logic Validation (Claude Prompt)

**Source**: `00_docs/04-detail-design/05-validation-rules.md` §3 + `03-business-logic-flows.md`.

## Core rules
- **Folder depth**: Reject operations that would exceed depth 10 or move folder into its own subtree.
- **Deck copy/move**: Validate destination ownership and ensure duplicates don’t break name uniqueness (auto-append suffix if conflict per spec).
- **Review limits**: Daily scheduler ensures new/review counts do not exceed user or deck overrides; respond with warning if limit reached.
- **Forgotten card strategy**: Validate strategy is supported; fallback to default when user tries unsupported combos.
- **Statistics**: After review/import/delete, re-calc stats; ensure operations are atomic to avoid mismatched counts.
- **Import**: Reject files exceeding 5MB or >5k rows; stop processing when >200 invalid rows.

## Claude tips
- Convert each bullet into guard clauses at service level—DTO validation alone is insufficient.
- When Claude asks about edge cases, refer back to the pseudocode in `03-business-logic-flows.md` for exact behaviour.
