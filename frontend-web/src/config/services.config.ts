/**
 * Services Configuration
 *
 * Configuration for external services and integrations
 * Analytics, monitoring, third-party APIs
 */

import { env } from './env';

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
 * All services configuration
 */
export const servicesConfig = {
  notification: notificationConfig,
  storage: storageConfig,
  logger: loggerConfig,
} as const;

export default servicesConfig;
