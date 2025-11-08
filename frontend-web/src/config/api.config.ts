/**
 * API Configuration
 *
 * Configuration for API endpoints, timeouts, and HTTP client settings.
 *
 * @module config/api
 */

import { env } from './env';

/**
 * API configuration object
 */
export const apiConfig = {
  /**
   * Base URL for API requests
   */
  baseURL: env.apiBaseUrl,

  /**
   * Request timeout (ms)
   * Default: 30 seconds
   */
  timeout: 30000,

  /**
   * Enable request/response logging
   */
  enableLogging: env.isDevelopment,

  /**
   * Enable request retry on failure
   */
  enableRetry: true,

  /**
   * Maximum retry attempts
   */
  maxRetries: 3,

  /**
   * Retry delay (ms)
   */
  retryDelay: 1000,

  /**
   * Status codes that trigger retry
   */
  retryStatusCodes: [408, 429, 500, 502, 503, 504],

  /**
   * Request headers
   */
  headers: {
    common: {
      'Content-Type': 'application/json',
      Accept: 'application/json',
    },
  },

  /**
   * With credentials (send cookies)
   */
  withCredentials: false,

  /**
   * API endpoints
   */
  endpoints: {
    /**
     * Authentication endpoints
     */
    auth: {
      login: '/auth/login',
      register: '/auth/register',
      logout: '/auth/logout',
      refresh: '/auth/refresh',
      forgotPassword: '/auth/forgot-password',
      resetPassword: '/auth/reset-password',
      verify: '/auth/verify',
      me: '/auth/me',
    },

    /**
     * User endpoints
     */
    users: {
      base: '/users',
      byId: (id: string | number) => `/users/${id}`,
      profile: '/users/profile',
      updateProfile: '/users/profile',
      changePassword: '/users/change-password',
      avatar: '/users/avatar',
    },

    /**
     * Deck endpoints
     */
    decks: {
      base: '/decks',
      byId: (id: string | number) => `/decks/${id}`,
      cards: (deckId: string | number) => `/decks/${deckId}/cards`,
      statistics: (deckId: string | number) => `/decks/${deckId}/statistics`,
      export: (deckId: string | number) => `/decks/${deckId}/export`,
      import: '/decks/import',
    },

    /**
     * Card endpoints
     */
    cards: {
      base: '/cards',
      byId: (id: string | number) => `/cards/${id}`,
      review: (cardId: string | number) => `/cards/${cardId}/review`,
      statistics: (cardId: string | number) => `/cards/${cardId}/statistics`,
    },

    /**
     * Folder endpoints
     */
    folders: {
      base: '/folders',
      byId: (id: string | number) => `/folders/${id}`,
      decks: (folderId: string | number) => `/folders/${folderId}/decks`,
      children: (folderId: string | number) => `/folders/${folderId}/children`,
    },

    /**
     * Review endpoints
     */
    reviews: {
      base: '/reviews',
      session: '/reviews/session',
      submit: '/reviews/submit',
      history: '/reviews/history',
      statistics: '/reviews/statistics',
    },

    /**
     * Statistics endpoints
     */
    statistics: {
      dashboard: '/statistics/dashboard',
      progress: '/statistics/progress',
      heatmap: '/statistics/heatmap',
      forecast: '/statistics/forecast',
    },

    /**
     * Tag endpoints
     */
    tags: {
      base: '/tags',
      byId: (id: string | number) => `/tags/${id}`,
    },

    /**
     * Media/Upload endpoints
     */
    media: {
      upload: '/media/upload',
      byId: (id: string | number) => `/media/${id}`,
      delete: (id: string | number) => `/media/${id}`,
    },
  },

  /**
   * API response codes
   */
  responseCodes: {
    success: 200,
    created: 201,
    accepted: 202,
    noContent: 204,
    badRequest: 400,
    unauthorized: 401,
    forbidden: 403,
    notFound: 404,
    conflict: 409,
    unprocessableEntity: 422,
    tooManyRequests: 429,
    internalServerError: 500,
    serviceUnavailable: 503,
  },

  /**
   * Error messages
   */
  errorMessages: {
    network: 'Network error. Please check your internet connection.',
    timeout: 'Request timeout. Please try again.',
    unauthorized: 'Unauthorized. Please login again.',
    forbidden: 'You do not have permission to perform this action.',
    notFound: 'The requested resource was not found.',
    serverError: 'An error occurred on the server. Please try again later.',
    unknown: 'An unknown error occurred. Please try again.',
  },
} as const;

/**
 * Type for API endpoint keys
 */
export type ApiEndpoint = keyof typeof apiConfig.endpoints;

/**
 * Type for response codes
 */
export type ResponseCode = keyof typeof apiConfig.responseCodes;

/**
 * Helper to build full URL
 *
 * @param endpoint - Endpoint path
 * @returns Full URL
 *
 * @example
 * ```ts
 * buildUrl('/users') // 'http://localhost:8080/api/users'
 * ```
 */
export const buildUrl = (endpoint: string): string => {
  const baseUrl = apiConfig.baseURL.endsWith('/')
    ? apiConfig.baseURL.slice(0, -1)
    : apiConfig.baseURL;

  const path = endpoint.startsWith('/') ? endpoint : `/${endpoint}`;

  return `${baseUrl}${path}`;
};

/**
 * Helper to check if status code is successful
 *
 * @param statusCode - HTTP status code
 * @returns True if status code is 2xx
 */
export const isSuccessStatusCode = (statusCode: number): boolean => {
  return statusCode >= 200 && statusCode < 300;
};

/**
 * Helper to check if status code should trigger retry
 *
 * @param statusCode - HTTP status code
 * @returns True if should retry
 */
export const shouldRetry = (statusCode: number): boolean => {
  return apiConfig.retryStatusCodes.includes(statusCode);
};

/**
 * Re-export for convenience
 */
export default apiConfig;
