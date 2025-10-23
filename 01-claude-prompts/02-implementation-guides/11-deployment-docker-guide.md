# Deployment & Docker Guide (Claude Prompt)

**Sources**
- `10-deployment-configs/*.md`
- Root `DOCKER-SETUP.md`, `DOCKER-QUICKSTART.md`

## Steps
1. **Local compose** – Use `docker-compose.yml` to start backend, frontend, db. Environment variables defined in `10-deployment-configs/03-environment-variables.md`.
2. **Production image** – Build backend with layered jar (`mvn -Pprod clean package`), frontend with Vite build. See `10-deployment-configs/02-docker-production.md`.
3. **CI/CD** – Reference GitHub Actions workflow summary in `10-deployment-configs/04-cicd-github-actions.md` (build → test → push image → deploy).
4. **Backup/restore** – Use scripts described in `10-deployment-configs/05-database-backup-restore.md`.
5. **Smoke checks** – Health endpoint, DB migrations, static asset availability.

## Claude tips
- Provide only the section relevant to the target environment (dev vs prod) to keep prompts short.
- Remind Claude that Kubernetes is out of scope for MVP; focus on Docker Compose.
