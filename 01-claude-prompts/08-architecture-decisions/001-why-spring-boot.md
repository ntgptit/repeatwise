# ADR – Why Spring Boot

**Decision**: Use Spring Boot 3 for backend.

**Rationale**
- Aligns with team expertise and existing conventions in `00_docs/05-quality/coding-convention-backend.md`.
- Provides built-in support for JPA, validation, security, and Flyway.
- Rapid prototyping with starter dependencies while supporting production-grade features.

**Alternatives considered**: Micronaut (less mature ecosystem), Node/NestJS (team lacks backend TS experience).

**Claude tips**
- When Claude suggests switching stacks, cite this ADR to keep conversation focused.
