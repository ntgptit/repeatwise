# Sequence Diagrams (MVP)

## 1) Refresh Token Rotation

```mermaid
sequenceDiagram
  autonumber
  participant FE as Web UI
  participant API as Backend API
  participant DB as PostgreSQL

  FE->>API: POST /api/auth/refresh (with cookie)
  API->>DB: Lookup hashed refresh token
  DB-->>API: Token row (valid?)
  API->>DB: Revoke old token (rotate)
  API-->>FE: New access token + Set‑Cookie(new refresh)
```

## 2) Move Folder with Depth Check

```mermaid
sequenceDiagram
  autonumber
  participant FE as Web UI
  participant API as Backend API
  participant DB as PostgreSQL

  FE->>API: POST /api/folders/{id}/move { destination }
  API->>DB: Load folder + destination
  API-->>API: Validate: not self/descendant; new depth <= 10
  API->>DB: Txn: update parent_id/path/depth for subtree
  DB-->>API: OK
  API-->>FE: 200 Updated folder
```

## 3) Copy Deck (Async Path)

```mermaid
sequenceDiagram
  autonumber
  participant FE as Web UI
  participant API as Backend API
  participant JOB as Job Store
  participant DB as PostgreSQL

  FE->>API: POST /api/decks/{id}/copy { dest }
  API->>DB: Count cards
  DB-->>API: N cards
  API-->>FE: 202 { jobId }
  API->>JOB: Enqueue copy job
  loop Worker
    JOB->>DB: Read chunk of cards
    JOB->>DB: Insert into new deck in batches
    JOB-->>JOB: Update progress
  end
  FE->>API: GET /api/jobs/{jobId}
  API-->>FE: { status: SUCCEEDED, resultId }
```

## 4) Import Cards (Validation Errors)

```mermaid
sequenceDiagram
  autonumber
  participant FE as Web UI
  participant API as Backend API
  participant DB as PostgreSQL

  FE->>API: POST /api/decks/{id}/import (file)
  API-->>FE: 202 { jobId } (for large files)
  Note over API: Stream parse rows
  API->>DB: Batch insert valid rows
  API-->>FE: Error report URL for invalid rows
```

## 5) Review + Rate + Undo

```mermaid
sequenceDiagram
  autonumber
  participant FE as Web UI
  participant API as Backend API
  participant DB as PostgreSQL

  FE->>API: POST /api/review/sessions (scope)
  API->>DB: Fetch due cards
  API-->>FE: sessionId + first card
  FE->>API: POST /api/review/sessions/{id}/rate (GOOD)
  API->>DB: Update box/due_date; insert review_log
  API-->>FE: Next card
  FE->>API: POST /api/review/sessions/{id}/undo
  API->>DB: Restore previous SRS state; adjust log
  API-->>FE: Restored card
```
