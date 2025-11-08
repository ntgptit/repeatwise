/**
 * Logger Service
 *
 * Centralized logging service with configurable log levels.
 * Provides structured logging with timestamps and context.
 *
 * @module common/services/logger
 */

import { servicesConfig } from '@/config';

/**
 * Log level type
 */
export type LogLevel = 'debug' | 'info' | 'warn' | 'error';

/**
 * Log entry interface
 */
export interface LogEntry {
  level: LogLevel;
  message: string;
  timestamp: Date;
  context?: Record<string, unknown>;
  error?: Error;
}

/**
 * Logger Service class
 */
class LoggerService {
  private enabled: boolean;
  private logLevel: LogLevel;
  private includeTimestamp: boolean;
  private includeStackTrace: boolean;

  constructor() {
    const config = servicesConfig.logging;
    this.enabled = config.enabled;
    this.logLevel = config.level as LogLevel;
    this.includeTimestamp = config.includeTimestamp;
    this.includeStackTrace = config.includeStackTrace;
  }

  /**
   * Check if log level should be logged
   */
  private shouldLog(level: LogLevel): boolean {
    if (!this.enabled) {
      return false;
    }

    const levels: LogLevel[] = ['debug', 'info', 'warn', 'error'];
    const currentLevelIndex = levels.indexOf(this.logLevel);
    const messageLevelIndex = levels.indexOf(level);

    return messageLevelIndex >= currentLevelIndex;
  }

  /**
   * Format log message
   */
  private formatMessage(entry: LogEntry): string {
    const parts: string[] = [];

    if (this.includeTimestamp) {
      parts.push(`[${entry.timestamp.toISOString()}]`);
    }

    parts.push(`[${entry.level.toUpperCase()}]`);
    parts.push(entry.message);

    return parts.join(' ');
  }

  /**
   * Log entry
   */
  private log(entry: LogEntry): void {
    if (!this.shouldLog(entry.level)) {
      return;
    }

    const message = this.formatMessage(entry);
    const consoleMethod = entry.level === 'debug' ? 'log' : entry.level;

    if (entry.context || entry.error) {
      console[consoleMethod](message, {
        ...(entry.context && { context: entry.context }),
        ...(entry.error && { error: entry.error }),
        ...(this.includeStackTrace && entry.error && { stack: entry.error.stack }),
      });
    } else {
      console[consoleMethod](message);
    }
  }

  /**
   * Log debug message
   *
   * @param message - Debug message
   * @param context - Optional context data
   *
   * @example
   * ```ts
   * logger.debug('User action', { userId: 123, action: 'click' });
   * ```
   */
  debug(message: string, context?: Record<string, unknown>): void {
    this.log({
      level: 'debug',
      message,
      timestamp: new Date(),
      context,
    });
  }

  /**
   * Log info message
   *
   * @param message - Info message
   * @param context - Optional context data
   *
   * @example
   * ```ts
   * logger.info('API call successful', { endpoint: '/users', duration: 123 });
   * ```
   */
  info(message: string, context?: Record<string, unknown>): void {
    this.log({
      level: 'info',
      message,
      timestamp: new Date(),
      context,
    });
  }

  /**
   * Log warning message
   *
   * @param message - Warning message
   * @param context - Optional context data
   *
   * @example
   * ```ts
   * logger.warn('Deprecated API usage', { api: 'oldMethod' });
   * ```
   */
  warn(message: string, context?: Record<string, unknown>): void {
    this.log({
      level: 'warn',
      message,
      timestamp: new Date(),
      context,
    });
  }

  /**
   * Log error message
   *
   * @param message - Error message
   * @param error - Error object
   * @param context - Optional context data
   *
   * @example
   * ```ts
   * logger.error('API call failed', error, { endpoint: '/users' });
   * ```
   */
  error(message: string, error?: Error, context?: Record<string, unknown>): void {
    this.log({
      level: 'error',
      message,
      timestamp: new Date(),
      error,
      context,
    });
  }

  /**
   * Create child logger with additional context
   *
   * @param context - Context to include in all logs
   * @returns Child logger instance
   *
   * @example
   * ```ts
   * const userLogger = logger.child({ userId: 123 });
   * userLogger.info('User logged in'); // Includes userId in context
   * ```
   */
  child(context: Record<string, unknown>): LoggerService {
    const childLogger = new LoggerService();
    const originalLog = childLogger.log.bind(childLogger);

    childLogger.log = (entry: LogEntry) => {
      originalLog({
        ...entry,
        context: { ...context, ...entry.context },
      });
    };

    return childLogger;
  }

  /**
   * Enable logging
   */
  enable(): void {
    this.enabled = true;
  }

  /**
   * Disable logging
   */
  disable(): void {
    this.enabled = false;
  }

  /**
   * Set log level
   *
   * @param level - New log level
   */
  setLevel(level: LogLevel): void {
    this.logLevel = level;
  }
}

/**
 * Singleton instance of LoggerService
 */
export const logger = new LoggerService();

/**
 * Export class for testing purposes
 */
export { LoggerService };

/**
 * Re-export as default
 */
export default logger;
