/**
 * usePost Hook
 * 
 * POST request hook with automatic query invalidation
 * 
 * ✅ Tên rõ ràng: usePost - diễn đạt rõ tác dụng
 * ✅ Type-safe: Sử dụng TypeScript Generics cho request/response types
 * ✅ Clean API: Trả về object rõ ràng với tên gợi nghĩa
 */

import { useMutation, useQueryClient } from '@tanstack/react-query'
import type { AxiosRequestConfig } from 'axios'
import { http } from '@/api/http/axiosInstance'
import type { ApiRequestConfig } from '@/api/types/api-response'

export interface UsePostReturn<T, D> {
  mutate: (data: D) => void
  mutateAsync: (data: D) => Promise<T>
  isPending: boolean
  isError: boolean
  error: Error | null
  isSuccess: boolean
  data: T | undefined
  reset: () => void
}

/**
 * usePost - POST request hook
 * 
 * @template T Response data type (required, no default unknown)
 * @template D Request data type (required, no default unknown)
 * @param queryKey Query key to invalidate after mutation
 * @param url API endpoint URL
 * @param config Optional Axios and API request config
 * @param options Optional mutation options (callbacks for UI layer)
 * @returns Object with mutation methods and states
 */
export function usePost<T, D>(
  queryKey: string[],
  url: string,
  config?: AxiosRequestConfig & ApiRequestConfig,
  options?: {
    onSuccess?: (data: T) => void
    onError?: (error: Error) => void
  },
): UsePostReturn<T, D> {
  const queryClient = useQueryClient()

  const mutation = useMutation({
    mutationFn: async (data: D) => {
      return http.post<T, D>(url, data, config)
    },
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey })
      options?.onSuccess?.(data)
    },
    onError: options?.onError,
  })

  return {
    mutate: mutation.mutate,
    mutateAsync: mutation.mutateAsync,
    isPending: mutation.isPending,
    isError: mutation.isError,
    error: mutation.error,
    isSuccess: mutation.isSuccess,
    data: mutation.data,
    reset: mutation.reset,
  }
}
