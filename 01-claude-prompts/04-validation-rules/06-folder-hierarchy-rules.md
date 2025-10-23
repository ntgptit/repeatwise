# Folder Hierarchy Rules (Claude Prompt)

**Source**: `00_docs/04-detail-design/05-validation-rules.md` §6 + folder business flows.

## Rules
- Max depth 10 (root depth 0). Moving/copying must ensure descendants don’t exceed depth.
- Prevent moving folder into itself or its children (check via materialized path prefix).
- Name uniqueness per parent; auto suffix "(Copy)" if conflict when copying.
- On delete, mark `is_deleted=true` for folder and descendants; ensure stats & deck references updated.
- Breadcrumb path constructed from parent chain; update caches after move/copy.

## Claude tips
- Provide the materialized path validation snippet when implementing repository queries.
- Remind Claude to call async job when copying large subtrees (>500 decks/cards).
