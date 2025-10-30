/**
 * Enhanced API Response Types with Generics
 */
export interface ApiResponse<T = unknown> {
  success: boolean
  data: T
  message?: string
  timestamp?: string
}

export interface ApiError {
  message: string
  code?: string
  status?: number
  errors?: Record<string, string[]>
  timestamp?: string
  path?: string
}

export interface ApiRequestConfig {
  /** Skip adding auth token (for public endpoints) */
  skipAuth?: boolean
  /** Skip global error handler (for custom error handling) */
  skipErrorHandler?: boolean
  /** Skip logging (for sensitive requests) */
  skipLogging?: boolean
  /** Enable retry on failure */
  retry?: boolean
  /** Custom timeout in milliseconds */
  timeout?: number
  /** Custom headers */
  headers?: Record<string, string>
}

export enum HttpStatus {
  OK = 200,
  CREATED = 201,
  NO_CONTENT = 204,
  BAD_REQUEST = 400,
  UNAUTHORIZED = 401,
  FORBIDDEN = 403,
  NOT_FOUND = 404,
  CONFLICT = 409,
  UNPROCESSABLE_ENTITY = 422,
  INTERNAL_SERVER_ERROR = 500,
  SERVICE_UNAVAILABLE = 503,
}

/**
 * API Request metadata for logging
 */
export interface ApiRequestMetadata {
  url: string
  method: string
  timestamp: number
  requestId: string
  duration?: number
}