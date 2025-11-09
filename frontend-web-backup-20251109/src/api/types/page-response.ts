/**
 * Paginated Response Types
 *
 * Standard paginated response structure from backend API
 */

import { type PaginationMeta } from '@/common/types/data-management'
import { type ApiResponse } from './api-response'

/**
 * Paginated response wrapper
 */
export interface PageResponse<T> extends ApiResponse<T[]> {
  /**
   * Pagination metadata
   */
  pagination: PaginationMeta
}

/**
 * Type guard for PageResponse
 */
export const isPageResponse = <T>(value: unknown): value is PageResponse<T> => {
  if (!value || typeof value !== 'object') {
    return false
  }

  const response = value as Record<string, unknown>

  return (
    Array.isArray(response['data']) &&
    typeof response['pagination'] === 'object' &&
    response['pagination'] !== null
  )
}
