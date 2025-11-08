/**
 * Pagination Types
 *
 * Type definitions for pagination functionality.
 *
 * @module common/types/data-management/pagination
 */

/**
 * Pagination parameters for API requests
 */
export interface PaginationParams {
  /**
   * Current page number (0-indexed or 1-indexed based on API)
   */
  page: number;

  /**
   * Number of items per page
   */
  pageSize: number;

  /**
   * Total number of items (optional, usually returned from API)
   */
  total?: number;
}

/**
 * Pagination metadata from API response
 */
export interface PaginationMeta {
  /**
   * Current page number
   */
  currentPage: number;

  /**
   * Page size (items per page)
   */
  pageSize: number;

  /**
   * Total number of items across all pages
   */
  totalItems: number;

  /**
   * Total number of pages
   */
  totalPages: number;

  /**
   * Whether there is a next page
   */
  hasNextPage: boolean;

  /**
   * Whether there is a previous page
   */
  hasPreviousPage: boolean;

  /**
   * First item index on current page (1-based)
   */
  firstItemIndex: number;

  /**
   * Last item index on current page (1-based)
   */
  lastItemIndex: number;
}

/**
 * Pagination state for UI components
 */
export interface PaginationState {
  /**
   * Current page number
   */
  page: number;

  /**
   * Page size
   */
  pageSize: number;

  /**
   * Total number of items
   */
  total: number;
}

/**
 * Pagination actions/handlers
 */
export interface PaginationActions {
  /**
   * Go to specific page
   */
  goToPage: (page: number) => void;

  /**
   * Go to next page
   */
  goToNextPage: () => void;

  /**
   * Go to previous page
   */
  goToPreviousPage: () => void;

  /**
   * Go to first page
   */
  goToFirstPage: () => void;

  /**
   * Go to last page
   */
  goToLastPage: () => void;

  /**
   * Change page size
   */
  changePageSize: (pageSize: number) => void;

  /**
   * Reset pagination to initial state
   */
  resetPagination: () => void;
}

/**
 * Complete pagination hook return type
 */
export interface UsePaginationReturn extends PaginationState, PaginationActions {
  /**
   * Computed pagination metadata
   */
  meta: PaginationMeta;
}

/**
 * Pagination configuration options
 */
export interface PaginationOptions {
  /**
   * Initial page number (default: 1)
   */
  initialPage?: number;

  /**
   * Initial page size (default: 20)
   */
  initialPageSize?: number;

  /**
   * Available page size options
   */
  pageSizeOptions?: readonly number[];

  /**
   * Whether to use 0-based indexing (default: false - 1-based)
   */
  zeroBased?: boolean;

  /**
   * Callback when pagination changes
   */
  onPaginationChange?: (state: PaginationState) => void;
}

/**
 * Paginated response from API
 */
export interface PaginatedResponse<T> {
  /**
   * Array of items for current page
   */
  data: T[];

  /**
   * Pagination metadata
   */
  pagination: PaginationMeta;
}

/**
 * Alternative paginated response format (for compatibility)
 */
export interface PageResponse<T> {
  /**
   * Array of items for current page
   */
  content: T[];

  /**
   * Current page number (0-based)
   */
  number: number;

  /**
   * Page size
   */
  size: number;

  /**
   * Total number of elements
   */
  totalElements: number;

  /**
   * Total number of pages
   */
  totalPages: number;

  /**
   * Whether this is the first page
   */
  first: boolean;

  /**
   * Whether this is the last page
   */
  last: boolean;

  /**
   * Whether the page is empty
   */
  empty: boolean;
}
