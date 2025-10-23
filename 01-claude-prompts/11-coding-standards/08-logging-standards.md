# Logging Standards (Claude Prompt)

**Guidelines**
- Use SLF4J `logger` with parameterised messages.
- Levels: `INFO` for lifecycle events, `DEBUG` for detailed diagnostics, `WARN` for recoverable issues, `ERROR` for failures.
- Include correlation IDs when available (e.g., request ID) and user context.
- Avoid logging sensitive data (passwords, tokens, card content).

## Claude tips
- Encourage Claude to add structured logging (key=value) to ease parsing.
