import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import { authService } from '@/api/services/auth.service'
import type {
  User,
  LoginRequest,
  RegisterRequest,
  UpdateProfileRequest,
  ChangePasswordRequest,
} from '@/api/types/auth.types'

// ==================== Types ====================

interface AuthState {
  // State
  user: User | null
  accessToken: string | null
  isAuthenticated: boolean
  isLoading: boolean
  error: string | null

  // Actions
  login: (payload: LoginRequest) => Promise<void>
  register: (payload: RegisterRequest) => Promise<void>
  logout: () => Promise<void>
  logoutAll: () => Promise<void>
  refreshToken: () => Promise<void>
  updateProfile: (payload: UpdateProfileRequest) => Promise<void>
  changePassword: (payload: ChangePasswordRequest) => Promise<void>
  setUser: (user: User | null) => void
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

      /**
       * UC-002: User Login
       */
      login: async (payload: LoginRequest) => {
        try {
          set({ isLoading: true, error: null })

          const response = await authService.login(payload)

          set({
            user: response.user,
            accessToken: response.accessToken,
            isAuthenticated: true,
            isLoading: false,
            error: null,
          })
        } catch (error: any) {
          // Handle both AxiosError and transformed ErrorResponse from interceptor
          const errorMessage =
            error.message || error.response?.data?.message || 'Login failed'

          set({
            error: errorMessage,
            isLoading: false,
            isAuthenticated: false,
          })

          // Re-throw error so calling code can handle it
          throw error
        }
      },

      /**
       * UC-001: User Registration
       */
      register: async (payload: RegisterRequest) => {
        try {
          set({ isLoading: true, error: null })

          await authService.register(payload)

          // Registration successful - user needs to login
          set({
            isLoading: false,
            error: null,
          })
        } catch (error: any) {
          // Handle both AxiosError and transformed ErrorResponse from interceptor
          const errorMessage =
            error.message || error.response?.data?.message || 'Registration failed'

          set({
            error: errorMessage,
            isLoading: false,
          })

          // Re-throw error so calling code can handle it
          throw error
        }
      },

      /**
       * UC-004: User Logout
       */
      logout: async () => {
        try {
          set({ isLoading: true })

          // Call logout endpoint to revoke refresh token
          await authService.logout()

          // Clear state
          set({
            user: null,
            accessToken: null,
            isAuthenticated: false,
            isLoading: false,
            error: null,
          })
        } catch (error: any) {
          // Even if logout fails on server, clear client state (optimistic logout)
          console.error('Logout error:', error)
          set({
            user: null,
            accessToken: null,
            isAuthenticated: false,
            isLoading: false,
            error: null,
          })
        }
      },

      /**
       * UC-004: Logout All Devices
       */
      logoutAll: async () => {
        try {
          set({ isLoading: true })

          // Call logout-all endpoint to revoke all refresh tokens
          await authService.logoutAll()

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
          console.error('Logout all error:', error)
          set({
            user: null,
            accessToken: null,
            isAuthenticated: false,
            isLoading: false,
            error: null,
          })
        }
      },

      /**
       * UC-003: Refresh Access Token
       * Called automatically by auth interceptor
       */
      refreshToken: async () => {
        try {
          const response = await authService.refreshToken()
          set({ accessToken: response.accessToken })
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

      /**
       * UC-005: Update User Profile
       */
      updateProfile: async (payload: UpdateProfileRequest) => {
        try {
          set({ isLoading: true, error: null })

          const response = await authService.updateProfile(payload)

          set({
            user: response.user,
            isLoading: false,
            error: null,
          })
        } catch (error: any) {
          // Handle both AxiosError and transformed ErrorResponse from interceptor
          const errorMessage =
            error.message || error.response?.data?.message || 'Profile update failed'

          set({
            error: errorMessage,
            isLoading: false,
          })

          // Re-throw error so calling code can handle it
          throw error
        }
      },

      /**
       * UC-006: Change Password
       * Clears auth state after successful password change (requires re-login)
       */
      changePassword: async (payload: ChangePasswordRequest) => {
        try {
          set({ isLoading: true, error: null })

          await authService.changePassword(payload)

          // Password changed successfully - clear auth state to force re-login
          set({
            user: null,
            accessToken: null,
            isAuthenticated: false,
            isLoading: false,
            error: null,
          })
        } catch (error: any) {
          // Handle both AxiosError and transformed ErrorResponse from interceptor
          const errorMessage =
            error.message || error.response?.data?.message || 'Password change failed'

          set({
            error: errorMessage,
            isLoading: false,
          })

          // Re-throw error so calling code can handle it
          throw error
        }
      },

      // Setters
      setUser: (user: User | null) => {
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
      name: 'repeatwise_auth', // localStorage key
      partialize: (state) => ({
        user: state.user,
        accessToken: state.accessToken,
        isAuthenticated: state.isAuthenticated,
      }),
    }
  )
)

export default useAuthStore
