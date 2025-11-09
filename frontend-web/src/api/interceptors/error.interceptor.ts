/**
 * Error Interceptor
 *
 * Handles API errors and transforms them into a consistent format
 */

import type { AxiosInstance, AxiosError } from 'axios'
import type { ErrorResponse, ValidationError } from '@/api/types'
import { notificationService } from '@/common/services'

/**
 * Error interceptor configuration
 */
export interface ErrorInterceptorConfig {
  /**
   * Show error notifications automatically
   * @default true
   */
  showNotifications?: boolean

  /**
   * Custom error handler
   */
  onError?: (error: ErrorResponse) => void

  /**
   * Status codes to ignore (no notification)
   */
  ignoreStatusCodes?: number[]

  /**
   * Enable error logging to console
   * @default true
   */
  enableLogging?: boolean
}

/**
 * Default configuration
 */
const defaultConfig: Required<ErrorInterceptorConfig> = {
  showNotifications: true,
  onError: () => {
    /* empty */
  },
  ignoreStatusCodes: [401], // Don't show notification for unauthorized
  enableLogging: true,
}

/**
 * Transform axios error to API error response
 */
const transformError = (error: AxiosError): ErrorResponse => {
  const { response } = error

  // Network error (no response)
  if (!response) {
    return {
      message: 'Network error. Please check your connection.',
      code: 'NETWORK_ERROR',
      statusCode: 0,
      timestamp: new Date().toISOString(),
      path: error.config?.url || '',
    }
  }

  // Server returned error response
  const data = response.data as Record<string, unknown> | undefined

  const result: ErrorResponse = {
    message: (data?.['message'] as string) || error.message || 'An unexpected error occurred',
    code: (data?.['errorCode'] as string) || (data?.['code'] as string) || 'UNKNOWN_ERROR',
    statusCode: response.status,
    timestamp: (data?.['timestamp'] as string) || new Date().toISOString(),
    path: (data?.['path'] as string) || error.config?.url || '',
  }

  // Transform validation errors from backend Map<String, String> to ValidationError[]
  const validationErrorsMap = data?.['validationErrors'] as Record<string, string> | undefined
  if (validationErrorsMap) {
    result.errors = Object.entries(validationErrorsMap).map(([field, message]) => ({
      field,
      message,
    }))
  }

  // Also handle errors array if present (for compatibility)
  const errorsArray = data?.['errors'] as ValidationError[] | undefined
  if (errorsArray && !result.errors) {
    result.errors = errorsArray
  }

  return result
}

/**
 * Get user-friendly error message based on status code
 */
const getErrorMessage = (statusCode: number, defaultMessage: string): string => {
  switch (statusCode) {
    case 400:
      return 'Invalid request. Please check your input.'
    case 401:
      return 'Authentication required. Please login.'
    case 403:
      return 'Access denied. You do not have permission.'
    case 404:
      return 'Resource not found.'
    case 409:
      return 'Conflict. The resource already exists or is in use.'
    case 422:
      return 'Validation failed. Please check your input.'
    case 429:
      return 'Too many requests. Please try again later.'
    case 500:
      return 'Server error. Please try again later.'
    case 502:
      return 'Bad gateway. The server is temporarily unavailable.'
    case 503:
      return 'Service unavailable. Please try again later.'
    case 504:
      return 'Gateway timeout. The server is taking too long to respond.'
    default:
      return defaultMessage
  }
}

/**
 * Log error to console
 */
const logError = (error: ErrorResponse): void => {
  const lines = [
    `[API Error] ${error.code}`,
    `Status: ${error.statusCode}`,
    `Message: ${error.message}`,
    `Path: ${error.path ?? 'N/A'}`,
    `Timestamp: ${error.timestamp ?? 'N/A'}`,
  ]

  if (error.errors && error.errors.length > 0) {
    lines.push(`Validation Errors: ${JSON.stringify(error.errors)}`)
  }

  console.error(lines.join('\n'))
}

/**
 * Show error notification
 */
const showNotification = (error: ErrorResponse): void => {
  const message = getErrorMessage(error.statusCode, error.message)

  // Show validation errors if available
  if (error.errors && error.errors.length > 0) {
    const validationMessages = error.errors
      .map((err: ValidationError) => `${err.field}: ${err.message}`)
      .join('\n')
    notificationService.error(`${message}\n\n${validationMessages}`)
    return
  }

  notificationService.error(message)
}

/**
 * Setup error interceptor
 */
export const errorInterceptor = (
  instance: AxiosInstance,
  config: ErrorInterceptorConfig = {}
): void => {
  const cfg = { ...defaultConfig, ...config }

  instance.interceptors.response.use(
    response => response,
    (error: AxiosError) => {
      // Transform error
      const apiError = transformError(error)

      // Log error
      if (cfg.enableLogging) {
        logError(apiError)
      }

      // Show notification
      if (cfg.showNotifications && !cfg.ignoreStatusCodes.includes(apiError.statusCode)) {
        showNotification(apiError)
      }

      // Call custom error handler
      if (cfg.onError) {
        cfg.onError(apiError)
      }

      // Reject with transformed error
      throw apiError
    }
  )
}

/**
 * Create error interceptor with specific configuration
 */
export const createErrorInterceptor = (config: ErrorInterceptorConfig) => {
  return (instance: AxiosInstance) => errorInterceptor(instance, config)
}

export default errorInterceptor
