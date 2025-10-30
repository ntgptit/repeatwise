# Data Flow Diagrams (MVP)

This document captures key flows at a system level using Mermaid diagrams.

## User Registration & Login

```mermaid
sequenceDiagram
  autonumber
  participant U as User
  participant FE as Web UI
  participant API as Backend API
  participant DB as PostgreSQL

  U->>FE: Submit registration form<br/>(email, optional username, password)
  FE->>API: POST /api/auth/register<br/>{email, username?, password}
  API->>API: Validate email format<br/>Validate username format (if provided)
  API->>DB: Check email uniqueness<br/>(case-insensitive)
  API->>DB: Check username uniqueness<br/>(case-sensitive, if provided)
  API->>API: Hash password (bcrypt, cost 12)
  API->>DB: Insert user record<br/>Insert default SRS settings<br/>Insert user_stats
  DB-->>API: User created
  API-->>FE: 201 Created<br/>{message: "Registration successful"}
  FE-->>U: Redirect to Login page

  U->>FE: Submit login form<br/>(username/email, password)
  FE->>API: POST /api/auth/login<br/>{identifier, password}
  API->>API: Detect if identifier is email or username
  API->>DB: Find user by email or username
  DB-->>API: User record
  API->>API: Verify password (bcrypt)
  API->>API: Generate JWT access token<br/>(15 minutes expiry)
  API->>API: Generate refresh token<br/>(7 days expiry)
  API->>API: Hash refresh token (bcrypt)
  API->>DB: Store refresh token<br/>(hashed, expires_at)
  API-->>FE: 200 OK<br/>{access_token}<br/>Set-Cookie: refresh_token
  FE->>FE: Store access_token in memory
  FE-->>U: Redirect to Dashboard
```

## Folder Copy (Sync/Async)

```mermaid
flowchart TD
  A[User clicks Copy Folder] --> B[Count items recursively<br/>folders + decks]
  B --> C{Item count}
  C -->|<= 50 items| D[Sync Copy Mode]
  C -->|51-500 items| E[Async Copy Mode]
  C -->|> 500 items| F[Reject with error<br/>400 Bad Request]
  
  D --> G[Start Transaction]
  G --> H[Create root folder copy<br/>with new UUID]
  H --> I[Recursively copy sub-folders<br/>and decks]
  I --> J[Update paths and depths<br/>for copied subtree]
  J --> K[Commit Transaction]
  K --> L[Return 200 OK<br/>with new folder data]
  
  E --> M[Create Job Record<br/>Status: PENDING]
  M --> N[Return 202 Accepted<br/>with job_id]
  N --> O[Background Worker Starts]
  O --> P[Update Job Status: IN_PROGRESS]
  P --> Q[Process in batches<br/>Update progress every 10 items]
  Q --> R{Completed?}
  R -->|Yes| S[Update Job Status: SUCCEEDED<br/>Store result_id]
  R -->|No| Q
  S --> T[Client polls job status]
  T --> U[Return job result]
```

## Import Cards (CSV/XLSX)

```mermaid
flowchart TD
  A[User uploads file] --> B[Validate file size<br/>max 50MB]
  B --> C{Valid size?}
  C -->|No| D[Return 413<br/>Payload Too Large]
  C -->|Yes| E[Detect format<br/>CSV or XLSX]
  E --> F[Parse file header<br/>Validate required columns<br/>Front, Back]
  F --> G{Valid columns?}
  G -->|No| H[Return 400<br/>Missing required columns]
  G -->|Yes| I[Count total rows]
  I --> J{Row count}
  J -->|<= 10,000| K[Sync Import Mode]
  J -->|> 10,000| L[Return 400<br/>Row limit exceeded]
  
  K --> M[Stream parse rows]
  M --> N[Validate each row<br/>Front/Back required<br/>max 5,000 chars]
  N --> O[Batch insert<br/>1,000 rows per transaction]
  O --> P[Collect errors<br/>per-row validation]
  P --> Q[Return 200 OK<br/>with import summary<br/>imported/skipped/failed]
  
  R[Async Mode if needed] --> S[Create Job Record]
  S --> T[Background Worker]
  T --> U[Stream parse + batch insert]
  U --> V[Update progress]
  V --> W[Return job_id]
```

## Review Session & Rating

```mermaid
sequenceDiagram
  autonumber
  participant U as User
  participant FE as Web UI
  participant API as Backend API
  participant DB as PostgreSQL

  U->>FE: Start Review Session<br/>(scope: DECK or FOLDER)
  FE->>API: POST /api/review/sessions<br/>{scopeType, scopeId}
  API->>DB: Fetch user's SRS settings<br/>(daily limits, review order)
  DB-->>API: SRS settings
  API->>DB: Query due cards<br/>WHERE due_date <= today<br/>AND user_id = ?<br/>ORDER BY due_date ASC, current_box ASC<br/>LIMIT 200
  API->>DB: Check daily review count<br/>respect max_reviews_per_day
  DB-->>API: Due cards list
  API->>API: Create session queue
  API-->>FE: 200 OK<br/>{sessionId, firstCard, totalCards}
  
  U->>FE: View card Front
  FE->>FE: Start timer (time_taken_ms)
  U->>FE: Reveal card Back
  U->>FE: Rate card (GOOD)
  FE->>API: POST /api/review/sessions/{id}/rate<br/>{cardId, rating: "GOOD", timeTakenMs}
  
  API->>DB: Get current card SRS state<br/>(current_box, due_date)
  DB-->>API: Card box position
  API->>API: Apply SRS algorithm<br/>GOOD: increment box by 1<br/>Calculate new due_date
  API->>DB: Start Transaction
  API->>DB: Update card_box_position<br/>SET current_box = ?,<br/>due_date = ?,<br/>last_reviewed_at = NOW()
  API->>DB: Insert review_log<br/>(rating, time_taken_ms, session_id)
  API->>DB: Increment daily review counter
  API->>DB: Commit Transaction
  DB-->>API: Success
  API->>DB: Get next card from queue
  DB-->>API: Next card or null
  API-->>FE: 200 OK<br/>{nextCard, remaining, progress}
  
## Move Folder with Depth Validation

```mermaid
flowchart TD
  A[User requests move folder] --> B[Load source folder<br/>Load destination folder]
  B --> C{Validate ownership}
  C -->|Not owner| D[Return 403 Forbidden]
  C -->|Owner| E{Check cycle prevention}
  E -->|Moving into self| F[Return 400<br/>Cannot move into itself]
  E -->|Moving into descendant| G[Return 400<br/>Cannot move into descendant]
  E -->|Valid| H[Calculate new depth<br/>source_depth + destination_depth + 1]
  H --> I{New depth <= 10?}
  I -->|No| J[Return 400<br/>Max depth exceeded]
  I -->|Yes| K[Start Transaction]
  K --> L[Update source folder<br/>SET parent_id = destination_id<br/>SET depth = new_depth]
  L --> M[Update materialized path<br/>for source folder]
  M --> N[Recursively update paths<br/>and depths for all descendants]
  N --> O[Commit Transaction]
  O --> P[Return 200 OK<br/>with updated folder]
```
