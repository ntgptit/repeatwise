/**
 * useProfile Hook
 * 
 * User profile domain hook
 * 
 * ✅ Tách biệt UI: Không có toast, alert
 * ✅ Type-safe: Sử dụng TypeScript Generics
 * ✅ Clean API: Trả về object rõ ràng với tên gợi nghĩa
 */

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useCallback } from 'react'
import type { UseMutationResult } from '@tanstack/react-query'
import {
  userApi,
  type User,
  type UpdateUserRequest,
  type ChangePasswordRequest,
} from '@/api/modules/user.api'
import { APP_CONFIG } from '@/constants/config'

const PROFILE_QUERY_KEY = ['profile']

export interface UseProfileReturn {
  // Profile data
  profile: User | undefined
  isLoading: boolean
  isError: boolean
  error: Error | null
  
  // Refetch profile
  refetch: () => void
  
  // Update profile
  updateProfile: (data: UpdateUserRequest) => void
  updateProfileAsync: (data: UpdateUserRequest) => Promise<User>
  isUpdating: boolean
  updateError: Error | null
  
  // Change password
  changePassword: (data: ChangePasswordRequest) => void
  changePasswordAsync: (data: ChangePasswordRequest) => Promise<void>
  isChangingPassword: boolean
  changePasswordError: Error | null
  
  // Raw mutations (for advanced usage)
  updateProfileMutation: UseMutationResult<User, Error, UpdateUserRequest>
  changePasswordMutation: UseMutationResult<void, Error, ChangePasswordRequest>
}

/**
 * useProfile - User profile hook
 * 
 * Provides profile data and update methods
 * 
 * @returns Profile state and methods
 */
export function useProfile(): UseProfileReturn {
  const queryClient = useQueryClient()

  // Get profile query
  const profileQuery = useQuery({
    queryKey: PROFILE_QUERY_KEY,
    queryFn: () => userApi.getProfile(),
    enabled: Boolean(
      localStorage.getItem(APP_CONFIG.STORAGE_KEYS.ACCESS_TOKEN),
    ),
  })

  // Update profile mutation
  const updateProfileMutation = useMutation({
    mutationFn: (data: UpdateUserRequest) => userApi.updateProfile(data),
    onSuccess: (data) => {
      queryClient.setQueryData<User>(PROFILE_QUERY_KEY, data)
      queryClient.invalidateQueries({ queryKey: PROFILE_QUERY_KEY })
      
      // Note: Success toast should be handled by UI layer via onSuccess callback
    },
  })

  // Change password mutation
  const changePasswordMutation = useMutation({
    mutationFn: (data: ChangePasswordRequest) => userApi.changePassword(data),
    // Note: Success toast should be handled by UI layer via onSuccess callback
  })

  // ✅ Performance: Memoize refetch to prevent unnecessary re-renders
  const refetch = useCallback(() => {
    profileQuery.refetch()
  }, [profileQuery])

  return {
    // Profile data
    profile: profileQuery.data,
    isLoading: profileQuery.isLoading,
    isError: profileQuery.isError,
    error: profileQuery.error,
    
    // Refetch profile
    refetch,
    
    // Update profile
    updateProfile: updateProfileMutation.mutate,
    updateProfileAsync: updateProfileMutation.mutateAsync,
    isUpdating: updateProfileMutation.isPending,
    updateError: updateProfileMutation.error,
    
    // Change password
    changePassword: changePasswordMutation.mutate,
    changePasswordAsync: changePasswordMutation.mutateAsync,
    isChangingPassword: changePasswordMutation.isPending,
    changePasswordError: changePasswordMutation.error,
    
    // Raw mutations (for advanced usage)
    updateProfileMutation,
    changePasswordMutation,
  }
}
