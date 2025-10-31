/**
 * useFetch Hook
 * 
 * GET request hook using TanStack Query
 * 
 * ✅ Tên rõ ràng: useFetch - diễn đạt rõ tác dụng
 * ✅ Type-safe: Sử dụng TypeScript Generic cho data type
 * ✅ Clean API: Trả về object rõ ràng với tên gợi nghĩa
 */

import { useQuery, type UseQueryOptions } from '@tanstack/react-query'
import { useCallback } from 'react'
import type { AxiosRequestConfig } from 'axios'
import { http } from '@/api/http/axiosInstance'
import type { ApiRequestConfig } from '@/api/types/api-response'

export interface UseFetchReturn<T> {
  data: T | undefined
  loading: boolean // ✅ Chuẩn: loading (không phải isLoading)
  error: Error | null
  refetch: () => void
}

/**
 * useFetch - GET request hook
 * 
 * @template T Response data type (required, no default unknown)
 * @param queryKey Unique query key for caching
 * @param url API endpoint URL
 * @param config Optional Axios and API request config
 * @param options Optional React Query options
 * @returns Object with data, loading, error states
 */
export function useFetch<T>(
  queryKey: string[],
  url: string,
  config?: AxiosRequestConfig & ApiRequestConfig,
  options?: Omit<
    UseQueryOptions<T, Error, T, string[]>,
    'queryKey' | 'queryFn'
  >,
): UseFetchReturn<T> {
  const query = useQuery({
    queryKey,
    queryFn: async () => {
      return http.get<T>(url, config)
    },
    ...options,
  })

  // ✅ Performance: Memoize refetch to prevent unnecessary re-renders
  const refetch = useCallback(() => {
    query.refetch()
  }, [query])

  return {
    data: query.data,
    loading: query.isLoading, // ✅ Chuẩn: loading
    error: query.error,
    refetch,
  }
}
