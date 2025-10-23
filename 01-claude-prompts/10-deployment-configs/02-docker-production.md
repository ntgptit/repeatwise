# Docker – Production Build

**Sources**
- Root `DOCKER-QUICKSTART.md` (production section)
- `DOCKER-SETUP.md` (env variables)
- NFRs in `00_docs/02-system-analysis/nfr.md`

## Steps
1. **Backend image** – Build with `Dockerfile` multi-stage: compile jar, copy into slim runtime. Use environment variables from `03-environment-variables.md`.
2. **Frontend image** – Build static assets via Vite, serve with Nginx (see quickstart doc).
3. **Compose/Stack** – Use production compose file to wire backend, frontend, PostgreSQL; enable HTTPS via reverse proxy (Traefik or Nginx per quickstart).
4. **Secrets** – Inject via environment or Docker secrets (JWT secret, DB credentials).
5. **Monitoring** – Enable health checks; tail logs for errors during deploy.

## Claude tips
- Remind Claude to keep images small (use JDK for build, JRE for runtime) and to configure timezone env for backend.
