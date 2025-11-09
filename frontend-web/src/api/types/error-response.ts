/**
 * API Error Response Types
 *
 * Standard error structure from backend API
 */

/**
 * Validation error detail
 */
export interface ValidationError {
  /**
   * Field name
   */
  field: string

  /**
   * Error message
   */
  message: string

  /**
   * Rejected value (optional)
   */
  rejectedValue?: unknown
}

/**
 * API error response
 */
export interface ErrorResponse {
  /**
   * Error code
   */
  code: string

  /**
   * Error message
   */
  message: string

  /**
   * HTTP status code
   */
  statusCode: number

  /**
   * Validation errors (field-specific)
   */
  errors?: ValidationError[]

  /**
   * Error timestamp
   */
  timestamp?: string

  /**
   * Request path that caused the error
   */
  path?: string

  /**
   * Stack trace (only in development)
   */
  stack?: string
}

/**
 * Type guard for ErrorResponse
 */
export const isErrorResponse = (value: unknown): value is ErrorResponse => {
  if (!value || typeof value !== 'object') {
    return false
  }

  const error = value as Record<string, unknown>

  return (
    typeof error['code'] === 'string' &&
    typeof error['message'] === 'string' &&
    typeof error['statusCode'] === 'number'
  )
}
