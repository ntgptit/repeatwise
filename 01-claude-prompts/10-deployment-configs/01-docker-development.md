# Docker – Development Setup

**Source**: `DOCKER-SETUP.md`, `00_docs/03-design/architecture/backend-detailed-design.md` (env).

## Steps
- Run `docker-compose up` to start PostgreSQL + supporting services.
- Backend connects via `SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/repeatwise`.
- Frontend uses `.env.local` pointing to `http://localhost:8080`.
- Use volume mounts for DB persistence under `./data/postgres`.

## Claude tips
- Remind Claude not to expose credentials; use `.env` placeholders.
