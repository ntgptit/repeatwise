# Table – folder_stats

**Columns**
- `folder_id UUID PK FK folders`
- `total_cards INT NOT NULL`
- `due_cards INT NOT NULL`
- `new_cards INT NOT NULL`
- `last_calculated_at TIMESTAMP NOT NULL`

**Indexes**
- `idx_folder_stats_due` (due_cards DESC) for dashboards

**Notes**
- Updated after mutations; TTL 5 minutes for cache invalidation.

**Claude tips**
- Use upsert when recalculating to avoid duplicate rows.
