/**
 * Shadow System
 *
 * Defines box shadow utilities for depth and elevation.
 * Provides a consistent elevation system across the application.
 *
 * @module design-system/foundations/shadows
 */

/**
 * Box shadow scale
 */
export const shadows = {
  /**
   * No shadow
   */
  none: 'none',

  /**
   * Extra small shadow (subtle elevation)
   * Elevation: 1
   */
  xs: '0 1px 2px 0 rgba(0, 0, 0, 0.05)',

  /**
   * Small shadow
   * Elevation: 2
   */
  sm: '0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px -1px rgba(0, 0, 0, 0.1)',

  /**
   * Medium shadow (default)
   * Elevation: 4
   */
  md: '0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -2px rgba(0, 0, 0, 0.1)',

  /**
   * Large shadow
   * Elevation: 8
   */
  lg: '0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -4px rgba(0, 0, 0, 0.1)',

  /**
   * Extra large shadow
   * Elevation: 12
   */
  xl: '0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 8px 10px -6px rgba(0, 0, 0, 0.1)',

  /**
   * 2XL shadow (maximum elevation)
   * Elevation: 16
   */
  '2xl': '0 25px 50px -12px rgba(0, 0, 0, 0.25)',

  /**
   * Inner shadow
   */
  inner: 'inset 0 2px 4px 0 rgba(0, 0, 0, 0.05)',
} as const;

/**
 * Colored shadows for status states
 */
export const coloredShadows = {
  /**
   * Primary colored shadow
   */
  primary: '0 4px 6px -1px rgba(var(--primary-rgb), 0.2), 0 2px 4px -2px rgba(var(--primary-rgb), 0.2)',

  /**
   * Success colored shadow (green)
   */
  success: '0 4px 6px -1px rgba(34, 197, 94, 0.2), 0 2px 4px -2px rgba(34, 197, 94, 0.2)',

  /**
   * Warning colored shadow (yellow/orange)
   */
  warning: '0 4px 6px -1px rgba(251, 146, 60, 0.2), 0 2px 4px -2px rgba(251, 146, 60, 0.2)',

  /**
   * Error/destructive colored shadow (red)
   */
  destructive: '0 4px 6px -1px rgba(239, 68, 68, 0.2), 0 2px 4px -2px rgba(239, 68, 68, 0.2)',

  /**
   * Info colored shadow (blue)
   */
  info: '0 4px 6px -1px rgba(59, 130, 246, 0.2), 0 2px 4px -2px rgba(59, 130, 246, 0.2)',
} as const;

/**
 * Elevation levels mapped to shadows
 */
export const elevation = {
  /**
   * Level 0 - No elevation (flat)
   */
  0: shadows.none,

  /**
   * Level 1 - Subtle elevation
   */
  1: shadows.xs,

  /**
   * Level 2 - Low elevation (cards, buttons)
   */
  2: shadows.sm,

  /**
   * Level 4 - Medium elevation (dropdowns, popovers)
   */
  4: shadows.md,

  /**
   * Level 8 - High elevation (modals, dialogs)
   */
  8: shadows.lg,

  /**
   * Level 12 - Very high elevation
   */
  12: shadows.xl,

  /**
   * Level 16 - Maximum elevation (tooltips, notifications)
   */
  16: shadows['2xl'],
} as const;

/**
 * Focus ring shadows
 */
export const focusRing = {
  /**
   * Default focus ring
   */
  default: '0 0 0 3px rgba(var(--ring-rgb), 0.5)',

  /**
   * Primary focus ring
   */
  primary: '0 0 0 3px rgba(var(--primary-rgb), 0.3)',

  /**
   * Error focus ring
   */
  error: '0 0 0 3px rgba(239, 68, 68, 0.3)',

  /**
   * Success focus ring
   */
  success: '0 0 0 3px rgba(34, 197, 94, 0.3)',
} as const;

/**
 * Type definitions
 */
export type ShadowKey = keyof typeof shadows;
export type ElevationLevel = keyof typeof elevation;
export type ColoredShadowKey = keyof typeof coloredShadows;
export type FocusRingKey = keyof typeof focusRing;

/**
 * Helper to get shadow by key
 *
 * @param key - Shadow key
 * @returns Shadow value
 *
 * @example
 * ```ts
 * getShadow('md') // '0 4px 6px -1px rgba(0, 0, 0, 0.1), ...'
 * ```
 */
export const getShadow = (key: ShadowKey): string => {
  return shadows[key];
};

/**
 * Helper to get shadow by elevation level
 *
 * @param level - Elevation level (0, 1, 2, 4, 8, 12, 16)
 * @returns Shadow value
 *
 * @example
 * ```ts
 * getElevation(4) // '0 4px 6px -1px rgba(0, 0, 0, 0.1), ...'
 * ```
 */
export const getElevation = (level: ElevationLevel): string => {
  return elevation[level];
};

/**
 * Re-export for convenience
 */
export default shadows;
