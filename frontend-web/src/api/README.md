# API Module

Centralized API client with interceptors for handling authentication, errors, retries, and logging.

## Structure

```
api/
├── clients/          # API client instances
│   ├── base.client.ts
│   └── index.ts
├── interceptors/     # Axios interceptors
│   ├── auth.interceptor.ts
│   ├── error.interceptor.ts
│   ├── logger.interceptor.ts
│   ├── retry.interceptor.ts
│   └── index.ts
├── types/           # TypeScript types
│   ├── api-response.ts
│   ├── error-response.ts
│   ├── page-response.ts
│   └── index.ts
└── index.ts
```

## Features

### 1. Auth Interceptor
Automatically adds authentication token to requests and handles token refresh on 401 errors.

- Adds `Authorization: Bearer <token>` header to all requests
- Automatically refreshes token when it expires (401 response)
- Queues failed requests during token refresh
- Redirects to login page if refresh fails

### 2. Error Interceptor
Transforms API errors into a consistent format and shows user-friendly notifications.

- Transforms axios errors to `ErrorResponse` format
- Shows toast notifications for errors (configurable)
- Logs errors to console in development
- Handles validation errors with field-specific messages

### 3. Retry Interceptor
Automatically retries failed requests for specific error codes.

- Retries on network errors and server errors (408, 429, 500, 502, 503, 504)
- Configurable retry count (default: 3)
- Exponential backoff delay (1s, 2s, 3s)

### 4. Logger Interceptor
Logs all API requests and responses in development mode.

- Only enabled when `VITE_ENABLE_API_LOGGING=true`
- Logs request method, URL
- Logs response status code

## Usage

### Basic Usage

```typescript
import { apiClient } from '@/api/clients'

// Make a GET request
const response = await apiClient.get('/users')

// Make a POST request
const response = await apiClient.post('/users', {
  name: 'John Doe',
  email: 'john@example.com'
})
```

### With TypeScript Types

```typescript
import { apiClient } from '@/api/clients'
import type { ApiResponse } from '@/api/types'

interface User {
  id: string
  name: string
  email: string
}

// Get single user
const response = await apiClient.get<ApiResponse<User>>('/users/123')
const user = response.data.data

// Get list of users
const response = await apiClient.get<ApiResponse<User[]>>('/users')
const users = response.data.data
```

### Error Handling

```typescript
import { apiClient } from '@/api/clients'
import type { ErrorResponse } from '@/api/types'

try {
  const response = await apiClient.post('/users', userData)
} catch (error) {
  // Error is transformed to ErrorResponse
  const apiError = error as ErrorResponse

  console.error(apiError.message)
  console.error(apiError.code)
  console.error(apiError.statusCode)

  // Handle validation errors
  if (apiError.errors) {
    apiError.errors.forEach(err => {
      console.error(`${err.field}: ${err.message}`)
    })
  }
}
```

## Configuration

### Environment Variables

Create a `.env` file with the following variables:

```env
# API Configuration
VITE_API_BASE_URL=http://localhost:8080/api
VITE_API_TIMEOUT=30000

# Logging
VITE_ENABLE_API_LOGGING=true
```

### API Config

Edit `src/config/api.config.ts` to customize:

- Retry behavior (max retries, delay, status codes)
- Request timeout
- Default headers

```typescript
export const apiConfig: ApiConfig = {
  baseURL: env.apiBaseUrl,
  timeout: env.apiTimeout,
  enableRetry: true,
  maxRetries: 3,
  retryDelay: 1000,
  retryStatusCodes: [408, 429, 500, 502, 503, 504],
  // ...
}
```

## Creating Custom API Clients

You can create specialized API clients for different endpoints:

```typescript
// src/api/clients/user.client.ts
import { apiClient } from './base.client'
import type { ApiResponse, PageResponse } from '@/api/types'

export interface User {
  id: string
  name: string
  email: string
}

export const userClient = {
  getAll: () =>
    apiClient.get<PageResponse<User>>('/users'),

  getById: (id: string) =>
    apiClient.get<ApiResponse<User>>(`/users/${id}`),

  create: (data: Partial<User>) =>
    apiClient.post<ApiResponse<User>>('/users', data),

  update: (id: string, data: Partial<User>) =>
    apiClient.put<ApiResponse<User>>(`/users/${id}`, data),

  delete: (id: string) =>
    apiClient.delete<ApiResponse<void>>(`/users/${id}`),
}
```

## Integration with Auth Store

The auth interceptor integrates with zustand auth store:

```typescript
// The interceptor automatically:
// 1. Reads accessToken from store
// 2. Adds it to request headers
// 3. Calls refreshToken() on 401 errors
// 4. Retries failed requests after refresh
// 5. Clears auth and redirects to login if refresh fails
```

## Notes

- All interceptors are automatically applied to the base `apiClient`
- Error notifications can be disabled per-request or globally
- Auth token is persisted in localStorage via zustand middleware
- The logger interceptor only runs in development mode
