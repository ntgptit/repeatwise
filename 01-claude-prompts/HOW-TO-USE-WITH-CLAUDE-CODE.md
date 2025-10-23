# How to work with Claude Code efficiently

1. **Start small**: Paste the relevant prompt pack (usually <200 tokens). Ask Claude to restate the goal; if it struggles, clarify before sharing specs.
2. **Reference, don't dump**: When Claude needs a spec, quote the minimal excerpt from `00_docs` (e.g. only the API method definition, not the whole file). Mention the file path so humans can verify later.
3. **Chunk large docs**: For long procedures (e.g. SRS algorithm design), split into numbered snippets of ≤150 lines. After each paste, ask Claude to summarise the key points before proceeding.
4. **Use scratchpads**: Encourage Claude to draft pseudo code or outline test cases first. Compare against `04-detail-design` pseudo code before finalising.
5. **Guard rails**: Remind Claude about coding standards (`11-coding-standards`) and validation rules whenever implementing backend or frontend logic.
6. **Token hygiene**: Close the conversation and reopen with a fresh paste if you exceed ~6K tokens. Re-send only the prompt packs you actually need for the new topic.
7. **Review checkpoints**: After Claude proposes a solution, cross-check with the linked documents (API specs, ERD, validation) before committing the change.
