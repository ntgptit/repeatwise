/**
 * Logger Service
 *
 * Centralized logging service with different log levels
 * Can be extended to send logs to external services (Sentry, LogRocket, etc.)
 */

import { env } from '@/config/env'

/**
 * Log level
 */
export enum LogLevel {
  DEBUG = 0,
  INFO = 1,
  WARN = 2,
  ERROR = 3,
  NONE = 4,
}

/**
 * Log entry
 */
export interface LogEntry {
  level: LogLevel
  message: string
  timestamp: string
  context?: string
  data?: unknown
  stack?: string
}

/**
 * Logger configuration
 */
export interface LoggerConfig {
  /**
   * Minimum log level to display
   * @default LogLevel.INFO
   */
  minLevel: LogLevel

  /**
   * Enable console logging
   * @default true
   */
  enableConsole: boolean

  /**
   * Enable log storage (for debugging)
   * @default false
   */
  enableStorage: boolean

  /**
   * Maximum number of logs to store
   * @default 100
   */
  maxStoredLogs: number

  /**
   * Context prefix (e.g., component name, module name)
   */
  contextPrefix?: string
}

/**
 * Default logger configuration
 */
const defaultConfig: LoggerConfig = {
  minLevel: env.isDevelopment ? LogLevel.DEBUG : LogLevel.WARN,
  enableConsole: true,
  enableStorage: env.isDevelopment,
  maxStoredLogs: 100,
}

/**
 * Logger Service Class
 */
class LoggerService {
  private config: LoggerConfig
  private logs: LogEntry[] = []

  constructor(config: Partial<LoggerConfig> = {}) {
    this.config = { ...defaultConfig, ...config }
  }

  /**
   * Create log entry
   */
  private createLogEntry(
    level: LogLevel,
    message: string,
    data?: unknown,
    context?: string
  ): LogEntry {
    return {
      level,
      message,
      timestamp: new Date().toISOString(),
      context: context || this.config.contextPrefix,
      data,
      stack: level === LogLevel.ERROR ? new Error().stack : undefined,
    }
  }

  /**
   * Store log entry
   */
  private storeLog(entry: LogEntry): void {
    if (!this.config.enableStorage) {
      return
    }

    this.logs.push(entry)

    // Limit stored logs
    if (this.logs.length > this.config.maxStoredLogs) {
      this.logs.shift()
    }
  }

  /**
   * Log to console
   */
  private logToConsole(entry: LogEntry): void {
    if (!this.config.enableConsole) {
      return
    }

    const contextStr = entry.context ? `[${entry.context}]` : ''
    const messageStr = `${contextStr} ${entry.message}`

    switch (entry.level) {
      case LogLevel.DEBUG:
        console.debug(messageStr, entry.data || '')
        break
      case LogLevel.INFO:
        console.log(messageStr, entry.data || '')
        break
      case LogLevel.WARN:
        console.warn(messageStr, entry.data || '')
        break
      case LogLevel.ERROR:
        console.error(messageStr, entry.data || '')
        if (entry.stack) {
          console.error(entry.stack)
        }
        break
    }
  }

  /**
   * Check if should log
   */
  private shouldLog(level: LogLevel): boolean {
    return level >= this.config.minLevel
  }

  /**
   * Log message
   */
  private log(level: LogLevel, message: string, data?: unknown, context?: string): void {
    if (!this.shouldLog(level)) {
      return
    }

    const entry = this.createLogEntry(level, message, data, context)

    this.logToConsole(entry)
    this.storeLog(entry)
  }

  /**
   * Debug log
   */
  debug(message: string, data?: unknown, context?: string): void {
    this.log(LogLevel.DEBUG, message, data, context)
  }

  /**
   * Info log
   */
  info(message: string, data?: unknown, context?: string): void {
    this.log(LogLevel.INFO, message, data, context)
  }

  /**
   * Warning log
   */
  warn(message: string, data?: unknown, context?: string): void {
    this.log(LogLevel.WARN, message, data, context)
  }

  /**
   * Error log
   */
  error(message: string, error?: Error | unknown, context?: string): void {
    const errorData = error instanceof Error ? { message: error.message, stack: error.stack } : error
    this.log(LogLevel.ERROR, message, errorData, context)
  }

  /**
   * Get stored logs
   */
  getLogs(): LogEntry[] {
    return [...this.logs]
  }

  /**
   * Clear stored logs
   */
  clearLogs(): void {
    this.logs = []
  }

  /**
   * Get logs by level
   */
  getLogsByLevel(level: LogLevel): LogEntry[] {
    return this.logs.filter(log => log.level === level)
  }

  /**
   * Get logs by context
   */
  getLogsByContext(context: string): LogEntry[] {
    return this.logs.filter(log => log.context === context)
  }

  /**
   * Export logs as JSON
   */
  exportLogs(): string {
    return JSON.stringify(this.logs, null, 2)
  }

  /**
   * Update configuration
   */
  setConfig(config: Partial<LoggerConfig>): void {
    this.config = { ...this.config, ...config }
  }

  /**
   * Create child logger with context
   */
  createChild(context: string): LoggerService {
    return new LoggerService({
      ...this.config,
      contextPrefix: context,
    })
  }
}

/**
 * Global logger instance
 */
export const logger = new LoggerService()

/**
 * Create logger with context
 */
export const createLogger = (context: string, config?: Partial<LoggerConfig>): LoggerService => {
  return new LoggerService({
    ...config,
    contextPrefix: context,
  })
}

/**
 * Log level utilities
 */
export const logLevelToString = (level: LogLevel): string => {
  switch (level) {
    case LogLevel.DEBUG:
      return 'DEBUG'
    case LogLevel.INFO:
      return 'INFO'
    case LogLevel.WARN:
      return 'WARN'
    case LogLevel.ERROR:
      return 'ERROR'
    case LogLevel.NONE:
      return 'NONE'
    default:
      return 'UNKNOWN'
  }
}

export default logger
