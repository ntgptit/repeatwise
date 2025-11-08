/**
 * API Error Response Types
 *
 * Standard error response format from API.
 *
 * @module api/types/error-response
 */

/**
 * API error response
 */
export interface ApiErrorResponse {
  /**
   * Error message
   */
  message: string;

  /**
   * HTTP status code
   */
  status: number;

  /**
   * Error code (application-specific)
   */
  code?: string;

  /**
   * Detailed errors (for validation)
   */
  errors?: Array<{
    field: string;
    message: string;
  }>;

  /**
   * Response timestamp
   */
  timestamp?: string;

  /**
   * Request path
   */
  path?: string;

  /**
   * Stack trace (only in development)
   */
  stack?: string;
}

/**
 * Validation error detail
 */
export interface ValidationError {
  field: string;
  message: string;
  value?: unknown;
}

/**
 * API error with validation details
 */
export interface ApiValidationErrorResponse extends ApiErrorResponse {
  errors: ValidationError[];
}
