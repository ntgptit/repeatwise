# HTTP Request Common - API Client

## Tổng quan

HTTP request common với interceptors để xử lý API calls, authentication, error handling và token refresh.

## Cấu trúc

```
src/
├── services/
│   ├── api.ts              # API client với interceptors
│   └── api.example.ts      # Ví dụ sử dụng
├── constants/
│   ├── api.ts              # API endpoints và config
│   └── config.ts           # App config và storage keys
├── types/
│   └── api.ts              # API types và interfaces
└── lib/
    └── tokenManager.ts     # Token management utilities
```

## Tính năng

### 1. Request Interceptor
- ✅ Tự động thêm Authorization header với Bearer token
- ✅ Thêm Request ID cho tracking
- ✅ Hỗ trợ skip auth cho các endpoint công khai

### 2. Response Interceptor
- ✅ Xử lý response data tự động
- ✅ Xử lý errors và hiển thị toast notifications
- ✅ Tự động refresh token khi token hết hạn (401)
- ✅ Queue requests khi đang refresh token

### 3. Error Handling
- ✅ Extract và format error messages
- ✅ Hiển thị toast notifications
- ✅ Xử lý network errors
- ✅ Xử lý validation errors

### 4. Token Management
- ✅ Tự động lưu/tải tokens từ localStorage
- ✅ Tự động refresh token khi hết hạn
- ✅ Logout tự động khi refresh token thất bại

## Cách sử dụng

### 1. Import API client

```typescript
import { api } from '@/services/api'
import { API_ENDPOINTS } from '@/constants/api'
```

### 2. GET Request

```typescript
// Đơn giản
const response = await api.get<User>('/users/123')
console.log(response.data) // User object

// Với query params
const response = await api.get<User[]>('/users', {
  params: { page: 1, limit: 10 }
})
```

### 3. POST Request

```typescript
const response = await api.post<{ id: string }>(
  API_ENDPOINTS.CARDS.BASE,
  { front: 'Question', back: 'Answer', deckId: '123' }
)
console.log(response.data.id)
```

### 4. PUT/PATCH Request

```typescript
// Update
const response = await api.put<Card>(
  API_ENDPOINTS.CARDS.BY_ID('123'),
  { front: 'Updated Question' }
)

// Partial update
const response = await api.patch<Card>(
  API_ENDPOINTS.CARDS.BY_ID('123'),
  { front: 'Updated Question' }
)
```

### 5. DELETE Request

```typescript
await api.delete(API_ENDPOINTS.CARDS.BY_ID('123'))
```

### 6. Skip Auth (cho public endpoints)

```typescript
// Login không cần token
const response = await api.post(
  API_ENDPOINTS.AUTH.LOGIN,
  { email, password },
  { skipAuth: true }
)
```

### 7. Skip Error Handler

```typescript
// Tự xử lý error
try {
  const response = await api.get('/endpoint', {
    skipErrorHandler: true
  })
} catch (error) {
  // Custom error handling
}
```

### 8. Custom Timeout

```typescript
const response = await api.get('/slow-endpoint', {
  timeout: 60000 // 60 seconds
})
```

## Cấu hình

### Environment Variables

Tạo file `.env`:

```env
VITE_API_BASE_URL=http://localhost:8080/api
```

### API Config

File `src/constants/api.ts`:

```typescript
export const API_CONFIG = {
  BASE_URL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  TIMEOUT: 30000,
  RETRY_COUNT: 3,
  RETRY_DELAY: 1000,
}
```

## Token Management

### Sử dụng tokenManager

```typescript
import { tokenManager } from '@/lib/tokenManager'

// Set tokens sau khi login
tokenManager.setTokens(accessToken, refreshToken)

// Check authentication
if (tokenManager.isAuthenticated()) {
  // User is logged in
}

// Clear tokens khi logout
tokenManager.clearTokens()
```

## Error Handling

### Error Structure

```typescript
interface ApiError {
  message: string
  code?: string
  status?: number
  errors?: Record<string, string[]>
  timestamp?: string
}
```

### Custom Error Handling

```typescript
import { api } from '@/services/api'
import type { ApiError } from '@/types/api'

try {
  const response = await api.get('/endpoint')
} catch (error) {
  const apiError = error as ApiError
  console.error('Status:', apiError.status)
  console.error('Message:', apiError.message)
  console.error('Errors:', apiError.errors)
}
```

## Response Structure

### Standard Response

```typescript
interface ApiResponse<T> {
  success: boolean
  data: T
  message?: string
  timestamp?: string
}
```

### Paginated Response

```typescript
interface PaginatedResponse<T> {
  items: T[]
  total: number
  page: number
  pageSize: number
  totalPages: number
}
```

## Best Practices

1. **Luôn sử dụng API_ENDPOINTS** thay vì hardcode URLs
2. **Sử dụng TypeScript types** cho request/response
3. **Xử lý errors** một cách phù hợp
4. **Không expose tokens** trong code hoặc logs
5. **Sử dụng skipAuth** cho public endpoints

## Ví dụ đầy đủ

Xem file `src/services/api.example.ts` để xem các ví dụ chi tiết về cách sử dụng API client trong các service functions.
