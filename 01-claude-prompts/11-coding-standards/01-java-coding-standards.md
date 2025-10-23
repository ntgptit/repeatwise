# Java Coding Standards (Claude Prompt)

**Source**: `00_docs/05-quality/coding-convention-backend.md`.

## Highlights
- Java 17, Spring Boot 3, MapStruct for mapping.
- Package by feature (`auth`, `folders`, etc.) with layers inside.
- Use guard clauses (no deep nesting); prefer `Optional` over null checks.
- Logging via SLF4J with structured messages; no `System.out`.
- Tests use JUnit 5 + AssertJ.

## Claude tips
- Mention this prompt before Claude generates backend code to enforce style.
