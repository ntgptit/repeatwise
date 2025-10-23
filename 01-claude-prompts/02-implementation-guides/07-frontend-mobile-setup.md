# Mobile Setup (Claude Prompt)

**Sources**
- `00_docs/03-design/architecture/frontend-architecture.md`
- `00_docs/04-detail-design/08-frontend-mobile-specs.md`

## Steps summary
1. **Prerequisites** – Node 18+, PNPM, Xcode/Android Studio for simulators.
2. **Install deps** – `pnpm install`, then `pnpm expo prebuild` if using native modules (see spec section 2.3).
3. **Environment** – Copy `.env.example` → `.env`; configure API base, Expo push keys.
4. **Design system** – React Native Paper + custom theme tokens from mobile specs.
5. **Navigation** – Set up React Navigation stack/tabs per architecture doc (Auth stack → App tabs).
6. **Device integrations** – Push notifications via Expo; follow `08-frontend-mobile-specs.md` §5 for permissions flow.

## Claude tips
- Keep file sizes lean by referencing only the component sections you’re touching in `08-frontend-mobile-specs.md`.
- Validate forms with React Hook Form + Zod identical to web.
