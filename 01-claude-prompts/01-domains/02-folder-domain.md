# Folder Hierarchy Domain (Claude Brief)

**Sources**
- `00_docs/02-system-analysis/domain-model.md` §2.2
- Use cases: `UC-005` Create hierarchy, `UC-006` Rename, `UC-007` Move, `UC-008` Copy, `UC-009` Delete, `UC-010` View folder stats
- Design references: `00_docs/03-design/architecture/design-patterns.md` (Composite, Visitor), `03-design/database/schema.md` (folders table), `04-detail-design/03-business-logic-flows.md` (Folder operations)

## Responsibilities
- Manage recursive folder tree (max depth 10) that can host both sub-folders and decks.
- Support CRUD, move, copy with async job tracking, and soft delete with cascade to child decks/cards.
- Provide breadcrumb navigation and aggregate statistics per folder.

## Key entities & invariants
- `Folder` fields: `id`, `user_id`, `parent_id (nullable)`, `name`, `materialized_path`, `depth`, `is_deleted`, timestamps.
- Name uniqueness enforced within the same parent; depth <= 10; materialized path stored for efficient traversal (see ADR `08-architecture-decisions/011-why-materialized-path.md`).
- Soft delete marks `is_deleted=true` and cascades logically to child decks/cards; physical purge handled later.

## Implementation checkpoints
1. **Create folder** – validate parent ownership & depth, generate `materialized_path` and default stats entry.
2. **Move folder** – ensure no circular move (destination cannot be within source subtree), recompute paths/depth for descendants, enqueue async job if subtree > threshold (see `03-design/architecture/backend-detailed-design.md` background jobs section).
3. **Copy folder** – duplicate subtree with new IDs, preserve structure, progress tracked via async job.
4. **Delete folder** – mark as deleted, update folder stats, schedule background cleanup if needed.

## Claude usage tips
- For traversal logic, reference only the snippet from `03-design/architecture/design-patterns.md` covering the Composite pattern implementation.
- When implementing path recalculation, fetch the pseudocode from `04-detail-design/03-business-logic-flows.md` → "Move Folder".
- Remind Claude that drag-and-drop, tags, and bulk selection are postponed features—keep APIs minimal.
