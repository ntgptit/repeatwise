# Custom Hooks - Naming & Return Standards

Tất cả hooks đã được chuẩn hóa theo nguyên tắc đặt tên và trả về chuẩn.

## 📋 Compliance Checklist

| Loại Hook | Tên Hook | Return Chuẩn | Status |
|-----------|----------|--------------|--------|
| Fetch data | `useFetch` | `{ data, loading, error, refetch }` | ✅ |
| State logic | `useModal` | `{ isOpen, open, close, toggle }` | ✅ |
| State logic | `useToggle` | `{ isOpen, open, close, toggle }` | ✅ |
| Form logic | `useForm` | `{ values, handleChange, reset }` | ✅ |
| Form logic | `useInput` | `{ value, handleChange, reset }` | ✅ |
| Utility | `useDebounce` | `debouncedValue` hoặc `{ value, cancel }` | ✅ |
| Utility | `useThrottle` | `throttledValue` hoặc `{ value, cancel }` | ✅ |
| Auth | `useAuth` | `{ user, isAuthenticated, login, logout }` | ✅ |

## ✅ 1. Fetch Data Hooks

### useFetch
```tsx
// ✅ Chuẩn: { data, loading, error, refetch }
const { data, loading, error, refetch } = useFetch<User>(
  ['user', userId],
  `/api/users/${userId}`
)

// ❌ Cũ: { data, isLoading, isError, error, refetch, isFetching }
```

**Changes:**
- ✅ Đổi `isLoading` → `loading`
- ✅ Loại bỏ `isError` (dùng `error` để check)
- ✅ Loại bỏ `isFetching` (dùng `loading`)

## ✅ 2. State Logic Hooks

### useModal
```tsx
// ✅ Chuẩn: { isOpen, open, close, toggle }
const { isOpen, open, close, toggle } = useModal()
```

### useToggle
```tsx
// ✅ Chuẩn: { isOpen, open, close, toggle } (giống useModal)
const { isOpen, open, close, toggle } = useToggle()

// ❌ Cũ: { isOn, turnOn, turnOff, toggle }
```

**Changes:**
- ✅ Đổi `isOn` → `isOpen`
- ✅ Đổi `turnOn` → `open`
- ✅ Đổi `turnOff` → `close`
- ✅ Giữ nguyên `toggle`

## ✅ 3. Form Logic Hooks

### useForm
```tsx
// ✅ Chuẩn: { values, handleChange, reset }
const { values, handleChange, reset } = useForm({
  email: '',
  password: '',
})

// Usage
<input 
  value={values.email}
  onChange={handleChange('email')}
/>
```

### useInput
```tsx
// ✅ Chuẩn: { value, handleChange, reset }
const { value, handleChange, reset } = useInput('')

// Usage
<input value={value} onChange={handleChange} />
```

## ✅ 4. Utility Hooks

### useDebounce
```tsx
// ✅ Chuẩn: Simple version - trả về debouncedValue
const debouncedSearch = useDebounce(searchTerm, 500)

// ✅ Chuẩn: With cancel - trả về { value, cancel }
const { value: debouncedSearch, cancel } = useDebounce(
  searchTerm,
  500,
  { cancelable: true }
)
```

### useThrottle
```tsx
// ✅ Chuẩn: Simple version - trả về throttledValue
const throttledScroll = useThrottle(scrollY, 100)

// ✅ Chuẩn: With cancel - trả về { value, cancel }
const { value: throttledScroll, cancel } = useThrottle(
  scrollY,
  100,
  { cancelable: true }
)
```

## ✅ 5. Auth Hooks

### useAuth
```tsx
// ✅ Chuẩn: { user, isAuthenticated, login, logout }
const {
  user,           // ✅ Chuẩn
  isAuthenticated, // ✅ Chuẩn
  login,           // ✅ Chuẩn
  logout,          // ✅ Chuẩn
  isLoading,
  isLoggingIn,
  loginError,
} = useAuth()

// Usage
if (!isAuthenticated) {
  return <LoginPage />
}

return <div>Welcome, {user?.name}!</div>
```

**Changes:**
- ✅ Thêm `user` field (lấy từ profile query)
- ✅ Đảm bảo có `isAuthenticated`
- ✅ Đảm bảo có `login` và `logout`

## 📝 Usage Examples

### Fetch Data
```tsx
const { data, loading, error, refetch } = useFetch<User[]>(
  ['users'],
  '/api/users'
)

if (loading) return <LoadingSpinner />
if (error) return <ErrorMessage error={error} />
return <UserList users={data} />
```

### State Logic
```tsx
const { isOpen, open, close, toggle } = useModal()

return (
  <>
    <button onClick={open}>Open</button>
    <Modal open={isOpen} onClose={close}>
      Content
    </Modal>
  </>
)
```

### Form Logic
```tsx
const { values, handleChange, reset } = useForm({
  email: '',
  password: '',
})

return (
  <form onSubmit={handleSubmit}>
    <input
      value={values.email}
      onChange={handleChange('email')}
    />
    <button onClick={reset}>Reset</button>
  </form>
)
```

### Utility
```tsx
const [searchTerm, setSearchTerm] = useState('')
const debouncedSearch = useDebounce(searchTerm, 500)

useEffect(() => {
  if (debouncedSearch) {
    searchApi.search(debouncedSearch)
  }
}, [debouncedSearch])
```

### Auth
```tsx
const { user, isAuthenticated, login, logout } = useAuth()

if (!isAuthenticated) {
  return <LoginPage />
}

return (
  <div>
    <p>Welcome, {user?.name}!</p>
    <button onClick={logout}>Logout</button>
  </div>
)
```

## ✅ Tất cả hooks đã tuân thủ chuẩn!
