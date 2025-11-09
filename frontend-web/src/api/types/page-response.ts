/**
 * Paginated Response Types
 *
 * Standard paginated response structure from backend API
 */

import { type ApiResponse } from './api-response'

/**
 * Pagination metadata
 */
export interface PaginationMeta {
  /**
   * Current page number (1-indexed)
   */
  currentPage: number

  /**
   * Page size (items per page)
   */
  pageSize: number

  /**
   * Total number of items
   */
  totalItems: number

  /**
   * Total number of pages
   */
  totalPages: number

  /**
   * Whether there is a next page
   */
  hasNext: boolean

  /**
   * Whether there is a previous page
   */
  hasPrevious: boolean
}

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
