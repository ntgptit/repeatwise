/**
 * API Response Types
 *
 * Standard response format for API calls.
 *
 * @module api/types/api-response
 */

/**
 * Standard API response wrapper
 */
export interface ApiResponse<T = unknown> {
  /**
   * Response data
   */
  data: T;

  /**
   * Response message (optional)
   */
  message?: string;

  /**
   * Success status
   */
  success: boolean;

  /**
   * Response timestamp
   */
  timestamp?: string;

  /**
   * Request ID for tracking
   */
  requestId?: string;
}

/**
 * API success response
 */
export type ApiSuccessResponse<T = unknown> = ApiResponse<T> & {
  success: true;
};

/**
 * Empty API response
 */
export type ApiEmptyResponse = ApiResponse<null>;
