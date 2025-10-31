# Custom Hooks Documentation

This directory contains reusable custom hooks organized by category.

## Structure

```
hooks/
├── http/          # HTTP request hooks (TanStack Query)
├── ui/            # UI state management hooks
├── utils/         # Utility hooks
└── domain/        # Domain-specific hooks
```

## HTTP Hooks

### useFetch

GET request hook using TanStack Query.

```tsx
import { useFetch } from '@/hooks/http'

function MyComponent() {
  const { data, isLoading, error } = useFetch<User>(
    ['user', userId],
    `/api/users/${userId}`
  )

  if (isLoading) return <div>Loading...</div>
  if (error) return <div>Error: {error.message}</div>
  
  return <div>{data?.name}</div>
}
```

### usePost

POST request hook with automatic query invalidation.

```tsx
import { usePost } from '@/hooks/http'

function CreateUser() {
  const { mutate, isPending } = usePost<{ id: string }, CreateUserRequest>(
    ['users'],
    '/api/users',
    undefined,
    {
      onSuccess: (data) => {
        console.log('User created:', data.id)
      }
    }
  )

  const handleSubmit = () => {
    mutate({ name: 'John', email: 'john@example.com' })
  }

  return <button onClick={handleSubmit} disabled={isPending}>
    {isPending ? 'Creating...' : 'Create User'}
  </button>
}
```

### useMutation

Generic mutation hook for any HTTP method.

```tsx
import { useMutation } from '@/hooks/http'

function UpdateUser() {
  const { mutate, isPending } = useMutation(
    (data: UpdateUserRequest) => userApi.updateProfile(data),
    {
      invalidateQueries: [['profile']],
      onSuccess: () => {
        toast.success('Profile updated!')
      }
    }
  )

  return <button onClick={() => mutate({ name: 'New Name' })}>
    Update Profile
  </button>
}
```

## UI Hooks

### useModal

Modal state management hook.

```tsx
import { useModal } from '@/hooks/ui'

function MyComponent() {
  const { isOpen, open, close } = useModal()

  return (
    <>
      <button onClick={open}>Open Modal</button>
      <Modal open={isOpen} onClose={close}>
        Modal Content
      </Modal>
    </>
  )
}
```

### useToggle

Toggle state hook.

```tsx
import { useToggle } from '@/hooks/ui'

function ToggleComponent() {
  const { isOn, toggle, turnOn, turnOff } = useToggle()

  return (
    <div>
      <button onClick={toggle}>Toggle</button>
      <button onClick={turnOn}>Turn On</button>
      <button onClick={turnOff}>Turn Off</button>
      <p>Status: {isOn ? 'ON' : 'OFF'}</p>
    </div>
  )
}
```

## Utils Hooks

### useDebounce

Debounce a value with a delay.

```tsx
import { useDebounce } from '@/hooks/utils'

function SearchComponent() {
  const [searchTerm, setSearchTerm] = useState('')
  const debouncedSearchTerm = useDebounce(searchTerm, 500)

  useEffect(() => {
    if (debouncedSearchTerm) {
      // Perform search
      searchApi.search(debouncedSearchTerm)
    }
  }, [debouncedSearchTerm])

  return (
    <input
      value={searchTerm}
      onChange={(e) => setSearchTerm(e.target.value)}
      placeholder="Search..."
    />
  )
}
```

### useOutsideClick

Detect clicks outside an element.

```tsx
import { useOutsideClick } from '@/hooks/utils'

function Dropdown() {
  const [isOpen, setIsOpen] = useState(false)
  const ref = useOutsideClick<HTMLDivElement>(() => setIsOpen(false))

  return (
    <div ref={ref}>
      <button onClick={() => setIsOpen(!isOpen)}>Toggle</button>
      {isOpen && <div>Dropdown Content</div>}
    </div>
  )
}
```

## Domain Hooks

### useAuth

Authentication hook with login, register, and logout.

```tsx
import { useAuth } from '@/hooks/domain'

function LoginPage() {
  const { login, isLoading, isAuthenticated } = useAuth()

  const handleLogin = () => {
    login({
      email: 'user@example.com',
      password: 'password123'
    })
  }

  if (isAuthenticated) {
    return <Navigate to="/dashboard" />
  }

  return (
    <button onClick={handleLogin} disabled={isLoading}>
      {isLoading ? 'Logging in...' : 'Login'}
    </button>
  )
}
```

### useProfile

User profile hook with query and mutations.

```tsx
import { useProfile } from '@/hooks/domain'

function ProfilePage() {
  const {
    profile,
    isLoading,
    updateProfile,
    changePassword,
    isUpdating
  } = useProfile()

  if (isLoading) return <div>Loading...</div>

  return (
    <div>
      <h1>{profile?.name}</h1>
      <button onClick={() => updateProfile({ name: 'New Name' })}>
        Update Profile
      </button>
    </div>
  )
}
```

## Query Client

The hooks use TanStack Query (React Query) for data fetching and caching. The query client is configured in `src/lib/queryClient.ts`:

```tsx
import { QueryClientProvider } from '@tanstack/react-query'
import { queryClient } from '@/lib/queryClient'

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      {/* Your app */}
    </QueryClientProvider>
  )
}
```

## Best Practices

1. **Query Keys**: Use consistent, hierarchical query keys
   ```tsx
   ['users']                    // List
   ['users', userId]           // Single item
   ['users', userId, 'posts']  // Nested resource
   ```

2. **Error Handling**: Handle errors appropriately
   ```tsx
   const { data, error } = useFetch(...)
   if (error) {
     // Show error message
   }
   ```

3. **Loading States**: Always show loading states
   ```tsx
   if (isLoading) return <LoadingSpinner />
   ```

4. **Optimistic Updates**: Use mutations for updates
   ```tsx
   const mutation = useMutation(updateFn, {
     onSuccess: () => {
       queryClient.invalidateQueries(['users'])
     }
   })
   ```
