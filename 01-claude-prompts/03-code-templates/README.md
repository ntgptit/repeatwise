# Code Template Prompts

Use this prompt before asking Claude to generate boilerplate. It points to the canonical pseudo code and template snippets stored in `00_docs/04-detail-design`.

## Key references
- `04-detail-design/03-business-logic-flows.md` – Service-level pseudo code for each use case.
- `04-detail-design/04-srs-algorithm-implementation.md` – Core SRS helper methods.
- `04-detail-design/07-frontend-web-specs.md` & `08-frontend-mobile-specs.md` – Component skeletons.
- `04-detail-design/02-api-request-response-specs.md` – DTO/contract templates.

## Usage tips
- Paste only the exact pseudo code block for the feature you’re implementing to save tokens.
- Ask Claude to convert pseudo code → concrete class/function, preserving method names and error codes.
- Keep placeholder comments (e.g. `// TODO logging`) unless the spec explicitly says to remove.
