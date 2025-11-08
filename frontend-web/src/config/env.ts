/**
 * Environment Configuration
 *
 * Centralized environment variable access with type safety.
 * All environment variables must be prefixed with VITE_ to be exposed to the client.
 *
 * @module config/env
 */

/**
 * Environment variable interface
 */
interface ImportMetaEnv {
  /**
   * API base URL
   */
  readonly VITE_API_BASE_URL: string;

  /**
   * Current environment (development, staging, production)
   */
  readonly VITE_ENV: 'development' | 'staging' | 'production';

  /**
   * Enable analytics tracking
   */
  readonly VITE_ENABLE_ANALYTICS: string;

  /**
   * Enable error tracking (Sentry, etc.)
   */
  readonly VITE_ENABLE_ERROR_TRACKING: string;

  /**
   * App mode (development or production)
   */
  readonly MODE: string;

  /**
   * Is development mode
   */
  readonly DEV: boolean;

  /**
   * Is production mode
   */
  readonly PROD: boolean;
}

/**
 * Extend ImportMeta interface
 */
interface ImportMeta {
  readonly env: ImportMetaEnv;
}

/**
 * Helper to safely get environment variable
 *
 * @param key - Environment variable key
 * @param defaultValue - Default value if not found
 * @returns Environment variable value or default
 */
const getEnvVar = <T = string>(key: keyof ImportMetaEnv, defaultValue?: T): T => {
  const value = import.meta.env[key];

  if (value === undefined) {
    if (defaultValue !== undefined) {
      return defaultValue;
    }
    throw new Error(`Environment variable ${key} is not defined`);
  }

  return value as T;
};

/**
 * Helper to get boolean environment variable
 *
 * @param key - Environment variable key
 * @param defaultValue - Default boolean value
 * @returns Boolean value
 */
const getBooleanEnvVar = (key: keyof ImportMetaEnv, defaultValue = false): boolean => {
  const value = import.meta.env[key];

  if (value === undefined) {
    return defaultValue;
  }

  return value === 'true' || value === '1';
};

/**
 * Environment configuration object
 */
export const env = {
  /**
   * API base URL
   */
  apiBaseUrl: getEnvVar('VITE_API_BASE_URL', 'http://localhost:8080/api'),

  /**
   * Current environment
   */
  environment: getEnvVar<'development' | 'staging' | 'production'>(
    'VITE_ENV',
    'development'
  ),

  /**
   * Enable analytics
   */
  enableAnalytics: getBooleanEnvVar('VITE_ENABLE_ANALYTICS', false),

  /**
   * Enable error tracking
   */
  enableErrorTracking: getBooleanEnvVar('VITE_ENABLE_ERROR_TRACKING', false),

  /**
   * Is development mode
   */
  isDevelopment: import.meta.env.DEV,

  /**
   * Is production mode
   */
  isProduction: import.meta.env.PROD,

  /**
   * App mode
   */
  mode: import.meta.env.MODE,
} as const;

/**
 * Type for environment keys
 */
export type EnvKey = keyof typeof env;

/**
 * Validate required environment variables
 */
export const validateEnv = (): void => {
  const requiredVars: Array<keyof ImportMetaEnv> = ['VITE_API_BASE_URL'];

  for (const varName of requiredVars) {
    if (!import.meta.env[varName]) {
      console.warn(`Warning: Required environment variable ${varName} is not set`);
    }
  }
};

/**
 * Re-export for convenience
 */
export default env;
