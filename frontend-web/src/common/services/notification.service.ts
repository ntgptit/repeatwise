/**
 * Notification Service
 *
 * Service for displaying toast notifications using sonner.
 * Provides a consistent API for showing success, error, info, and warning messages.
 *
 * @module common/services/notification
 */

import { toast, type ExternalToast } from 'sonner';

/**
 * Notification type
 */
export type NotificationType = 'success' | 'error' | 'info' | 'warning' | 'loading';

/**
 * Notification options (extends sonner's ExternalToast)
 */
export interface NotificationOptions extends ExternalToast {
  /**
   * Auto-close duration in milliseconds
   * Set to 0 or Infinity to disable auto-close
   */
  duration?: number;

  /**
   * Action button configuration
   */
  action?: {
    label: string;
    onClick: () => void;
  };

  /**
   * Cancel button configuration
   */
  cancel?: {
    label: string;
    onClick?: () => void;
  };
}

/**
 * Notification Service class
 */
class NotificationService {
  /**
   * Show success notification
   *
   * @param message - Message to display
   * @param options - Notification options
   * @returns Toast ID for dismissal
   *
   * @example
   * ```ts
   * notificationService.success('Profile updated successfully');
   * notificationService.success('Saved', {
   *   description: 'Your changes have been saved',
   *   duration: 3000,
   * });
   * ```
   */
  success(message: string, options?: NotificationOptions): string | number {
    return toast.success(message, options);
  }

  /**
   * Show error notification
   *
   * @param message - Message to display
   * @param options - Notification options
   * @returns Toast ID for dismissal
   *
   * @example
   * ```ts
   * notificationService.error('Failed to save profile');
   * notificationService.error('Error', {
   *   description: error.message,
   * });
   * ```
   */
  error(message: string, options?: NotificationOptions): string | number {
    return toast.error(message, options);
  }

  /**
   * Show info notification
   *
   * @param message - Message to display
   * @param options - Notification options
   * @returns Toast ID for dismissal
   *
   * @example
   * ```ts
   * notificationService.info('New features available');
   * ```
   */
  info(message: string, options?: NotificationOptions): string | number {
    return toast.info(message, options);
  }

  /**
   * Show warning notification
   *
   * @param message - Message to display
   * @param options - Notification options
   * @returns Toast ID for dismissal
   *
   * @example
   * ```ts
   * notificationService.warning('Your session will expire soon');
   * ```
   */
  warning(message: string, options?: NotificationOptions): string | number {
    return toast.warning(message, options);
  }

  /**
   * Show loading notification
   *
   * @param message - Message to display
   * @param options - Notification options
   * @returns Toast ID for updating/dismissal
   *
   * @example
   * ```ts
   * const toastId = notificationService.loading('Saving...');
   * // Later update it
   * notificationService.success('Saved!', { id: toastId });
   * ```
   */
  loading(message: string, options?: NotificationOptions): string | number {
    return toast.loading(message, options);
  }

  /**
   * Show custom notification
   *
   * @param message - Message to display
   * @param options - Notification options
   * @returns Toast ID for dismissal
   *
   * @example
   * ```ts
   * notificationService.custom('Custom message', {
   *   icon: '<‰',
   * });
   * ```
   */
  custom(message: string, options?: NotificationOptions): string | number {
    return toast(message, options);
  }

  /**
   * Show promise-based notification
   * Automatically shows loading, then success or error based on promise result
   *
   * @param promise - Promise to track
   * @param messages - Messages for each state
   * @param options - Notification options
   * @returns Promise result
   *
   * @example
   * ```ts
   * await notificationService.promise(
   *   saveProfile(data),
   *   {
   *     loading: 'Saving profile...',
   *     success: 'Profile saved successfully',
   *     error: 'Failed to save profile',
   *   }
   * );
   * ```
   */
  promise<T>(
    promise: Promise<T>,
    messages: {
      loading: string;
      success: string | ((data: T) => string);
      error: string | ((error: unknown) => string);
    },
    options?: NotificationOptions
  ): Promise<T> {
    return toast.promise(promise, messages, options);
  }

  /**
   * Dismiss a specific notification
   *
   * @param toastId - Toast ID to dismiss
   *
   * @example
   * ```ts
   * const id = notificationService.info('Message');
   * setTimeout(() => notificationService.dismiss(id), 1000);
   * ```
   */
  dismiss(toastId?: string | number): void {
    toast.dismiss(toastId);
  }

  /**
   * Dismiss all notifications
   *
   * @example
   * ```ts
   * notificationService.dismissAll();
   * ```
   */
  dismissAll(): void {
    toast.dismiss();
  }

  /**
   * Show confirmation dialog (action + cancel)
   *
   * @param message - Message to display
   * @param onConfirm - Callback when confirmed
   * @param options - Notification options
   * @returns Toast ID
   *
   * @example
   * ```ts
   * notificationService.confirm(
   *   'Delete this item?',
   *   () => deleteItem(id),
   *   {
   *     description: 'This action cannot be undone',
   *   }
   * );
   * ```
   */
  confirm(
    message: string,
    onConfirm: () => void,
    options?: NotificationOptions
  ): string | number {
    return toast(message, {
      ...options,
      action: {
        label: 'Confirm',
        onClick: onConfirm,
      },
      cancel: {
        label: 'Cancel',
        onClick: () => {
          // Just dismiss
        },
      },
    });
  }
}

/**
 * Singleton instance of NotificationService
 */
export const notificationService = new NotificationService();

/**
 * Export class for testing purposes
 */
export { NotificationService };

/**
 * Re-export as default
 */
export default notificationService;
