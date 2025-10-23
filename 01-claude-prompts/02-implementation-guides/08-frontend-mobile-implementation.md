# Mobile Implementation (Claude Prompt)

**Goal**: Build React Native features aligned with mobile specs.

## Workflow
1. **Understand UX** – Review relevant flows in `04-detail-design/10-wireframes-mobile.md` and component contracts in `08-frontend-mobile-specs.md`.
2. **Data access** – Use TanStack Query (via React Query for RN) hitting the same endpoints as web; share API client where possible.
3. **State & navigation** – Follow architecture doc: Auth stack → Home tabs (Today, Library, Review). Respect deep link patterns.
4. **Forms** – React Hook Form + Zod; ensure keyboard handling per platform.
5. **Offline** – MVP is online-only; avoid caching strategies implying offline support.
6. **Testing** – Write unit tests with Jest + React Native Testing Library; E2E via Detox if time permits (see testing prompt).

## Claude tips
- Provide only the needed component spec snippet to save tokens.
- Remind Claude to synchronise styling tokens with design system (colors/spacing defined in `08-frontend-mobile-specs.md`).
