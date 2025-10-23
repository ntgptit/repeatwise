# Backend Setup (Claude Prompt)

**Goal**: Spin up the Spring Boot backend consistent with `00_docs/03-design/architecture/backend-detailed-design.md`.

## Steps summary
1. **Clone & prerequisites** – Java 17, Maven, Docker (for PostgreSQL). Use `DOCKER-SETUP.md` as reference.
2. **Configuration** – Copy `.env.example` → `.env` (see `10-deployment-configs/03-environment-variables.md`); set `SPRING_PROFILES_ACTIVE=local`.
3. **Database** – Start PostgreSQL via docker-compose, run Flyway migrations in order (see `07-database-schemas/13-flyway-migration-order.md`).
4. **Seed data** – Optional dev seeds in `backend-api/src/main/resources/db/dev-seed`. Enable only in local profile.
5. **Run app** – `mvn spring-boot:run` or `./mvnw` command. Ensure ports align with API spec (`localhost:8080`).
6. **Smoke test** – Hit `/actuator/health` and `/api/auth/register` using the request example from `06-api-specifications/01-authentication-apis.md`.

## Claude tips
- When Claude asks for entity definitions, provide only the specific snippets from `04-detail-design/01-entity-specifications.md` needed for the current task.
- Remind it to enable auditing (`@EnableJpaAuditing`) and configure MapStruct as per ADR `08-architecture-decisions/004-why-mapstruct.md`.
