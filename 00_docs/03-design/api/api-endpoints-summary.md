# API Endpoints Summary - RepeatWise MVP

## Overview

RESTful API endpoints cho RepeatWise MVP. Tất cả endpoints yêu cầu JWT authentication (trừ auth endpoints).

**Base URL**: `/api`
**API Version**: v1 (implicit, không có prefix `/v1` trong MVP)
**Content-Type**: `application/json`
**Authentication**: `Authorization: Bearer <access_token>`

---

## 1. Authentication ⭐

### 1.1 Register
```http
POST /api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "securepass123",
  "name": "John Doe"
}

Response 201:
{
  "id": "uuid",
  "email": "user@example.com",
  "name": "John Doe",
  "createdAt": "2025-01-10T10:00:00Z"
}
```

### 1.2 Login với Refresh Token
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "securepass123"
}

Response 200:
{
  "accessToken": "eyJhbGciOiJIUzI1...",
  "expiresIn": 900
}
Set-Cookie: refresh_token=<token>; HttpOnly; Secure; SameSite=Strict; Max-Age=604800
```

### 1.3 Refresh Access Token
```http
POST /api/auth/refresh
Cookie: refresh_token=<token>

Response 200:
{
  "accessToken": "eyJhbGciOiJIUzI1...",
  "expiresIn": 900
}
Set-Cookie: refresh_token=<new_token>; HttpOnly; Secure; SameSite=Strict; Max-Age=604800
```

### 1.4 Logout
```http
POST /api/auth/logout
Cookie: refresh_token=<token>

Response 204 No Content
```

### 1.5 Logout All Devices
```http
POST /api/auth/logout-all
Authorization: Bearer <access_token>

Response 204 No Content
```

---

## 2. User Profile

### 2.1 Get Current User
```http
GET /api/users/me
Authorization: Bearer <access_token>

Response 200:
{
  "id": "uuid",
  "email": "user@example.com",
  "name": "John Doe",
  "timezone": "Asia/Ho_Chi_Minh",
  "language": "VI",
  "theme": "DARK"
}
```

### 2.2 Update Profile
```http
PUT /api/users/me
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "name": "John Updated",
  "timezone": "Asia/Bangkok",
  "language": "EN",
  "theme": "LIGHT"
}

Response 200: Updated user object
```

---

## 3. Folders ⭐

### 3.1 Get Folder Tree
```http
GET /api/folders?maxDepth=10
Authorization: Bearer <access_token>

Response 200:
[
  {
    "id": "uuid",
    "name": "English Learning",
    "parentId": null,
    "depth": 0,
    "path": "/uuid",
    "childrenCount": 2,
    "deckCount": 0,
    "totalCards": 150,
    "dueCards": 20
  },
  {
    "id": "uuid2",
    "name": "IELTS",
    "parentId": "uuid",
    "depth": 1,
    "path": "/uuid/uuid2",
    "childrenCount": 1,
    "deckCount": 3,
    "totalCards": 100,
    "dueCards": 15
  }
]
```

### 3.2 Get Folder Details
```http
GET /api/folders/{folderId}
Authorization: Bearer <access_token>

Response 200:
{
  "id": "uuid",
  "name": "English Learning",
  "description": "All English learning materials",
  "parentId": null,
  "depth": 0,
  "path": "/uuid",
  "createdAt": "2025-01-01T00:00:00Z",
  "updatedAt": "2025-01-10T00:00:00Z"
}
```

### 3.3 Create Folder
```http
POST /api/folders
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "name": "Business English",
  "description": "Business vocabulary and phrases",
  "parentFolderId": "uuid"
}

Response 201: Folder object
```

### 3.4 Update Folder
```http
PUT /api/folders/{folderId}
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "name": "English Learning Updated",
  "description": "Updated description"
}

Response 200: Updated folder object
```

### 3.5 Move Folder
```http
POST /api/folders/{folderId}/move
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "newParentFolderId": "uuid"
}

Response 200: Updated folder object
```

### 3.6 Copy Folder (Async for >50 items)
```http
POST /api/folders/{folderId}/copy
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "destinationFolderId": "uuid",
  "copyDecks": true
}

Response 200 (Sync ≤50 items):
{
  "newFolderId": "uuid",
  "status": "COMPLETED"
}

Response 202 (Async 51-500 items):
{
  "jobId": "uuid",
  "status": "PROCESSING",
  "message": "Folder copy in progress"
}
```

### 3.7 Get Folder Copy Status
```http
GET /api/folders/copy-status/{jobId}
Authorization: Bearer <access_token>

Response 200:
{
  "jobId": "uuid",
  "status": "PROCESSING", // PROCESSING | COMPLETED | FAILED
  "progress": 45, // 0-100
  "message": "Copying 45/100 items",
  "completedAt": null
}
```

### 3.8 Delete Folder (Soft Delete)
```http
DELETE /api/folders/{folderId}
Authorization: Bearer <access_token>

Response 204 No Content
```

### 3.9 Get Folder Statistics
```http
GET /api/folders/{folderId}/stats
Authorization: Bearer <access_token>

Response 200:
{
  "folderId": "uuid",
  "totalCards": 150,
  "dueCards": 20,
  "newCards": 10,
  "matureCards": 80,
  "lastComputedAt": "2025-01-10T10:00:00Z"
}
```

### 3.10 Get Breadcrumb Path
```http
GET /api/folders/{folderId}/breadcrumb
Authorization: Bearer <access_token>

Response 200:
[
  { "id": null, "name": "Home" },
  { "id": "uuid1", "name": "English Learning" },
  { "id": "uuid2", "name": "IELTS" },
  { "id": "uuid3", "name": "Vocabulary" }
]
```

---

## 4. Decks

### 4.1 Get Decks in Folder
```http
GET /api/decks?folderId={folderId}
Authorization: Bearer <access_token>

Response 200:
[
  {
    "id": "uuid",
    "name": "Academic Words",
    "description": "IELTS academic vocabulary",
    "folderId": "uuid",
    "cardCount": 100,
    "dueCardCount": 15,
    "lastStudiedAt": "2025-01-09T10:00:00Z",
    "createdAt": "2025-01-01T00:00:00Z"
  }
]
```

### 4.2 Create Deck
```http
POST /api/decks
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "name": "Speaking Topics",
  "description": "Common IELTS speaking topics",
  "folderId": "uuid"
}

Response 201: Deck object
```

### 4.3 Get Deck Details
```http
GET /api/decks/{deckId}
Authorization: Bearer <access_token>

Response 200: Deck object with stats
```

### 4.4 Update Deck
```http
PUT /api/decks/{deckId}
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "name": "Updated Deck Name",
  "description": "Updated description"
}

Response 200: Updated deck object
```

### 4.5 Move Deck
```http
POST /api/decks/{deckId}/move
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "newFolderId": "uuid"
}

Response 200: Updated deck object
```

### 4.6 Copy Deck (Async for >1000 cards)
```http
POST /api/decks/{deckId}/copy
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "destinationFolderId": "uuid"
}

Response 200 (Sync ≤1000 cards):
{
  "newDeckId": "uuid",
  "status": "COMPLETED"
}

Response 202 (Async 1001-10,000 cards):
{
  "jobId": "uuid",
  "status": "PROCESSING"
}
```

### 4.7 Delete Deck
```http
DELETE /api/decks/{deckId}
Authorization: Bearer <access_token>

Response 204 No Content
```

---

## 5. Cards

### 5.1 Get Cards in Deck
```http
GET /api/decks/{deckId}/cards?page=0&size=100
Authorization: Bearer <access_token>

Response 200:
{
  "content": [
    {
      "id": "uuid",
      "front": "ubiquitous",
      "back": "existing everywhere",
      "deckId": "uuid",
      "createdAt": "2025-01-01T00:00:00Z",
      "updatedAt": "2025-01-10T00:00:00Z"
    }
  ],
  "page": 0,
  "size": 100,
  "totalElements": 500,
  "totalPages": 5
}
```

### 5.2 Create Card
```http
POST /api/decks/{deckId}/cards
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "front": "ephemeral",
  "back": "lasting for a very short time"
}

Response 201: Card object
```

### 5.3 Update Card
```http
PUT /api/cards/{cardId}
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "front": "ephemeral (updated)",
  "back": "lasting for a very short time (transient)"
}

Response 200: Updated card object
```

### 5.4 Delete Card
```http
DELETE /api/cards/{cardId}
Authorization: Bearer <access_token>

Response 204 No Content
```

### 5.5 Import Cards (CSV/Excel) ⭐
```http
POST /api/decks/{deckId}/cards/import
Authorization: Bearer <access_token>
Content-Type: multipart/form-data

file: <uploaded_file.csv or .xlsx>

Response 200:
{
  "successCount": 950,
  "errorCount": 50,
  "totalRows": 1000,
  "errors": [
    {
      "row": 15,
      "error": "Front field is empty"
    },
    {
      "row": 42,
      "error": "Duplicate card: 'ubiquitous'"
    }
  ]
}
```

### 5.6 Export Cards (CSV/Excel) ⭐
```http
GET /api/decks/{deckId}/cards/export?format=xlsx&filter=all
Authorization: Bearer <access_token>

Query params:
- format: csv | xlsx
- filter: all | due

Response 200:
Content-Disposition: attachment; filename="Deck_Name_2025-01-10.xlsx"
Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet

Binary file download
```

### 5.7 Download Import Template
```http
GET /api/cards/template?format=xlsx
Authorization: Bearer <access_token>

Query params:
- format: csv | xlsx

Response 200:
Content-Disposition: attachment; filename="import_template.xlsx"
Binary file download
```

---

## 6. SRS Settings

### 6.1 Get SRS Settings
```http
GET /api/srs/settings
Authorization: Bearer <access_token>

Response 200:
{
  "userId": "uuid",
  "totalBoxes": 7,
  "reviewOrder": "RANDOM", // ASCENDING | DESCENDING | RANDOM
  "notificationEnabled": true,
  "notificationTime": "09:00",
  "forgottenCardAction": "MOVE_TO_BOX_1", // MOVE_TO_BOX_1 | MOVE_DOWN_N_BOXES | STAY_IN_BOX
  "moveDownBoxes": 1,
  "newCardsPerDay": 20,
  "maxReviewsPerDay": 200
}
```

### 6.2 Update SRS Settings
```http
PUT /api/srs/settings
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "reviewOrder": "ASCENDING",
  "notificationEnabled": false,
  "newCardsPerDay": 30,
  "maxReviewsPerDay": 300
}

Response 200: Updated settings object
```

### 6.3 Reset Settings to Default
```http
POST /api/srs/settings/reset
Authorization: Bearer <access_token>

Response 200: Default settings object
```

---

## 7. Review Session ⭐

### 7.1 Get Due Cards
```http
GET /api/review/due?mode=SPACED_REPETITION&scope=deck&scopeId={deckId}&limit=100
Authorization: Bearer <access_token>

Query params:
- mode: SPACED_REPETITION | CRAM | RANDOM
- scope: all | folder | deck
- scopeId: folder or deck UUID (required for folder/deck scope)
- limit: max cards to fetch (default 100, max 200)

Response 200:
{
  "cards": [
    {
      "cardId": "uuid",
      "front": "ubiquitous",
      "back": "existing everywhere",
      "deckName": "Academic Words",
      "currentBox": 2,
      "dueDate": "2025-01-10",
      "reviewCount": 5,
      "lastReviewedAt": "2025-01-07T10:00:00Z"
    }
  ],
  "totalDue": 150,
  "returned": 100
}
```

### 7.2 Submit Card Rating
```http
POST /api/review/submit
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "cardId": "uuid",
  "rating": "GOOD" // AGAIN | HARD | GOOD | EASY
}

Response 200:
{
  "cardId": "uuid",
  "previousBox": 2,
  "newBox": 3,
  "previousDueDate": "2025-01-10",
  "newDueDate": "2025-01-17",
  "nextInterval": 7
}
```

### 7.3 Undo Last Review
```http
POST /api/review/undo
Authorization: Bearer <access_token>

Response 200:
{
  "cardId": "uuid",
  "restoredBox": 2,
  "restoredDueDate": "2025-01-10"
}
```

### 7.4 Skip Card
```http
POST /api/review/skip/{cardId}
Authorization: Bearer <access_token>

Response 200:
{
  "cardId": "uuid",
  "skipped": true,
  "message": "Card will appear at end of session"
}
```

---

## 8. Statistics

### 8.1 Get User Statistics
```http
GET /api/stats/user
Authorization: Bearer <access_token>

Response 200:
{
  "userId": "uuid",
  "totalCardsLearned": 1500,
  "streakDays": 7,
  "lastStudyDate": "2025-01-10",
  "totalStudyTimeMinutes": 450,
  "todayCardsReviewed": 50,
  "todayNewCards": 10
}
```

### 8.2 Get Folder Statistics
```http
GET /api/stats/folder/{folderId}
Authorization: Bearer <access_token>

Response 200: Same as GET /api/folders/{folderId}/stats
```

### 8.3 Get Deck Statistics
```http
GET /api/stats/deck/{deckId}
Authorization: Bearer <access_token>

Response 200:
{
  "deckId": "uuid",
  "totalCards": 100,
  "dueCards": 15,
  "newCards": 5,
  "matureCards": 60,
  "lastStudiedAt": "2025-01-10T10:00:00Z"
}
```

### 8.4 Get Box Distribution
```http
GET /api/stats/box-distribution?scope=folder&scopeId={folderId}
Authorization: Bearer <access_token>

Query params:
- scope: all | folder | deck
- scopeId: required for folder/deck scope

Response 200:
{
  "scope": "folder",
  "scopeId": "uuid",
  "distribution": {
    "box1": 20,
    "box2": 30,
    "box3": 25,
    "box4": 15,
    "box5": 10,
    "box6": 5,
    "box7": 5
  },
  "totalCards": 110
}
```

---

## 9. Notifications (UC-024)

### 9.1 Get Notification Settings
```http
GET /api/notifications/settings
Authorization: Bearer <access_token>

Response 200:
{
  "dailyReminderEnabled": true,
  "dailyReminderTime": "09:00",
  "dailyReminderDays": ["MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"],
  "notificationMethod": "EMAIL",
  "notificationEmail": null
}
```

### 9.2 Update Notification Settings
```http
PUT /api/notifications/settings
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "dailyReminderEnabled": true,
  "dailyReminderTime": "19:00",
  "dailyReminderDays": ["MON", "TUE", "WED", "THU", "FRI"],
  "notificationMethod": "EMAIL",
  "notificationEmail": "custom@example.com"
}

Response 200:
{
  "dailyReminderEnabled": true,
  "dailyReminderTime": "19:00",
  "dailyReminderDays": ["MON", "TUE", "WED", "THU", "FRI"],
  "notificationMethod": "EMAIL",
  "notificationEmail": "custom@example.com",
  "nextReminderAt": "2025-01-10T19:00:00Z"
}
```

**Validation Rules**:
- `dailyReminderTime`: HH:MM format (00:00 to 23:59)
- `dailyReminderDays`: At least one day required if enabled
- `notificationEmail`: Valid email format or null (uses user email)

### 9.3 Send Test Notification
```http
POST /api/notifications/test
Authorization: Bearer <access_token>

Response 202 Accepted:
{
  "message": "Test notification sent to your-email@example.com",
  "sentAt": "2025-01-10T15:30:00Z"
}
```

### 9.4 Get Notification Logs (History)
```http
GET /api/notifications/logs?page=0&size=20
Authorization: Bearer <access_token>

Response 200:
{
  "content": [
    {
      "id": "uuid",
      "type": "DAILY_REMINDER",
      "recipient": "user@example.com",
      "status": "DELIVERED",
      "sentAt": "2025-01-10T09:00:00Z",
      "metadata": {
        "dueCardsCount": 20,
        "streakDays": 15
      }
    },
    {
      "id": "uuid",
      "type": "DAILY_REMINDER",
      "recipient": "user@example.com",
      "status": "FAILED",
      "sentAt": "2025-01-09T09:00:00Z",
      "errorMessage": "SMTP connection timeout"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 45,
  "totalPages": 3
}
```

**Requirements Mapping**:
- [UC-024: Manage Notifications](../../02-system-analysis/use-cases/UC-024-manage-notifications.md)
- [schema.md](../database/schema.md) Section 3.4-3.5: Notification tables

---

## Error Responses

### Standard Error Format
```json
{
  "timestamp": "2025-01-10T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Folder name must not be empty",
  "path": "/api/folders",
  "traceId": "abc-123-def"
}
```

### Common HTTP Status Codes
- `200 OK` - Request successful
- `201 Created` - Resource created
- `202 Accepted` - Async operation accepted
- `204 No Content` - Success with no response body
- `400 Bad Request` - Validation error
- `401 Unauthorized` - Missing/invalid auth token
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found
- `409 Conflict` - Duplicate/constraint violation
- `429 Too Many Requests` - Rate limit exceeded
- `500 Internal Server Error` - Server error

---

## Rate Limiting

**MVP Rate Limits**:
- Global: 100 requests/minute/user
- Login: 5 attempts/minute/IP
- Copy operations: 10 requests/hour
- Import: 5 concurrent operations max

**Rate Limit Headers**:
```
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 45
X-RateLimit-Reset: 1673356800
```

---

## Pagination

All list endpoints support pagination:

**Query Parameters**:
- `page`: Page number (0-indexed, default 0)
- `size`: Page size (default 20, max 100)
- `sort`: Sort field and direction (e.g., `name,asc` or `createdAt,desc`)

**Response Format**:
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

---

## Authentication Flow

1. **Login**: POST `/api/auth/login` → Get access token (15min) + refresh token (7 days in cookie)
2. **API Calls**: Include `Authorization: Bearer <access_token>` header
3. **Token Expires**: Frontend gets 401 → Call POST `/api/auth/refresh` → Get new access token
4. **Refresh Token Expires**: Redirect to login
5. **Logout**: POST `/api/auth/logout` → Revoke refresh token

---

**API Version**: 1.0
**Last Updated**: 2025-01-10
**Status**: MVP Ready
