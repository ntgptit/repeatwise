# Table – srs_settings

**Columns**
- `user_id UUID PK FK users`
- `new_cards_per_day INT NOT NULL DEFAULT 20`
- `review_cards_per_day INT NOT NULL DEFAULT 200`
- `review_order_strategy VARCHAR(20) NOT NULL DEFAULT 'ASCENDING'`
- `forgotten_card_action VARCHAR(20) NOT NULL DEFAULT 'RESET_TO_BOX_ONE'`
- `enable_cram_mode BOOLEAN NOT NULL DEFAULT TRUE`
- `created_at`, `updated_at`

**Notes**
- Deck overrides stored separately in `deck_settings`.
- Enum values must match application constants.

**Claude tips**
- Update timestamps whenever settings change; use optimistic locking if available.
