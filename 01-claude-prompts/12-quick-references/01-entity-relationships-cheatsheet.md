# Entity Relationship Cheatsheet

**Source**: `00_docs/03-design/database/schema.md`.

- User 1—N Folders, Decks, RefreshTokens; 1—1 SrsSettings, UserStats, NotificationSettings.
- Folder 1—N Folders (children), 1—N Decks.
- Deck 1—N Cards; 1—1 DeckSettings (optional).
- Card 1—1 CardBoxPosition (per user), 1—N ReviewLogs.
- ReviewSession 1—N ReviewLogs.

Use this to quickly remind Claude of relationships before writing queries.
