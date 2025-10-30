import type { AxiosError, InternalAxiosRequestConfig } from 'axios'
import type { ApiRequestMetadata } from '@/api/types/api-response'

/**
 * Logging Interceptor
 * Centralized logging for all HTTP requests and responses
 */
export class LoggingInterceptor {
  private isDevelopment = import.meta.env.DEV

  /**
   * Log request
   */
  logRequest(config: InternalAxiosRequestConfig): ApiRequestMetadata {
    const metadata: ApiRequestMetadata = {
      url: config.url || '',
      method: config.method?.toUpperCase() || 'UNKNOWN',
      timestamp: Date.now(),
      requestId: config.headers['X-Request-ID'] as string,
    }

    if (this.isDevelopment) {
      // eslint-disable-next-line no-console
      console.group(`🚀 [${metadata.method}] ${metadata.url}`)
      // eslint-disable-next-line no-console
      console.warn('Request ID:', metadata.requestId)
      // eslint-disable-next-line no-console
      console.warn('Headers:', config.headers)
      if (config.data) {
        // eslint-disable-next-line no-console
        console.warn('Body:', config.data)
      }
      // eslint-disable-next-line no-console
      console.groupEnd()
    }

    return metadata
  }

  /**
   * Log response
   */
  logResponse(
    metadata: ApiRequestMetadata,
    response: unknown,
  ): ApiRequestMetadata {
    const duration = Date.now() - metadata.timestamp
    metadata.duration = duration

    if (this.isDevelopment) {
      // eslint-disable-next-line no-console
      console.group(`✅ [${metadata.method}] ${metadata.url}`)
      // eslint-disable-next-line no-console
      console.warn('Request ID:', metadata.requestId)
      // eslint-disable-next-line no-console
      console.warn('Duration:', `${duration}ms`)
      // eslint-disable-next-line no-console
      console.warn('Response:', response)
      // eslint-disable-next-line no-console
      console.groupEnd()
    }

    return metadata
  }

  /**
   * Log error
   */
  logError(metadata: ApiRequestMetadata, error: unknown): void {
    const duration = Date.now() - metadata.timestamp
    metadata.duration = duration

    if (this.isDevelopment) {
      // eslint-disable-next-line no-console
      console.group(`❌ [${metadata.method}] ${metadata.url}`)
      // eslint-disable-next-line no-console
      console.warn('Request ID:', metadata.requestId)
      // eslint-disable-next-line no-console
      console.warn('Duration:', `${duration}ms`)
      console.error('Error:', error)
      // eslint-disable-next-line no-console
      console.groupEnd()
    }

    // In production, you might want to send errors to logging service
    if (!this.isDevelopment) {
      this.sendToLoggingService(metadata, error)
    }
  }

  /**
   * Send error to logging service (e.g., Sentry, LogRocket)
   */
  private sendToLoggingService(
    metadata: ApiRequestMetadata,
    error: unknown,
  ): void {
    // TODO: Implement logging service integration
    // Example: Sentry.captureException(error, { extra: metadata })
  }
}

// Export singleton instance
export const loggingInterceptor = new LoggingInterceptor()
