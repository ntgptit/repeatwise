/**
 * API Configuration Constants
 */
export const API_CONFIG = {
  BASE_URL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  TIMEOUT: 30000, // 30 seconds
  RETRY_COUNT: 3,
  RETRY_DELAY: 1000, // 1 second
} as const

export const API_ENDPOINTS = {
  // Auth
  AUTH: {
    LOGIN: '/auth/login',
    REGISTER: '/auth/register',
    LOGOUT: '/auth/logout',
    REFRESH: '/auth/refresh',
    PROFILE: '/auth/profile',
  },
  // Cards
  CARDS: {
    BASE: '/cards',
    BY_ID: (id: string) => `/cards/${id}`,
    BY_DECK: (deckId: string) => `/cards/deck/${deckId}`,
  },
  // Decks
  DECKS: {
    BASE: '/decks',
    BY_ID: (id: string) => `/decks/${id}`,
    BY_FOLDER: (folderId: string) => `/decks/folder/${folderId}`,
  },
  // Folders
  FOLDERS: {
    BASE: '/folders',
    BY_ID: (id: string) => `/folders/${id}`,
    TREE: '/folders/tree',
  },
  // Review
  REVIEW: {
    BASE: '/review',
    SESSION: '/review/session',
    COMPLETE: '/review/complete',
  },
  // Stats
  STATS: {
    BASE: '/stats',
    OVERVIEW: '/stats/overview',
  },
  // Import/Export
  IMPORT: {
    BASE: '/import',
    CSV: '/import/csv',
    JSON: '/import/json',
  },
  EXPORT: {
    BASE: '/export',
    CSV: '/export/csv',
    JSON: '/export/json',
  },
} as const
