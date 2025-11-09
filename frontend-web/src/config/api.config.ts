/**
 * API Configuration
 *
 * HTTP client configuration for API requests
 * Axios instance setup, interceptors, retry logic
 */

import { env } from './env';

/**
 * API Configuration Interface
 */
export interface ApiConfig {
  /**
   * Base URL for API requests
   */
  baseURL: string;

  /**
   * Request timeout in milliseconds
   */
  timeout: number;

  /**
   * Request headers
   */
  headers: Record<string, string>;

  /**
   * Enable request/response logging
   */
  enableLogging: boolean;

  /**
   * Enable retry on failure
   */
  enableRetry: boolean;

  /**
   * Maximum retry attempts
   */
  maxRetries: number;

  /**
   * Retry delay in milliseconds
   */
  retryDelay: number;

  /**
   * Status codes to retry on
   */
  retryStatusCodes: number[];

  /**
   * Enable request cancellation
   */
  enableCancellation: boolean;

  /**
   * Enable response caching
   */
  enableCaching: boolean;

  /**
   * Cache duration in milliseconds
   */
  cacheDuration: number;
}

/**
 * Default API configuration
 */
export const apiConfig: ApiConfig = {
  baseURL: env.apiBaseUrl,
  timeout: env.apiTimeout,
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json',
  },
  enableLogging: env.enableApiLogging,
  enableRetry: true,
  maxRetries: 3,
  retryDelay: 1000, // 1 second
  retryStatusCodes: [408, 429, 500, 502, 503, 504], // Retry on these status codes
  enableCancellation: true,
  enableCaching: false,
  cacheDuration: 5 * 60 * 1000, // 5 minutes
};

/**
 * API Error Codes
 */
export const API_ERROR_CODES = {
  // Authentication errors (401)
  INVALID_CREDENTIALS: 'INVALID_CREDENTIALS',
  TOKEN_EXPIRED: 'TOKEN_EXPIRED',
  TOKEN_INVALID: 'TOKEN_INVALID',
  UNAUTHORIZED: 'UNAUTHORIZED',

  // Authorization errors (403)
  FORBIDDEN: 'FORBIDDEN',
  INSUFFICIENT_PERMISSIONS: 'INSUFFICIENT_PERMISSIONS',

  // Validation errors (400)
  VALIDATION_ERROR: 'VALIDATION_ERROR',
  INVALID_REQUEST: 'INVALID_REQUEST',
  MISSING_REQUIRED_FIELD: 'MISSING_REQUIRED_FIELD',

  // Resource errors (404)
  NOT_FOUND: 'NOT_FOUND',
  RESOURCE_NOT_FOUND: 'RESOURCE_NOT_FOUND',

  // Conflict errors (409)
  CONFLICT: 'CONFLICT',
  DUPLICATE_RESOURCE: 'DUPLICATE_RESOURCE',
  USERNAME_TAKEN: 'USERNAME_TAKEN',
  EMAIL_TAKEN: 'EMAIL_TAKEN',

  // Server errors (500+)
  INTERNAL_SERVER_ERROR: 'INTERNAL_SERVER_ERROR',
  SERVICE_UNAVAILABLE: 'SERVICE_UNAVAILABLE',
  GATEWAY_TIMEOUT: 'GATEWAY_TIMEOUT',

  // Network errors
  NETWORK_ERROR: 'NETWORK_ERROR',
  TIMEOUT_ERROR: 'TIMEOUT_ERROR',
  CONNECTION_ERROR: 'CONNECTION_ERROR',

  // Client errors
  CANCELLED: 'CANCELLED',
  UNKNOWN_ERROR: 'UNKNOWN_ERROR',
} as const;

/**
 * HTTP Status Codes
 */
export const HTTP_STATUS = {
  // Success
  OK: 200,
  CREATED: 201,
  ACCEPTED: 202,
  NO_CONTENT: 204,

  // Redirection
  MOVED_PERMANENTLY: 301,
  FOUND: 302,
  NOT_MODIFIED: 304,

  // Client Errors
  BAD_REQUEST: 400,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  METHOD_NOT_ALLOWED: 405,
  CONFLICT: 409,
  UNPROCESSABLE_ENTITY: 422,
  TOO_MANY_REQUESTS: 429,

  // Server Errors
  INTERNAL_SERVER_ERROR: 500,
  NOT_IMPLEMENTED: 501,
  BAD_GATEWAY: 502,
  SERVICE_UNAVAILABLE: 503,
  GATEWAY_TIMEOUT: 504,
} as const;

export default apiConfig;
