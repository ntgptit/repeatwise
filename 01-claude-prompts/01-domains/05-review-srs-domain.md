# Spaced Repetition (SRS) Domain (Claude Brief)

**Sources**
- `00_docs/03-design/architecture/srs-algorithm-design.md`
- `00_docs/04-detail-design/04-srs-algorithm-implementation.md`
- Use cases: `UC-019` SRS review, `UC-020` Cram mode, `UC-021` Random mode, `UC-022` Configure SRS settings

## Responsibilities
- Schedule card reviews using a 7-box Leitner system with fixed intervals (1,3,7,14,30,60,120 days).
- Apply rating outcomes (`AGAIN`, `HARD`, `GOOD`, `EASY`) with configurable forgotten-card actions.
- Provide daily study queues (due cards, new card caps) and update statistics after each session.

## Key entities & invariants
- `CardBoxPosition`: tracks `current_box`, `interval_days`, `due_date`, `last_reviewed_at`, `streakCount` per card/user.
- `ReviewLog`: stores each review event (`rating`, `elapsedSeconds`, `session_id`); used for analytics.
- `SrsSettings`: per-user defaults (new/review daily limits, review order strategy, forgotten card action, cram settings).
- All operations scoped to one user; due calculation uses user timezone.

## Algorithm rules (condensed)
- New cards start in Box 1, due today.
- `AGAIN`: delegate to strategy (`RESET_TO_BOX_ONE` default). `HARD`: stay in box, halve interval (min 1 day).
- `GOOD`: advance one box (capped at 7). `EASY`: skip one box or multiply interval ×4, whichever yields the farther box but ≤7.
- Daily scheduler fetches cards due today + new cards respecting limits and order strategy (Ascending/Descending/Random) – see pseudocode in `04-detail-design/04-srs-algorithm-implementation.md`.

## Implementation checkpoints
1. **Review session API** – load queue via `CardBoxPositionRepository.findDueByUser` filtered by timezone; update positions within a transaction.
2. **Cram mode** – bypass box updates; uses temporary list of selected cards.
3. **Random mode** – sample `n` cards ignoring due dates but still logging results.
4. **Settings** – expose GET/PUT to adjust strategy enums; ensure validations match `05-validation-rules.md` SRS section.

## Claude usage tips
- When implementing rating handlers, copy only the `calculateNextReview` method snippet; no need to paste the entire doc.
- Reinforce that intervals are fixed constants—no dynamic SM-2 calculations in MVP.
- Remind Claude to update `UserStats` and `FolderStats` after each review (see statistics domain brief).
