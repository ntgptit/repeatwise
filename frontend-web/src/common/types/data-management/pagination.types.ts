/**
 * Pagination Types
 *
 * Type definitions for pagination functionality
 * Used across all features for consistent pagination behavior
 */

/**
 * Pagination parameters for API requests
 */
export interface PaginationParams {
  /**
   * Page number (1-based)
   */
  page: number

  /**
   * Number of items per page
   */
  pageSize: number
}

/**
 * Pagination metadata from API responses
 */
export interface PaginationMeta {
  /**
   * Current page number (1-based)
   */
  currentPage: number

  /**
   * Number of items per page
   */
  pageSize: number

  /**
   * Total number of items across all pages
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

  /**
   * Index of the first item on current page (0-based)
   */
  startIndex: number

  /**
   * Index of the last item on current page (0-based)
   */
  endIndex: number
}

/**
 * Pagination state for UI components
 */
export interface PaginationState extends PaginationParams {
  /**
   * Total number of items (from server response)
   */
  totalItems: number

  /**
   * Total number of pages (computed)
   */
  totalPages: number
}

/**
 * Pagination actions/handlers
 */
export interface PaginationActions {
  /**
   * Go to specific page
   */
  onPageChange: (page: number) => void

  /**
   * Change page size
   */
  onPageSizeChange: (pageSize: number) => void

  /**
   * Go to next page
   */
  onNextPage: () => void

  /**
   * Go to previous page
   */
  onPreviousPage: () => void

  /**
   * Go to first page
   */
  onFirstPage: () => void

  /**
   * Go to last page
   */
  onLastPage: () => void

  /**
   * Reset pagination to initial state
   */
  onReset: () => void
}

/**
 * Combined pagination state and actions
 */
export interface PaginationControl extends PaginationState, PaginationActions {}

/**
 * Pagination configuration/options
 */
export interface PaginationConfig {
  /**
   * Default page number
   * @default 1
   */
  defaultPage?: number

  /**
   * Default page size
   * @default 10
   */
  defaultPageSize?: number

  /**
   * Available page size options
   * @default [10, 20, 50, 100]
   */
  pageSizeOptions?: number[]

  /**
   * Show page size selector
   * @default true
   */
  showPageSizeSelector?: boolean

  /**
   * Show pagination info (e.g., "Showing 1-10 of 100")
   * @default true
   */
  showPaginationInfo?: boolean

  /**
   * Show first/last page buttons
   * @default true
   */
  showFirstLastButtons?: boolean

  /**
   * Show previous/next page buttons
   * @default true
   */
  showPrevNextButtons?: boolean

  /**
   * Maximum number of page buttons to show
   * @default 5
   */
  maxPageButtons?: number

  /**
   * Enable keyboard navigation
   * @default false
   */
  enableKeyboardNavigation?: boolean
}

/**
 * Pagination response wrapper
 */
export interface PaginatedResponse<T> {
  /**
   * Array of items for current page
   */
  items: T[]

  /**
   * Pagination metadata
   */
  pagination: PaginationMeta
}

/**
 * Pagination info display props
 */
export interface PaginationInfoProps {
  /**
   * Current page number
   */
  currentPage: number

  /**
   * Page size
   */
  pageSize: number

  /**
   * Total items
   */
  totalItems: number

  /**
   * Custom template for info text
   * @default "Showing {start}-{end} of {total}"
   */
  template?: string
}

/**
 * Page size selector props
 */
export interface PageSizeSelectorProps {
  /**
   * Current page size
   */
  pageSize: number

  /**
   * Available page size options
   */
  options: number[]

  /**
   * Change handler
   */
  onChange: (pageSize: number) => void

  /**
   * Disabled state
   */
  disabled?: boolean
}

/**
 * Type guards
 */

/**
 * Check if pagination meta is valid
 */
export const isPaginationMeta = (value: unknown): value is PaginationMeta => {
  if (!value || typeof value !== 'object') {
    return false
  }

  const meta = value as Record<string, unknown>

  return (
    typeof meta['currentPage'] === 'number' &&
    typeof meta['pageSize'] === 'number' &&
    typeof meta['totalItems'] === 'number' &&
    typeof meta['totalPages'] === 'number' &&
    typeof meta['hasNext'] === 'boolean' &&
    typeof meta['hasPrevious'] === 'boolean'
  )
}

/**
 * Check if paginated response is valid
 */
export const isPaginatedResponse = <T>(value: unknown): value is PaginatedResponse<T> => {
  if (!value || typeof value !== 'object') {
    return false
  }

  const response = value as Record<string, unknown>

  return Array.isArray(response['items']) && isPaginationMeta(response['pagination'])
}

/**
 * Utility functions
 */

/**
 * Calculate pagination metadata from params
 */
export const calculatePaginationMeta = (
  page: number,
  pageSize: number,
  totalItems: number
): PaginationMeta => {
  const totalPages = Math.ceil(totalItems / pageSize)
  const startIndex = (page - 1) * pageSize
  const endIndex = Math.min(startIndex + pageSize - 1, totalItems - 1)

  return {
    currentPage: page,
    pageSize,
    totalItems,
    totalPages,
    hasNext: page < totalPages,
    hasPrevious: page > 1,
    startIndex,
    endIndex,
  }
}

/**
 * Get default pagination config
 */
export const getDefaultPaginationConfig = (): Required<PaginationConfig> => ({
  defaultPage: 1,
  defaultPageSize: 10,
  pageSizeOptions: [10, 20, 50, 100],
  showPageSizeSelector: true,
  showPaginationInfo: true,
  showFirstLastButtons: true,
  showPrevNextButtons: true,
  maxPageButtons: 5,
  enableKeyboardNavigation: false,
})

/**
 * Create initial pagination state
 */
export const createInitialPaginationState = (
  config?: Partial<PaginationConfig>
): PaginationState => {
  const defaults = getDefaultPaginationConfig()
  return {
    page: config?.defaultPage ?? defaults.defaultPage,
    pageSize: config?.defaultPageSize ?? defaults.defaultPageSize,
    totalItems: 0,
    totalPages: 0,
  }
}
