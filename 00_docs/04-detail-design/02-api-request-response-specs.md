# API Request/Response Specifications - RepeatWise MVP

## Document Purpose

This document provides **detailed DTO specifications** for critical endpoints to enable AI-assisted code generation with full validation rules. For complete endpoint list, see [API Endpoints Summary](../03-design/api/api-endpoints-summary.md).

**Detailed Specifications**: 6 critical endpoints with full request/response DTOs and validation
**Brief References**: All other endpoints reference the summary document

---

## Critical Endpoints - Full Specifications

### 1. POST /api/auth/login

**Purpose**: Authenticate user and return JWT access token with refresh token in cookie

#### Request DTO: LoginRequest

| Field | Type | Required | Constraints | Example |
|-------|------|----------|-------------|---------|
| email | String | Yes | Valid email format, max 255 chars | "user@example.com" |
| password | String | Yes | Min 8 chars, max 255 chars | "securepass123" |

#### Validation Rules
- `email`: Must match regex `^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}$`
- `email`: Lowercase before validation
- `password`: No whitespace trimming (preserve exact password)
- Rate limit: 5 attempts per minute per IP

#### Response DTO: LoginResponse

**Success (200 OK)**:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 900
}
```

| Field | Type | Description |
|-------|------|-------------|
| accessToken | String | JWT token (HS256, 15 min expiry) |
| expiresIn | Integer | Token lifetime in seconds (900) |

**Set-Cookie Header**:
```
refresh_token=<token>; HttpOnly; Secure; SameSite=Strict; Max-Age=604800; Path=/api/auth
```

#### Error Responses

| Status | Error Code | Message | Scenario |
|--------|------------|---------|----------|
| 400 | VALIDATION_ERROR | "Email is required" | Missing email field |
| 400 | VALIDATION_ERROR | "Invalid email format" | Malformed email |
| 400 | VALIDATION_ERROR | "Password must be at least 8 characters" | Password too short |
| 401 | INVALID_CREDENTIALS | "Invalid email or password" | Wrong credentials |
| 429 | RATE_LIMIT_EXCEEDED | "Too many login attempts. Try again in 60 seconds" | >5 attempts/min |

---

### 2. POST /api/folders

**Purpose**: Create a new folder in the user's hierarchy

#### Request DTO: CreateFolderRequest

| Field | Type | Required | Constraints | Example |
|-------|------|----------|-------------|---------|
| name | String | Yes | Min 1, max 100 chars, no leading/trailing spaces | "Business English" |
| description | String | No | Max 500 chars | "Business vocabulary" |
| parentFolderId | UUID | No | Must exist and belong to user | "a1b2c3d4..." |

#### Validation Rules
- `name`: Trim whitespace, must not be empty after trim
- `name`: No special characters except: `-`, `_`, `.`, spaces
- `name`: Must be unique within parent folder (case-insensitive)
- `parentFolderId`: If null, creates folder at root level
- `parentFolderId`: If provided, must exist and belong to authenticated user
- Depth limit: New folder cannot exceed depth 10 in hierarchy
- User limit: Max 1000 folders per user (business rule)

#### Response DTO: FolderResponse

**Success (201 Created)**:
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Business English",
  "description": "Business vocabulary",
  "parentId": "a1b2c3d4-5678-90ab-cdef-123456789012",
  "depth": 2,
  "path": "/uuid1/uuid2/uuid3",
  "createdAt": "2025-01-10T10:30:00Z",
  "updatedAt": "2025-01-10T10:30:00Z"
}
```

| Field | Type | Description |
|-------|------|-------------|
| id | UUID | Generated folder ID |
| name | String | Folder name (trimmed) |
| description | String | Folder description (nullable) |
| parentId | UUID | Parent folder ID (null if root) |
| depth | Integer | Depth in hierarchy (0 = root) |
| path | String | Full path from root (slash-separated UUIDs) |
| createdAt | DateTime | ISO 8601 timestamp (UTC) |
| updatedAt | DateTime | ISO 8601 timestamp (UTC) |

#### Error Responses

| Status | Error Code | Message | Scenario |
|--------|------------|---------|----------|
| 400 | VALIDATION_ERROR | "Folder name is required" | Missing/empty name |
| 400 | VALIDATION_ERROR | "Folder name cannot exceed 100 characters" | Name too long |
| 400 | INVALID_FORMAT | "Folder name contains invalid characters" | Contains `<>:"/\|?*` |
| 404 | NOT_FOUND | "Parent folder not found" | Invalid parentFolderId |
| 409 | DUPLICATE_NAME | "Folder 'Business English' already exists in this location" | Name conflict |
| 422 | HIERARCHY_LIMIT | "Maximum folder depth (10) exceeded" | Too deep |
| 422 | QUOTA_EXCEEDED | "Maximum folder limit (1000) reached" | User quota exceeded |

---

### 3. POST /api/folders/{id}/copy

**Purpose**: Copy folder with all descendants and optionally decks/cards (sync for <=50 items, async for 51-500 items)

#### Request DTO: CopyFolderRequest

| Field | Type | Required | Constraints | Example |
|-------|------|----------|-------------|---------|
| destinationFolderId | UUID | No | Must exist and belong to user | null (copies to root) |
| copyDecks | Boolean | Yes | - | true |

#### Validation Rules
- `destinationFolderId`: If null, copies to root level
- `destinationFolderId`: If provided, must exist and belong to user
- **Circular Reference Check**: Source folder cannot be copied into itself or any of its descendants
- **Size Validation**:
  - Count total items (source folder + all descendants + decks if `copyDecks=true`)
  - If total items > 500: Return 422 error
  - If total items <= 50: Process synchronously, return `CopyFolderSyncResponse`
  - If total items 51-500: Queue async job, return `CopyFolderAsyncResponse`
- **Name Conflict Resolution**: If folder with same name exists in destination, append " (Copy N)"

#### Response DTO (Sync): CopyFolderSyncResponse

**Condition**: Total items <= 50

**Success (200 OK)**:
```json
{
  "newFolderId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "COMPLETED",
  "message": "Folder copied successfully",
  "itemsCopied": {
    "folders": 5,
    "decks": 3,
    "cards": 120
  }
}
```

| Field | Type | Description |
|-------|------|-------------|
| newFolderId | UUID | ID of the newly created folder |
| status | Enum | Always "COMPLETED" for sync |
| message | String | Human-readable success message |
| itemsCopied | Object | Breakdown of copied items |
| itemsCopied.folders | Integer | Number of folders copied |
| itemsCopied.decks | Integer | Number of decks copied |
| itemsCopied.cards | Integer | Number of cards copied |

#### Response DTO (Async): CopyFolderAsyncResponse

**Condition**: Total items 51-500

**Success (202 Accepted)**:
```json
{
  "jobId": "660e8400-e29b-41d4-a716-446655440001",
  "status": "PROCESSING",
  "message": "Folder copy job started. Check status at /api/folders/copy-status/{jobId}",
  "estimatedItems": 250
}
```

| Field | Type | Description |
|-------|------|-------------|
| jobId | UUID | Unique job identifier for status tracking |
| status | Enum | "PROCESSING" (initial state) |
| message | String | Instructions for status checking |
| estimatedItems | Integer | Estimated total items to copy |

#### Error Responses

| Status | Error Code | Message | Scenario |
|--------|------------|---------|----------|
| 400 | VALIDATION_ERROR | "copyDecks field is required" | Missing copyDecks |
| 404 | NOT_FOUND | "Source folder not found" | Invalid folder ID in path |
| 404 | NOT_FOUND | "Destination folder not found" | Invalid destinationFolderId |
| 409 | CIRCULAR_REFERENCE | "Cannot copy folder into itself or its descendants" | Circular dependency |
| 422 | FOLDER_TOO_LARGE | "Folder exceeds maximum copy limit (500 items). Total: 750" | Too many items |

---

### 4. POST /api/cards/{deckId}/import

**Purpose**: Bulk import cards from CSV or Excel file (max 10,000 cards per file)

#### Request DTO: ImportCardsRequest

**Content-Type**: `multipart/form-data`

| Field | Type | Required | Constraints | Example |
|-------|------|----------|-------------|---------|
| file | File | Yes | .csv or .xlsx, max 5MB | academic_words.xlsx |

**File Format Requirements**:
- **CSV**: UTF-8 encoding, comma-separated, optional header row
- **Excel**: .xlsx format, first sheet only, max 10,000 rows
- **Columns**: Must have 2 columns: `front`, `back` (order matters)
- **Header Row**: Optional, auto-detected if first row contains "front" or "back" (case-insensitive)

#### Validation Rules
- `deckId`: Must exist and belong to authenticated user
- File size: Max 5MB
- File type: Only `.csv` or `.xlsx` accepted (check MIME type + extension)
- Row limit: Max 10,000 rows (excluding header)
- **Per-Row Validation**:
  - `front`: Required, min 1 char (after trim), max 2000 chars
  - `back`: Required, min 1 char (after trim), max 5000 chars
  - Duplicate detection: Skip if exact duplicate (case-sensitive `front` + `back`) exists in deck
- **Error Handling**: Continue processing on row errors, collect all errors, return summary

#### Response DTO: ImportCardsResponse

**Success (200 OK)**:
```json
{
  "successCount": 950,
  "errorCount": 50,
  "duplicateCount": 20,
  "totalRows": 1020,
  "errors": [
    {
      "row": 15,
      "field": "front",
      "error": "Front field is empty or whitespace only"
    },
    {
      "row": 42,
      "field": "back",
      "error": "Back field exceeds 5000 characters"
    },
    {
      "row": 105,
      "field": "front",
      "error": "Front field exceeds 2000 characters"
    }
  ],
  "message": "Import completed with 950 successes, 20 duplicates skipped, 50 errors"
}
```

| Field | Type | Description |
|-------|------|-------------|
| successCount | Integer | Cards successfully imported |
| errorCount | Integer | Rows that failed validation |
| duplicateCount | Integer | Rows skipped as duplicates |
| totalRows | Integer | Total rows processed (excl. header) |
| errors | Array | List of validation errors (max 100 shown) |
| errors[].row | Integer | Row number (1-indexed, after header) |
| errors[].field | String | Field name ("front" or "back") |
| errors[].error | String | Error description |
| message | String | Summary message |

#### Error Responses

| Status | Error Code | Message | Scenario |
|--------|------------|---------|----------|
| 400 | VALIDATION_ERROR | "File is required" | Missing file in request |
| 400 | INVALID_FILE_TYPE | "Invalid file type. Only .csv and .xlsx are supported" | Wrong file type |
| 400 | FILE_TOO_LARGE | "File size exceeds 5MB limit" | File > 5MB |
| 404 | NOT_FOUND | "Deck not found" | Invalid deckId |
| 422 | ROW_LIMIT_EXCEEDED | "File contains 15,000 rows. Maximum is 10,000" | Too many rows |
| 422 | INVALID_FORMAT | "File must contain exactly 2 columns (front, back)" | Wrong column count |

---

### 5. POST /api/review/submit

**Purpose**: Submit a user's rating for a card and update SRS state (box, due date)

#### Request DTO: SubmitReviewRequest

| Field | Type | Required | Constraints | Example |
|-------|------|----------|-------------|---------|
| cardId | UUID | Yes | Must exist and belong to user | "550e8400..." |
| rating | Enum | Yes | AGAIN \| HARD \| GOOD \| EASY | "GOOD" |

#### Validation Rules
- `cardId`: Must exist and belong to a deck owned by authenticated user
- `rating`: Must be one of: `AGAIN`, `HARD`, `GOOD`, `EASY`
- **SRS Algorithm** (Leitner System):
  - `AGAIN`: Move to Box 1
  - `HARD`: Move down 1 box (min Box 1)
  - `GOOD`: Move up 1 box (max Box 7)
  - `EASY`: Move up 2 boxes (max Box 7)
- **Due Date Calculation** (see `docs/03-design/domain/leitner-srs-algorithm.md`):
  - Box 1: 1 day
  - Box 2: 3 days
  - Box 3: 7 days
  - Box 4: 14 days
  - Box 5: 30 days
  - Box 6: 90 days
  - Box 7: 180 days
- **Review History**: Create `ReviewLog` entry with timestamp, rating, previous/new box

#### Response DTO: SubmitReviewResponse

**Success (200 OK)**:
```json
{
  "cardId": "550e8400-e29b-41d4-a716-446655440000",
  "previousBox": 2,
  "newBox": 3,
  "previousDueDate": "2025-01-10",
  "newDueDate": "2025-01-17",
  "nextInterval": 7,
  "reviewCount": 6,
  "lastReviewedAt": "2025-01-10T10:30:00Z"
}
```

| Field | Type | Description |
|-------|------|-------------|
| cardId | UUID | Card ID reviewed |
| previousBox | Integer | Box number before review (1-7) |
| newBox | Integer | Box number after review (1-7) |
| previousDueDate | Date | Due date before review (ISO 8601 date) |
| newDueDate | Date | New due date (ISO 8601 date) |
| nextInterval | Integer | Days until next review |
| reviewCount | Integer | Total times this card has been reviewed |
| lastReviewedAt | DateTime | Timestamp of this review (ISO 8601 UTC) |

#### Error Responses

| Status | Error Code | Message | Scenario |
|--------|------------|---------|----------|
| 400 | VALIDATION_ERROR | "cardId is required" | Missing cardId |
| 400 | VALIDATION_ERROR | "rating is required" | Missing rating |
| 400 | INVALID_RATING | "Invalid rating. Must be one of: AGAIN, HARD, GOOD, EASY" | Invalid rating value |
| 404 | NOT_FOUND | "Card not found" | Invalid cardId |
| 403 | FORBIDDEN | "You do not have permission to review this card" | Card belongs to different user |

---

### 6. GET /api/review/due

**Purpose**: Retrieve cards due for review based on mode, scope, and limit

#### Request Parameters (Query String)

| Parameter | Type | Required | Constraints | Default | Example |
|-----------|------|----------|-------------|---------|---------|
| mode | Enum | No | SPACED_REPETITION \| CRAM \| RANDOM | SPACED_REPETITION | SPACED_REPETITION |
| scope | Enum | No | all \| folder \| deck | all | deck |
| scopeId | UUID | Conditional | Required if scope = folder or deck | null | "550e8400..." |
| limit | Integer | No | Min 1, max 200 | 100 | 50 |

#### Validation Rules
- `mode`: Defaults to `SPACED_REPETITION` if not provided
- `scope`: Defaults to `all` if not provided
- `scopeId`: **Required** if `scope = folder` or `scope = deck`, must be null if `scope = all`
- `scopeId`: Must exist and belong to authenticated user
- `limit`: Must be between 1 and 200, defaults to 100
- **Card Selection Logic**:
  - `SPACED_REPETITION`: Filter by `dueDate <= today`, ordered by SRS settings `reviewOrder`
  - `CRAM`: All cards in scope, random order
  - `RANDOM`: All cards in scope, random order (same as CRAM in MVP)
- **Review Order** (from SRS settings):
  - `ASCENDING`: Box 1 → Box 7
  - `DESCENDING`: Box 7 → Box 1
  - `RANDOM`: Shuffle

#### Response DTO: DueCardsResponse

**Success (200 OK)**:
```json
{
  "cards": [
    {
      "cardId": "550e8400-e29b-41d4-a716-446655440000",
      "front": "ubiquitous",
      "back": "existing everywhere",
      "deckId": "660e8400-e29b-41d4-a716-446655440001",
      "deckName": "Academic Words",
      "currentBox": 2,
      "dueDate": "2025-01-10",
      "reviewCount": 5,
      "lastReviewedAt": "2025-01-07T10:00:00Z"
    },
    {
      "cardId": "770e8400-e29b-41d4-a716-446655440002",
      "front": "ephemeral",
      "back": "lasting for a very short time",
      "deckId": "660e8400-e29b-41d4-a716-446655440001",
      "deckName": "Academic Words",
      "currentBox": 1,
      "dueDate": "2025-01-10",
      "reviewCount": 2,
      "lastReviewedAt": "2025-01-09T15:30:00Z"
    }
  ],
  "totalDue": 150,
  "returned": 2,
  "mode": "SPACED_REPETITION",
  "scope": "deck"
}
```

| Field | Type | Description |
|-------|------|-------------|
| cards | Array | List of due cards (limited by `limit` param) |
| cards[].cardId | UUID | Card ID |
| cards[].front | String | Card front content |
| cards[].back | String | Card back content |
| cards[].deckId | UUID | Deck ID containing this card |
| cards[].deckName | String | Deck name |
| cards[].currentBox | Integer | Current Leitner box (1-7) |
| cards[].dueDate | Date | Due date (ISO 8601 date) |
| cards[].reviewCount | Integer | Total review count |
| cards[].lastReviewedAt | DateTime | Last review timestamp (ISO 8601 UTC, null if never reviewed) |
| totalDue | Integer | Total due cards in scope (not limited) |
| returned | Integer | Number of cards returned in this response |
| mode | Enum | Review mode used |
| scope | Enum | Scope filter applied |

#### Error Responses

| Status | Error Code | Message | Scenario |
|--------|------------|---------|----------|
| 400 | VALIDATION_ERROR | "Invalid mode. Must be one of: SPACED_REPETITION, CRAM, RANDOM" | Invalid mode |
| 400 | VALIDATION_ERROR | "Invalid scope. Must be one of: all, folder, deck" | Invalid scope |
| 400 | VALIDATION_ERROR | "scopeId is required when scope is 'folder' or 'deck'" | Missing scopeId |
| 400 | VALIDATION_ERROR | "limit must be between 1 and 200" | Invalid limit |
| 404 | NOT_FOUND | "Folder not found" | Invalid folder scopeId |
| 404 | NOT_FOUND | "Deck not found" | Invalid deck scopeId |

---

## Other Endpoints - Brief Reference

For complete specifications of the following endpoints, see [API Endpoints Summary](../03-design/api/api-endpoints-summary.md).

### Authentication
- **POST /api/auth/register** - Create new user account
- **POST /api/auth/refresh** - Refresh access token using refresh cookie
- **POST /api/auth/logout** - Revoke refresh token
- **POST /api/auth/logout-all** - Revoke all user sessions

### User Profile
- **GET /api/users/me** - Get current user profile
- **PUT /api/users/me** - Update user profile (name, timezone, language, theme)

### Folders
- **GET /api/folders** - Get folder tree with stats
- **GET /api/folders/{id}** - Get folder details
- **PUT /api/folders/{id}** - Update folder (name, description)
- **DELETE /api/folders/{id}** - Soft delete folder
- **POST /api/folders/{id}/move** - Move folder to new parent
- **GET /api/folders/copy-status/{jobId}** - Check async copy job status
- **GET /api/folders/{id}/stats** - Get folder statistics
- **GET /api/folders/{id}/breadcrumb** - Get folder breadcrumb path

### Decks
- **GET /api/decks** - List decks in folder
- **POST /api/decks** - Create deck
- **GET /api/decks/{id}** - Get deck details
- **PUT /api/decks/{id}** - Update deck
- **DELETE /api/decks/{id}** - Delete deck
- **POST /api/decks/{id}/move** - Move deck to folder
- **POST /api/decks/{id}/copy** - Copy deck (async if >1000 cards)

### Cards
- **GET /api/decks/{deckId}/cards** - List cards (paginated)
- **POST /api/decks/{deckId}/cards** - Create single card
- **PUT /api/cards/{id}** - Update card
- **DELETE /api/cards/{id}** - Delete card
- **GET /api/decks/{deckId}/cards/export** - Export cards to CSV/Excel
- **GET /api/cards/template** - Download import template

### SRS Settings
- **GET /api/srs/settings** - Get user SRS configuration
- **PUT /api/srs/settings** - Update SRS settings
- **POST /api/srs/settings/reset** - Reset to default settings

### Review Session
- **POST /api/review/undo** - Undo last review
- **POST /api/review/skip/{cardId}** - Skip card (move to end)

### Statistics
- **GET /api/stats/user** - User-level statistics
- **GET /api/stats/folder/{id}** - Folder statistics
- **GET /api/stats/deck/{id}** - Deck statistics
- **GET /api/stats/box-distribution** - Card distribution across boxes

### Notifications
- **GET /api/notifications** - List notifications (paginated)
- **PUT /api/notifications/{id}/read** - Mark notification as read

---

## Common Patterns

### Standard Error Response Format

All error responses follow this structure:

```json
{
  "timestamp": "2025-01-10T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Folder name must not be empty",
  "path": "/api/folders",
  "traceId": "abc-123-def-456"
}
```

| Field | Type | Description |
|-------|------|-------------|
| timestamp | DateTime | ISO 8601 timestamp when error occurred |
| status | Integer | HTTP status code |
| error | String | HTTP status text |
| message | String | Human-readable error message |
| path | String | Request path that caused error |
| traceId | String | Unique ID for error tracking/logging |

### Validation Error Response (Multiple Fields)

When multiple validation errors occur:

```json
{
  "timestamp": "2025-01-10T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/folders",
  "traceId": "abc-123-def-456",
  "errors": [
    {
      "field": "name",
      "rejectedValue": "",
      "message": "Folder name must not be empty"
    },
    {
      "field": "description",
      "rejectedValue": "Lorem ipsum... (600 chars)",
      "message": "Description cannot exceed 500 characters"
    }
  ]
}
```

### Pagination Response Format

Endpoints returning lists use this structure:

```json
{
  "content": [...],
  "page": 0,
  "size": 20,
  "totalElements": 150,
  "totalPages": 8,
  "last": false,
  "first": true
}
```

**Query Parameters**:
- `page`: Page number (0-indexed, default 0)
- `size`: Page size (default 20, max 100)
- `sort`: Sort field and direction (e.g., `name,asc`)

---

## Data Type Specifications

### UUID Format
- Standard UUID v4 format: `550e8400-e29b-41d4-a716-446655440000`
- Case-insensitive (stored lowercase)
- Validation regex: `^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$`

### DateTime Format
- ISO 8601 format: `2025-01-10T10:30:00Z`
- Always in UTC timezone
- Stored in database as UTC timestamp
- Frontend converts to user's timezone

### Date Format
- ISO 8601 date: `2025-01-10`
- No time component
- Used for due dates (time-agnostic)

### Enum Values
All enums are **case-sensitive** and use **UPPER_SNAKE_CASE**:

- **ReviewMode**: `SPACED_REPETITION`, `CRAM`, `RANDOM`
- **ReviewScope**: `all`, `folder`, `deck` (lowercase for these)
- **Rating**: `AGAIN`, `HARD`, `GOOD`, `EASY`
- **ReviewOrder**: `ASCENDING`, `DESCENDING`, `RANDOM`
- **ForgottenCardAction**: `MOVE_TO_BOX_1`, `MOVE_DOWN_N_BOXES`, `STAY_IN_BOX`
- **Language**: `EN`, `VI`
- **Theme**: `LIGHT`, `DARK`
- **JobStatus**: `PROCESSING`, `COMPLETED`, `FAILED`

---

## Validation Constraints Summary

### String Fields
- **Folder Name**: 1-100 chars, no special chars except `-_.` and spaces
- **Deck Name**: 1-100 chars, same as folder name
- **Description**: 0-500 chars
- **Card Front**: 1-2000 chars
- **Card Back**: 1-5000 chars
- **Email**: 1-255 chars, valid email format
- **Password**: 8-255 chars
- **User Name**: 1-100 chars

### Numeric Limits
- **Folder Depth**: Max 10 levels
- **Folders per User**: Max 1000
- **Cards per Deck**: Max 10,000
- **File Size**: Max 5MB for imports
- **Import Rows**: Max 10,000 rows
- **Review Limit**: Max 200 cards per request

### Business Rules
- Folder names must be unique within parent (case-insensitive)
- Deck names must be unique within folder (case-insensitive)
- Duplicate cards (exact front+back match) are skipped during import
- Circular folder references are blocked
- Deleted folders/decks are soft-deleted (not actually removed)

---

## Authentication & Authorization

### JWT Token Structure

**Access Token (Header)**:
```
Authorization: Bearer <access_token>
```

**Access Token Claims**:
```json
{
  "sub": "user-uuid",
  "email": "user@example.com",
  "iat": 1673356800,
  "exp": 1673357700
}
```

**Refresh Token (Cookie)**:
```
refresh_token=<token>; HttpOnly; Secure; SameSite=Strict; Max-Age=604800; Path=/api/auth
```

### Authorization Rules
- All endpoints require authentication **except**:
  - `POST /api/auth/register`
  - `POST /api/auth/login`
  - `POST /api/auth/refresh` (uses refresh cookie)
- Users can only access their own resources
- No admin/role-based permissions in MVP

---

## Rate Limiting

### Limits
- **Global**: 100 requests/minute/user
- **Login**: 5 attempts/minute/IP
- **Copy Operations**: 10 requests/hour/user
- **Import**: 5 concurrent operations/user

### Headers
```
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 45
X-RateLimit-Reset: 1673356800
```

### Error Response (429)
```json
{
  "timestamp": "2025-01-10T10:30:00Z",
  "status": 429,
  "error": "Too Many Requests",
  "message": "Rate limit exceeded. Try again in 45 seconds",
  "path": "/api/folders",
  "traceId": "abc-123-def-456",
  "retryAfter": 45
}
```

---

**Document Version**: 1.0
**Last Updated**: 2025-01-10
**Status**: MVP Ready
**Total Token Count**: ~14,500 tokens
