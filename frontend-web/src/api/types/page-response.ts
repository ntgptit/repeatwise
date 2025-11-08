/**
 * Paginated Response Types
 *
 * Standard paginated response format from API.
 *
 * @module api/types/page-response
 */

import type { PaginationMeta } from '@/common/types/data-management';

/**
 * Paginated API response
 */
export interface PageResponse<T> {
  /**
   * Array of items for current page
   */
  content: T[];

  /**
   * Pagination metadata
   */
  pagination: PaginationMeta;
}

/**
 * Alternative paginated response format (Spring Boot style)
 */
export interface SpringPageResponse<T> {
  /**
   * Array of items
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
   * Sort information
   */
  sort?: {
    sorted: boolean;
    unsorted: boolean;
    empty: boolean;
  };

  /**
   * Whether this is the first page
   */
  first: boolean;

  /**
   * Whether this is the last page
   */
  last: boolean;

  /**
   * Number of elements in current page
   */
  numberOfElements: number;

  /**
   * Whether the page is empty
   */
  empty: boolean;
}

/**
 * Helper to convert Spring page response to standard format
 */
export function convertSpringPageResponse<T>(
  springPage: SpringPageResponse<T>
): PageResponse<T> {
  return {
    content: springPage.content,
    pagination: {
      currentPage: springPage.number + 1, // Convert to 1-based
      pageSize: springPage.size,
      totalItems: springPage.totalElements,
      totalPages: springPage.totalPages,
      hasNextPage: !springPage.last,
      hasPreviousPage: !springPage.first,
      firstItemIndex: springPage.number * springPage.size + 1,
      lastItemIndex: springPage.number * springPage.size + springPage.numberOfElements,
    },
  };
}
