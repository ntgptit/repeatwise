# Error Handling Patterns (Claude Prompt)

**Source**: `00_docs/04-detail-design/06-error-handling-specs.md`.

## Patterns
- Throw domain-specific exceptions (e.g., `FolderDepthExceededException`) mapped to error codes.
- Use global exception handler to convert to standard JSON error format.
- Log errors with context (`userId`, `resourceId`) but exclude sensitive data.
- Frontend: show toast/dialog with user-friendly message and fallback action.

## Claude tips
- Remind Claude to reference the canonical error code list before adding new ones.
