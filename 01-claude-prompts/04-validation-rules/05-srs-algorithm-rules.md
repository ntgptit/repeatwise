# SRS Algorithm Rules (Claude Prompt)

**Source**: `00_docs/04-detail-design/04-srs-algorithm-implementation.md` + `05-validation-rules.md` §5.

## Key rules
- Ratings limited to `AGAIN`, `HARD`, `GOOD`, `EASY`. Reject others.
- Daily review queue ensures due cards loaded by timezone; new cards limited by `SrsSettings.newCardsPerDay` and deck overrides.
- Prevent duplicate reviews in same session (`ReviewLog` ensures unique card per active session unless marked retry).
- Box bounds 1–7; guard against under/overflow when applying strategies.
- Forgotten card strategies allowed: reset to box 1 (default), move down one box (min 1), stay (no change). Reject combos conflicting with user settings.

## Claude tips
- Provide only the rating transition table when coding the service to stay within token limits.
- Remind Claude to wrap updates in transactions to keep `CardBoxPosition` and `ReviewLog` consistent.
