/**
 * Notification Service
 *
 * Centralized service for toast notifications
 * Can be integrated with react-toastify, sonner, or custom toast
 */

import { notificationConfig } from '@/config/services.config';

/**
 * Notification types
 */
export type NotificationType = 'success' | 'error' | 'warning' | 'info';

/**
 * Notification options
 */
export interface NotificationOptions {
  /**
   * Notification title
   */
  title?: string;

  /**
   * Notification message
   */
  message: string;

  /**
   * Notification type
   * @default 'info'
   */
  type?: NotificationType;

  /**
   * Duration in milliseconds
   * @default 3000
   */
  duration?: number;

  /**
   * Position
   */
  position?: typeof notificationConfig.toast.position;

  /**
   * Auto close
   * @default true
   */
  autoClose?: boolean;

  /**
   * Show close button
   * @default true
   */
  closeable?: boolean;

  /**
   * Click handler
   */
  onClick?: () => void;

  /**
   * Close handler
   */
  onClose?: () => void;
}

/**
 * Notification Service
 *
 * This is a placeholder implementation.
 * Integrate with your preferred toast library (react-toastify, sonner, etc.)
 */
class NotificationService {
  private notifications: NotificationOptions[] = [];
  private maxNotifications = notificationConfig.toast.maxToasts;

  /**
   * Show notification
   */
  show(options: NotificationOptions): void {
    const notification: NotificationOptions = {
      type: 'info',
      duration: notificationConfig.toast.duration,
      position: notificationConfig.toast.position,
      autoClose: true,
      closeable: true,
      ...options,
    };

    // Add to notifications array
    this.notifications.push(notification);

    // Limit number of notifications
    if (this.notifications.length > this.maxNotifications) {
      this.notifications.shift();
    }

    // Log to console (replace with actual toast implementation)
    this.logNotification(notification);

    // Auto close if enabled
    if (notification.autoClose && notification.duration) {
      setTimeout(() => {
        this.close(notification);
      }, notification.duration);
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
    });
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
    });
  }

  /**
   * Show warning notification
   */
  warning(message: string, options?: Partial<NotificationOptions>): void {
    this.show({
      message,
      type: 'warning',
      ...options,
    });
  }

  /**
   * Show info notification
   */
  info(message: string, options?: Partial<NotificationOptions>): void {
    this.show({
      message,
      type: 'info',
      ...options,
    });
  }

  /**
   * Close notification
   */
  close(notification: NotificationOptions): void {
    const index = this.notifications.indexOf(notification);
    if (index > -1) {
      this.notifications.splice(index, 1);
    }

    if (notification.onClose) {
      notification.onClose();
    }
  }

  /**
   * Close all notifications
   */
  closeAll(): void {
    this.notifications = [];
  }

  /**
   * Log notification (development)
   */
  private logNotification(notification: NotificationOptions): void {
    const emoji = {
      success: '',
      error: 'L',
      warning: ' ',
      info: '9',
    }[notification.type || 'info'];

    console.log(
      `${emoji} ${notification.title ? `${notification.title}: ` : ''}${notification.message}`
    );
  }

  /**
   * Get active notifications
   */
  getNotifications(): NotificationOptions[] {
    return [...this.notifications];
  }
}

/**
 * Notification service instance
 */
export const notificationService = new NotificationService();

export default notificationService;
