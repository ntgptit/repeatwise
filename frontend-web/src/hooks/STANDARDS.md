# Custom Hooks Standards Compliance

Tất cả các custom hooks đã được xây dựng theo các tiêu chuẩn bắt buộc sau:

## ✅ 1️⃣ Tên & Vai Trò Rõ Ràng

### Quy tắc đặt tên:
- ✅ Tất cả hooks bắt đầu bằng `use`
- ✅ Tên diễn đạt rõ tác dụng:
  - `useFetch` - GET requests
  - `usePost` - POST requests
  - `useMutation` - Generic mutations
  - `useModal` - Modal state
  - `useToggle` - Toggle state
  - `useDebounce` - Debounce values
  - `useOutsideClick` - Detect outside clicks
  - `useAuth` - Authentication
  - `useProfile` - User profile

### Một hook = một nhiệm vụ:
- ✅ Mỗi hook chỉ làm một việc duy nhất
- ✅ Không có hook làm nhiều việc không liên quan

## ✅ 2️⃣ Tách Biệt UI

### Không render JSX:
- ✅ Không có hook nào render JSX
- ✅ Tất cả hooks chỉ trả về state và functions

### Không gọi alert, toast trực tiếp:
- ✅ `useAuth`: Đã loại bỏ `navigate()` - UI layer tự xử lý
- ✅ `useProfile`: Không có toast - UI layer tự xử lý qua callbacks
- ✅ Tất cả hooks chỉ trả về state để UI layer tự quyết định hiển thị

### Ví dụ sử dụng đúng:
```tsx
// ✅ ĐÚNG: UI layer xử lý navigation và toast
function LoginPage() {
  const { login, loginError } = useAuth()
  const navigate = useNavigate()

  const handleLogin = () => {
    login(
      { email, password },
      {
        onSuccess: () => {
          toast.success('Login successful!')
          navigate('/dashboard')
        },
        onError: () => {
          toast.error('Login failed!')
        }
      }
    )
  }
}

// ❌ SAI: Hook tự xử lý navigation
// Hook không nên có navigate() bên trong
```

## ✅ 3️⃣ Type-Safe Tuyệt Đối

### Sử dụng TypeScript Generics:
- ✅ `useFetch<T>` - Generic cho response type
- ✅ `usePost<T, D>` - Generics cho response và request types
- ✅ `useMutation<TData, TVariables, TError>` - Generics cho data, variables, error
- ✅ `useDebounce<T>` - Generic cho value type

### Không dùng `any` hoặc `unknown` không cần thiết:
- ✅ Tất cả hooks có type rõ ràng
- ✅ Return types được định nghĩa trong interfaces:
  - `UseFetchReturn<T>`
  - `UsePostReturn<T, D>`
  - `UseMutationReturn<TData, TVariables, TError>`
  - `UseAuthReturn`
  - `UseProfileReturn`
  - `UseModalReturn`
  - `UseToggleReturn`

### Return types rõ ràng:
```tsx
// ✅ ĐÚNG: Return type rõ ràng
export interface UseFetchReturn<T> {
  data: T | undefined
  isLoading: boolean
  isError: boolean
  error: Error | null
  refetch: () => void
  isFetching: boolean
}
```

## ✅ 4️⃣ Không Side-Effect Ẩn

### useEffect chỉ cho dependencies cần thiết:
- ✅ `useDebounce`: useEffect chỉ chạy khi `value` hoặc `delay` thay đổi
- ✅ `useOutsideClick`: useEffect chỉ chạy khi `handler` hoặc `enabled` thay đổi
- ✅ Không có useEffect không cần thiết

### Không gọi API hay update state trong render:
- ✅ Tất cả API calls được thực hiện trong mutation functions
- ✅ Không có side-effects trong render phase

### Không mutate data gốc:
- ✅ Tất cả hooks sử dụng immutable updates
- ✅ Query client invalidation thay vì mutate trực tiếp

## ✅ 5️⃣ Clean API Surface

### Input: Nhận params/config rõ ràng:
```tsx
// ✅ ĐÚNG: Input rõ ràng
useFetch<User>(
  ['user', userId],
  `/api/users/${userId}`,
  { timeout: 5000 },
  { enabled: !!userId }
)

usePost<CreateUserResponse, CreateUserRequest>(
  ['users'],
  '/api/users',
  undefined,
  {
    onSuccess: (data) => { /* UI layer handles */ },
    onError: (error) => { /* UI layer handles */ }
  }
)
```

### Output: Trả về object rõ ràng với tên gợi nghĩa:
```tsx
// ✅ ĐÚNG: Output rõ ràng với tên gợi nghĩa
const {
  data,           // ✅ Rõ ràng
  isLoading,      // ✅ Rõ ràng
  isError,        // ✅ Rõ ràng
  error,          // ✅ Rõ ràng
  refetch         // ✅ Rõ ràng
} = useFetch(...)

const {
  login,          // ✅ Rõ ràng
  isLoggingIn,    // ✅ Rõ ràng
  loginError,     // ✅ Rõ ràng
  loginAsync      // ✅ Rõ ràng
} = useAuth()
```

## 📋 Checklist Compliance

| Hook | Tên rõ ràng | Tách biệt UI | Type-safe | Không side-effect | Clean API |
|------|-------------|--------------|-----------|-------------------|-----------|
| `useFetch` | ✅ | ✅ | ✅ | ✅ | ✅ |
| `usePost` | ✅ | ✅ | ✅ | ✅ | ✅ |
| `useMutation` | ✅ | ✅ | ✅ | ✅ | ✅ |
| `useModal` | ✅ | ✅ | ✅ | ✅ | ✅ |
| `useToggle` | ✅ | ✅ | ✅ | ✅ | ✅ |
| `useDebounce` | ✅ | ✅ | ✅ | ✅ | ✅ |
| `useOutsideClick` | ✅ | ✅ | ✅ | ✅ | ✅ |
| `useAuth` | ✅ | ✅ | ✅ | ✅ | ✅ |
| `useProfile` | ✅ | ✅ | ✅ | ✅ | ✅ |

## 🎯 Best Practices

### 1. Sử dụng hooks với callbacks:
```tsx
const { login } = useAuth()

login(
  { email, password },
  {
    onSuccess: () => {
      toast.success('Success!')
      navigate('/dashboard')
    },
    onError: (error) => {
      toast.error(error.message)
    }
  }
)
```

### 2. Type inference tự động:
```tsx
// Type được infer tự động từ generic
const { data } = useFetch<User>(['user'], '/api/user')
// data type: User | undefined
```

### 3. Error handling:
```tsx
const { data, error, isError } = useFetch<User>(['user'], '/api/user')

if (isError) {
  // UI layer handles error display
  return <ErrorDisplay error={error} />
}
```

Tất cả hooks đã tuân thủ đầy đủ các tiêu chuẩn bắt buộc!
