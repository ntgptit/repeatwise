/**
 * Application Configuration
 *
 * General application settings and constants.
 *
 * @module config/app
 */

import { env } from './env';

/**
 * Application metadata
 */
export const appConfig = {
  /**
   * Application name
   */
  name: 'RepeatWise',

  /**
   * Application version (from package.json)
   */
  version: '0.0.0',

  /**
   * Application description
   */
  description: 'Spaced Repetition Learning System',

  /**
   * Application environment
   */
  environment: env.environment,

  /**
   * Application base URL
   */
  baseUrl: '/',

  /**
   * Application title template
   * @param pageTitle - Page title
   * @returns Full page title
   */
  titleTemplate: (pageTitle?: string) => {
    return pageTitle ? `${pageTitle} | RepeatWise` : 'RepeatWise';
  },

  /**
   * Default page title
   */
  defaultTitle: 'RepeatWise - Spaced Repetition Learning',

  /**
   * Meta description
   */
  metaDescription:
    'Master any subject with RepeatWise, a powerful spaced repetition system for effective learning',

  /**
   * Feature flags
   */
  features: {
    /**
     * Enable analytics tracking
     */
    analytics: env.enableAnalytics,

    /**
     * Enable error tracking
     */
    errorTracking: env.enableErrorTracking,

    /**
     * Enable dark mode
     */
    darkMode: true,

    /**
     * Enable i18n (internationalization)
     */
    i18n: false,

    /**
     * Enable PWA (Progressive Web App)
     */
    pwa: false,
  },

  /**
   * Application limits
   */
  limits: {
    /**
     * Maximum file upload size (in bytes)
     * Default: 5MB
     */
    maxFileSize: 5 * 1024 * 1024,

    /**
     * Maximum items per page
     */
    maxItemsPerPage: 100,

    /**
     * Minimum password length
     */
    minPasswordLength: 8,

    /**
     * Maximum deck name length
     */
    maxDeckNameLength: 100,

    /**
     * Maximum card content length
     */
    maxCardContentLength: 10000,
  },

  /**
   * UI configuration
   */
  ui: {
    /**
     * Default theme
     */
    defaultTheme: 'light' as const,

    /**
     * Enable theme persistence
     */
    persistTheme: true,

    /**
     * Default locale
     */
    defaultLocale: 'en' as const,

    /**
     * Available locales
     */
    availableLocales: ['en', 'vi'] as const,

    /**
     * Toast duration (ms)
     */
    toastDuration: 5000,

    /**
     * Debounce delay for search (ms)
     */
    searchDebounceDelay: 300,

    /**
     * Animation duration (ms)
     */
    animationDuration: 200,
  },

  /**
   * Pagination defaults
   */
  pagination: {
    /**
     * Default page size
     */
    defaultPageSize: 20,

    /**
     * Available page sizes
     */
    pageSizeOptions: [10, 20, 50, 100] as const,
  },

  /**
   * Date/Time configuration
   */
  dateTime: {
    /**
     * Default date format
     */
    defaultDateFormat: 'MMM dd, yyyy',

    /**
     * Default time format
     */
    defaultTimeFormat: 'HH:mm',

    /**
     * Default date-time format
     */
    defaultDateTimeFormat: 'MMM dd, yyyy HH:mm',

    /**
     * Relative time threshold (ms)
     * Show relative time if less than this value
     */
    relativeTimeThreshold: 7 * 24 * 60 * 60 * 1000, // 7 days
  },

  /**
   * SRS (Spaced Repetition System) configuration
   */
  srs: {
    /**
     * Initial ease factor
     */
    initialEase: 2.5,

    /**
     * Minimum ease factor
     */
    minEase: 1.3,

    /**
     * Easy bonus multiplier
     */
    easyBonus: 1.3,

    /**
     * Hard interval multiplier
     */
    hardInterval: 1.2,

    /**
     * New interval multiplier after lapse
     */
    lapseInterval: 0.5,

    /**
     * Graduating interval (days)
     */
    graduatingInterval: 1,

    /**
     * Easy interval (days)
     */
    easyInterval: 4,
  },
} as const;

/**
 * Type for app config keys
 */
export type AppConfigKey = keyof typeof appConfig;

/**
 * Type for feature flags
 */
export type FeatureFlag = keyof typeof appConfig.features;

/**
 * Type for locales
 */
export type Locale = (typeof appConfig.ui.availableLocales)[number];

/**
 * Type for themes
 */
export type Theme = 'light' | 'dark';

/**
 * Helper to check if feature is enabled
 *
 * @param feature - Feature flag name
 * @returns True if feature is enabled
 *
 * @example
 * ```ts
 * if (isFeatureEnabled('analytics')) {
 *   // Track analytics
 * }
 * ```
 */
export const isFeatureEnabled = (feature: FeatureFlag): boolean => {
  return appConfig.features[feature];
};

/**
 * Re-export for convenience
 */
export default appConfig;
