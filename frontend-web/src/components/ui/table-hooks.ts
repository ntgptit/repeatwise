/**
 * Hooks for Table Sorting and Pagination
 * 
 * External hooks for table functionality (no business logic)
 */

import * as React from 'react'

export type SortDirection = 'asc' | 'desc' | null

export interface SortConfig {
  key: string
  direction: SortDirection
}

export interface UseTableSortOptions {
  /** Initial sort configuration */
  initialSort?: SortConfig
  /** Callback when sort changes */
  onSortChange?: (sort: SortConfig) => void
}

export interface UseTableSortReturn {
  /** Current sort configuration */
  sort: SortConfig
  /** Set sort configuration */
  setSort: (key: string, direction?: SortDirection) => void
  /** Toggle sort for a column */
  toggleSort: (key: string) => void
  /** Clear sort */
  clearSort: () => void
}

/**
 * Hook for table sorting
 * 
 * @example
 * const { sort, toggleSort } = useTableSort({ initialSort: { key: 'name', direction: 'asc' } })
 * 
 * <TableHead onClick={() => toggleSort('name')}>
 *   Name {sort.key === 'name' && (sort.direction === 'asc' ? '↑' : '↓')}
 * </TableHead>
 */
export function useTableSort(
  options: UseTableSortOptions = {},
): UseTableSortReturn {
  const { initialSort, onSortChange } = options

  const [sort, setSortState] = React.useState<SortConfig>(
    initialSort || { key: '', direction: null },
  )

  const setSort = React.useCallback(
    (key: string, direction?: SortDirection) => {
      const newSort: SortConfig = {
        key,
        direction: direction ?? (sort.key === key && sort.direction === 'asc' ? 'desc' : 'asc'),
      }
      setSortState(newSort)
      onSortChange?.(newSort)
    },
    [sort.key, sort.direction, onSortChange],
  )

  const toggleSort = React.useCallback(
    (key: string) => {
      if (sort.key === key) {
        // Cycle: asc -> desc -> null
        if (sort.direction === 'asc') {
          setSort(key, 'desc')
        } else if (sort.direction === 'desc') {
          setSort(key, null)
        } else {
          setSort(key, 'asc')
        }
      } else {
        setSort(key, 'asc')
      }
    },
    [sort, setSort],
  )

  const clearSort = React.useCallback(() => {
    const newSort: SortConfig = { key: '', direction: null }
    setSortState(newSort)
    onSortChange?.(newSort)
  }, [onSortChange])

  return {
    sort,
    setSort,
    toggleSort,
    clearSort,
  }
}

export interface UseTablePaginationOptions {
  /** Initial page number (1-based) */
  initialPage?: number
  /** Initial page size */
  initialPageSize?: number
  /** Total number of items */
  totalItems?: number
  /** Callback when page changes */
  onPageChange?: (page: number) => void
  /** Callback when page size changes */
  onPageSizeChange?: (pageSize: number) => void
}

export interface UseTablePaginationReturn {
  /** Current page number (1-based) */
  currentPage: number
  /** Current page size */
  pageSize: number
  /** Total number of items */
  totalItems: number
  /** Total number of pages */
  totalPages: number
  /** Go to specific page */
  goToPage: (page: number) => void
  /** Go to next page */
  nextPage: () => void
  /** Go to previous page */
  prevPage: () => void
  /** Change page size */
  setPageSize: (size: number) => void
  /** Start index of current page (0-based) */
  startIndex: number
  /** End index of current page (0-based) */
  endIndex: number
  /** Is there a next page */
  hasNextPage: boolean
  /** Is there a previous page */
  hasPrevPage: boolean
}

/**
 * Hook for table pagination
 * 
 * @example
 * const { currentPage, pageSize, goToPage, paginatedData } = useTablePagination({
 *   initialPage: 1,
 *   initialPageSize: 10,
 *   totalItems: data.length
 * })
 * 
 * const paginatedData = data.slice(startIndex, endIndex + 1)
 */
export function useTablePagination(
  options: UseTablePaginationOptions = {},
): UseTablePaginationReturn {
  const {
    initialPage = 1,
    initialPageSize = 10,
    totalItems = 0,
    onPageChange,
    onPageSizeChange,
  } = options

  const [currentPage, setCurrentPage] = React.useState(initialPage)
  const [pageSize, setPageSizeState] = React.useState(initialPageSize)

  const totalPages = Math.max(1, Math.ceil(totalItems / pageSize))
  const startIndex = (currentPage - 1) * pageSize
  const endIndex = Math.min(startIndex + pageSize - 1, totalItems - 1)
  const hasNextPage = currentPage < totalPages
  const hasPrevPage = currentPage > 1

  const goToPage = React.useCallback(
    (page: number) => {
      const validPage = Math.max(1, Math.min(page, totalPages))
      setCurrentPage(validPage)
      onPageChange?.(validPage)
    },
    [totalPages, onPageChange],
  )

  const nextPage = React.useCallback(() => {
    if (hasNextPage) {
      goToPage(currentPage + 1)
    }
  }, [hasNextPage, currentPage, goToPage])

  const prevPage = React.useCallback(() => {
    if (hasPrevPage) {
      goToPage(currentPage - 1)
    }
  }, [hasPrevPage, goToPage])

  const setPageSize = React.useCallback(
    (size: number) => {
      setPageSizeState(size)
      onPageSizeChange?.(size)
      // Reset to first page when page size changes
      setCurrentPage(1)
    },
    [onPageSizeChange],
  )

  return {
    currentPage,
    pageSize,
    totalItems,
    totalPages,
    goToPage,
    nextPage,
    prevPage,
    setPageSize,
    startIndex,
    endIndex,
    hasNextPage,
    hasPrevPage,
  }
}

