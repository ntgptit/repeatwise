/**
 * Common Utilities
 *
 * Centralized exports for all utility functions
 */

// String utilities
export * from './string.util';

// Number utilities
export * from './number.util';

// Date utilities
export * from './date.util';

// Validation utilities
export * from './validation.util';

// Array utilities
export * from './array.util';

// URL utilities
export * from './url.util';

// Re-export as namespaced objects for convenience
export { default as stringUtils } from './string.util';
export { default as numberUtils } from './number.util';
export { default as dateUtils } from './date.util';
export { default as validationUtils } from './validation.util';
export { default as arrayUtils } from './array.util';
export { default as urlUtils } from './url.util';
