# ADR – Why 7-box SRS

**Decision**: Use fixed 7-box Leitner intervals (1,3,7,14,30,60,120 days).

**Rationale**
- Simple enough for MVP while reflecting proven spaced repetition.
- Easy to explain in UI and align with statistics (box distribution).
- Works with configurable strategies (skip, stay, reset) without dynamic SM-2 complexity.

**Alternatives**: SM-2 algorithm (more complex, requires more data), Anki-style variable intervals.

**Claude tips**
- Reference this when Claude suggests dynamic interval calculations.
