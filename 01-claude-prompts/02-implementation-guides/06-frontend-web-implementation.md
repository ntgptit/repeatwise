# Frontend Web Implementation (Claude Prompt)

**Goal**: Implement React features aligned with the UX specs.

## Workflow per feature
1. **Understand the flow** – Read corresponding use case + API prompt. Check wireframes in `04-detail-design/09-wireframes-web.md`.
2. **Design contract** – Review component hierarchy + props tables in `07-frontend-web-specs.md`.
3. **Data layer** – Use TanStack Query hooks hitting endpoints described in `06-api-specifications`. Handle pagination/filtering as defined in `11-pagination-filtering.md`.
4. **Forms & validation** – Use React Hook Form with Zod schemas mirroring backend validation rules.
5. **State & navigation** – Follow routing structure from `frontend-architecture.md`; ensure breadcrumbs match folder hierarchy.
6. **Testing** – Add component tests (Testing Library) and integration tests (Playwright) referencing `09-testing-specifications/03-e2e-testing-guide.md`.

## Claude tips
- Encourage Claude to stub data with typed fixtures from `12-quick-references/02-api-endpoints-cheatsheet.md` for quick prototyping.
- Remind it to keep styling tokens consistent with Tailwind config; no custom CSS unless specified.
