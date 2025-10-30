import type { AxiosError } from 'axios'
import type { ApiError } from '@/api/types/api-response'

/**
 * Enhanced Error Handler
 * Provides type-safe error handling utilities
 */
export class ApiErrorHandler {
  /**
   * Check if error is an API error
   */
  static isApiError(error: unknown): error is ApiError {
    return (
      typeof error === 'object' &&
      error !== null &&
      'message' in error &&
      typeof (error as ApiError).message === 'string'
    )
  }

  /**
   * Extract error from unknown error type
   */
  static extractError(error: unknown): ApiError {
    if (this.isApiError(error)) {
      return error
    }

    if (this.isAxiosError(error)) {
      const apiError: ApiError = {
        message:
          error.response?.data?.message ||
          error.message ||
          'An error occurred',
      }

      if (error.response?.data?.code) {
        apiError.code = error.response.data.code
      }

      if (error.response?.status) {
        apiError.status = error.response.status
      }

      if (error.response?.data?.errors) {
        apiError.errors = error.response.data.errors
      }

      if (error.response?.data?.timestamp) {
        apiError.timestamp = error.response.data.timestamp
      }

      if (error.config?.url) {
        apiError.path = error.config.url
      }

      return apiError
    }

    if (error instanceof Error) {
      return {
        message: error.message,
      }
    }

    return {
      message: 'An unexpected error occurred',
    }
  }

  /**
   * Check if error is axios error
   */
  private static isAxiosError(
    error: unknown,
  ): error is AxiosError<ApiError> {
    return (
      typeof error === 'object' &&
      error !== null &&
      'isAxiosError' in error &&
      (error as AxiosError).isAxiosError === true
    )
  }

  /**
   * Get error message
   */
  static getErrorMessage(error: unknown): string {
    const apiError = this.extractError(error)
    return apiError.message || 'An error occurred'
  }

  /**
   * Get error status code
   */
  static getErrorStatus(error: unknown): number | undefined {
    const apiError = this.extractError(error)
    return apiError.status
  }

  /**
   * Get validation errors
   */
  static getValidationErrors(error: unknown): Record<string, string[]> {
    const apiError = this.extractError(error)
    return apiError.errors || {}
  }
}
