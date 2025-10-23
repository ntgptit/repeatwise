# ADR – Why Flyway

**Decision**: Use Flyway for database migrations.

**Rationale**
- Versioned scripts tracked in Git; easy rollback strategy.
- Works seamlessly with Spring Boot auto-migration.
- Supports repeatable migrations for seeds and views.

**Alternatives**: Liquibase (heavier XML/JSON configs), manual SQL (error-prone).

**Claude tips**
- Ensure new migrations follow naming and ordering defined in `07-database-schemas/13-flyway-migration-order.md`.
