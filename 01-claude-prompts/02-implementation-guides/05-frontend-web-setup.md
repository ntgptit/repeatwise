# Frontend Web Setup (Claude Prompt)

**Sources**
- `00_docs/03-design/architecture/frontend-architecture.md`
- `00_docs/04-detail-design/07-frontend-web-specs.md`

## Steps summary
1. **Prerequisites** – Node 18+, PNPM. Install dependencies with `pnpm install`.
2. **Environment** – Copy `.env.example` → `.env.local`; configure API base URL, Sentry (optional), feature flags. Reference `10-deployment-configs/03-environment-variables.md` for keys.
3. **Design system** – Tailwind + Shadcn; run `pnpm shadcn:add` only with approved components listed in spec.
4. **State management** – Use TanStack Query for server data, Zustand/Context for local state per architecture doc.
5. **Token handling** – Leverage Axios interceptor for JWT refresh (see `frontend-architecture.md` token flow).
6. **Lint/test** – Run `pnpm lint` and `pnpm test` before PR.

## Claude tips
- When Claude requests UI copy or layout, provide screenshot references from `04-detail-design/09-wireframes-web.md` as-needed.
- Remind it that forms use React Hook Form + Zod per spec.
