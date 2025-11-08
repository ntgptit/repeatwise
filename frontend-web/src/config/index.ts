/**
 * Configuration Module
 *
 * Centralized configuration for the application.
 * Exports all configuration objects and utilities.
 *
 * @module config
 */

export * from './env';
export * from './app.config';
export * from './api.config';
export * from './services.config';

export { default as env } from './env';
export { default as appConfig } from './app.config';
export { default as apiConfig } from './api.config';
export { default as servicesConfig } from './services.config';

/**
 * Combined configuration object
 */
export const config = {
  env: () => import('./env').then((m) => m.default),
  app: () => import('./app.config').then((m) => m.default),
  api: () => import('./api.config').then((m) => m.default),
  services: () => import('./services.config').then((m) => m.default),
} as const;
