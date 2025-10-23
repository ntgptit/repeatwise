# ADR – Why MapStruct

**Decision**: Use MapStruct for mapping between entities and DTOs.

**Rationale**
- Compile-time safety, avoids reflection cost.
- Keeps controllers/services concise; mapping rules centralised.
- Works well with immutable DTOs defined in `02-api-request-response-specs.md`.

**Alternatives**: Manual mapping (verbose), ModelMapper (runtime reflection).

**Claude tips**
- Remind Claude to create mapper interfaces and leverage dependency injection.
