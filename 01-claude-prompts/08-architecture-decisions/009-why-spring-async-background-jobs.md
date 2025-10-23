# ADR – Why Spring Async for Background Jobs

**Decision**: Use Spring `@Async` + job tables for long-running tasks (folder copy, imports, exports).

**Rationale**
- Lightweight alternative to full queue systems; sufficient for MVP workload.
- Jobs persisted for progress tracking and retries.
- Aligns with existing Spring Boot infrastructure.

**Alternatives**: RabbitMQ/Kafka (overkill), Quartz (heavier scheduling).

**Claude tips**
- Remind Claude to record job status transitions and to cap concurrent jobs per user.
