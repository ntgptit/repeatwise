# API Architecture - Complete Guide

## Mục tiêu đạt được

✅ **Dễ đọc, dễ debug, dễ mở rộng**
✅ **Tách biệt hoàn toàn logic HTTP khỏi UI**
✅ **Type-safe 100% (sử dụng TypeScript Generics)**
✅ **Interceptor trung tâm để xử lý token, lỗi, logging**
✅ **Mọi module API đều tuân theo cùng một convention**

## Cấu trúc

```
src/api/
├── http/
│   ├── axiosInstance.ts          # Axios instance với interceptors
│   ├── base.api.ts                # Base class cho tất cả API modules
│   ├── error.handler.ts           # Error handling utilities
│   ├── index.ts                   # HTTP exports
│   └── interceptors/
│       ├── request.interceptor.ts   # Request interceptor
│       ├── response.interceptor.ts  # Response interceptor
│       ├── error.interceptor.ts     # Error interceptor
│       └── logging.interceptor.ts  # Logging interceptor
├── modules/
│   ├── auth.api.ts               # Auth API module
│   ├── user.api.ts               # User API module
│   ├── folder.api.ts             # Folder API module
│   └── index.ts                  # Module exports
└── types/
    ├── api-response.ts           # API response types
    ├── pagination.ts             # Pagination types
    └── index.ts                  # Type exports
```

## Convention

### 1. API Module Convention

Mọi API module đều:
- Extend từ `BaseApi`
- Sử dụng TypeScript Generics cho type safety
- Có interface riêng cho Request/Response types
- Export singleton instance
- Tuân theo naming convention:
  - `getById(id)` - Get single item
  - `getList(params?)` - Get list
  - `getPaginated(params)` - Get paginated list
  - `create(data)` - Create new item
  - `update(id, data)` - Update item
  - `patch(id, data)` - Partial update
  - `delete(id)` - Delete item
  - `customGet/Post/Put/Patch/Delete(endpoint, ...)` - Custom endpoints

### 2. Type Safety

- Tất cả methods đều có generic types
- Request/Response types được define riêng
- Return types được infer tự động

### 3. Error Handling

- Centralized error handling trong interceptor
- Type-safe error extraction
- Automatic toast notifications
- Custom error handling support

### 4. Logging

- Automatic request/response logging (dev mode)
- Request ID tracking
- Duration tracking
- Error logging với metadata

## Cách sử dụng

### 1. Import API Module

```typescript
import { authApi, userApi, folderApi } from '@/api/modules'
```

### 2. Sử dụng với Type Safety

```typescript
// Login - Type-safe
const result = await authApi.login({
  email: 'user@example.com',
  password: 'password123'
})
// result type: LoginResponse

// Get profile - Type-safe
const profile = await userApi.getProfile()
// profile type: User

// Get folders - Type-safe
const folders = await folderApi.getFolders()
// folders type: Folder[]
```

### 3. Error Handling

```typescript
import { ApiErrorHandler } from '@/api/http'

try {
  const data = await authApi.login({ email, password })
} catch (error) {
  // Type-safe error extraction
  const apiError = ApiErrorHandler.extractError(error)
  const message = ApiErrorHandler.getErrorMessage(error)
  const status = ApiErrorHandler.getErrorStatus(error)
  const validationErrors = ApiErrorHandler.getValidationErrors(error)
}
```

### 4. Custom Request Options

```typescript
// Skip auth (for public endpoints)
await authApi.login(data, { skipAuth: true })

// Skip error handler (custom handling)
await api.get('/endpoint', { skipErrorHandler: true })

// Skip logging (sensitive data)
await api.post('/sensitive', data, { skipLogging: true })

// Custom timeout
await api.get('/slow-endpoint', { timeout: 60000 })
```

## Tạo API Module mới

### Step 1: Define Types

```typescript
// card.api.ts
export interface Card {
  id: string
  front: string
  back: string
  deckId: string
}

export interface CreateCardRequest {
  front: string
  back: string
  deckId: string
}

export interface UpdateCardRequest {
  front?: string
  back?: string
}
```

### Step 2: Create API Class

```typescript
import { BaseApi } from '../http/base.api'
import { API_ENDPOINTS } from '@/constants/api'

class CardApi extends BaseApi {
  constructor() {
    super(API_ENDPOINTS.CARDS.BASE)
  }

  // Get card by ID
  async getById(id: string): Promise<Card> {
    return super.getById<Card>(id)
  }

  // Get cards by deck
  async getByDeck(deckId: string): Promise<Card[]> {
    return this.customGet<Card[]>(`/deck/${deckId}`)
  }

  // Create card
  async create(data: CreateCardRequest): Promise<Card> {
    return super.create<Card, CreateCardRequest>(data)
  }

  // Update card
  async update(id: string, data: UpdateCardRequest): Promise<Card> {
    return super.update<Card, UpdateCardRequest>(id, data)
  }

  // Delete card
  async delete(id: string): Promise<void> {
    await super.delete(id)
  }
}

export const cardApi = new CardApi()
```

### Step 3: Export

```typescript
// modules/index.ts
export * from './card.api'
```

## Best Practices

1. **Luôn sử dụng BaseApi** cho consistency
2. **Define types rõ ràng** cho Request/Response
3. **Sử dụng TypeScript Generics** cho type safety
4. **Export singleton instance** (không export class)
5. **Follow naming convention** đã định nghĩa
6. **Tách biệt logic HTTP** khỏi UI logic
7. **Sử dụng error handler** cho custom error handling

## Debugging

### Development Mode

Trong development mode, tất cả requests/responses được log tự động:
- Request: URL, method, headers, body
- Response: Data, duration
- Errors: Error details, status code

### Request ID Tracking

Mỗi request có unique Request ID để tracking:
```
X-Request-ID: 1234567890-abc123xyz
```

### Logging Format

```
🚀 [GET] /api/cards
Request ID: 1234567890-abc123xyz
Headers: {...}
Body: {...}

✅ [GET] /api/cards
Request ID: 1234567890-abc123xyz
Duration: 45ms
Response: {...}

❌ [GET] /api/cards
Request ID: 1234567890-abc123xyz
Duration: 120ms
Error: {...}
```

## Type Safety Examples

### Generic Types

```typescript
// Return type được infer tự động
const user = await userApi.getProfile()
// user: User

// Request type được validate
await authApi.login({
  email: 'test@example.com',
  password: 'password'
  // missing field sẽ báo lỗi TypeScript
})
```

### Pagination Types

```typescript
const result = await userApi.getUsers({
  page: 1,
  pageSize: 20
})
// result: PaginatedResponse<User>
// result.items: User[]
// result.total: number
// result.page: number
```

## Testing

### Mock API Calls

```typescript
import { http } from '@/api/http'

// Mock axios instance
jest.mock('@/api/http/axiosInstance', () => ({
  http: {
    get: jest.fn(),
    post: jest.fn(),
  }
}))
```

## Performance

- Request queuing khi refresh token
- Automatic retry support
- Timeout configuration
- Request cancellation support (via axios cancel token)
