/**
 * Sort Types
 *
 * Type definitions for sorting functionality.
 *
 * @module common/types/data-management/sort
 */

/**
 * Sort direction
 */
export type SortDirection = 'asc' | 'desc';

/**
 * Sort order (alternative naming)
 */
export type SortOrder = 'ascending' | 'descending';

/**
 * Sort field configuration
 */
export interface SortField<T = string> {
  /**
   * Field name to sort by
   */
  field: T;

  /**
   * Sort direction
   */
  direction: SortDirection;

  /**
   * Optional label for display
   */
  label?: string;

  /**
   * Whether this field is sortable
   */
  sortable?: boolean;
}

/**
 * Sort parameters for API requests
 */
export interface SortParams<T = string> {
  /**
   * Field to sort by
   */
  sortBy?: T;

  /**
   * Sort direction
   */
  sortDirection?: SortDirection;

  /**
   * Alternative: sort order string (e.g., "name,asc")
   */
  sort?: string;
}

/**
 * Multi-field sort parameters
 */
export interface MultiSortParams<T = string> {
  /**
   * Array of sort fields
   */
  sorts: Array<SortField<T>>;
}

/**
 * Sort state for UI components
 */
export interface SortState<T = string> {
  /**
   * Currently active sort field
   */
  sortBy: T | null;

  /**
   * Current sort direction
   */
  sortDirection: SortDirection;
}

/**
 * Sort actions/handlers
 */
export interface SortActions<T = string> {
  /**
   * Set sort field and direction
   */
  setSort: (field: T, direction: SortDirection) => void;

  /**
   * Toggle sort direction for a field
   * If field is different, sets to ascending
   * If same field, toggles direction
   */
  toggleSort: (field: T) => void;

  /**
   * Clear/reset sort
   */
  clearSort: () => void;

  /**
   * Check if a field is currently sorted
   */
  isSorted: (field: T) => boolean;

  /**
   * Get sort direction for a field
   */
  getSortDirection: (field: T) => SortDirection | null;
}

/**
 * Complete sort hook return type
 */
export interface UseSortReturn<T = string> extends SortState<T>, SortActions<T> {
  /**
   * Sort parameters ready for API request
   */
  sortParams: SortParams<T>;
}

/**
 * Sort configuration options
 */
export interface SortOptions<T = string> {
  /**
   * Initial sort field
   */
  initialSortBy?: T;

  /**
   * Initial sort direction (default: 'asc')
   */
  initialSortDirection?: SortDirection;

  /**
   * Available sortable fields
   */
  sortableFields?: T[];

  /**
   * Callback when sort changes
   */
  onSortChange?: (state: SortState<T>) => void;
}

/**
 * Sort option for select/dropdown
 */
export interface SortOption<T = string> {
  /**
   * Unique value/id
   */
  value: string;

  /**
   * Display label
   */
  label: string;

  /**
   * Field to sort by
   */
  field: T;

  /**
   * Sort direction
   */
  direction: SortDirection;
}

/**
 * Table column sort configuration
 */
export interface ColumnSortConfig<T = string> {
  /**
   * Whether column is sortable
   */
  sortable: boolean;

  /**
   * Field name for sorting
   */
  sortField?: T;

  /**
   * Custom sort comparator function
   */
  sortComparator?: (a: unknown, b: unknown, direction: SortDirection) => number;

  /**
   * Initial sort direction
   */
  defaultSortDirection?: SortDirection;
}
