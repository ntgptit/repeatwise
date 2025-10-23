# Claude Prompt Packs for RepeatWise

These prompt packs compress the essential knowledge from `00_docs` so you can brief Claude Code/Codex quickly without paying to stream the entire documentation set. Use them as "knowledge bookmarks": start from the pack that matches the task, skim the bullet points, then selectively pull the linked source files when you truly need details.

## How to navigate
1. **Start with [`00-MASTER-KNOWLEDGE-BASE.md`](./00-MASTER-KNOWLEDGE-BASE.md)** for the global context and reading order.
2. Jump into the folder that matches your activity (domains, implementation, APIs, testing, etc.).
3. Copy the relevant snippet into your Claude session, then call out only the referenced documents you actually need Claude to open.

> ⚠️ Reminder: Claude cannot remember hundreds of lines reliably. Feed the minimal pack, confirm understanding, then drip-feed extra documents on demand.

## Folder overview
- `01-domains`: domain-driven snapshots of user, folder, deck, card, SRS, statistics, settings, and import/export contexts.
- `02-implementation-guides`: roadmap-style prompts that map business intent → implementation steps.
- `03-code-templates`: instructions for re-using the pseudo code and skeletons stored in `00_docs/04-detail-design`.
- `04-validation-rules`: high-signal summaries of the validation catalog plus links to canonical specs.
- `05-use-case-mappings`: traceability cheat-sheets connecting use cases → APIs → components.
- `06-api-specifications`: concise endpoint descriptions with request/response highlights and source pointers.
- `07-database-schemas`: ERD highlights, table purposes, and Flyway ordering.
- `08-architecture-decisions`: ADR-style rationales tailored for quick justification in reviews.
- `09-testing-specifications`: what to test, how deep, and where to grab fixtures.
- `10-deployment-configs`: containerisation and release prompts.
- `11-coding-standards`: language-specific guardrails to keep reviews short.
- `12-quick-references`: copy-ready cheat-sheets (entity relationships, API lists, troubleshooting, etc.).

## Token discipline tips
- Summaries rarely exceed 200 tokens—paste them verbatim first.
- Ask Claude to confirm assumptions before fetching a full spec.
- When linking a source file, quote only the exact subsection that matters (line ranges help).
