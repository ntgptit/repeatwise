# Business Rules - RepeatWise MVP

## Introduction

This document defines business rules (constraints and policies) that the RepeatWise MVP must enforce to ensure consistency, correctness, and security across the system.

---

## BR-1: User Management Rules

### BR-1.1: Email Validation

- Email must be unique in the system.
- Email must match standard format (regex validation).
- Email is stored in lowercase; comparisons are case‑insensitive.

### BR-1.2: Password Policy

- Minimum length 8 characters.
- Passwords must be hashed with bcrypt (cost factor 12) before storage.
- Plain‑text passwords must never be stored or logged.

### BR-1.3: Token Management

- Access token expiry: 15 minutes.
- Refresh token expiry: 7 days; stored hashed (bcrypt) in database.
- Refresh tokens are rotated on every refresh (one‑time use).
- On logout, the active refresh token is revoked (set `revoked_at`).
- On password change, revoke all refresh tokens for the user.

### BR-1.4: User Profile

- Name: 1–100 characters.
- Timezone: must be a valid IANA timezone (e.g., `Asia/Ho_Chi_Minh`).
- Language: `VI` or `EN`.
- Theme: `LIGHT`, `DARK`, or `SYSTEM`.

---

## BR-2: Folder Management Rules

### BR-2.1: Folder Hierarchy

- A folder can contain multiple sub‑folders and decks.
- Maximum depth = 10 levels (root depth = 0).
- Database constraint: `CHECK (depth >= 0 AND depth <= 10)`.

### BR-2.2: Folder Naming

- Name is required; max length 100 characters.
- Name must be unique among siblings (case‑insensitive within the same parent).
- Leading/trailing whitespace is trimmed.

### BR-2.3: Folder Path (Materialized Path)

- Path format: `/ancestor1_id/.../parent_id/folder_id`.
- Root folder path: `/{folder_id}`.
- Path is recalculated on create/move and used for descendant queries.

### BR-2.4: Folder Creation

- Cannot create a child folder if the parent is already at depth 10.
- Error: "Maximum folder depth (10 levels) exceeded".
- Users can only create folders under their own hierarchy.

### BR-2.5: Folder Move

- Cannot move a folder into itself or any of its descendants.
- After move, new depth for all nodes must remain <= 10.
- Move must update `parent_id`, `path`, and `depth` for the moved subtree within a transaction.

### BR-2.6: Folder Copy

- Deep copy: folder + all sub‑folders + decks (+ cards within decks).
- Limits:
  - <= 50 items (folders + decks): synchronous copy.
  - 51–500 items: asynchronous job with progress.
  - > 500: reject with error.
- Default naming policy for conflicts: append " (Copy)".

### BR-2.7: Folder Delete

- Soft delete by setting `deleted_at` timestamp (cascade to descendants/decks/cards logically).
- Confirmation required if the folder contains children.
- Restore within 30 days by setting `deleted_at = NULL`.
- Permanent cleanup via background job after 30 days.

### BR-2.8: Folder Ownership

- Users can only view/edit/delete their own folders.
- No shared/public folders in MVP.

---

## BR-3: Deck Management Rules

### BR-3.1: Deck Naming

- Name required; max 100 characters.
- Name unique within the same folder (case‑insensitive).
- At root level (no folder), name unique among root decks.

### BR-3.2: Deck Creation

- Deck can be created in a folder or at root.
- Users can only create decks in their own hierarchy.

### BR-3.3: Deck Move

- Deck can be moved across user’s folders or to root.

### BR-3.4: Deck Copy

- Deep copy of deck and all its cards.
- Limits:
  - <= 1,000 cards: synchronous copy.
  - 1,001–10,000 cards: asynchronous job.
  - > 10,000: reject with error.
- Default naming policy: append " (Copy)" on conflict.

### BR-3.5: Deck Delete

- Soft delete by setting `deleted_at`.

---

## BR-4: Card Management Rules

- Front and Back are required; max 5,000 characters each.
- A card belongs to exactly one deck.
- Soft delete with `deleted_at` and exclude from queries by default.

---

## BR-5: Import/Export Rules

### BR-5.1: Import

- File size <= 50 MB; row limit <= 10,000 per file.
- Formats: CSV (UTF‑8, comma) or Excel .xlsx.
- Required columns: Front, Back.
- Duplicate handling policy: SKIP | REPLACE | KEEP_BOTH.
- Stream processing and batch inserts (e.g., 1,000 rows/transaction).
- Per‑row validation and error report (CSV) for failures.

### BR-5.2: Export

- Max 50,000 cards per export.
- If > 5,000 cards, perform async export with job status.
- Columns: Front, Back, Current Box, Due Date (and other metadata as needed).

---

## BR-6: SRS Rules

- Default total_boxes = 7.
- Due card: `due_date <= current_date` and not soft‑deleted.
- Ratings:
  - AGAIN: move to box 1 (forgotten policy)
  - HARD: optional keep/decrement per policy
  - GOOD: increment box by 1 (cap at max)
  - EASY: increment by >1 (e.g., +2) (cap at max)
- Daily limits: respect `new_cards_per_day` and `max_reviews_per_day`.

---

## BR-7: Review Session Rules

- Batch size capped (e.g., 200) with prefetching for smooth UX.
- Default ordering: `due_date ASC, current_box ASC` (configurable).
- Handle session timeout/inactivity gracefully.

---

## BR-8: Statistics Rules

- Folder statistics are recursive (include all descendants).
- Optional cached/denormalized tables for faster aggregation.
- Timestamps are user timezone‑aware where displayed.

---

## BR-9: Notifications (Future‑ready)

- Title: "Time to review!"
- Body: "You have X cards to review today."
- Action opens Review screen.
- User can enable/disable and set `notification_time` (default 09:00).

---

## BR-10: Async Operations Rules

### BR-10.1: Folder Copy Job

- Trigger: total items (folders + decks) > 50 and <= 500.
- Thread pool: core 5, max 10, queue 100 (reference values).
- Job tracking: UUID `job_id`, in‑memory store with TTL 1 hour.
- Progress: `items_processed / total_items * 100`, update every ~10 items.
- Timeout: 5 minutes; on timeout, auto‑cancel and attempt rollback.

### BR-10.2: Deck Copy Job

- Trigger: card count > 1,000 and <= 10,000.
- Same tracking and pool parameters as folder copy.
- Timeout: 10 minutes; batch inserts (~1,000 cards/tx).

### BR-10.3: Job Status API

- `GET /api/jobs/{job_id}` returns `{ status, progress, message, resultId? }`.
- Cleanup job removes completed/failed jobs after 1 hour.

---

## BR-11: Data Integrity Rules

### BR-11.1: Foreign Keys (examples)

- `folders.user_id -> users.id (ON DELETE CASCADE)`
- `folders.parent_folder_id -> folders.id (ON DELETE CASCADE)`
- `decks.user_id -> users.id (ON DELETE CASCADE)`
- `decks.folder_id -> folders.id (ON DELETE SET NULL)`
- `cards.deck_id -> decks.id (ON DELETE CASCADE)`
- `card_box_position.card_id -> cards.id (ON DELETE CASCADE)`
- `card_box_position.user_id -> users.id (ON DELETE CASCADE)`
- `review_logs.card_id -> cards.id (ON DELETE CASCADE)`
- `review_logs.user_id -> users.id (ON DELETE CASCADE)`

### BR-11.2: Unique Constraints

- `users.email` unique.
- `folders`: unique `(user_id, parent_folder_id, name)`.
- `decks`: unique `(user_id, folder_id, name)`.

### BR-11.3: Check Constraints

- `folders.depth` between 0 and 10.
- `srs_settings.new_cards_per_day` > 0 and <= 500.
- `srs_settings.max_reviews_per_day` > 0 and <= 1000.
- `card_box_position.current_box` between 1 and 7.

### BR-11.4: Soft Delete Query Rules

- All queries exclude soft‑deleted records (`deleted_at IS NULL`).
- Provide restore by setting `deleted_at = NULL` for eligible records.

---

## BR-12: Performance Rules

### BR-12.1: Index Requirements

- Critical indexes:
  - `idx_card_box_user_due` on `(user_id, due_date, current_box)` for review queries.
  - `idx_folders_path` on `folders(path varchar_pattern_ops)` for descendant queries.
- Mandatory: all foreign keys must be indexed; columns used in WHERE/JOIN/ORDER BY must be indexed.

### BR-12.2: Query Optimization

- Use batch fetch with JOINs instead of N+1 queries.
- LIMIT results (e.g., 200 cards per review request).
- Consider CTEs for complex tree operations.

### BR-12.3: Pagination & UI

- Folders/Decks list: 50 items per page.
- Cards list: 100 cards per page.
- Review: load 100 at a time and prefetch.
- Use virtual scrolling for large lists (> 100 items).

---

## Summary

These rules must be implemented across frontend and backend to ensure data integrity and a consistent user experience for the MVP.
