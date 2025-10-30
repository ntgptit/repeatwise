import type { AxiosResponse } from 'axios'
import type { ApiResponse } from '@/api/types/api-response'

/**
 * Response Interceptor
 * Handles successful responses
 */
export const responseInterceptor = {
  onFulfilled: <T>(response: AxiosResponse<ApiResponse<T>>) => {
    // Extract data from response if needed
    if (response.data?.data !== undefined) {
      return {
        ...response,
        data: response.data,
      }
    }
    return response
  },

  onRejected: (error: unknown) => {
    return Promise.reject(error)
  },
}