# Naming Conventions (Claude Prompt)

**Source**: Coding convention docs across backend/web/mobile.

## Rules
- Packages/folders lowercase with hyphen or dot notation (e.g., `com.repeatwise.auth`).
- Classes PascalCase (`UserService`), interfaces prefixed `I` only when necessary (avoid otherwise).
- Methods camelCase; constants UPPER_SNAKE.
- React components PascalCase, hooks use `useSomething`.
- Files: backend `UserService.java`, frontend `folder-card-list.tsx`, tests `*.test.tsx` or `*Test.java`.

## Claude tips
- Mention this prompt if Claude proposes snake_case method names or inconsistent file names.
