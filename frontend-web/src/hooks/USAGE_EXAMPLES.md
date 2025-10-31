# Custom Hooks - Usage Examples

## ✅ Đúng cách: UI Layer xử lý navigation và toast

### useAuth Example

```tsx
import { useAuth } from '@/hooks/domain'
import { useNavigate } from 'react-router-dom'
import { toast } from 'sonner'

function LoginPage() {
  const { login, isLoggingIn, loginError } = useAuth()
  const navigate = useNavigate()

  const handleLogin = (email: string, password: string) => {
    login(
      { email, password },
      {
        onSuccess: (data) => {
          // ✅ UI layer xử lý toast
          toast.success('Login successful!')
          
          // ✅ UI layer xử lý navigation
          navigate('/dashboard')
        },
        onError: (error) => {
          // ✅ UI layer xử lý error toast
          toast.error(error.message || 'Login failed')
        },
      },
    )
  }

  return (
    <form onSubmit={(e) => {
      e.preventDefault()
      handleLogin(email, password)
    }}>
      {/* Form fields */}
      {isLoggingIn && <LoadingSpinner />}
      {loginError && <ErrorMessage error={loginError} />}
    </form>
  )
}
```

### useProfile Example

```tsx
import { useProfile } from '@/hooks/domain'
import { toast } from 'sonner'

function ProfilePage() {
  const {
    profile,
    isLoading,
    updateProfile,
    isUpdating,
    updateError,
  } = useProfile()

  const handleUpdate = (data: UpdateUserRequest) => {
    // ✅ Sử dụng mutation với callbacks
    updateProfileMutation.mutate(data, {
      onSuccess: () => {
        // ✅ UI layer xử lý toast
        toast.success('Profile updated!')
      },
      onError: (error) => {
        // ✅ UI layer xử lý error
        toast.error(error.message || 'Update failed')
      },
    })
  }

  if (isLoading) return <LoadingSpinner />
  if (!profile) return <EmptyState />

  return (
    <ProfileForm
      initialData={profile}
      onSubmit={handleUpdate}
      isLoading={isUpdating}
    />
  )
}
```

### useFetch Example

```tsx
import { useFetch } from '@/hooks/http'

function UserList() {
  const { data: users, isLoading, error, refetch } = useFetch<User[]>(
    ['users'],
    '/api/users',
  )

  if (isLoading) return <LoadingSpinner />
  if (error) return <ErrorMessage error={error} />
  if (!users) return <EmptyState />

  return (
    <div>
      {users.map((user) => (
        <UserCard key={user.id} user={user} />
      ))}
    </div>
  )
}
```

### usePost Example

```tsx
import { usePost } from '@/hooks/http'
import { toast } from 'sonner'

function CreateUserForm() {
  const { mutate: createUser, isPending } = usePost<{ id: string }, CreateUserRequest>(
    ['users'],
    '/api/users',
    undefined,
    {
      onSuccess: (data) => {
        // ✅ UI layer xử lý toast
        toast.success(`User ${data.id} created!`)
        // ✅ UI layer có thể navigate hoặc refetch
      },
      onError: (error) => {
        // ✅ UI layer xử lý error
        toast.error(error.message)
      },
    },
  )

  const handleSubmit = (data: CreateUserRequest) => {
    createUser(data)
  }

  return (
    <form onSubmit={handleSubmit}>
      {/* Form fields */}
      <button disabled={isPending}>
        {isPending ? 'Creating...' : 'Create User'}
      </button>
    </form>
  )
}
```

## ❌ Sai cách: Hook tự xử lý UI

```tsx
// ❌ SAI: Hook không nên có navigate bên trong
function useAuth() {
  const navigate = useNavigate() // ❌ UI logic trong hook
  
  const login = (data) => {
    authApi.login(data).then(() => {
      navigate('/dashboard') // ❌ Navigation trong hook
      toast.success('Success!') // ❌ Toast trong hook
    })
  }
}

// ❌ SAI: Hook không nên render JSX
function useUserList() {
  return (
    <div>{/* JSX trong hook */}</div> // ❌ JSX trong hook
  )
}

// ❌ SAI: Hook không nên gọi alert
function useConfirm() {
  const confirm = () => {
    alert('Are you sure?') // ❌ Alert trong hook
  }
}
```

## Best Practices

1. **Hooks chỉ trả về state và functions**
2. **UI layer tự quyết định cách hiển thị**
3. **Sử dụng callbacks để UI layer xử lý side effects**
4. **Type-safe với TypeScript Generics**
5. **Clean API với tên rõ ràng**
