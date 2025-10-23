# Table – deck_settings

**Columns**
- `deck_id UUID PK FK decks`
- `new_cards_per_day INT NULL`
- `review_cards_per_day INT NULL`
- `review_order_strategy VARCHAR(20) NULL`
- `forgotten_card_action VARCHAR(20) NULL`
- `created_at`, `updated_at`

**Notes**
- Null values inherit from `srs_settings`.
- Use when deck-specific overrides differ from user default.

**Claude tips**
- When saving overrides, explicitly set null to remove override.
