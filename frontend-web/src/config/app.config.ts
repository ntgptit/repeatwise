/**
 * Application Configuration
 *
 * Application-wide constants and configuration
 * Business logic constants, UI settings, feature flags
 */

import { env } from './env';

/**
 * Application metadata
 */
export const APP_INFO = {
  name: env.appName,
  version: env.appVersion,
  description: 'Spaced Repetition System for effective learning',
  author: 'RepeatWise Team',
  repository: 'https://github.com/ntgptit/repeatwise',
  buildDate: env.buildDate,
} as const;

/**
 * Application routes
 */
export const APP_ROUTES = {
  // Public routes
  HOME: '/',
  LOGIN: '/login',
  REGISTER: '/register',
  FORGOT_PASSWORD: '/forgot-password',

  // Authenticated routes
  DASHBOARD: '/dashboard',

  // Folder routes
  FOLDERS: '/folders',
  FOLDER_DETAIL: '/folders/:id',
  FOLDER_CREATE: '/folders/new',
  FOLDER_EDIT: '/folders/:id/edit',

  // Deck routes
  DECKS: '/decks',
  DECK_DETAIL: '/decks/:id',
  DECK_CREATE: '/decks/new',
  DECK_EDIT: '/decks/:id/edit',

  // Card routes
  CARDS: '/decks/:deckId/cards',
  CARD_CREATE: '/decks/:deckId/cards/new',
  CARD_EDIT: '/decks/:deckId/cards/:id/edit',

  // Review routes
  REVIEW: '/review',
  REVIEW_SESSION: '/review/:deckId',
  REVIEW_CRAM: '/review/:deckId/cram',
  REVIEW_RANDOM: '/review/:deckId/random',

  // Statistics routes
  STATISTICS: '/statistics',
  STATISTICS_USER: '/statistics/user',
  STATISTICS_DECK: '/statistics/deck/:id',
  STATISTICS_FOLDER: '/statistics/folder/:id',

  // User routes
  PROFILE: '/profile',
  SETTINGS: '/settings',
  SETTINGS_ACCOUNT: '/settings/account',
  SETTINGS_SRS: '/settings/srs',
  SETTINGS_PREFERENCES: '/settings/preferences',

  // Error routes
  NOT_FOUND: '/404',
  UNAUTHORIZED: '/401',
  FORBIDDEN: '/403',
  SERVER_ERROR: '/500',
} as const;

/**
 * Local storage keys
 */
export const STORAGE_KEYS = {
  // Authentication
  ACCESS_TOKEN: 'repeatwise_access_token',
  REFRESH_TOKEN: 'repeatwise_refresh_token',
  USER: 'repeatwise_user',
  REMEMBER_ME: 'repeatwise_remember_me',

  // User preferences
  THEME: 'repeatwise_theme',
  LANGUAGE: 'repeatwise_language',
  TIMEZONE: 'repeatwise_timezone',

  // SRS settings
  SRS_CONFIG: 'repeatwise_srs_config',
  SRS_BOXES: 'repeatwise_srs_boxes',

  // UI state
  SIDEBAR_COLLAPSED: 'repeatwise_sidebar_collapsed',
  TABLE_PAGE_SIZE: 'repeatwise_table_page_size',
  FOLDER_TREE_EXPANDED: 'repeatwise_folder_tree_expanded',

  // Temporary data
  DRAFT_CARD: 'repeatwise_draft_card',
  REVIEW_STATE: 'repeatwise_review_state',
} as const;

/**
 * Session storage keys (cleared on browser close)
 */
export const SESSION_KEYS = {
  REDIRECT_URL: 'repeatwise_redirect_url',
  REVIEW_SESSION: 'repeatwise_review_session',
} as const;

/**
 * API endpoints
 */
export const API_ENDPOINTS = {
  // Authentication
  AUTH: {
    REGISTER: '/auth/register',
    LOGIN: '/auth/login',
    LOGOUT: '/auth/logout',
    REFRESH_TOKEN: '/auth/refresh-token',
    FORGOT_PASSWORD: '/auth/forgot-password',
    RESET_PASSWORD: '/auth/reset-password',
    VERIFY_EMAIL: '/auth/verify-email',
  },

  // User
  USER: {
    PROFILE: '/users/profile',
    UPDATE_PROFILE: '/users/profile',
    CHANGE_PASSWORD: '/users/change-password',
    SETTINGS: '/users/settings',
  },

  // Folders
  FOLDERS: {
    LIST: '/folders',
    CREATE: '/folders',
    GET: '/folders/:id',
    UPDATE: '/folders/:id',
    DELETE: '/folders/:id',
    MOVE: '/folders/:id/move',
    COPY: '/folders/:id/copy',
    STATISTICS: '/folders/:id/statistics',
  },

  // Decks
  DECKS: {
    LIST: '/decks',
    CREATE: '/decks',
    GET: '/decks/:id',
    UPDATE: '/decks/:id',
    DELETE: '/decks/:id',
    MOVE: '/decks/:id/move',
    COPY: '/decks/:id/copy',
    STATISTICS: '/decks/:id/statistics',
  },

  // Cards
  CARDS: {
    LIST: '/cards',
    CREATE: '/cards',
    GET: '/cards/:id',
    UPDATE: '/cards/:id',
    DELETE: '/cards/:id',
    IMPORT: '/cards/import',
    EXPORT: '/cards/export',
  },

  // Review
  REVIEW: {
    START: '/review/:deckId/start',
    SUBMIT: '/review/:deckId/submit',
    SKIP: '/review/:deckId/skip',
    UNDO: '/review/:deckId/undo',
    COMPLETE: '/review/:deckId/complete',
  },

  // Statistics
  STATISTICS: {
    USER: '/statistics/user',
    DECK: '/statistics/deck/:id',
    FOLDER: '/statistics/folder/:id',
    BOX_DISTRIBUTION: '/statistics/boxes',
  },
} as const;

/**
 * Date/Time formats
 */
export const DATE_FORMATS = {
  DISPLAY: 'MMM dd, yyyy', // Jan 15, 2024
  DISPLAY_LONG: 'MMMM dd, yyyy', // January 15, 2024
  DISPLAY_WITH_TIME: 'MMM dd, yyyy HH:mm', // Jan 15, 2024 14:30
  DISPLAY_TIME: 'HH:mm', // 14:30
  ISO: "yyyy-MM-dd'T'HH:mm:ss.SSSxxx", // ISO 8601
  API: 'yyyy-MM-dd', // 2024-01-15
  API_WITH_TIME: "yyyy-MM-dd'T'HH:mm:ss", // 2024-01-15T14:30:00
} as const;

/**
 * Pagination defaults
 */
export const PAGINATION_DEFAULTS = {
  PAGE: 1,
  PAGE_SIZE: 10,
  PAGE_SIZE_OPTIONS: [10, 20, 50, 100],
  MAX_PAGE_BUTTONS: 5,
} as const;

/**
 * SRS (Spaced Repetition System) defaults
 */
export const SRS_DEFAULTS = {
  TOTAL_BOXES: 7,
  MIN_BOXES: 3,
  MAX_BOXES: 10,
  BOX_INTERVALS: [1, 3, 7, 14, 30, 60, 120], // Days
  REVIEW_ORDER: 'dueDate' as const, // 'dueDate' | 'random' | 'box'
  NEW_CARDS_PER_DAY: 20,
  MAX_REVIEWS_PER_DAY: 200,
  FORGOTTEN_CARD_ACTION: 'moveToBox1' as const, // 'moveToBox1' | 'moveDown' | 'repeat'
  MOVE_DOWN_BOXES: 1,
} as const;

/**
 * Card limits
 */
export const CARD_LIMITS = {
  FRONT_MAX_LENGTH: 5000,
  BACK_MAX_LENGTH: 5000,
  MIN_LENGTH: 1,
} as const;

/**
 * Folder/Deck limits
 */
export const FOLDER_LIMITS = {
  NAME_MAX_LENGTH: 100,
  DESCRIPTION_MAX_LENGTH: 500,
  MAX_DEPTH: 10,
} as const;

export const DECK_LIMITS = {
  NAME_MAX_LENGTH: 100,
  DESCRIPTION_MAX_LENGTH: 500,
} as const;

/**
 * Import/Export limits
 */
export const IMPORT_LIMITS = {
  MAX_FILE_SIZE: 50 * 1024 * 1024, // 50MB
  MAX_ROWS: 10000,
  SUPPORTED_FORMATS: ['csv', 'xlsx'],
} as const;

/**
 * Validation rules
 */
export const VALIDATION_RULES = {
  EMAIL: {
    pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
    message: 'Invalid email format',
  },
  USERNAME: {
    pattern: /^[a-zA-Z0-9_-]{3,30}$/,
    minLength: 3,
    maxLength: 30,
    message: 'Username must be 3-30 characters, alphanumeric, dash, or underscore only',
  },
  PASSWORD: {
    minLength: 8,
    maxLength: 100,
    message: 'Password must be at least 8 characters',
  },
  NAME: {
    minLength: 1,
    maxLength: 100,
    message: 'Name must be 1-100 characters',
  },
} as const;

/**
 * UI Constants
 */
export const UI_CONSTANTS = {
  // Debounce delays (milliseconds)
  DEBOUNCE_DELAY: {
    SEARCH: 300,
    FILTER: 300,
    RESIZE: 150,
    SCROLL: 100,
  },

  // Animation durations (milliseconds)
  ANIMATION_DURATION: {
    FAST: 150,
    NORMAL: 300,
    SLOW: 500,
  },

  // Breakpoints (pixels) - matches design system
  BREAKPOINTS: {
    XS: 0,
    SM: 640,
    MD: 768,
    LG: 1024,
    XL: 1280,
    '2XL': 1536,
  },

  // Z-index layers
  Z_INDEX: {
    DROPDOWN: 1000,
    STICKY: 1020,
    FIXED: 1030,
    MODAL_BACKDROP: 1040,
    MODAL: 1050,
    POPOVER: 1060,
    TOOLTIP: 1070,
  },
} as const;

/**
 * Feature flags
 */
export const FEATURE_FLAGS = {
  // Enabled features
  ENABLE_FOLDERS: true,
  ENABLE_DECKS: true,
  ENABLE_CARDS: true,
  ENABLE_REVIEW: true,
  ENABLE_STATISTICS: true,
  ENABLE_IMPORT_EXPORT: true,

  // Beta features
  ENABLE_CRAM_MODE: true,
  ENABLE_RANDOM_MODE: true,
  ENABLE_EDIT_DURING_REVIEW: true,

  // Future features (disabled)
  ENABLE_COLLABORATION: false,
  ENABLE_AI_SUGGESTIONS: false,
  ENABLE_GAMIFICATION: false,
  ENABLE_SOCIAL_SHARING: false,
} as const;

/**
 * Error messages
 */
export const ERROR_MESSAGES = {
  NETWORK_ERROR: 'Network error. Please check your internet connection.',
  UNAUTHORIZED: 'You are not authorized to perform this action.',
  FORBIDDEN: 'Access forbidden.',
  NOT_FOUND: 'Resource not found.',
  SERVER_ERROR: 'Server error. Please try again later.',
  VALIDATION_ERROR: 'Validation error. Please check your input.',
  UNKNOWN_ERROR: 'An unknown error occurred.',
} as const;

/**
 * Success messages
 */
export const SUCCESS_MESSAGES = {
  SAVE_SUCCESS: 'Changes saved successfully.',
  CREATE_SUCCESS: 'Created successfully.',
  UPDATE_SUCCESS: 'Updated successfully.',
  DELETE_SUCCESS: 'Deleted successfully.',
  IMPORT_SUCCESS: 'Import completed successfully.',
  EXPORT_SUCCESS: 'Export completed successfully.',
} as const;

/**
 * Helper to build route with params
 */
export const buildRoute = (route: string, params: Record<string, string | number>): string => {
  let result = route;
  Object.entries(params).forEach(([key, value]) => {
    result = result.replace(`:${key}`, String(value));
  });
  return result;
};

/**
 * Helper to build API endpoint with params
 */
export const buildApiEndpoint = (endpoint: string, params: Record<string, string | number>): string => {
  return buildRoute(endpoint, params);
};

export default {
  APP_INFO,
  APP_ROUTES,
  STORAGE_KEYS,
  SESSION_KEYS,
  API_ENDPOINTS,
  DATE_FORMATS,
  PAGINATION_DEFAULTS,
  SRS_DEFAULTS,
  CARD_LIMITS,
  FOLDER_LIMITS,
  DECK_LIMITS,
  IMPORT_LIMITS,
  VALIDATION_RULES,
  UI_CONSTANTS,
  FEATURE_FLAGS,
  ERROR_MESSAGES,
  SUCCESS_MESSAGES,
  buildRoute,
  buildApiEndpoint,
};
