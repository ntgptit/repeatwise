import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import { authClient, type UserResponse, type LoginRequest, type RegisterRequest } from '@/api/clients/auth.client'
import { STORAGE_KEYS } from '@/config/app.config'

// ==================== Types ====================

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

          const response = await authClient.login(payload)

          set({
            user: response.user,
            accessToken: response.accessToken,
            isAuthenticated: true,
            isLoading: false,
            error: null,
          })
        } catch (error: any) {
          set({
            error: error.response?.data?.message || 'Login failed',
            isLoading: false,
          })
          throw error
        }
      },

      // UC-001: User Registration
      register: async (payload: RegisterRequest) => {
        try {
          set({ isLoading: true, error: null })

          await authClient.register(payload)

          set({
            isLoading: false,
            error: null,
          })
        } catch (error: any) {
          set({
            error: error.response?.data?.message || 'Registration failed',
            isLoading: false,
          })
          throw error
        }
      },

      // UC-004: User Logout
      logout: async () => {
        try {
          set({ isLoading: true })

          await authClient.logout()

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
          const response = await authClient.refreshToken()

          set({
            accessToken: response.access_token,
          })
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
      name: STORAGE_KEYS.USER, // localStorage key
      partialize: (state) => ({
        user: state.user,
        accessToken: state.accessToken,
        isAuthenticated: state.isAuthenticated,
      }),
    }
  )
)

export default useAuthStore
