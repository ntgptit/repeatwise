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

/**
 * Content Types
 */
export const CONTENT_TYPES = {
  JSON: 'application/json',
  FORM_DATA: 'multipart/form-data',
  FORM_URLENCODED: 'application/x-www-form-urlencoded',
  TEXT: 'text/plain',
  HTML: 'text/html',
  XML: 'application/xml',
  PDF: 'application/pdf',
  CSV: 'text/csv',
  EXCEL: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
} as const;

/**
 * Request Methods
 */
export const HTTP_METHODS = {
  GET: 'GET',
  POST: 'POST',
  PUT: 'PUT',
  PATCH: 'PATCH',
  DELETE: 'DELETE',
  HEAD: 'HEAD',
  OPTIONS: 'OPTIONS',
} as const;

/**
 * API Response Status
 */
export type ApiStatus = 'idle' | 'loading' | 'success' | 'error';

/**
 * Generic API Response Structure
 */
export interface ApiResponse<T = unknown> {
  /**
   * Response data
   */
  data: T;

  /**
   * Success flag
   */
  success: boolean;

  /**
   * Response message
   */
  message?: string;

  /**
   * Response timestamp
   */
  timestamp?: string;
}

/**
 * Generic API Error Structure
 */
export interface ApiError {
  /**
   * Error code
   */
  code: string;

  /**
   * Error message
   */
  message: string;

  /**
   * HTTP status code
   */
  statusCode: number;

  /**
   * Validation errors (field-specific)
   */
  errors?: Array<{
    field: string;
    message: string;
  }>;

  /**
   * Error timestamp
   */
  timestamp?: string;

  /**
   * Request path that caused the error
   */
  path?: string;
}

/**
 * Paginated API Response
 */
export interface PaginatedApiResponse<T> extends ApiResponse<T[]> {
  /**
   * Pagination metadata
   */
  pagination: {
    currentPage: number;
    pageSize: number;
    totalItems: number;
    totalPages: number;
    hasNext: boolean;
    hasPrevious: boolean;
  };
}

/**
 * API Request Config
 */
export interface ApiRequestConfig {
  /**
   * Request URL (relative to baseURL)
   */
  url: string;

  /**
   * HTTP method
   */
  method?: keyof typeof HTTP_METHODS;

  /**
   * Request headers
   */
  headers?: Record<string, string>;

  /**
   * Request params (query string)
   */
  params?: Record<string, string | number | boolean | undefined>;

  /**
   * Request body
   */
  data?: unknown;

  /**
   * Request timeout
   */
  timeout?: number;

  /**
   * Enable retry for this request
   */
  retry?: boolean;

  /**
   * Enable caching for this request
   */
  cache?: boolean;

  /**
   * Cancel token
   */
  signal?: AbortSignal;

  /**
   * Response type
   */
  responseType?: 'json' | 'blob' | 'text' | 'arraybuffer';

  /**
   * Upload progress callback
   */
  onUploadProgress?: (progressEvent: ProgressEvent) => void;

  /**
   * Download progress callback
   */
  onDownloadProgress?: (progressEvent: ProgressEvent) => void;
}

/**
 * Check if status code is successful (2xx)
 */
export const isSuccessStatus = (status: number): boolean => {
  return status >= 200 && status < 300;
};

/**
 * Check if status code is client error (4xx)
 */
export const isClientError = (status: number): boolean => {
  return status >= 400 && status < 500;
};

/**
 * Check if status code is server error (5xx)
 */
export const isServerError = (status: number): boolean => {
  return status >= 500 && status < 600;
};

/**
 * Check if status code should be retried
 */
export const shouldRetry = (status: number): boolean => {
  return apiConfig.retryStatusCodes.includes(status);
};

/**
 * Get error message from status code
 */
export const getErrorMessageFromStatus = (status: number): string => {
  switch (status) {
    case HTTP_STATUS.BAD_REQUEST:
      return 'Invalid request';
    case HTTP_STATUS.UNAUTHORIZED:
      return 'Authentication required';
    case HTTP_STATUS.FORBIDDEN:
      return 'Access forbidden';
    case HTTP_STATUS.NOT_FOUND:
      return 'Resource not found';
    case HTTP_STATUS.CONFLICT:
      return 'Resource conflict';
    case HTTP_STATUS.UNPROCESSABLE_ENTITY:
      return 'Validation error';
    case HTTP_STATUS.TOO_MANY_REQUESTS:
      return 'Too many requests';
    case HTTP_STATUS.INTERNAL_SERVER_ERROR:
      return 'Internal server error';
    case HTTP_STATUS.SERVICE_UNAVAILABLE:
      return 'Service unavailable';
    case HTTP_STATUS.GATEWAY_TIMEOUT:
      return 'Gateway timeout';
    default:
      return 'An error occurred';
  }
};

export default apiConfig;
