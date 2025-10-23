# Implementation Roadmap (Claude Starter)

Use this when you need Claude to plan delivery for RepeatWise MVP.

1. **Bootstrap environment**
   - Backend: follow `01-backend-setup-guide.md` referencing `00_docs/03-design/architecture/backend-detailed-design.md` and `04-detail-design/01-entity-specifications.md`.
   - Frontend web & mobile: see `05-frontend-web-setup.md` and `07-frontend-mobile-setup.md`.
2. **Deliver features in vertical slices**
   - Start with authentication → folders/decks → cards → SRS review → statistics → settings/notifications → import/export.
   - Map each slice to use cases + APIs using `05-use-case-mappings` prompts.
3. **Apply quality gates**
   - Validation & error handling: `04-validation-rules` and `04-detail-design/06-error-handling-specs.md`.
   - Testing: consult `09-testing-specifications` prompts, implementing unit → integration → E2E.
4. **Wrap up**
   - Ensure Flyway migrations align with `07-database-schemas/13-flyway-migration-order.md`.
   - Prepare deployment artifacts via `10-deployment-configs`.

> Ask Claude to expand each bullet into tasks/checklists only when needed to avoid unnecessary tokens.
