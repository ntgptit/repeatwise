# CI/CD – GitHub Actions

**Sources**: `DOCKER-TEST-RESULTS.md`, repo workflows (if present).

## Pipeline outline
1. **Build** – Backend `mvn verify`, frontend `pnpm build`.
2. **Test** – Run unit/integration suites; collect coverage artifacts.
3. **Docker build/push** – Build backend/frontend images, push to registry (tag `main-<sha>`).
4. **Deploy** – Trigger deploy job (manual approval for production).

## Claude tips
- When updating workflows, remind Claude to cache Maven/PNPM directories and to upload test reports for visibility.
