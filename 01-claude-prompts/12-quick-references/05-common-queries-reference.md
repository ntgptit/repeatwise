# Common Query Patterns

**Source**: `03-design/database/indexing-strategy.md` & domain prompts.

- Fetch due cards: `SELECT ... FROM card_box_positions WHERE user_id=? AND is_active AND due_date <= current_date ORDER BY due_date ASC LIMIT ?`.
- Folder children: `SELECT * FROM folders WHERE user_id=? AND parent_id=? AND is_deleted=false ORDER BY name ASC`.
- Folder subtree: use `materialized_path LIKE 'path%'`.
- Stats update: aggregate cards per folder via CTE on materialized paths.

Use as starting point before writing repository queries.
