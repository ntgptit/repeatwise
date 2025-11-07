# API Contracts Summary (MVP)

This is a concise summary of key REST endpoints. Detailed contracts should be maintained in `00_docs/06-api/` and OpenAPI (`openapi-spec.yml`).

## Authentication Endpoints

### POST `/api/auth/register`

**Purpose**: Register a new user account

**Request Body**:

```json
{
  "email": "user@example.com",           // Required, unique, valid email format
  "username": "john_doe123",              // Optional, 3-30 chars, alphanumeric + underscore/hyphen, unique if provided
  "password": "Password123",              // Required, min 8 characters
  "name": "John Doe"                      // Optional
}
```

**Response**:

- `201 Created`: Registration successful

  ```json
  {
    "message": "Registration successful. Please login."
  }
  ```

- `400 Bad Request`: Validation errors (email exists, username exists, invalid format, weak password)
- `409 Conflict`: Email or username already exists

**Behavior**: Does not auto-login; redirects to login page

### POST `/api/auth/login`

**Purpose**: Authenticate user and receive tokens

**Request Body**:

```json
{
  "identifier": "user@example.com",       // Username or email
  "password": "Password123"
}
```

**Response**:

- `200 OK`: Login successful

  ```json
  {
    "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": "uuid",
      "email": "user@example.com",
      "username": "john_doe123",
      "name": "John Doe"
    }
  }
  ```

  - Sets HTTP-only cookie: `refresh_token`
- `401 Unauthorized`: Invalid credentials

**Behavior**: System detects if identifier is email or username automatically

### POST `/api/auth/refresh`

**Purpose**: Rotate refresh token and get new access token

**Request**: Cookie with `refresh_token` (HTTP-only)

**Response**:

- `200 OK`: Token refreshed

  ```json
  {
    "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
  ```

  - Sets new HTTP-only cookie: `refresh_token` (rotated)
- `401 Unauthorized`: Refresh token expired or invalid

**Behavior**: Old refresh token is revoked (one-time use)

### POST `/api/auth/logout`

**Purpose**: Logout current device

**Request**: JWT access token in Authorization header

**Response**:

- `200 OK`: Logout successful
  - Clears refresh token cookie
  - Revokes current refresh token in database

### POST `/api/auth/logout-all`

**Purpose**: Logout all devices

**Request**: JWT access token in Authorization header

**Response**:

- `200 OK`: All sessions terminated
  - Revokes all refresh tokens for user

## User Management Endpoints

### GET `/api/users/me`

**Purpose**: Get current user profile

**Response**:

```json
{
  "id": "uuid",
  "email": "user@example.com",
  "username": "john_doe123",              // Nullable
  "name": "John Doe",
  "timezone": "Asia/Ho_Chi_Minh",
  "language": "VI",                        // VI or EN
  "theme": "SYSTEM",                       // LIGHT, DARK, or SYSTEM
  "created_at": "2024-01-01T00:00:00Z",
  "updated_at": "2024-01-01T00:00:00Z"
}
```

### PATCH `/api/users/me`

**Purpose**: Update user profile

**Request Body**:

```json
{
  "name": "John Doe",                     // Optional, 1-100 chars
  "username": "new_username",             // Optional, 3-30 chars, unique if set
  "timezone": "Asia/Ho_Chi_Minh",         // Optional, valid IANA timezone
  "language": "EN",                       // Optional, VI or EN
  "theme": "DARK"                         // Optional, LIGHT, DARK, or SYSTEM
}
```

**Response**:

- `200 OK`: Profile updated
- `400 Bad Request`: Validation errors
- `409 Conflict`: Username already exists

### PATCH `/api/users/me/password`

**Purpose**: Change password

**Request Body**:

```json
{
  "current_password": "OldPassword123",
  "new_password": "NewPassword123"        // Min 8 characters
}
```

**Response**:

- `200 OK`: Password changed
  - All refresh tokens revoked
  - User redirected to login
- `400 Bad Request`: Current password incorrect or validation error

### GET `/api/users/me/stats`

**Purpose**: Get user statistics

**Response**:

```json
{
  "total_cards": 1250,
  "total_decks": 15,
  "total_folders": 8,
  "cards_reviewed_today": 45,
  "streak_days": 7,
  "last_study_date": "2024-01-15",
  "total_study_time_minutes": 1250
}
```

## SRS Settings Endpoints

### GET `/api/srs-settings`

**Purpose**: Get user SRS settings

**Response**:

```json
{
  "total_boxes": 7,                       // 3-10, default: 7
  "review_order": "DUE_DATE_ASC",         // DUE_DATE_ASC, RANDOM, CURRENT_BOX_ASC
  "new_cards_per_day": 20,               // 1-500, default: 20
  "max_reviews_per_day": 200,            // 1-1000, default: 200
  "forgotten_card_action": "MOVE_TO_BOX_1", // MOVE_TO_BOX_1, MOVE_DOWN_N_BOXES, REPEAT_IN_SESSION
  "move_down_boxes": 1,                  // 1-3, required if forgotten_card_action = MOVE_DOWN_N_BOXES
  "notification_enabled": true,
  "notification_time": "09:00"
}
```

### PATCH `/api/srs-settings`

**Purpose**: Update SRS settings

**Request Body**: Same fields as GET response (all optional)

**Response**:

- `200 OK`: Settings updated
- `400 Bad Request`: Validation errors (out of range values)

## Folder Management Endpoints

### GET `/api/folders?parentId={parentId}`

**Purpose**: List folders and decks under a parent (paginated)

**Query Parameters**:

- `parentId`: UUID or null (for root level)
- `page`: Page number (default: 0)
- `size`: Page size (default: 50)

**Response**:

```json
{
  "content": [
    {
      "id": "uuid",
      "type": "FOLDER",                   // or "DECK"
      "name": "Folder Name",
      "description": "Optional description",
      "depth": 2,
      "created_at": "2024-01-01T00:00:00Z",
      "updated_at": "2024-01-01T00:00:00Z"
    }
  ],
  "total_elements": 25,
  "total_pages": 1,
  "page": 0,
  "size": 50
}
```

### POST `/api/folders`

**Purpose**: Create folder

**Request Body**:

```json
{
  "parent_id": "uuid",                   // Nullable (root level)
  "name": "New Folder",                   // Required, max 100 chars, unique per parent
  "description": "Optional description"
}
```

**Response**:

- `201 Created`: Folder created

  ```json
  {
    "id": "uuid",
    "name": "New Folder",
    "parent_id": "uuid",
    "path": "/uuid",
    "depth": 1,
    "created_at": "2024-01-01T00:00:00Z"
  }
  ```

- `400 Bad Request`: Validation errors (max depth exceeded, duplicate name)
- `404 Not Found`: Parent folder not found

**Validation**: Max depth 10, unique name per parent (case-insensitive)

### PATCH `/api/folders/{folderId}`

**Purpose**: Rename or update folder description

**Request Body**:

```json
{
  "name": "Renamed Folder",              // Optional
  "description": "New description"       // Optional
}
```

**Response**:

- `200 OK`: Folder updated
- `400 Bad Request`: Validation errors (duplicate name)
- `404 Not Found`: Folder not found

### POST `/api/folders/{folderId}/move`

**Purpose**: Move folder to new location

**Request Body**:

```json
{
  "destination_folder_id": "uuid"        // Nullable (root level)
}
```

**Response**:

- `200 OK`: Folder moved
- `400 Bad Request`: Cannot move into self/descendant, max depth exceeded
- `404 Not Found`: Folder or destination not found

**Validation**: Cycle prevention, depth check (max 10)

### POST `/api/folders/{folderId}/copy`

**Purpose**: Copy folder (including sub-folders and decks)

**Request Body**:

```json
{
  "destination_folder_id": "uuid",      // Nullable (root level)
  "name": "Folder Copy"                  // Optional, auto-appends " (Copy)" if needed
}
```

**Response**:

- `200 OK`: Folder copied synchronously (≤50 items)

  ```json
  {
    "folder": {...},
    "copied_items": 25
  }
  ```

- `202 Accepted`: Copy job enqueued (51-500 items)

  ```json
  {
    "job_id": "uuid",
    "message": "Copy job started"
  }
  ```

- `400 Bad Request`: Folder too large (>500 items), max depth exceeded

**Behavior**: Sync ≤50 items, async 51-500 items, reject >500 items

### DELETE `/api/folders/{folderId}`

**Purpose**: Soft delete folder (recursive)

**Response**:

- `200 OK`: Folder deleted
  - Sets `deleted_at` timestamp
  - Recursively soft-deletes all descendants

### GET `/api/folders/{folderId}/stats`

**Purpose**: Get recursive folder statistics

**Response**:

```json
{
  "total_decks": 15,                     // Recursive count
  "total_cards": 1250,                   // Recursive count
  "due_cards": 45,                       // Recursive count
  "new_cards": 12,                       // Recursive count
  "last_modified": "2024-01-15T10:30:00Z"
}
```

## Deck Management Endpoints

### GET `/api/decks?folderId={folderId}`

**Purpose**: List decks (paginated)

**Query Parameters**:

- `folderId`: UUID or null (for root level decks)
- `page`: Page number (default: 0)
- `size`: Page size (default: 50)

**Response**: Paginated list of decks

### POST `/api/decks`

**Purpose**: Create deck

**Request Body**:

```json
{
  "folder_id": "uuid",                    // Nullable (root level)
  "name": "New Deck",                     // Required, max 100 chars, unique per folder
  "description": "Optional description"
}
```

**Response**:

- `201 Created`: Deck created
- `400 Bad Request`: Validation errors (duplicate name)
- `404 Not Found`: Folder not found

### POST `/api/decks/{deckId}/copy`

**Purpose**: Copy deck (including all cards)

**Request Body**:

```json
{
  "destination_folder_id": "uuid",      // Nullable (root level)
  "name": "Deck Copy"                    // Optional
}
```

**Response**:

- `200 OK`: Deck copied synchronously (≤1,000 cards)
- `202 Accepted`: Copy job enqueued (1,001-10,000 cards)

  ```json
  {
    "job_id": "uuid"
  }
  ```

- `400 Bad Request`: Deck too large (>10,000 cards)

## Card Management Endpoints

### GET `/api/decks/{deckId}/cards?page={page}`

**Purpose**: List cards in deck (paginated)

**Query Parameters**:

- `page`: Page number (default: 0)
- `size`: Page size (default: 100)

**Response**: Paginated list of cards

### POST `/api/decks/{deckId}/cards`

**Purpose**: Create card

**Request Body**:

```json
{
  "front": "Card front text",            // Required, max 5,000 chars
  "back": "Card back text"               // Required, max 5,000 chars
}
```

**Response**:

- `201 Created`: Card created
- `400 Bad Request`: Validation errors (empty front/back, exceeds max length)

## Import/Export Endpoints

### POST `/api/decks/{deckId}/import`

**Purpose**: Import cards from CSV/XLSX file

**Request**: Multipart form data with file

**Constraints**:

- File size: max 50MB
- Row limit: max 10,000 rows
- Required columns: Front, Back
- Format: CSV (UTF-8) or Excel (.xlsx)

**Response**:

- `200 OK`: Import completed synchronously

  ```json
  {
    "imported": 950,
    "skipped": 10,
    "failed": 5,
    "errors": [...]
  }
  ```

- `202 Accepted`: Import job enqueued (large files)

  ```json
  {
    "job_id": "uuid"
  }
  ```

- `400 Bad Request`: Invalid format, missing columns, exceeds limits
- `413 Payload Too Large`: File size exceeds 50MB

### GET `/api/decks/{deckId}/export?format={format}&scope={scope}`

**Purpose**: Export cards to CSV/XLSX

**Query Parameters**:

- `format`: `csv` or `xlsx` (default: csv)
- `scope`: `ALL` or `DUE_ONLY` (default: ALL)

**Response**:

- `200 OK`: File download (sync for ≤5,000 cards)
- `202 Accepted`: Export job enqueued (>5,000 cards)
- `400 Bad Request`: Invalid parameters

### GET `/api/jobs/{jobId}`

**Purpose**: Get async job status

**Response**:

```json
{
  "job_id": "uuid",
  "status": "IN_PROGRESS",               // PENDING, IN_PROGRESS, SUCCEEDED, FAILED
  "progress": 65,                        // Percentage (0-100)
  "message": "Processing...",
  "result_id": "uuid",                   // Present if SUCCEEDED
  "error": "Error message"               // Present if FAILED
}
```

## Review (SRS) Endpoints

### POST `/api/review/sessions`

**Purpose**: Start review session

**Request Body**:

```json
{
  "scope_type": "DECK",                  // DECK or FOLDER
  "scope_id": "uuid"                    // Deck ID or Folder ID
}
```

**Response**:

```json
{
  "session_id": "uuid",
  "total_cards": 120,
  "first_card": {
    "id": "uuid",
    "front": "Card front text",
    "back": "Card back text"
  }
}
```

**Behavior**: Respects daily limits (new_cards_per_day, max_reviews_per_day)

### POST `/api/review/sessions/{sessionId}/rate`

**Purpose**: Rate current card

**Request Body**:

```json
{
  "card_id": "uuid",
  "rating": "GOOD",                      // AGAIN, HARD, GOOD, or EASY
  "time_taken_ms": 5000                  // Time in milliseconds
}
```

**Response**:

```json
{
  "next_card": {...},                    // Next card or null
  "remaining": 119,
  "progress": {
    "completed": 1,
    "total": 120
  },
  "completed": false                     // true if session complete
}
```

**Behavior**: Applies SRS algorithm, updates box/due_date, creates review log

### POST `/api/review/sessions/{sessionId}/undo`

**Purpose**: Undo last rating

**Response**:

```json
{
  "card": {...},                         // Restored card
  "restored": true
}
```

**Behavior**: Restores previous SRS state, removes last review log (windowed)

### POST `/api/review/sessions/{sessionId}/skip`

**Purpose**: Skip current card (no SRS change)

**Response**:

```json
{
  "next_card": {...},
  "skipped": true
}
```

**Behavior**: Moves card to end of queue, does not change SRS state

## Statistics Endpoints

### GET `/api/stats/box-distribution?scopeType={scopeType}&scopeId={scopeId}`

**Purpose**: Get box distribution statistics

**Query Parameters**:

- `scopeType`: `ALL`, `FOLDER`, or `DECK`
- `scopeId`: UUID (required if scopeType is FOLDER or DECK)

**Response**:

```json
{
  "box_distribution": [
    {"box": 1, "count": 150},
    {"box": 2, "count": 80},
    {"box": 3, "count": 45},
    ...
  ],
  "total_cards": 1250
}
```

## Common Behaviors

- All endpoints (except public auth) require JWT access token.
- Soft‑deleted records are excluded by default.
- Pagination defaults: folders/decks 50 per page, cards 100 per page.
- Validation errors return 400 with field details; 401/403 for authz errors; 404 if not found; 409 for conflicts; 429 for rate limits (future).
