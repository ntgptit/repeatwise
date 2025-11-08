/**
 * Error Handler Service
 *
 * Centralized error handling service.
 * Handles logging, reporting, and user-friendly error messages.
 *
 * @module common/services/error-handler
 */

import { logger } from './logger.service';
import { notificationService } from './notification.service';
import { servicesConfig } from '@/config';

/**
 * Error severity level
 */
export type ErrorSeverity = 'low' | 'medium' | 'high' | 'critical';

/**
 * Error context interface
 */
export interface ErrorContext {
  /**
   * Component or module where error occurred
   */
  component?: string;

  /**
   * User action that triggered the error
   */
  action?: string;

  /**
   * Additional metadata
   */
  metadata?: Record<string, unknown>;

  /**
   * Error severity
   */
  severity?: ErrorSeverity;

  /**
   * Whether to show notification to user
   */
  showNotification?: boolean;

  /**
   * Whether to send to error tracking service (e.g., Sentry)
   */
  sendToTracker?: boolean;
}

/**
 * Error Handler Service class
 */
class ErrorHandlerService {
  private errorTrackingEnabled: boolean;

  constructor() {
    this.errorTrackingEnabled = servicesConfig.errorTracking.enabled;
  }

  /**
   * Handle error
   *
   * @param error - Error object or message
   * @param context - Error context
   *
   * @example
   * ```ts
   * errorHandler.handle(error, {
   *   component: 'UserProfile',
   *   action: 'saveProfile',
   *   severity: 'high',
   *   showNotification: true,
   * });
   * ```
   */
  handle(error: Error | string, context: ErrorContext = {}): void {
    const errorObj = typeof error === 'string' ? new Error(error) : error;
    const {
      component,
      action,
      metadata,
      severity = 'medium',
      showNotification = true,
      sendToTracker = true,
    } = context;

    // Log error
    logger.error(errorObj.message, errorObj, {
      component,
      action,
      severity,
      ...metadata,
    });

    // Show notification to user
    if (showNotification) {
      const userMessage = this.getUserFriendlyMessage(errorObj, severity);
      notificationService.error(userMessage, {
        description: component ? `in ${component}` : undefined,
      });
    }

    // Send to error tracking service
    if (sendToTracker && this.errorTrackingEnabled) {
      this.sendToTracker(errorObj, context);
    }
  }

  /**
   * Handle API error
   *
   * @param error - Error from API call
   * @param context - Error context
   *
   * @example
   * ```ts
   * try {
   *   await api.post('/users', data);
   * } catch (error) {
   *   errorHandler.handleApiError(error, { action: 'createUser' });
   * }
   * ```
   */
  handleApiError(error: unknown, context: ErrorContext = {}): void {
    // Extract error message from different error formats
    let message = 'An error occurred';
    let statusCode: number | undefined;

    if (error instanceof Error) {
      message = error.message;
    }

    // Handle axios error
    if (typeof error === 'object' && error !== null && 'response' in error) {
      const response = (error as { response?: { status?: number; data?: { message?: string } } })
        .response;
      statusCode = response?.status;
      message = response?.data?.message || message;
    }

    this.handle(message, {
      ...context,
      severity: this.getApiErrorSeverity(statusCode),
      metadata: {
        ...context.metadata,
        statusCode,
      },
    });
  }

  /**
   * Handle async function with error handling
   *
   * @param fn - Async function to execute
   * @param context - Error context
   * @returns Function result or null if error
   *
   * @example
   * ```ts
   * const result = await errorHandler.handleAsync(
   *   () => api.getUser(id),
   *   { action: 'fetchUser' }
   * );
   * ```
   */
  async handleAsync<T>(
    fn: () => Promise<T>,
    context: ErrorContext = {}
  ): Promise<T | null> {
    try {
      return await fn();
    } catch (error) {
      this.handle(error as Error, context);
      return null;
    }
  }

  /**
   * Get user-friendly error message
   */
  private getUserFriendlyMessage(error: Error, severity: ErrorSeverity): string {
    // Map common technical errors to user-friendly messages
    const messageMap: Record<string, string> = {
      'Network Error': 'Unable to connect. Please check your internet connection.',
      'Request failed with status code 401': 'Your session has expired. Please log in again.',
      'Request failed with status code 403': 'You do not have permission to perform this action.',
      'Request failed with status code 404': 'The requested resource was not found.',
      'Request failed with status code 500': 'A server error occurred. Please try again later.',
      'Request timeout': 'The request took too long. Please try again.',
    };

    const userMessage = messageMap[error.message];

    if (userMessage) {
      return userMessage;
    }

    // For critical errors, show generic message
    if (severity === 'critical' || severity === 'high') {
      return 'An unexpected error occurred. Please try again or contact support.';
    }

    // For other errors, show actual message (cleaned up)
    return this.cleanErrorMessage(error.message);
  }

  /**
   * Clean up technical error message
   */
  private cleanErrorMessage(message: string): string {
    // Remove technical details
    return message
      .replace(/Request failed with status code \d+:?\s*/i, '')
      .replace(/Error:\s*/i, '')
      .trim();
  }

  /**
   * Get error severity from HTTP status code
   */
  private getApiErrorSeverity(statusCode?: number): ErrorSeverity {
    if (!statusCode) return 'medium';

    if (statusCode >= 500) return 'high';
    if (statusCode === 401 || statusCode === 403) return 'medium';
    if (statusCode === 404) return 'low';
    if (statusCode >= 400) return 'medium';

    return 'low';
  }

  /**
   * Send error to tracking service (e.g., Sentry)
   */
  private sendToTracker(error: Error, context: ErrorContext): void {
    // TODO: Implement Sentry or other error tracking integration
    // For now, just log that we would send it
    logger.debug('Would send error to tracker', {
      error: error.message,
      context,
    });

    // Example Sentry integration (when implemented):
    // Sentry.captureException(error, {
    //   level: context.severity,
    //   tags: {
    //     component: context.component,
    //     action: context.action,
    //   },
    //   extra: context.metadata,
    // });
  }

  /**
   * Enable error tracking
   */
  enableTracking(): void {
    this.errorTrackingEnabled = true;
  }

  /**
   * Disable error tracking
   */
  disableTracking(): void {
    this.errorTrackingEnabled = false;
  }
}

/**
 * Singleton instance of ErrorHandlerService
 */
export const errorHandler = new ErrorHandlerService();

/**
 * Export class for testing purposes
 */
export { ErrorHandlerService };

/**
 * Re-export as default
 */
export default errorHandler;
