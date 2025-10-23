# Backend Layer Implementation (Claude Prompt)

**Goal**: Implement features respecting the layered architecture.

## Layer stack
- **Controller** – REST endpoints from `06-api-specifications`. Validate input via DTO annotations + service-level checks.
- **Service** – Business logic referencing `04-detail-design/03-business-logic-flows.md` and domain prompts.
- **Repository** – Spring Data JPA repositories; follow entity specs in `04-detail-design/01-entity-specifications.md` and query hints from `03-design/database/indexing-strategy.md`.
- **Mapper/DTO** – MapStruct mappers per ADR `004-why-mapstruct.md`; DTO contracts defined in `02-api-request-response-specs.md`.

## Implementation steps per feature
1. Read corresponding use case & API prompt to understand inputs/outputs.
2. Update entities/migrations if necessary (check database prompt first).
3. Implement service logic using pseudo code from detail design; ensure transactions wrap operations touching multiple aggregates.
4. Add controller endpoints; apply validation groups.
5. Write unit tests (services) + integration tests (controllers) referencing `09-testing-specifications`.

## Claude tips
- When Claude attempts to bypass service layer, remind it all business logic lives in services.
- Provide only the necessary pseudo-code snippet instead of entire documents to save tokens.
