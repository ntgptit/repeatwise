/**
 * Search Types
 *
 * Type definitions for search functionality.
 *
 * @module common/types/data-management/search
 */

/**
 * Search parameters for API requests
 */
export interface SearchParams<T = string> {
  /**
   * Search query string
   */
  query?: string;

  /**
   * Fields to search in (optional)
   */
  searchFields?: T[];

  /**
   * Search mode
   */
  searchMode?: SearchMode;

  /**
   * Alternative parameter name (q, search, keyword, etc.)
   */
  q?: string;
}

/**
 * Search mode/strategy
 */
export type SearchMode =
  | 'contains' // Default: search if field contains query
  | 'startsWith' // Search if field starts with query
  | 'endsWith' // Search if field ends with query
  | 'exact' // Exact match only
  | 'fuzzy'; // Fuzzy/approximate matching

/**
 * Search state for UI components
 */
export interface SearchState {
  /**
   * Current search query
   */
  query: string;

  /**
   * Whether search is active (has query)
   */
  isSearching: boolean;

  /**
   * Debounced query value
   */
  debouncedQuery: string;
}

/**
 * Search actions/handlers
 */
export interface SearchActions {
  /**
   * Set search query
   */
  setQuery: (query: string) => void;

  /**
   * Clear search query
   */
  clearQuery: () => void;

  /**
   * Handle search input change
   */
  handleSearchChange: (event: React.ChangeEvent<HTMLInputElement>) => void;

  /**
   * Handle search submit (Enter key)
   */
  handleSearchSubmit?: (event: React.FormEvent) => void;
}

/**
 * Complete search hook return type
 */
export interface UseSearchReturn extends SearchState, SearchActions {
  /**
   * Search parameters ready for API request
   */
  searchParams: SearchParams;
}

/**
 * Search configuration options
 */
export interface SearchOptions<T = string> {
  /**
   * Initial search query
   */
  initialQuery?: string;

  /**
   * Debounce delay in milliseconds (default: 300)
   */
  debounceDelay?: number;

  /**
   * Minimum query length to trigger search (default: 0)
   */
  minQueryLength?: number;

  /**
   * Fields to search in
   */
  searchFields?: T[];

  /**
   * Search mode
   */
  searchMode?: SearchMode;

  /**
   * Callback when search query changes
   */
  onSearchChange?: (query: string) => void;

  /**
   * Callback when search is submitted
   */
  onSearchSubmit?: (query: string) => void;

  /**
   * Whether to trim whitespace from query
   */
  trimQuery?: boolean;

  /**
   * Whether to convert query to lowercase
   */
  lowercaseQuery?: boolean;
}

/**
 * Search suggestion item
 */
export interface SearchSuggestion {
  /**
   * Suggestion ID
   */
  id: string;

  /**
   * Suggestion text/value
   */
  value: string;

  /**
   * Display label (if different from value)
   */
  label?: string;

  /**
   * Category/type of suggestion
   */
  category?: string;

  /**
   * Icon for suggestion (optional)
   */
  icon?: string;

  /**
   * Additional metadata
   */
  metadata?: Record<string, unknown>;
}

/**
 * Search history item
 */
export interface SearchHistoryItem {
  /**
   * Unique ID
   */
  id: string;

  /**
   * Search query
   */
  query: string;

  /**
   * Timestamp when search was performed
   */
  timestamp: Date;

  /**
   * Number of results (optional)
   */
  resultCount?: number;
}

/**
 * Search result highlight
 */
export interface SearchHighlight {
  /**
   * Field name that matched
   */
  field: string;

  /**
   * Highlighted text with markers
   */
  highlightedText: string;

  /**
   * Match positions (start, end)
   */
  matches: Array<{
    start: number;
    end: number;
  }>;
}

/**
 * Search result item with highlighting
 */
export interface SearchResult<T> {
  /**
   * The actual data item
   */
  item: T;

  /**
   * Relevance score (0-1)
   */
  score?: number;

  /**
   * Highlighted fields
   */
  highlights?: SearchHighlight[];

  /**
   * Matched fields
   */
  matchedFields?: string[];
}

/**
 * Search response with results
 */
export interface SearchResponse<T> {
  /**
   * Array of search results
   */
  results: Array<SearchResult<T>>;

  /**
   * Total number of results
   */
  totalResults: number;

  /**
   * Search query that was executed
   */
  query: string;

  /**
   * Search suggestions (if available)
   */
  suggestions?: SearchSuggestion[];

  /**
   * Search execution time (ms)
   */
  executionTime?: number;
}

/**
 * Advanced search filters
 */
export interface AdvancedSearchFilters {
  /**
   * Search query
   */
  query: string;

  /**
   * Date range filter
   */
  dateRange?: {
    from: Date | null;
    to: Date | null;
  };

  /**
   * Category filter
   */
  categories?: string[];

  /**
   * Tags filter
   */
  tags?: string[];

  /**
   * Custom filters
   */
  customFilters?: Record<string, unknown>;
}
