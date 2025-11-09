/**
 * Sort Types
 *
 * Type definitions for sorting functionality
 * Used across all features for consistent sorting behavior
 */

/**
 * Sort direction
 */
export type SortDirection = 'asc' | 'desc'

/**
 * Single sort condition
 */
export interface SortCondition {
  /**
   * Field name to sort by
   */
  field: string

  /**
   * Sort direction
   */
  direction: SortDirection

  /**
   * Optional label for display
   */
  label?: string

  /**
   * Order priority (for multi-column sorting)
   * Lower numbers are sorted first
   */
  priority?: number
}

/**
 * Sort state for UI components
 */
export interface SortState {
  /**
   * Active sort conditions
   */
  conditions: SortCondition[]
}

/**
 * Sort actions/handlers
 */
export interface SortActions {
  /**
   * Set sort by field (replaces existing sort)
   */
  onSort: (field: string, direction?: SortDirection) => void

  /**
   * Add additional sort condition (multi-column sort)
   */
  onAddSort: (condition: SortCondition) => void

  /**
   * Remove sort condition by field
   */
  onRemoveSort: (field: string) => void

  /**
   * Toggle sort direction for a field
   */
  onToggleSort: (field: string) => void

  /**
   * Clear all sort conditions
   */
  onClearSort: () => void

  /**
   * Update sort priority
   */
  onUpdatePriority?: (field: string, priority: number) => void
}

/**
 * Combined sort state and actions
 */
export interface SortControl extends SortState, SortActions {}

/**
 * Sort field configuration
 */
export interface SortFieldConfig {
  /**
   * Field name (API field name)
   */
  field: string

  /**
   * Display label
   */
  label: string

  /**
   * Whether this field is sortable
   * @default true
   */
  sortable?: boolean

  /**
   * Default sort direction when first clicked
   * @default 'asc'
   */
  defaultDirection?: SortDirection

  /**
   * Whether to allow this field in multi-sort
   * @default true
   */
  allowMultiSort?: boolean

  /**
   * Custom sort function (for client-side sorting)
   */
  customSort?: <T>(a: T, b: T, direction: SortDirection) => number

  /**
   * Format value for display in sort indicator
   */
  formatValue?: (value: unknown) => string
}

/**
 * Sort configuration
 */
export interface SortConfig {
  /**
   * Available fields for sorting
   */
  fields?: SortFieldConfig[]

  /**
   * Default sort conditions
   */
  defaultSort?: SortCondition[]

  /**
   * Enable multi-column sorting
   * @default false
   */
  enableMultiSort?: boolean

  /**
   * Maximum number of sort conditions
   * @default 3
   */
  maxSortConditions?: number

  /**
   * Default sort direction
   * @default 'asc'
   */
  defaultDirection?: SortDirection

  /**
   * Show sort indicators in column headers
   * @default true
   */
  showSortIndicators?: boolean

  /**
   * Sort mode: 'single' or 'multiple'
   * @default 'single'
   */
  sortMode?: 'single' | 'multiple'
}

/**
 * Sort parameters for API requests
 */
export interface SortParams {
  /**
   * Field to sort by
   * For single sort mode
   */
  sortBy?: string

  /**
   * Sort direction
   * For single sort mode
   */
  sortDirection?: SortDirection

  /**
   * Multiple sort conditions
   * For multi-sort mode
   * Format: "field1:asc,field2:desc"
   */
  sort?: string
}

/**
 * Sort indicator props
 */
export interface SortIndicatorProps {
  /**
   * Current sort direction (undefined if not sorted)
   */
  direction?: SortDirection

  /**
   * Whether this column is actively sorted
   */
  active: boolean

  /**
   * Sort priority (for multi-sort)
   */
  priority?: number

  /**
   * Custom icon components
   */
  icons?: {
    asc?: React.ReactNode
    desc?: React.ReactNode
    neutral?: React.ReactNode
  }
}

/**
 * Sortable column header props
 */
export interface SortableColumnProps {
  /**
   * Field name
   */
  field: string

  /**
   * Column label
   */
  label: string

  /**
   * Current sort state
   */
  sortState?: SortState

  /**
   * Sort handler
   */
  onSort: (field: string, direction?: SortDirection) => void

  /**
   * Whether this column is sortable
   * @default true
   */
  sortable?: boolean

  /**
   * Custom className
   */
  className?: string
}

/**
 * Type guards
 */

/**
 * Check if value is a valid sort condition
 */
export const isSortCondition = (value: unknown): value is SortCondition => {
  if (!value || typeof value !== 'object') {
    return false
  }

  const condition = value as Record<string, unknown>

  return (
    typeof condition['field'] === 'string' &&
    (condition['direction'] === 'asc' || condition['direction'] === 'desc')
  )
}

/**
 * Check if value is a valid sort state
 */
export const isSortState = (value: unknown): value is SortState => {
  if (!value || typeof value !== 'object') {
    return false
  }

  const state = value as Record<string, unknown>

  return (
    Array.isArray(state['conditions']) && (state['conditions'] as unknown[]).every(isSortCondition)
  )
}

/**
 * Utility functions
 */

/**
 * Toggle sort direction
 */
export const toggleDirection = (current: SortDirection): SortDirection => {
  return current === 'asc' ? 'desc' : 'asc'
}

/**
 * Get next sort direction in cycle: null -> asc -> desc -> null
 */
export const getNextDirection = (current: SortDirection | undefined): SortDirection | undefined => {
  if (!current) {
    return 'asc'
  }

  if (current === 'asc') {
    return 'desc'
  }

  return undefined
}

/**
 * Find sort condition by field
 */
export const findSortCondition = (
  conditions: SortCondition[],
  field: string
): SortCondition | undefined => {
  return conditions.find(c => c.field === field)
}

/**
 * Get sort direction for a field
 */
export const getSortDirection = (
  conditions: SortCondition[],
  field: string
): SortDirection | undefined => {
  return findSortCondition(conditions, field)?.direction
}

/**
 * Check if a field is currently sorted
 */
export const isFieldSorted = (conditions: SortCondition[], field: string): boolean => {
  return conditions.some(c => c.field === field)
}

/**
 * Get sort priority for a field
 */
export const getSortPriority = (conditions: SortCondition[], field: string): number | undefined => {
  const condition = findSortCondition(conditions, field)
  return condition?.priority
}

const normalizeComparableValue = (value: unknown): unknown => {
  if (value instanceof Date) {
    return value.getTime()
  }

  return value
}

const compareValues = (aValue: unknown, bValue: unknown): number => {
  const valueA = normalizeComparableValue(aValue)
  const valueB = normalizeComparableValue(bValue)

  if (valueA === valueB) {
    return 0
  }

  if (valueA === null || valueA === undefined) {
    return 1
  }

  if (valueB === null || valueB === undefined) {
    return -1
  }

  if (valueA < valueB) {
    return -1
  }

  if (valueA > valueB) {
    return 1
  }

  return 0
}

const compareByCondition = <T>(
  a: T,
  b: T,
  condition: SortCondition,
  fieldConfig?: SortFieldConfig
): number => {
  if (fieldConfig?.customSort) {
    return fieldConfig.customSort(a, b, condition.direction)
  }

  const recordA = a as Record<string, unknown>
  const recordB = b as Record<string, unknown>

  const aValue = recordA[condition.field]
  const bValue = recordB[condition.field]

  return compareValues(aValue, bValue)
}

/**
 * Create sort condition
 */
export const createSortCondition = (
  field: string,
  direction: SortDirection = 'asc',
  label?: string,
  priority?: number
): SortCondition => {
  const condition: SortCondition = {
    field,
    direction,
  }

  if (label !== undefined) {
    condition.label = label
  }

  if (priority !== undefined) {
    condition.priority = priority
  }

  return condition
}

/**
 * Create initial sort state
 */
export const createInitialSortState = (config?: SortConfig): SortState => ({
  conditions: config?.defaultSort || [],
})

/**
 * Convert sort state to API params (single sort)
 */
export const sortStateToParams = (state: SortState): SortParams => {
  if (state.conditions.length === 0) {
    return {}
  }

  // For single sort mode, use the first condition
  const [primarySort] = state.conditions

  if (!primarySort) {
    return {}
  }

  return {
    sortBy: primarySort.field,
    sortDirection: primarySort.direction,
  }
}

/**
 * Convert sort state to API params (multi sort)
 * Format: "field1:asc,field2:desc,field3:asc"
 */
export const sortStateToMultiParams = (state: SortState): SortParams => {
  if (state.conditions.length === 0) {
    return {}
  }

  // Sort by priority if available
  const sortedConditions = [...state.conditions].sort((a, b) => {
    const aPriority = a.priority ?? Number.MAX_SAFE_INTEGER
    const bPriority = b.priority ?? Number.MAX_SAFE_INTEGER
    return aPriority - bPriority
  })

  const sortString = sortedConditions.map(c => `${c.field}:${c.direction}`).join(',')

  return {
    sort: sortString,
  }
}

/**
 * Parse sort string to conditions
 * Format: "field1:asc,field2:desc" -> [{ field: 'field1', direction: 'asc' }, ...]
 */
export const parseSortString = (sortString: string): SortCondition[] => {
  if (!sortString) {
    return []
  }

  return sortString
    .split(',')
    .map((part, index) => {
      const [field, direction] = part.trim().split(':')
      if (!field || (direction !== 'asc' && direction !== 'desc')) {
        return null
      }
      return createSortCondition(field, direction, undefined, index)
    })
    .filter((c): c is SortCondition => c !== null)
}

/**
 * Format sort condition for display
 */
export const formatSortCondition = (
  condition: SortCondition,
  fieldConfig?: SortFieldConfig
): string => {
  const fieldLabel = fieldConfig?.label || condition.label || condition.field
  const directionLabel = condition.direction === 'asc' ? 'ascending' : 'descending'
  return `${fieldLabel} (${directionLabel})`
}

/**
 * Client-side array sorting utility
 */
export const sortArray = <T>(
  array: T[],
  conditions: SortCondition[],
  fieldConfigs?: SortFieldConfig[]
): T[] => {
  if (conditions.length === 0) {
    return array
  }

  return [...array].sort((a, b) => {
    for (const condition of conditions) {
      const { field, direction } = condition
      const fieldConfig = fieldConfigs?.find(f => f.field === field)

      const comparison = compareByCondition(a, b, condition, fieldConfig)

      if (comparison !== 0) {
        return direction === 'asc' ? comparison : -comparison
      }
    }

    return 0
  })
}
