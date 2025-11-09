/**
 * Filter Types
 *
 * Type definitions for filtering functionality
 * Used across all features for consistent filtering behavior
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
  | 'between'
  | 'in'
  | 'notIn'
  | 'isNull'
  | 'isNotNull'
  | 'isEmpty'
  | 'isNotEmpty'

/**
 * Filter value types
 */
export type FilterValue = string | number | boolean | Date | null | undefined | FilterValue[]

/**
 * Single filter condition
 */
export interface FilterCondition<T = FilterValue> {
  /**
   * Field name to filter on
   */
  field: string

  /**
   * Filter operator
   */
  operator: FilterOperator

  /**
   * Filter value(s)
   */
  value: T

  /**
   * Optional label for display
   */
  label?: string
}

/**
 * Filter group (multiple conditions with AND/OR logic)
 */
export interface FilterGroup {
  /**
   * Logical operator to combine conditions
   */
  logic: 'AND' | 'OR'

  /**
   * Array of filter conditions
   */
  conditions: FilterCondition[]
}

/**
 * Filter state for UI components
 */
export interface FilterState {
  /**
   * Active filter conditions
   */
  conditions: FilterCondition[]

  /**
   * Logical operator for combining conditions
   * @default 'AND'
   */
  logic: 'AND' | 'OR'
}

/**
 * Filter actions/handlers
 */
export interface FilterActions {
  /**
   * Add a new filter condition
   */
  onAddFilter: (condition: FilterCondition) => void

  /**
   * Remove a filter condition by field
   */
  onRemoveFilter: (field: string) => void

  /**
   * Update an existing filter condition
   */
  onUpdateFilter: (field: string, condition: Partial<FilterCondition>) => void

  /**
   * Clear all filters
   */
  onClearFilters: () => void

  /**
   * Change logical operator
   */
  onChangeLogic: (logic: 'AND' | 'OR') => void

  /**
   * Reset to default filters
   */
  onResetFilters: () => void
}

/**
 * Combined filter state and actions
 */
export interface FilterControl extends FilterState, FilterActions {}

/**
 * Filter field configuration
 */
export interface FilterFieldConfig {
  /**
   * Field name (API field name)
   */
  field: string

  /**
   * Display label
   */
  label: string

  /**
   * Field type
   */
  type: 'text' | 'number' | 'date' | 'boolean' | 'select' | 'multiselect'

  /**
   * Available operators for this field
   */
  operators?: FilterOperator[]

  /**
   * Default operator
   */
  defaultOperator?: FilterOperator

  /**
   * Options for select/multiselect type
   */
  options?: Array<{ label: string; value: FilterValue }>

  /**
   * Placeholder text
   */
  placeholder?: string

  /**
   * Whether this field is required
   */
  required?: boolean

  /**
   * Custom validation function
   */
  validate?: (value: FilterValue) => boolean | string

  /**
   * Format value for display
   */
  formatValue?: (value: FilterValue) => string

  /**
   * Parse value from input
   */
  parseValue?: (value: string) => FilterValue
}

/**
 * Filter configuration
 */
export interface FilterConfig {
  /**
   * Available fields for filtering
   */
  fields: FilterFieldConfig[]

  /**
   * Default filters to apply on load
   */
  defaultFilters?: FilterCondition[]

  /**
   * Default logic operator
   * @default 'AND'
   */
  defaultLogic?: 'AND' | 'OR'

  /**
   * Allow multiple filters on same field
   * @default false
   */
  allowMultiplePerField?: boolean

  /**
   * Show logic operator selector
   * @default false
   */
  showLogicSelector?: boolean

  /**
   * Enable saved filters
   * @default false
   */
  enableSavedFilters?: boolean

  /**
   * Maximum number of filter conditions
   */
  maxConditions?: number
}

/**
 * Filter parameters for API requests
 */
export interface FilterParams {
  /**
   * Array of filter conditions
   */
  filters?: FilterCondition[]

  /**
   * Logical operator
   */
  logic?: 'AND' | 'OR'
}

/**
 * Saved filter preset
 */
export interface FilterPreset {
  /**
   * Unique identifier
   */
  id: string

  /**
   * Preset name
   */
  name: string

  /**
   * Filter conditions
   */
  conditions: FilterCondition[]

  /**
   * Logical operator
   */
  logic: 'AND' | 'OR'

  /**
   * Optional description
   */
  description?: string

  /**
   * Whether this is a default preset
   */
  isDefault?: boolean

  /**
   * Created timestamp
   */
  createdAt?: Date
}

/**
 * Filter chip/tag for displaying active filters
 */
export interface FilterChip {
  /**
   * Field name
   */
  field: string

  /**
   * Display label
   */
  label: string

  /**
   * Formatted value for display
   */
  displayValue: string

  /**
   * Remove handler
   */
  onRemove: () => void
}

/**
 * Type guards
 */

/**
 * Check if value is a valid filter condition
 */
export const isFilterCondition = (value: unknown): value is FilterCondition => {
  if (!value || typeof value !== 'object') {
    return false
  }

  const condition = value as Record<string, unknown>

  return (
    typeof condition['field'] === 'string' &&
    typeof condition['operator'] === 'string' &&
    'value' in condition
  )
}

/**
 * Check if value is a valid filter group
 */
export const isFilterGroup = (value: unknown): value is FilterGroup => {
  if (!value || typeof value !== 'object') {
    return false
  }

  const group = value as Record<string, unknown>

  return (
    (group['logic'] === 'AND' || group['logic'] === 'OR') &&
    Array.isArray(group['conditions']) &&
    (group['conditions'] as unknown[]).every(isFilterCondition)
  )
}

/**
 * Utility functions
 */

/**
 * Get default operators for field type
 */
export const getDefaultOperators = (type: FilterFieldConfig['type']): FilterOperator[] => {
  switch (type) {
    case 'text':
      return ['contains', 'equals', 'startsWith', 'endsWith', 'isEmpty', 'isNotEmpty']
    case 'number':
      return [
        'equals',
        'notEquals',
        'greaterThan',
        'greaterThanOrEqual',
        'lessThan',
        'lessThanOrEqual',
        'between',
      ]
    case 'date':
      return [
        'equals',
        'greaterThan',
        'greaterThanOrEqual',
        'lessThan',
        'lessThanOrEqual',
        'between',
      ]
    case 'boolean':
      return ['equals']
    case 'select':
      return ['equals', 'notEquals', 'in', 'notIn']
    case 'multiselect':
      return ['in', 'notIn']
    default:
      return ['equals', 'notEquals']
  }
}

/**
 * Get operator label for display
 */
export const getOperatorLabel = (operator: FilterOperator): string => {
  const labels: Record<FilterOperator, string> = {
    equals: 'Equals',
    notEquals: 'Not Equals',
    contains: 'Contains',
    notContains: 'Does Not Contain',
    startsWith: 'Starts With',
    endsWith: 'Ends With',
    greaterThan: 'Greater Than',
    greaterThanOrEqual: 'Greater Than or Equal',
    lessThan: 'Less Than',
    lessThanOrEqual: 'Less Than or Equal',
    between: 'Between',
    in: 'In',
    notIn: 'Not In',
    isNull: 'Is Null',
    isNotNull: 'Is Not Null',
    isEmpty: 'Is Empty',
    isNotEmpty: 'Is Not Empty',
  }

  return labels[operator] || operator
}

/**
 * Format filter condition for display
 */
export const formatFilterCondition = (
  condition: FilterCondition,
  fieldConfig?: FilterFieldConfig
): string => {
  const operatorLabel = getOperatorLabel(condition.operator)
  const fieldLabel = fieldConfig?.label || condition.label || condition.field

  if (condition.operator === 'isNull' || condition.operator === 'isNotNull') {
    return `${fieldLabel} ${operatorLabel}`
  }

  if (condition.operator === 'isEmpty' || condition.operator === 'isNotEmpty') {
    return `${fieldLabel} ${operatorLabel}`
  }

  const valueStr = fieldConfig?.formatValue
    ? fieldConfig.formatValue(condition.value)
    : String(condition.value)

  return `${fieldLabel} ${operatorLabel} ${valueStr}`
}

/**
 * Create initial filter state
 */
export const createInitialFilterState = (config?: FilterConfig): FilterState => ({
  conditions: config?.defaultFilters || [],
  logic: config?.defaultLogic || 'AND',
})

/**
 * Convert filter state to API params
 */
export const filterStateToParams = (state: FilterState): FilterParams => {
  const params: FilterParams = {
    logic: state.logic,
  }

  if (state.conditions.length > 0) {
    params.filters = state.conditions
  }

  return params
}
