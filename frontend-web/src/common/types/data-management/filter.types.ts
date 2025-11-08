/**
 * Filter Types
 *
 * Type definitions for filtering functionality.
 *
 * @module common/types/data-management/filter
 */

/**
 * Filter operator types
 */
export type FilterOperator =
  | 'equals'
  | 'notEquals'
  | 'contains'
  | 'notContains'
  | 'startsWith'
  | 'endsWith'
  | 'greaterThan'
  | 'greaterThanOrEqual'
  | 'lessThan'
  | 'lessThanOrEqual'
  | 'in'
  | 'notIn'
  | 'between'
  | 'isNull'
  | 'isNotNull'
  | 'isEmpty'
  | 'isNotEmpty';

/**
 * Filter value type
 */
export type FilterValue = string | number | boolean | Date | null | undefined | FilterValue[];

/**
 * Single filter condition
 */
export interface FilterCondition<T = string> {
  /**
   * Field name to filter
   */
  field: T;

  /**
   * Filter operator
   */
  operator: FilterOperator;

  /**
   * Filter value(s)
   */
  value: FilterValue;

  /**
   * Optional label for display
   */
  label?: string;
}

/**
 * Filter group with logical operator
 */
export interface FilterGroup<T = string> {
  /**
   * Logical operator (AND/OR)
   */
  logic: 'AND' | 'OR';

  /**
   * Array of filter conditions
   */
  conditions: Array<FilterCondition<T>>;
}

/**
 * Filter parameters for API requests
 */
export interface FilterParams<T = string> {
  /**
   * Map of field names to filter values
   */
  filters?: Record<string, FilterValue>;

  /**
   * Alternative: array of filter conditions
   */
  conditions?: Array<FilterCondition<T>>;

  /**
   * Alternative: filter groups with logic
   */
  filterGroups?: Array<FilterGroup<T>>;
}

/**
 * Filter state for UI components
 */
export interface FilterState<T = string> {
  /**
   * Active filters map
   */
  activeFilters: Map<T, FilterValue>;

  /**
   * Filter conditions (for advanced filtering)
   */
  conditions?: Array<FilterCondition<T>>;
}

/**
 * Filter actions/handlers
 */
export interface FilterActions<T = string> {
  /**
   * Set a filter value
   */
  setFilter: (field: T, value: FilterValue) => void;

  /**
   * Remove a specific filter
   */
  removeFilter: (field: T) => void;

  /**
   * Clear all filters
   */
  clearFilters: () => void;

  /**
   * Set multiple filters at once
   */
  setFilters: (filters: Record<string, FilterValue>) => void;

  /**
   * Check if a field is filtered
   */
  isFiltered: (field: T) => boolean;

  /**
   * Get filter value for a field
   */
  getFilterValue: (field: T) => FilterValue;

  /**
   * Get count of active filters
   */
  getActiveFilterCount: () => number;
}

/**
 * Complete filter hook return type
 */
export interface UseFilterReturn<T = string> extends FilterState<T>, FilterActions<T> {
  /**
   * Filter parameters ready for API request
   */
  filterParams: FilterParams<T>;

  /**
   * Whether any filters are active
   */
  hasActiveFilters: boolean;
}

/**
 * Filter configuration options
 */
export interface FilterOptions<T = string> {
  /**
   * Initial filters
   */
  initialFilters?: Record<string, FilterValue>;

  /**
   * Available filterable fields
   */
  filterableFields?: T[];

  /**
   * Callback when filters change
   */
  onFilterChange?: (state: FilterState<T>) => void;

  /**
   * Debounce delay for filter changes (ms)
   */
  debounceDelay?: number;
}

/**
 * Filter field configuration
 */
export interface FilterFieldConfig<T = string> {
  /**
   * Field name
   */
  field: T;

  /**
   * Display label
   */
  label: string;

  /**
   * Input type
   */
  type: 'text' | 'number' | 'date' | 'select' | 'multiselect' | 'boolean' | 'daterange';

  /**
   * Available operators for this field
   */
  operators?: FilterOperator[];

  /**
   * Default operator
   */
  defaultOperator?: FilterOperator;

  /**
   * Options for select/multiselect
   */
  options?: Array<{
    value: FilterValue;
    label: string;
  }>;

  /**
   * Placeholder text
   */
  placeholder?: string;

  /**
   * Whether field is required
   */
  required?: boolean;

  /**
   * Custom validation function
   */
  validate?: (value: FilterValue) => boolean | string;
}

/**
 * Filter preset for quick filtering
 */
export interface FilterPreset<T = string> {
  /**
   * Preset ID
   */
  id: string;

  /**
   * Preset name/label
   */
  label: string;

  /**
   * Preset description
   */
  description?: string;

  /**
   * Predefined filters
   */
  filters: Record<string, FilterValue>;

  /**
   * Icon for preset (optional)
   */
  icon?: string;
}

/**
 * Date range filter value
 */
export interface DateRangeFilter {
  /**
   * Start date
   */
  start: Date | null;

  /**
   * End date
   */
  end: Date | null;
}

/**
 * Number range filter value
 */
export interface NumberRangeFilter {
  /**
   * Minimum value
   */
  min: number | null;

  /**
   * Maximum value
   */
  max: number | null;
}
