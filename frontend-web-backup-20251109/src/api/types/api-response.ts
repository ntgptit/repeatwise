/**
 * API Response Types
 *
 * Standard response structure from backend API
 */

/**
 * Generic API response wrapper
 */
export interface ApiResponse<T = unknown> {
  /**
   * Response data
   */
  data: T

  /**
   * Success flag
   */
  success: boolean

  /**
   * Response message
   */
  message?: string

  /**
   * Response timestamp
   */
  timestamp?: string
}

/**
 * Type guard for ApiResponse
 */
export const isApiResponse = <T>(value: unknown): value is ApiResponse<T> => {
  if (!value || typeof value !== 'object') {
    return false
  }

  const response = value as Record<string, unknown>

  return 'data' in response && typeof response['success'] === 'boolean'
}
