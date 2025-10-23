# TypeScript Coding Standards (Claude Prompt)

**Sources**: `00_docs/05-quality/coding-convention-web.md`, `coding-convention-mobile.md`.

## Highlights
- Use TypeScript strict mode; prefer `type` aliases for DTOs, `interface` for contracts.
- Components as function components with explicit props typing.
- Styling via Tailwind (web) or React Native Paper theme (mobile); no inline random styles.
- State management: TanStack Query for server state, Zustand/Context for local state.
- Testing with React Testing Library / Jest; follow data-testid conventions.

## Claude tips
- Remind Claude to maintain consistent import order and avoid default exports unless necessary.
