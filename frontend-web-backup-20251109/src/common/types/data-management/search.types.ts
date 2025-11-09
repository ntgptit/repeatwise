/**
 * Search Types
 *
 * Type definitions for search functionality
 * Used across all features for consistent search behavior
 */

/**
 * Search parameters for API requests
 */
export interface SearchParams {
  /**
   * Search query string
   */
  query: string

  /**
   * Fields to search in (optional)
   */
  fields?: string[]
}

/**
 * Search state for UI components
 */
export interface SearchState {
  /**
   * Current search query
   */
  query: string

  /**
   * Whether search is active/focused
   */
  isActive: boolean

  /**
   * Whether search is loading
   */
  isLoading: boolean
}

/**
 * Search actions/handlers
 */
export interface SearchActions {
  /**
   * Update search query
   */
  onSearch: (query: string) => void

  /**
   * Clear search
   */
  onClear: () => void

  /**
   * Submit search
   */
  onSubmit?: (query: string) => void

  /**
   * Focus search input
   */
  onFocus?: () => void

  /**
   * Blur search input
   */
  onBlur?: () => void
}

/**
 * Combined search state and actions
 */
export interface SearchControl extends SearchState, SearchActions {}

/**
 * Search configuration/options
 */
export interface SearchConfig {
  /**
   * Placeholder text
   * @default 'Search...'
   */
  placeholder?: string

  /**
   * Debounce delay in milliseconds
   * @default 300
   */
  debounceDelay?: number

  /**
   * Minimum characters to trigger search
   * @default 1
   */
  minChars?: number

  /**
   * Maximum characters allowed
   * @default 100
   */
  maxChars?: number

  /**
   * Search on input change
   * @default true
   */
  searchOnChange?: boolean

  /**
   * Search on submit (Enter key)
   * @default true
   */
  searchOnSubmit?: boolean

  /**
   * Show clear button
   * @default true
   */
  showClearButton?: boolean

  /**
   * Show search icon
   * @default true
   */
  showSearchIcon?: boolean

  /**
   * Case sensitive search
   * @default false
   */
  caseSensitive?: boolean

  /**
   * Fields to search in
   */
  fields?: string[]
}

/**
 * Search result highlight
 */
export interface SearchHighlight {
  /**
   * Original text
   */
  text: string

  /**
   * Highlighted text (with marks)
   */
  highlighted: string

  /**
   * Match positions
   */
  matches: Array<{
    start: number
    end: number
  }>
}

/**
 * Search suggestion
 */
export interface SearchSuggestion {
  /**
   * Suggestion text
   */
  text: string

  /**
   * Suggestion type
   */
  type?: 'history' | 'suggestion' | 'autocomplete'

  /**
   * Click handler
   */
  onClick?: () => void
}

/**
 * Type guards
 */

/**
 * Check if search params is valid
 */
export const isSearchParams = (value: unknown): value is SearchParams => {
  if (!value || typeof value !== 'object') {
    return false
  }

  const params = value as Record<string, unknown>

  return typeof params['query'] === 'string'
}

/**
 * Utility functions
 */

/**
 * Create initial search state
 */
export const createInitialSearchState = (_config?: SearchConfig): SearchState => ({
  query: '',
  isActive: false,
  isLoading: false,
})

/**
 * Get default search config
 */
export const getDefaultSearchConfig = (): Required<SearchConfig> => ({
  placeholder: 'Search...',
  debounceDelay: 300,
  minChars: 1,
  maxChars: 100,
  searchOnChange: true,
  searchOnSubmit: true,
  showClearButton: true,
  showSearchIcon: true,
  caseSensitive: false,
  fields: [],
})

/**
 * Validate search query
 */
export const validateSearchQuery = (query: string, config?: SearchConfig): boolean => {
  const cfg = { ...getDefaultSearchConfig(), ...config }

  if (query.length < cfg.minChars) {
    return false
  }
  if (query.length > cfg.maxChars) {
    return false
  }

  return true
}

/**
 * Highlight search matches in text
 */
export const highlightSearchMatches = (
  text: string,
  query: string,
  caseSensitive = false
): SearchHighlight => {
  if (!query) {
    return {
      text,
      highlighted: text,
      matches: [],
    }
  }

  const flags = caseSensitive ? 'g' : 'gi'
  const escapedQuery = query.replaceAll(/[.*+?^${}()|[\]\\]/g, String.raw`\$&`)
  const regex = new RegExp(`(${escapedQuery})`, flags)

  const matches: Array<{ start: number; end: number }> = []
  let match
  const searchRegex = new RegExp(escapedQuery, flags)

  while ((match = searchRegex.exec(text)) !== null) {
    matches.push({
      start: match.index,
      end: match.index + match[0].length,
    })
  }

  const highlighted = text.replace(regex, '<mark>$1</mark>')

  return {
    text,
    highlighted,
    matches,
  }
}

/**
 * Clean search query
 */
export const cleanSearchQuery = (query: string): string => {
  return query.trim().replaceAll(/\s+/g, ' ')
}

export default {
  createInitialSearchState,
  getDefaultSearchConfig,
  validateSearchQuery,
  highlightSearchMatches,
  cleanSearchQuery,
}
