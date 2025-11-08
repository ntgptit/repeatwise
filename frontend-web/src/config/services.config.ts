/**
 * Services Configuration
 *
 * Configuration for external services and integrations
 * Analytics, monitoring, third-party APIs
 */

import { env } from './env';

/**
 * Analytics Configuration
 */
export const analyticsConfig = {
  enabled: env.isProduction,
  googleAnalyticsId: '', // Add GA ID when available
  debug: env.isDevelopment,
} as const;

/**
 * Sentry/Error Tracking Configuration
 */
export const sentryConfig = {
  enabled: env.isProduction,
  dsn: '', // Add Sentry DSN when available
  environment: env.environment,
  tracesSampleRate: env.isProduction ? 0.1 : 1.0,
  debug: env.isDevelopment,
} as const;

/**
 * Feature Flags Configuration
 */
export const featureFlagsConfig = {
  enabled: false,
  apiKey: '', // Add feature flags service API key
  environment: env.environment,
} as const;

/**
 * Notification Service Configuration
 */
export const notificationConfig = {
  // Toast notifications
  toast: {
    position: 'top-right' as const,
    duration: 3000, // 3 seconds
    maxToasts: 3,
  },

  // Push notifications (future)
  push: {
    enabled: false,
    vapidPublicKey: '',
  },
} as const;

/**
 * Storage Service Configuration
 */
export const storageConfig = {
  // LocalStorage
  localStorage: {
    prefix: 'repeatwise_',
    enabled: true,
  },

  // SessionStorage
  sessionStorage: {
    prefix: 'repeatwise_session_',
    enabled: true,
  },

  // IndexedDB (future)
  indexedDB: {
    name: 'repeatwise_db',
    version: 1,
    enabled: false,
  },
} as const;

/**
 * Logger Service Configuration
 */
export const loggerConfig = {
  enabled: true,
  level: env.isDevelopment ? 'debug' : 'warn',
  enableConsole: env.isDevelopment,
  enableRemote: env.isProduction,
  remoteEndpoint: '', // Add remote logging endpoint
} as const;

/**
 * Cache Service Configuration
 */
export const cacheConfig = {
  enabled: true,
  defaultTTL: 5 * 60 * 1000, // 5 minutes
  maxSize: 100, // Max cache entries
  strategy: 'lru' as const, // Least Recently Used
} as const;

/**
 * i18n Configuration
 */
export const i18nConfig = {
  defaultLanguage: 'en',
  supportedLanguages: ['en', 'vi'],
  fallbackLanguage: 'en',
  autoDetect: true,
} as const;

/**
 * Theme Configuration
 */
export const themeConfig = {
  defaultTheme: 'light' as const,
  supportedThemes: ['light', 'dark'] as const,
  autoDetectSystemTheme: true,
} as const;

/**
 * Export/Import Service Configuration
 */
export const exportImportConfig = {
  maxFileSize: 50 * 1024 * 1024, // 50MB
  supportedFormats: ['csv', 'xlsx'],
  chunkSize: 1000, // Process in chunks of 1000 rows
} as const;

/**
 * All services configuration
 */
export const servicesConfig = {
  analytics: analyticsConfig,
  sentry: sentryConfig,
  featureFlags: featureFlagsConfig,
  notification: notificationConfig,
  storage: storageConfig,
  logger: loggerConfig,
  cache: cacheConfig,
  i18n: i18nConfig,
  theme: themeConfig,
  exportImport: exportImportConfig,
} as const;

export default servicesConfig;
