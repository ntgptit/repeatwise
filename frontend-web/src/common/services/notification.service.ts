/**
 * Notification Service
 *
 * Centralized service for toast notifications
 * Can be integrated with react-toastify, sonner, or custom toast
 */

import { notificationConfig } from '@/config/services.config'

export interface NotificationInstance extends NotificationOptions {
  id: number
}
type NotificationListener = (notifications: NotificationInstance[]) => void

/**
 * Notification types
 */
export type NotificationType = 'success' | 'error' | 'warning' | 'info'

/**
 * Notification options
 */
export interface NotificationOptions {
  /**
   * Notification title
   */
  title?: string

  /**
   * Notification message
   */
  message: string

  /**
   * Notification type
   * @default 'info'
   */
  type?: NotificationType

  /**
   * Duration in milliseconds
   * @default 3000
   */
  duration?: number

  /**
   * Position
   */
  position?: typeof notificationConfig.toast.position

  /**
   * Auto close
   * @default true
   */
  autoClose?: boolean

  /**
   * Show close button
   * @default true
   */
  closeable?: boolean

  /**
   * Click handler
   */
  onClick?: () => void

  /**
   * Close handler
   */
  onClose?: () => void
}

/**
 * Notification Service
 *
 * This is a placeholder implementation.
 * Integrate with your preferred toast library (react-toastify, sonner, etc.)
 */
class NotificationService {
  private notifications: NotificationInstance[] = []
  private readonly maxNotifications = notificationConfig.toast.maxToasts
  private listeners = new Set<NotificationListener>()
  private counter = 0

  /**
   * Show notification
   */
  show(options: NotificationOptions): void {
    const notification: NotificationInstance = {
      id: ++this.counter,
      type: 'info',
      duration: notificationConfig.toast.duration,
      position: notificationConfig.toast.position,
      autoClose: true,
      closeable: true,
      ...options,
    }

    // Add to notifications array
    this.notifications.push(notification)

    // Limit number of notifications
    if (this.notifications.length > this.maxNotifications) {
      this.notifications.shift()
    }

    // Log to console (replace with actual toast implementation)
    this.logNotification(notification)

    this.notify()

    // Auto close if enabled
    if (notification.autoClose && notification.duration) {
      setTimeout(() => {
        this.close(notification.id)
      }, notification.duration)
    }
  }

  /**
   * Show success notification
   */
  success(message: string, options?: Partial<NotificationOptions>): void {
    this.show({
      message,
      type: 'success',
      ...options,
    })
  }

  /**
   * Show error notification
   */
  error(message: string, options?: Partial<NotificationOptions>): void {
    this.show({
      message,
      type: 'error',
      duration: 5000, // Longer duration for errors
      ...options,
    })
  }

  /**
   * Show warning notification
   */
  warning(message: string, options?: Partial<NotificationOptions>): void {
    this.show({
      message,
      type: 'warning',
      ...options,
    })
  }

  /**
   * Show info notification
   */
  info(message: string, options?: Partial<NotificationOptions>): void {
    this.show({
      message,
      type: 'info',
      ...options,
    })
  }

  /**
   * Close notification
   */
  close(notificationId: number): void {
    const index = this.notifications.findIndex((item) => item.id === notificationId)
    if (index === -1) {
      return
    }

    const [notification] = this.notifications.splice(index, 1)

    if (notification.onClose) {
      notification.onClose()
    }

    this.notify()
  }

  /**
   * Close all notifications
   */
  closeAll(): void {
    this.notifications = []
    this.notify()
  }

  /**
   * Log notification (development)
   */
  private logNotification(notification: NotificationOptions): void {
    const emoji = {
      success: '✓',
      error: '✗',
      warning: '⚠',
      info: 'ℹ',
    }[notification.type || 'info']

    const titlePrefix = notification.title ? `${notification.title}: ` : ''
    const message = `${emoji} ${titlePrefix}${notification.message}`
    console.warn(message)
  }

  /**
   * Get active notifications
   */
  getNotifications(): NotificationInstance[] {
    return [...this.notifications]
  }

  subscribe(listener: NotificationListener): () => void {
    this.listeners.add(listener)
    listener([...this.notifications])

    return () => {
      this.listeners.delete(listener)
    }
  }

  private notify(): void {
    const snapshot = [...this.notifications]
    for (const listener of this.listeners) {
      listener(snapshot)
    }
  }
}

/**
 * Notification service instance
 */
export const notificationService = new NotificationService()

export default notificationService
