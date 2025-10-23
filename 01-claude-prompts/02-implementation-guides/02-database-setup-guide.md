# Database Setup (Claude Prompt)

**Goal**: Initialise PostgreSQL schema for RepeatWise.

## Checklist
1. **Create database** – Use docker-compose service defined in repo root; default DB name `repeatwise`. Credentials documented in `10-deployment-configs/01-docker-development.md`.
2. **Apply migrations** – Run Flyway via Spring Boot startup. Ensure scripts follow the order listed in `07-database-schemas/13-flyway-migration-order.md`.
3. **Extensions & settings** – Enable `uuid-ossp` and `pgcrypto` as per schema doc `03-design/database/schema.md`.
4. **Verify tables** – Cross-check using `07-database-schemas/00-complete-erd.md`; confirm sequences and foreign keys exist.
5. **Seed data (optional)** – For dev, insert sample user + deck as described in `backend-api` README.

## Claude tips
- When editing migrations, pull only the relevant table spec from `07-database-schemas/*` to avoid copying the full ERD.
- Reinforce naming conventions: tables snake_case, indexes `idx_<table>_<column>`, per ADR `010-why-soft-delete.md` (soft delete columns).
