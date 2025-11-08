/**
 * Common Services
 *
 * Collection of reusable services for application-wide functionality.
 *
 * @module common/services
 */

export * from './web-storage.service';
export * from './notification.service';
export * from './logger.service';
export * from './error-handler.service';

export { default as storageService } from './web-storage.service';
export { default as notificationService } from './notification.service';
export { default as logger } from './logger.service';
export { default as errorHandler } from './error-handler.service';
