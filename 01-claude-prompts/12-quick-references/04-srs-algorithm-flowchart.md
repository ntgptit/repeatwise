# SRS Algorithm Quick Flow

**Source**: `01-domains/05-review-srs-domain.md` & `04-detail-design/04-srs-algorithm-implementation.md`.

1. Load due + new cards respecting limits.
2. Present card → user selects rating (Again/Hard/Good/Easy).
3. Apply strategy:
   - Again → forgotten strategy (default reset to box 1).
   - Hard → stay same box, halve interval (min 1 day).
   - Good → move +1 box (max 7).
   - Easy → skip one box or ×4 interval (max 7).
4. Update `CardBoxPosition`, log event, refresh stats.

Paste this before diving into algorithm code.
