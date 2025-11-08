/**
 * Error Handler Service
 *
 * Centralized error handling service for the application
 * Provides error tracking, reporting, and recovery strategies
 */

import { logger } from './logger.service'
import { notificationService } from './notification.service'
import type { ErrorResponse } from '@/api/types'

/**
 * Error severity level
 */
export const ErrorSeverity = {
  LOW: 'low',
  MEDIUM: 'medium',
  HIGH: 'high',
  CRITICAL: 'critical',
} as const

export type ErrorSeverity = (typeof ErrorSeverity)[keyof typeof ErrorSeverity]

/**
 * Error category
 */
export const ErrorCategory = {
  NETWORK: 'network',
  VALIDATION: 'validation',
  AUTHENTICATION: 'authentication',
  AUTHORIZATION: 'authorization',
  NOT_FOUND: 'not_found',
  SERVER: 'server',
  CLIENT: 'client',
  UNKNOWN: 'unknown',
} as const

export type ErrorCategory = (typeof ErrorCategory)[keyof typeof ErrorCategory]

/**
 * Application error
 */
export interface AppError {
  id: string
  message: string
  code?: string
  category: ErrorCategory
  severity: ErrorSeverity
  timestamp: string
  context?: string
  stack?: string
  metadata?: Record<string, unknown>
  originalError?: unknown
}

/**
 * Error handler configuration
 */
export interface ErrorHandlerConfig {
  /**
   * Show notifications for errors
   * @default true
   */
  showNotifications: boolean

  /**
   * Log errors to console
   * @default true
   */
  logErrors: boolean

  /**
   * Send errors to external service (Sentry, etc.)
   * @default false
   */
  reportToExternalService: boolean

  /**
   * Custom external reporting callback
   */
  externalReporter?: (error: AppError) => void

  /**
   * Minimum severity to report
   * @default ErrorSeverity.MEDIUM
   */
  minReportSeverity: ErrorSeverity

  /**
   * Maximum errors to store
   * @default 50
   */
  maxStoredErrors: number
}

/**
 * Default configuration
 */
const defaultConfig: ErrorHandlerConfig = {
  showNotifications: true,
  logErrors: true,
  reportToExternalService: false,
  minReportSeverity: ErrorSeverity.MEDIUM,
  maxStoredErrors: 50,
}

/**
 * Error Handler Service Class
 */
class ErrorHandlerService {
  private config: ErrorHandlerConfig
  private errors: AppError[] = []
  private errorCount = 0

  constructor(config: Partial<ErrorHandlerConfig> = {}) {
    this.config = { ...defaultConfig, ...config }
    this.setupGlobalErrorHandlers()
  }

  /**
   * Setup global error handlers
   */
  private setupGlobalErrorHandlers(): void {
    // Handle unhandled promise rejections
    globalThis.addEventListener('unhandledrejection', event => {
      this.handleError(event.reason, {
        context: 'Unhandled Promise Rejection',
        category: ErrorCategory.UNKNOWN,
        severity: ErrorSeverity.HIGH,
      })
      event.preventDefault()
    })

    // Handle global errors
    globalThis.addEventListener('error', event => {
      this.handleError(event.error || event.message, {
        context: 'Global Error',
        category: ErrorCategory.UNKNOWN,
        severity: ErrorSeverity.HIGH,
      })
    })
  }

  /**
   * Generate unique error ID
   */
  private generateErrorId(): string {
    this.errorCount++
    return `err_${Date.now()}_${this.errorCount}`
  }

  /**
   * Determine error category from error
   */
  private determineCategory(error: unknown): ErrorCategory {
    if (!error || typeof error !== 'object') {
      return ErrorCategory.UNKNOWN
    }

    const errorObj = error as Record<string, unknown>

    const statusCategory = this.getCategoryFromStatus(errorObj)
    if (statusCategory) {
      return statusCategory
    }

    const codeCategory = this.getCategoryFromCode(errorObj)
    if (codeCategory) {
      return codeCategory
    }

    return ErrorCategory.UNKNOWN
  }

  private getCategoryFromStatus(errorObj: Record<string, unknown>): ErrorCategory | undefined {
    if (!('statusCode' in errorObj)) {
      return undefined
    }

    const statusValue = Number(errorObj['statusCode'])
    if (!Number.isFinite(statusValue)) {
      return undefined
    }

    switch (statusValue) {
      case 0:
        return ErrorCategory.NETWORK
      case 401:
        return ErrorCategory.AUTHENTICATION
      case 403:
        return ErrorCategory.AUTHORIZATION
      case 404:
        return ErrorCategory.NOT_FOUND
      default:
        if (statusValue >= 400 && statusValue < 500) {
          return ErrorCategory.CLIENT
        }

        if (statusValue >= 500) {
          return ErrorCategory.SERVER
        }

        return undefined
    }
  }

  private getCategoryFromCode(errorObj: Record<string, unknown>): ErrorCategory | undefined {
    if (!('code' in errorObj)) {
      return undefined
    }

    const { code } = errorObj
    if (typeof code !== 'string') {
      return undefined
    }

    switch (code) {
      case 'NETWORK_ERROR':
        return ErrorCategory.NETWORK
      case 'VALIDATION_ERROR':
        return ErrorCategory.VALIDATION
      case 'AUTH_ERROR':
        return ErrorCategory.AUTHENTICATION
      case 'AUTHZ_ERROR':
        return ErrorCategory.AUTHORIZATION
      default:
        return undefined
    }
  }

  /**
   * Determine error severity
   */
  private determineSeverity(
    category: ErrorCategory,
    providedSeverity?: ErrorSeverity
  ): ErrorSeverity {
    if (providedSeverity) {
      return providedSeverity
    }

    switch (category) {
      case ErrorCategory.NETWORK:
        return ErrorSeverity.HIGH
      case ErrorCategory.VALIDATION:
        return ErrorSeverity.LOW
      case ErrorCategory.AUTHENTICATION:
        return ErrorSeverity.CRITICAL
      case ErrorCategory.AUTHORIZATION:
        return ErrorSeverity.HIGH
      case ErrorCategory.NOT_FOUND:
        return ErrorSeverity.LOW
      case ErrorCategory.SERVER:
        return ErrorSeverity.CRITICAL
      case ErrorCategory.CLIENT:
        return ErrorSeverity.MEDIUM
      default:
        return ErrorSeverity.MEDIUM
    }
  }

  /**
   * Extract error message
   */
  private extractErrorMessage(error: unknown): string {
    if (typeof error === 'string') {
      return error
    }

    if (error instanceof Error) {
      return error.message
    }

    if (error && typeof error === 'object') {
      const errorObj = error as Record<string, unknown>
      if ('message' in errorObj && typeof errorObj['message'] === 'string') {
        return errorObj['message']
      }
    }

    return 'An unexpected error occurred'
  }

  /**
   * Create app error from any error type
   */
  private createAppError(
    error: unknown,
    options: {
      context?: string
      category?: ErrorCategory
      severity?: ErrorSeverity
      metadata?: Record<string, unknown>
    } = {}
  ): AppError {
    const message = this.extractErrorMessage(error)
    const category = options.category || this.determineCategory(error)
    const severity = this.determineSeverity(category, options.severity)

    const appError: AppError = {
      id: this.generateErrorId(),
      message,
      category,
      severity,
      timestamp: new Date().toISOString(),
      ...(options.context === undefined ? {} : { context: options.context }),
      ...(options.metadata === undefined ? {} : { metadata: options.metadata }),
      originalError: error,
    }

    // Extract additional properties
    if (error && typeof error === 'object') {
      const errorObj = error as Record<string, unknown>

      if ('code' in errorObj && typeof errorObj['code'] === 'string') {
        appError.code = errorObj['code']
      }

      if (error instanceof Error && error.stack) {
        appError.stack = error.stack
      }
    }

    return appError
  }

  /**
   * Store error
   */
  private storeError(error: AppError): void {
    this.errors.push(error)

    // Limit stored errors
    if (this.errors.length > this.config.maxStoredErrors) {
      this.errors.shift()
    }
  }

  /**
   * Log error
   */
  private logError(error: AppError): void {
    if (!this.config.logErrors) {
      return
    }

    const contextStr = error.context ? `[${error.context}]` : ''
    const logMessage = `${contextStr} ${error.message}`

    logger.error(logMessage, error.originalError, error.category)
  }

  /**
   * Show notification
   */
  private showNotification(error: AppError): void {
    if (!this.config.showNotifications) {
      return
    }

    // Don't show notifications for low severity errors
    if (error.severity === ErrorSeverity.LOW) {
      return
    }

    // Don't show notification for 401 (handled by auth flow)
    if (error.category === ErrorCategory.AUTHENTICATION) {
      return
    }

    notificationService.error(error.message, {
      duration: error.severity === ErrorSeverity.CRITICAL ? 10000 : 5000,
    })
  }

  /**
   * Report to external service (Sentry, etc.)
   */
  private reportToExternalService(error: AppError): void {
    if (!this.config.reportToExternalService) {
      return
    }

    // Check minimum severity
    const severityOrder = [
      ErrorSeverity.LOW,
      ErrorSeverity.MEDIUM,
      ErrorSeverity.HIGH,
      ErrorSeverity.CRITICAL,
    ]

    const errorSeverityIndex = severityOrder.indexOf(error.severity)
    const minSeverityIndex = severityOrder.indexOf(this.config.minReportSeverity)

    if (errorSeverityIndex < minSeverityIndex) {
      return
    }

    if (this.config.externalReporter) {
      this.config.externalReporter(error)
      return
    }

    logger.info('External reporting not configured. Error details:', error)
  }

  /**
   * Handle error
   */
  handleError(
    error: unknown,
    options: {
      context?: string
      category?: ErrorCategory
      severity?: ErrorSeverity
      metadata?: Record<string, unknown>
      showNotification?: boolean
    } = {}
  ): AppError {
    const appError = this.createAppError(error, options)

    this.storeError(appError)
    this.logError(appError)

    if (options.showNotification !== false) {
      this.showNotification(appError)
    }

    this.reportToExternalService(appError)

    return appError
  }

  /**
   * Handle API error response
   */
  handleApiError(error: ErrorResponse, context?: string): AppError {
    return this.handleError(error, {
      context: context || 'API Error',
      category: this.determineCategory(error),
    })
  }

  /**
   * Get all stored errors
   */
  getErrors(): AppError[] {
    return [...this.errors]
  }

  /**
   * Get errors by category
   */
  getErrorsByCategory(category: ErrorCategory): AppError[] {
    return this.errors.filter(error => error.category === category)
  }

  /**
   * Get errors by severity
   */
  getErrorsBySeverity(severity: ErrorSeverity): AppError[] {
    return this.errors.filter(error => error.severity === severity)
  }

  /**
   * Clear all errors
   */
  clearErrors(): void {
    this.errors = []
  }

  /**
   * Clear errors by category
   */
  clearErrorsByCategory(category: ErrorCategory): void {
    this.errors = this.errors.filter(error => error.category !== category)
  }

  /**
   * Export errors as JSON
   */
  exportErrors(): string {
    return JSON.stringify(this.errors, null, 2)
  }

  /**
   * Update configuration
   */
  setConfig(config: Partial<ErrorHandlerConfig>): void {
    this.config = { ...this.config, ...config }
  }
}

/**
 * Global error handler instance
 */
export const errorHandler = new ErrorHandlerService()

export default errorHandler
