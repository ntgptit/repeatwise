/**
 * Environment Configuration
 *
 * Centralized environment variables configuration
 * Type-safe access to environment variables with defaults
 */

/**
 * Environment type
 */
export type Environment = 'development' | 'staging' | 'production' | 'test';

/**
 * Environment configuration interface
 */
export interface EnvConfig {
  /**
   * Current environment
   */
  environment: Environment;

  /**
   * Whether in production mode
   */
  isProduction: boolean;

  /**
   * Whether in development mode
   */
  isDevelopment: boolean;

  /**
   * Whether in test mode
   */
  isTest: boolean;

  /**
   * Backend API base URL
   */
  apiBaseUrl: string;

  /**
   * API timeout in milliseconds
   */
  apiTimeout: number;

  /**
   * Enable API request logging
   */
  enableApiLogging: boolean;

  /**
   * Enable Redux DevTools
   */
  enableDevTools: boolean;

  /**
   * Enable service worker
   */
  enableServiceWorker: boolean;

  /**
   * Application name
   */
  appName: string;

  /**
   * Application version
   */
  appVersion: string;

  /**
   * Build date
   */
  buildDate: string;

  /**
   * Public URL/base path
   */
  publicUrl: string;
}

/**
 * Get environment variable with type safety
 */
const getEnvVar = (key: string, defaultValue: string = ''): string => {
  return import.meta.env[key] || process.env[key] || defaultValue;
};

/**
 * Get boolean environment variable
 */
const getBooleanEnv = (key: string, defaultValue: boolean = false): boolean => {
  const value = getEnvVar(key);
  if (!value) return defaultValue;
  return value.toLowerCase() === 'true' || value === '1';
};

/**
 * Get number environment variable
 */
const getNumberEnv = (key: string, defaultValue: number): number => {
  const value = getEnvVar(key);
  const parsed = parseInt(value, 10);
  return isNaN(parsed) ? defaultValue : parsed;
};

/**
 * Get current environment
 */
const getEnvironment = (): Environment => {
  const env = getEnvVar('VITE_APP_ENV', getEnvVar('NODE_ENV', 'development'));

  switch (env) {
    case 'production':
    case 'staging':
    case 'test':
      return env;
    default:
      return 'development';
  }
};

/**
 * Environment configuration object
 */
export const env: EnvConfig = {
  // Environment
  environment: getEnvironment(),
  isProduction: getEnvironment() === 'production',
  isDevelopment: getEnvironment() === 'development',
  isTest: getEnvironment() === 'test',

  // API Configuration
  apiBaseUrl: getEnvVar('VITE_API_BASE_URL', 'http://localhost:8080/api'),
  apiTimeout: getNumberEnv('VITE_API_TIMEOUT', 30000), // 30 seconds
  enableApiLogging: getBooleanEnv('VITE_ENABLE_API_LOGGING', true),

  // Development Tools
  enableDevTools: getBooleanEnv('VITE_ENABLE_DEV_TOOLS', getEnvironment() === 'development'),

  // PWA/Service Worker
  enableServiceWorker: getBooleanEnv('VITE_ENABLE_SERVICE_WORKER', getEnvironment() === 'production'),

  // Application Info
  appName: getEnvVar('VITE_APP_NAME', 'RepeatWise'),
  appVersion: getEnvVar('VITE_APP_VERSION', '0.1.0'),
  buildDate: getEnvVar('VITE_BUILD_DATE', new Date().toISOString()),
  publicUrl: getEnvVar('VITE_PUBLIC_URL', '/'),
};

/**
 * Validate required environment variables
 */
export const validateEnv = (): void => {
  const requiredVars: Array<keyof EnvConfig> = ['apiBaseUrl'];

  const missing = requiredVars.filter((key) => {
    const value = env[key];
    return !value || (typeof value === 'string' && value.trim() === '');
  });

  if (missing.length > 0) {
    const message = `Missing required environment variables: ${missing.join(', ')}`;
    if (env.isProduction) {
      throw new Error(message);
    } else {
      console.warn(message);
    }
  }
};

/**
 * Print environment configuration (for debugging)
 */
export const printEnv = (): void => {
  if (env.isDevelopment) {
    console.group('=== Environment Configuration ===')
    console.log('Environment:', env.environment);
    console.log('API Base URL:', env.apiBaseUrl);
    console.log('API Timeout:', env.apiTimeout);
    console.log('API Logging:', env.enableApiLogging);
    console.log('Dev Tools:', env.enableDevTools);
    console.log('Service Worker:', env.enableServiceWorker);
    console.log('App Name:', env.appName);
    console.log('App Version:', env.appVersion);
    console.log('Build Date:', env.buildDate);
    console.groupEnd();
  }
};

// Validate environment on load (only in development)
if (env.isDevelopment) {
  validateEnv();
}

export default env;
