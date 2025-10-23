# Test Data Builders (Claude Prompt)

**Purpose**: Centralise factories/builders for tests.

## Backend
- `UserBuilder`, `FolderBuilder`, `DeckBuilder`, `CardBuilder`, `CardBoxPositionBuilder` from `04-detail-design/03-business-logic-flows.md` appendix.
- Provide defaults aligning with validation rules; override methods for specific fields.

## Frontend
- Mock DTO generators matching API responses (see `02-api-request-response-specs.md`).

## Claude tips
- Encourage reuse of builders instead of inline object creation to reduce boilerplate.
