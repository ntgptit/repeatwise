/**
 * useAuth Hook
 * 
 * Authentication domain hook
 * 
 * ✅ Tách biệt UI: Không có navigation, toast, alert
 * ✅ Type-safe: Sử dụng TypeScript Generics
 * ✅ Clean API: Trả về object rõ ràng với tên gợi nghĩa
 */

import { useMutation, useQueryClient, useQuery } from '@tanstack/react-query'
import { useCallback, useMemo } from 'react'
import type { UseMutationResult } from '@tanstack/react-query'
import {
  authApi,
  type LoginRequest,
  type RegisterRequest,
  type LoginResponse,
  type RegisterResponse,
  type UserProfile,
} from '@/api/modules/auth.api'
import { APP_CONFIG } from '@/constants/config'

const AUTH_QUERY_KEY = ['auth']
const PROFILE_QUERY_KEY = ['profile']

export interface UseAuthReturn {
  // ✅ Chuẩn: user, isAuthenticated, login, logout
  user: LoginResponse['user'] | null
  isAuthenticated: boolean
  
  // Login
  login: (
    data: LoginRequest,
    options?: {
      onSuccess?: (data: LoginResponse) => void
      onError?: (error: Error) => void
    },
  ) => void
  loginAsync: (data: LoginRequest) => Promise<LoginResponse>
  isLoggingIn: boolean
  loginError: Error | null
  
  // Register
  register: (
    data: RegisterRequest,
    options?: {
      onSuccess?: (data: RegisterResponse) => void
      onError?: (error: Error) => void
    },
  ) => void
  registerAsync: (data: RegisterRequest) => Promise<RegisterResponse>
  isRegistering: boolean
  registerError: Error | null
  
  // Logout
  logout: (options?: {
    onSuccess?: () => void
    onError?: (error: Error) => void
  }) => void
  logoutAsync: () => Promise<void>
  isLoggingOut: boolean
  logoutError: Error | null
  
  // Loading state
  isLoading: boolean
  
  // Raw mutations (for advanced usage)
  loginMutation: UseMutationResult<LoginResponse, Error, LoginRequest>
  registerMutation: UseMutationResult<RegisterResponse, Error, RegisterRequest>
  logoutMutation: UseMutationResult<void, Error, void>
}

/**
 * useAuth - Authentication hook
 * 
 * Provides authentication state and methods
 * 
 * @returns Authentication state and methods
 */
export function useAuth(): UseAuthReturn {
  const queryClient = useQueryClient()

  // Get user profile query
  const profileQuery = useQuery({
    queryKey: PROFILE_QUERY_KEY,
    queryFn: () => authApi.getProfile(),
    enabled: Boolean(localStorage.getItem(APP_CONFIG.STORAGE_KEYS.ACCESS_TOKEN)),
  })

  // Login mutation (no onSuccess - handled by wrapper)
  const loginMutation = useMutation({
    mutationFn: (data: LoginRequest) => authApi.login(data),
  })

  // ✅ Performance: Memoize login handler to prevent unnecessary re-renders
  const login = useCallback(
    (
      data: LoginRequest,
      options?: {
        onSuccess?: (data: LoginResponse) => void
        onError?: (error: Error) => void
      },
    ) => {
      loginMutation.mutate(data, {
        onSuccess: (response) => {
          // ✅ Side-effect isolation: Only modify localStorage and query cache
          // Access token stored in memory/localStorage
          localStorage.setItem(APP_CONFIG.STORAGE_KEYS.ACCESS_TOKEN, response.access_token)
          // Refresh token is stored in HTTP-only cookie by backend, no need to store here
          
          // Set user data and invalidate queries
          queryClient.setQueryData(['auth', 'profile'], response.user)
          queryClient.invalidateQueries({ queryKey: AUTH_QUERY_KEY })
          queryClient.invalidateQueries({ queryKey: PROFILE_QUERY_KEY })
          
          // Call UI layer callback
          options?.onSuccess?.(response)
        },
        onError: options?.onError,
      })
    },
    [loginMutation, queryClient],
  )

  // Register mutation
  const registerMutation = useMutation({
    mutationFn: (data: RegisterRequest) => authApi.register(data),
  })

  // ✅ Performance: Memoize register handler
  const register = useCallback(
    (
      data: RegisterRequest,
      options?: {
        onSuccess?: (data: RegisterResponse) => void
        onError?: (error: Error) => void
      },
    ) => {
      registerMutation.mutate(data, {
        onSuccess: options?.onSuccess,
        onError: options?.onError,
      })
    },
    [registerMutation],
  )

  // Logout mutation (no onSuccess - handled by wrapper)
  const logoutMutation = useMutation({
    mutationFn: () => authApi.logout(),
  })

  // ✅ Performance: Memoize logout handler
  const logout = useCallback(
    (options?: {
      onSuccess?: () => void
      onError?: (error: Error) => void
    }) => {
      logoutMutation.mutate(undefined, {
        onSuccess: () => {
          // ✅ Side-effect isolation: Only modify localStorage and query cache
          // Clear access token (refresh token cleared by backend via cookie)
          localStorage.removeItem(APP_CONFIG.STORAGE_KEYS.ACCESS_TOKEN)
          
          // Clear all queries
          queryClient.clear()
          
          // Call UI layer callback
          options?.onSuccess?.()
        },
        onError: options?.onError,
      })
    },
    [logoutMutation, queryClient],
  )

  // ✅ Performance: Memoize user transformation to prevent recalculation
  const user = useMemo(() => {
    return profileQuery.data
      ? {
          id: profileQuery.data.id,
          email: profileQuery.data.email,
          username: profileQuery.data.username,
          name: profileQuery.data.name,
          language: profileQuery.data.language,
          theme: profileQuery.data.theme,
          timezone: profileQuery.data.timezone,
        }
      : null
  }, [profileQuery.data])

  // ✅ Performance: Memoize authentication check
  // Note: localStorage.getItem is synchronous, so we check it once per render
  // but only recalculate when user changes
  const isAuthenticated = useMemo(() => {
    return Boolean(
      localStorage.getItem(APP_CONFIG.STORAGE_KEYS.ACCESS_TOKEN) && user,
    )
  }, [user])

  return {
    // ✅ Chuẩn: user, isAuthenticated, login, logout
    user,
    isAuthenticated,
    isLoading: loginMutation.isPending || registerMutation.isPending || profileQuery.isLoading,
    
    // Login
    login,
    loginAsync: loginMutation.mutateAsync,
    isLoggingIn: loginMutation.isPending,
    loginError: loginMutation.error,
    
    // Register
    register,
    registerAsync: registerMutation.mutateAsync,
    isRegistering: registerMutation.isPending,
    registerError: registerMutation.error,
    
    // Logout
    logout,
    logoutAsync: logoutMutation.mutateAsync,
    isLoggingOut: logoutMutation.isPending,
    logoutError: logoutMutation.error,
    
    // Raw mutations (for advanced usage)
    loginMutation,
    registerMutation,
    logoutMutation,
  }
}
