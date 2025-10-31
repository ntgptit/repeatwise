# Test Examples - Custom Hooks

Ví dụ test cho các hooks để đảm bảo testability.

## 🧪 Test Setup

```typescript
import { renderHook, waitFor } from '@testing-library/react'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { http } from '@/api/http/axiosInstance'
import { useFetch, usePost, useMutation } from '@/hooks/http'
import { useAuth } from '@/hooks/domain'
import { useModal, useToggle } from '@/hooks/ui'
import { useForm, useInput } from '@/hooks/form'
import { useDebounce, useThrottle } from '@/hooks/utils'

// Mock setup
jest.mock('@/api/http/axiosInstance')
const mockHttp = http as jest.Mocked<typeof http>

const createWrapper = () => {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false },
      mutations: { retry: false },
    },
  })
  return ({ children }: { children: React.ReactNode }) => (
    <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
  )
}
```

## 📋 Test Examples

### useFetch

```typescript
describe('useFetch', () => {
  it('should fetch data successfully', async () => {
    const mockData = { id: '1', name: 'Test' }
    mockHttp.get.mockResolvedValue(mockData)

    const { result } = renderHook(
      () => useFetch(['test'], '/api/test'),
      { wrapper: createWrapper() }
    )

    expect(result.current.loading).toBe(true)
    expect(result.current.data).toBeUndefined()

    await waitFor(() => {
      expect(result.current.loading).toBe(false)
    })

    expect(result.current.data).toEqual(mockData)
    expect(result.current.error).toBeNull()
  })

  it('should handle error', async () => {
    const mockError = new Error('Fetch failed')
    mockHttp.get.mockRejectedValue(mockError)

    const { result } = renderHook(
      () => useFetch(['test'], '/api/test'),
      { wrapper: createWrapper() }
    )

    await waitFor(() => {
      expect(result.current.error).toBeTruthy()
    })

    expect(result.current.loading).toBe(false)
    expect(result.current.data).toBeUndefined()
  })

  it('should refetch data', async () => {
    const mockData = { id: '1', name: 'Test' }
    mockHttp.get.mockResolvedValue(mockData)

    const { result } = renderHook(
      () => useFetch(['test'], '/api/test'),
      { wrapper: createWrapper() }
    )

    await waitFor(() => {
      expect(result.current.loading).toBe(false)
    })

    result.current.refetch()

    await waitFor(() => {
      expect(mockHttp.get).toHaveBeenCalledTimes(2)
    })
  })
})
```

### usePost

```typescript
describe('usePost', () => {
  it('should post data successfully', async () => {
    const mockResponse = { id: '1', name: 'Created' }
    mockHttp.post.mockResolvedValue(mockResponse)

    const { result } = renderHook(
      () => usePost(['test'], '/api/test'),
      { wrapper: createWrapper() }
    )

    expect(result.current.isPending).toBe(false)

    result.current.mutate({ name: 'Test' })

    expect(result.current.isPending).toBe(true)

    await waitFor(() => {
      expect(result.current.isPending).toBe(false)
    })

    expect(result.current.data).toEqual(mockResponse)
    expect(result.current.isSuccess).toBe(true)
  })

  it('should handle error', async () => {
    const mockError = new Error('Post failed')
    mockHttp.post.mockRejectedValue(mockError)

    const { result } = renderHook(
      () => usePost(['test'], '/api/test'),
      { wrapper: createWrapper() }
    )

    result.current.mutate({ name: 'Test' })

    await waitFor(() => {
      expect(result.current.error).toBeTruthy()
    })

    expect(result.current.isError).toBe(true)
    expect(result.current.isSuccess).toBe(false)
  })
})
```

### useModal

```typescript
describe('useModal', () => {
  it('should initialize with closed state', () => {
    const { result } = renderHook(() => useModal())

    expect(result.current.isOpen).toBe(false)
  })

  it('should open modal', () => {
    const { result } = renderHook(() => useModal())

    result.current.open()

    expect(result.current.isOpen).toBe(true)
  })

  it('should close modal', () => {
    const { result } = renderHook(() => useModal(true))

    result.current.close()

    expect(result.current.isOpen).toBe(false)
  })

  it('should toggle modal', () => {
    const { result } = renderHook(() => useModal())

    result.current.toggle()
    expect(result.current.isOpen).toBe(true)

    result.current.toggle()
    expect(result.current.isOpen).toBe(false)
  })
})
```

### useForm

```typescript
describe('useForm', () => {
  it('should initialize with initial values', () => {
    const initialValues = { email: '', password: '' }
    const { result } = renderHook(() => useForm(initialValues))

    expect(result.current.values).toEqual(initialValues)
  })

  it('should update values on change', () => {
    const { result } = renderHook(() => useForm({ email: '', password: '' }))

    const mockEvent = {
      target: { value: 'test@example.com' },
    } as React.ChangeEvent<HTMLInputElement>

    result.current.handleChange('email')(mockEvent)

    expect(result.current.values.email).toBe('test@example.com')
  })

  it('should reset values', () => {
    const initialValues = { email: '', password: '' }
    const { result } = renderHook(() => useForm(initialValues))

    result.current.handleChangeValue('email', 'test@example.com')
    result.current.reset()

    expect(result.current.values).toEqual(initialValues)
  })
})
```

### useDebounce

```typescript
describe('useDebounce', () => {
  beforeEach(() => {
    jest.useFakeTimers()
  })

  afterEach(() => {
    jest.useRealTimers()
  })

  it('should debounce value', () => {
    const { result, rerender } = renderHook(
      ({ value }) => useDebounce(value, 500),
      { initialProps: { value: 'test' } }
    )

    expect(result.current).toBe('test')

    rerender({ value: 'updated' })
    expect(result.current).toBe('test') // Still old value

    jest.advanceTimersByTime(500)
    expect(result.current).toBe('updated')
  })

  it('should cancel debounce', () => {
    const { result, rerender } = renderHook(
      ({ value }) => useDebounce(value, 500, { cancelable: true }),
      { initialProps: { value: 'test' } }
    )

    rerender({ value: 'updated' })
    result.current.cancel()

    jest.advanceTimersByTime(500)
    expect(result.current.value).toBe('test') // Not updated
  })
})
```

### useAuth

```typescript
describe('useAuth', () => {
  beforeEach(() => {
    localStorage.clear()
    jest.clearAllMocks()
  })

  it('should login successfully', async () => {
    const mockResponse = {
      accessToken: 'token',
      refreshToken: 'refresh',
      user: { id: '1', email: 'test@example.com', name: 'Test' },
    }
    mockHttp.post.mockResolvedValue(mockResponse)

    const { result } = renderHook(() => useAuth(), {
      wrapper: createWrapper(),
    })

    expect(result.current.isAuthenticated).toBe(false)
    expect(result.current.user).toBeNull()

    const onSuccess = jest.fn()
    result.current.login({ email: 'test@example.com', password: 'pass' }, {
      onSuccess,
    })

    await waitFor(() => {
      expect(result.current.isAuthenticated).toBe(true)
    })

    expect(result.current.user).toEqual(mockResponse.user)
    expect(onSuccess).toHaveBeenCalled()
  })

  it('should logout successfully', async () => {
    localStorage.setItem('accessToken', 'token')

    const { result } = renderHook(() => useAuth(), {
      wrapper: createWrapper(),
    })

    const onSuccess = jest.fn()
    result.current.logout({ onSuccess })

    await waitFor(() => {
      expect(result.current.isAuthenticated).toBe(false)
    })

    expect(localStorage.getItem('accessToken')).toBeNull()
    expect(onSuccess).toHaveBeenCalled()
  })
})
```

## ✅ Test Coverage Goals

- **Loading states**: ✅ Test loading/error states
- **Success cases**: ✅ Test successful API calls
- **Error cases**: ✅ Test error handling
- **Side effects**: ✅ Test state changes
- **Performance**: ✅ Test memoization và re-renders

Tất cả hooks đều dễ test với Jest và mocks!
