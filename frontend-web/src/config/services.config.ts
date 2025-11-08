/**
 * Services Configuration
 *
 * Configuration for third-party services and integrations.
 *
 * @module config/services
 */

import { env } from './env';

/**
 * Services configuration object
 */
export const servicesConfig = {
  /**
   * Storage service configuration
   */
  storage: {
    /**
     * Storage key prefix
     */
    keyPrefix: 'repeatwise_',

    /**
     * Default storage type
     */
    defaultType: 'localStorage' as const,

    /**
     * Storage keys
     */
    keys: {
      authToken: 'auth_token',
      refreshToken: 'refresh_token',
      user: 'user',
      theme: 'theme',
      locale: 'locale',
      recentDecks: 'recent_decks',
      reviewSettings: 'review_settings',
    },
  },

  /**
   * Analytics service configuration
   */
  analytics: {
    /**
     * Enable analytics
     */
    enabled: env.enableAnalytics,

    /**
     * Google Analytics ID
     */
    googleAnalyticsId: '',

    /**
     * Track page views
     */
    trackPageViews: true,

    /**
     * Track events
     */
    trackEvents: true,

    /**
     * Debug mode
     */
    debug: env.isDevelopment,
  },

  /**
   * Error tracking service configuration (Sentry)
   */
  errorTracking: {
    /**
     * Enable error tracking
     */
    enabled: env.enableErrorTracking,

    /**
     * Sentry DSN
     */
    sentryDsn: '',

    /**
     * Environment
     */
    environment: env.environment,

    /**
     * Sample rate (0.0 to 1.0)
     */
    sampleRate: env.isProduction ? 0.5 : 1.0,

    /**
     * Traces sample rate
     */
    tracesSampleRate: env.isProduction ? 0.1 : 1.0,

    /**
     * Enable in development
     */
    enableInDev: false,
  },

  /**
   * React Query configuration
   */
  reactQuery: {
    /**
     * Default query options
     */
    defaultOptions: {
      queries: {
        /**
         * Stale time (ms)
         * Time until query data is considered stale
         */
        staleTime: 1000 * 60 * 5, // 5 minutes

        /**
         * Cache time (ms)
         * Time until inactive query data is garbage collected
         */
        gcTime: 1000 * 60 * 30, // 30 minutes (formerly cacheTime)

        /**
         * Retry failed queries
         */
        retry: 1,

        /**
         * Retry delay (ms)
         */
        retryDelay: (attemptIndex: number) => Math.min(1000 * 2 ** attemptIndex, 30000),

        /**
         * Refetch on window focus
         */
        refetchOnWindowFocus: false,

        /**
         * Refetch on reconnect
         */
        refetchOnReconnect: true,

        /**
         * Refetch on mount
         */
        refetchOnMount: true,
      },
      mutations: {
        /**
         * Retry failed mutations
         */
        retry: 0,
      },
    },

    /**
     * DevTools configuration
     */
    devtools: {
      /**
       * Enable React Query DevTools
       */
      enabled: env.isDevelopment,

      /**
       * Initial is open
       */
      initialIsOpen: false,

      /**
       * Position
       */
      position: 'bottom-right' as const,
    },
  },

  /**
   * Notification service configuration (Toasts)
   */
  notification: {
    /**
     * Default position
     */
    position: 'top-right' as const,

    /**
     * Default duration (ms)
     */
    duration: 5000,

    /**
     * Maximum toasts to show
     */
    maxToasts: 5,

    /**
     * Enable sound
     */
    enableSound: false,

    /**
     * Enable animations
     */
    enableAnimations: true,
  },

  /**
   * Logging service configuration
   */
  logging: {
    /**
     * Enable logging
     */
    enabled: env.isDevelopment,

    /**
     * Log level
     */
    level: env.isDevelopment ? 'debug' : 'error',

    /**
     * Log to console
     */
    logToConsole: true,

    /**
     * Include timestamps
     */
    includeTimestamp: true,

    /**
     * Include stack traces for errors
     */
    includeStackTrace: env.isDevelopment,
  },
} as const;

/**
 * Type for storage keys
 */
export type StorageKey = keyof typeof servicesConfig.storage.keys;

/**
 * Type for notification positions
 */
export type NotificationPosition =
  | 'top-left'
  | 'top-center'
  | 'top-right'
  | 'bottom-left'
  | 'bottom-center'
  | 'bottom-right';

/**
 * Type for log levels
 */
export type LogLevel = 'debug' | 'info' | 'warn' | 'error';

/**
 * Helper to get storage key with prefix
 *
 * @param key - Storage key
 * @returns Prefixed storage key
 *
 * @example
 * ```ts
 * getStorageKey('authToken') // 'repeatwise_auth_token'
 * ```
 */
export const getStorageKey = (key: StorageKey): string => {
  return `${servicesConfig.storage.keyPrefix}${servicesConfig.storage.keys[key]}`;
};

/**
 * Re-export for convenience
 */
export default servicesConfig;
