import { create } from 'zustand'
import { persist } from 'zustand/middleware'

// ==================== Types ====================

export interface UserResponse {
  id: string
  email: string
  username: string
  firstName?: string
  lastName?: string
  role?: string
}

export interface LoginRequest {
  email: string
  password: string
}

export interface RegisterRequest {
  email: string
  username: string
  password: string
  firstName?: string
  lastName?: string
}

interface AuthState {
  // State
  user: UserResponse | null
  accessToken: string | null
  isAuthenticated: boolean
  isLoading: boolean
  error: string | null

  // Actions
  login: (payload: LoginRequest) => Promise<void>
  register: (payload: RegisterRequest) => Promise<void>
  logout: () => Promise<void>
  refreshToken: () => Promise<void>
  setUser: (user: UserResponse | null) => void
  setAccessToken: (token: string | null) => void
  clearAuth: () => void
  clearError: () => void
}

// ==================== Store ====================

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      // Initial state
      user: null,
      accessToken: null,
      isAuthenticated: false,
      isLoading: false,
      error: null,

      // UC-002: User Login
      login: async (payload: LoginRequest) => {
        try {
          set({ isLoading: true, error: null })

          // TODO: Implement actual API call
          // const response = await authClient.login(payload)

          // Placeholder - will be implemented when authClient is ready
          throw new Error('Login not yet implemented - authClient needed')
        } catch (error: any) {
          set({
            error: error.response?.data?.message || error.message || 'Login failed',
            isLoading: false,
          })
          throw error
        }
      },

      // UC-001: User Registration
      register: async (payload: RegisterRequest) => {
        try {
          set({ isLoading: true, error: null })

          // TODO: Implement actual API call
          // await authClient.register(payload)

          // Placeholder - will be implemented when authClient is ready
          throw new Error('Register not yet implemented - authClient needed')
        } catch (error: any) {
          set({
            error: error.response?.data?.message || error.message || 'Registration failed',
            isLoading: false,
          })
          throw error
        }
      },

      // UC-004: User Logout
      logout: async () => {
        try {
          set({ isLoading: true })

          // TODO: Implement actual API call
          // await authClient.logout()

          // Clear state
          set({
            user: null,
            accessToken: null,
            isAuthenticated: false,
            isLoading: false,
            error: null,
          })
        } catch (error: any) {
          // Even if logout fails on server, clear client state
          set({
            user: null,
            accessToken: null,
            isAuthenticated: false,
            isLoading: false,
            error: null,
          })
        }
      },

      // UC-003: Refresh Access Token
      refreshToken: async () => {
        try {
          // TODO: Implement actual API call
          // const response = await authClient.refreshToken()
          // set({ accessToken: response.access_token })

          // Placeholder - will be implemented when authClient is ready
          throw new Error('RefreshToken not yet implemented - authClient needed')
        } catch (error: any) {
          // If refresh fails, clear auth state
          set({
            user: null,
            accessToken: null,
            isAuthenticated: false,
            error: 'Session expired',
          })
          throw error
        }
      },

      // Setters
      setUser: (user: UserResponse | null) => {
        set({ user, isAuthenticated: !!user })
      },

      setAccessToken: (accessToken: string | null) => {
        set({ accessToken })
      },

      clearAuth: () => {
        set({
          user: null,
          accessToken: null,
          isAuthenticated: false,
          error: null,
        })
      },

      clearError: () => {
        set({ error: null })
      },
    }),
    {
      name: 'repeatwise_user', // localStorage key
      partialize: (state) => ({
        user: state.user,
        accessToken: state.accessToken,
        isAuthenticated: state.isAuthenticated,
      }),
    }
  )
)

export default useAuthStore
