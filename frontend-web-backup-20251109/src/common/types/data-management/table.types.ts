/**
 * Table Types
 *
 * Type definitions for data table functionality
 * Combines pagination, filtering, sorting, and search
 */

import type { PaginationActions, PaginationState } from './pagination.types'
import type { FilterActions, FilterState } from './filter.types'
import type { SortActions, SortState } from './sort.types'
import type { SearchState, SearchActions } from './search.types'

/**
 * Table column definition
 */
export interface TableColumn<T = unknown> {
  /**
   * Unique column identifier
   */
  id: string

  /**
   * Column header label
   */
  label: string

  /**
   * Data field accessor (key in row data)
   */
  field?: keyof T

  /**
   * Custom accessor function
   */
  accessor?: (row: T) => unknown

  /**
   * Custom cell renderer
   */
  render?: (value: unknown, row: T, index: number) => React.ReactNode

  /**
   * Column width
   */
  width?: string | number

  /**
   * Minimum width
   */
  minWidth?: string | number

  /**
   * Maximum width
   */
  maxWidth?: string | number

  /**
   * Whether column is sortable
   * @default true
   */
  sortable?: boolean

  /**
   * Whether column is filterable
   * @default true
   */
  filterable?: boolean

  /**
   * Whether column is visible
   * @default true
   */
  visible?: boolean

  /**
   * Whether column is pinned (left/right)
   */
  pinned?: 'left' | 'right'

  /**
   * Column alignment
   * @default 'left'
   */
  align?: 'left' | 'center' | 'right'

  /**
   * Header alignment (overrides align for header)
   */
  headerAlign?: 'left' | 'center' | 'right'

  /**
   * CSS class for column
   */
  className?: string

  /**
   * CSS class for header
   */
  headerClassName?: string

  /**
   * Custom header renderer
   */
  renderHeader?: () => React.ReactNode
}

/**
 * Table row selection
 */
export interface TableSelection<T = unknown> {
  /**
   * Selected row IDs
   */
  selectedIds: Set<string>

  /**
   * All rows selected
   */
  allSelected: boolean

  /**
   * Some rows selected (indeterminate)
   */
  someSelected: boolean

  /**
   * Get row ID
   */
  getRowId: (row: T) => string

  /**
   * Toggle row selection
   */
  toggleRow: (rowId: string) => void

  /**
   * Toggle all rows
   */
  toggleAll: () => void

  /**
   * Clear selection
   */
  clearSelection: () => void

  /**
   * Check if row is selected
   */
  isRowSelected: (rowId: string) => boolean
}

/**
 * Table state (combined)
 */
export interface TableState<T = unknown> {
  /**
   * Table data
   */
  data: T[]

  /**
   * Pagination state
   */
  pagination: PaginationState

  /**
   * Filter state
   */
  filter: FilterState

  /**
   * Sort state
   */
  sort: SortState

  /**
   * Search state
   */
  search: SearchState

  /**
   * Selection state
   */
  selection?: TableSelection<T>

  /**
   * Loading state
   */
  isLoading: boolean

  /**
   * Error state
   */
  error?: string | null
}

/**
 * Table actions (combined)
 */
export interface TableActions extends PaginationActions, FilterActions, SortActions, SearchActions {
  /**
   * Refresh/reload table data
   */
  onRefresh: () => void

  /**
   * Reset all filters, sorts, search
   */
  onResetAll: () => void
}

/**
 * Combined table control
 */
export interface TableControl<T = unknown> extends TableState<T>, TableActions {}

/**
 * Table configuration
 */
export interface TableConfig<T = unknown> {
  /**
   * Table columns
   */
  columns: TableColumn<T>[]

  /**
   * Row ID accessor
   */
  getRowId?: (row: T) => string

  /**
   * Enable row selection
   * @default false
   */
  enableSelection?: boolean

  /**
   * Selection mode
   * @default 'multiple'
   */
  selectionMode?: 'single' | 'multiple'

  /**
   * Enable pagination
   * @default true
   */
  enablePagination?: boolean

  /**
   * Enable sorting
   * @default true
   */
  enableSorting?: boolean

  /**
   * Enable filtering
   * @default true
   */
  enableFiltering?: boolean

  /**
   * Enable search
   * @default true
   */
  enableSearch?: boolean

  /**
   * Enable column resizing
   * @default false
   */
  enableColumnResizing?: boolean

  /**
   * Enable column reordering
   * @default false
   */
  enableColumnReordering?: boolean

  /**
   * Enable column visibility toggle
   * @default false
   */
  enableColumnVisibility?: boolean

  /**
   * Sticky header
   * @default false
   */
  stickyHeader?: boolean

  /**
   * Sticky first column
   * @default false
   */
  stickyFirstColumn?: boolean

  /**
   * Table density
   * @default 'normal'
   */
  density?: 'compact' | 'normal' | 'comfortable'

  /**
   * Show row hover
   * @default true
   */
  showRowHover?: boolean

  /**
   * Show row borders
   * @default true
   */
  showRowBorders?: boolean

  /**
   * Striped rows
   * @default false
   */
  stripedRows?: boolean

  /**
   * Empty state message
   */
  emptyMessage?: string

  /**
   * Loading message
   */
  loadingMessage?: string

  /**
   * Row click handler
   */
  onRowClick?: (row: T, index: number) => void

  /**
   * Row double click handler
   */
  onRowDoubleClick?: (row: T, index: number) => void
}

/**
 * Table props (for component)
 */
export interface TableProps<T = unknown> extends TableConfig<T> {
  /**
   * Table data
   */
  data: T[]

  /**
   * Loading state
   */
  isLoading?: boolean

  /**
   * Error message
   */
  error?: string | null

  /**
   * CSS class
   */
  className?: string
}

/**
 * Type guards
 */

/**
 * Check if value is a valid table column
 */
export const isTableColumn = <T>(value: unknown): value is TableColumn<T> => {
  if (!value || typeof value !== 'object') {
    return false
  }

  const column = value as Record<string, unknown>

  return typeof column['id'] === 'string' && typeof column['label'] === 'string'
}

/**
 * Utility functions
 */

/**
 * Create initial table state
 */
export const createInitialTableState = <T>(_config?: Partial<TableConfig<T>>): TableState<T> => ({
  data: [],
  pagination: {
    page: 1,
    pageSize: 10,
    totalItems: 0,
    totalPages: 0,
  },
  filter: {
    conditions: [],
    logic: 'AND',
  },
  sort: {
    conditions: [],
  },
  search: {
    query: '',
    isActive: false,
    isLoading: false,
  },
  isLoading: false,
  error: null,
})

/**
 * Get visible columns
 */
export const getVisibleColumns = <T>(columns: TableColumn<T>[]): TableColumn<T>[] => {
  return columns.filter(col => col.visible !== false)
}

/**
 * Get column by ID
 */
export const getColumnById = <T>(
  columns: TableColumn<T>[],
  id: string
): TableColumn<T> | undefined => {
  return columns.find(col => col.id === id)
}

/**
 * Get cell value
 */
export const getCellValue = <T>(column: TableColumn<T>, row: T): unknown => {
  if (column.accessor) {
    return column.accessor(row)
  }

  if (column.field) {
    return row[column.field]
  }

  return undefined
}

export default {
  createInitialTableState,
  getVisibleColumns,
  getColumnById,
  getCellValue,
}
