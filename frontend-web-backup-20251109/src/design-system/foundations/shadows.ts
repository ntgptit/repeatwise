/**
 * Shadow System - RepeatWise Design System
 *
 * Elevation system using box shadows:
 * - None: No shadow (flat)
 * - SM: Small elevation (cards, buttons)
 * - MD: Medium elevation (dropdowns, modals)
 * - LG: Large elevation (popups, dialogs)
 * - XL: Extra large elevation (overlays)
 * - 2XL: Maximum elevation
 * - Inner: Inset shadow (inputs, wells)
 *
 * Also includes colored shadows and focus rings
 */

/**
 * Shadow values
 */
export const shadows = {
  // No shadow
  none: 'none',

  // Small shadow - Subtle elevation
  // Use for: Cards, buttons (hover), small raised elements
  sm: '0 1px 2px 0 rgb(var(--color-shadow-rgb) / 0.05)',

  // Default shadow - Standard elevation
  // Use for: Cards, panels, raised surfaces
  base: '0 1px 3px 0 rgb(var(--color-shadow-rgb) / 0.1), 0 1px 2px -1px rgb(var(--color-shadow-rgb) / 0.1)',

  // Medium shadow - Moderate elevation
  // Use for: Dropdowns, popovers, tooltips
  md: '0 4px 6px -1px rgb(var(--color-shadow-rgb) / 0.1), 0 2px 4px -2px rgb(var(--color-shadow-rgb) / 0.1)',

  // Large shadow - High elevation
  // Use for: Modals, dialogs, floating panels
  lg: '0 10px 15px -3px rgb(var(--color-shadow-rgb) / 0.1), 0 4px 6px -4px rgb(var(--color-shadow-rgb) / 0.1)',

  // Extra large shadow - Very high elevation
  // Use for: Modal overlays, important popups
  xl: '0 20px 25px -5px rgb(var(--color-shadow-rgb) / 0.1), 0 8px 10px -6px rgb(var(--color-shadow-rgb) / 0.1)',

  // 2XL shadow - Maximum elevation
  // Use for: Full screen overlays, critical notifications
  '2xl': '0 25px 50px -12px rgb(var(--color-shadow-rgb) / 0.25)',

  // Inner shadow - Inset effect
  // Use for: Input fields, wells, pressed states
  inner: 'inset 0 2px 4px 0 rgb(var(--color-shadow-rgb) / 0.05)',
} as const

/**
 * Colored Shadows
 * Use for focus states, active states, or branded elements
 */
export const coloredShadows = {
  // Primary colored shadow (brand blue)
  primary: {
    sm: '0 1px 2px 0 rgb(59 130 246 / 0.3)',
    md: '0 4px 6px -1px rgb(59 130 246 / 0.3), 0 2px 4px -2px rgb(59 130 246 / 0.2)',
    lg: '0 10px 15px -3px rgb(59 130 246 / 0.3), 0 4px 6px -4px rgb(59 130 246 / 0.2)',
  },

  // Secondary colored shadow (indigo)
  secondary: {
    sm: '0 1px 2px 0 rgb(99 102 241 / 0.3)',
    md: '0 4px 6px -1px rgb(99 102 241 / 0.3), 0 2px 4px -2px rgb(99 102 241 / 0.2)',
    lg: '0 10px 15px -3px rgb(99 102 241 / 0.3), 0 4px 6px -4px rgb(99 102 241 / 0.2)',
  },

  // Success colored shadow (green)
  success: {
    sm: '0 1px 2px 0 rgb(34 197 94 / 0.3)',
    md: '0 4px 6px -1px rgb(34 197 94 / 0.3), 0 2px 4px -2px rgb(34 197 94 / 0.2)',
    lg: '0 10px 15px -3px rgb(34 197 94 / 0.3), 0 4px 6px -4px rgb(34 197 94 / 0.2)',
  },

  // Warning colored shadow (amber)
  warning: {
    sm: '0 1px 2px 0 rgb(245 158 11 / 0.3)',
    md: '0 4px 6px -1px rgb(245 158 11 / 0.3), 0 2px 4px -2px rgb(245 158 11 / 0.2)',
    lg: '0 10px 15px -3px rgb(245 158 11 / 0.3), 0 4px 6px -4px rgb(245 158 11 / 0.2)',
  },

  // Error colored shadow (red)
  error: {
    sm: '0 1px 2px 0 rgb(239 68 68 / 0.3)',
    md: '0 4px 6px -1px rgb(239 68 68 / 0.3), 0 2px 4px -2px rgb(239 68 68 / 0.2)',
    lg: '0 10px 15px -3px rgb(239 68 68 / 0.3), 0 4px 6px -4px rgb(239 68 68 / 0.2)',
  },

  // Info colored shadow (cyan)
  info: {
    sm: '0 1px 2px 0 rgb(6 182 212 / 0.3)',
    md: '0 4px 6px -1px rgb(6 182 212 / 0.3), 0 2px 4px -2px rgb(6 182 212 / 0.2)',
    lg: '0 10px 15px -3px rgb(6 182 212 / 0.3), 0 4px 6px -4px rgb(6 182 212 / 0.2)',
  },
} as const

/**
 * Focus Rings
 * Use for keyboard navigation focus states
 */
export const focusRings = {
  // Default focus ring (primary color)
  default: '0 0 0 3px rgb(59 130 246 / 0.5)',

  // Thin focus ring (subtle)
  thin: '0 0 0 2px rgb(59 130 246 / 0.5)',

  // Thick focus ring (prominent)
  thick: '0 0 0 4px rgb(59 130 246 / 0.5)',

  // Error focus ring
  error: '0 0 0 3px rgb(239 68 68 / 0.5)',

  // Success focus ring
  success: '0 0 0 3px rgb(34 197 94 / 0.5)',

  // Warning focus ring
  warning: '0 0 0 3px rgb(245 158 11 / 0.5)',

  // White focus ring (for dark backgrounds)
  white: '0 0 0 3px rgb(255 255 255 / 0.5)',

  // Dark focus ring (for light backgrounds)
  dark: '0 0 0 3px rgb(var(--color-shadow-rgb) / 0.5)',
} as const

/**
 * Semantic Shadows
 * Pre-configured shadow combinations for specific components
 */
export const semanticShadows = {
  // Button shadows
  button: {
    default: shadows.sm,
    hover: shadows.md,
    active: shadows.inner,
    focus: focusRings.default,
  },

  // Card shadows
  card: {
    default: shadows.base,
    hover: shadows.md,
    active: shadows.lg,
  },

  // Dropdown/Popover shadows
  dropdown: {
    default: shadows.lg,
  },

  // Modal shadows
  modal: {
    default: shadows.xl,
    backdrop: 'none', // Backdrop handles darkening
  },

  // Input shadows
  input: {
    default: shadows.none,
    focus: focusRings.default,
    error: focusRings.error,
    disabled: shadows.none,
  },

  // Tooltip shadows
  tooltip: {
    default: shadows.md,
  },

  // Toast/Notification shadows
  toast: {
    default: shadows.lg,
  },

  // Table shadows
  table: {
    header: shadows.sm,
    row: shadows.none,
    hoverRow: shadows.sm,
  },

  // Navigation shadows
  nav: {
    default: shadows.base,
    sticky: shadows.md,
  },

  // Drawer shadows
  drawer: {
    default: shadows['2xl'],
  },
} as const

export const elevation = semanticShadows

/**
 * Dark Mode Shadows
 * Adjusted shadows for dark theme (more subtle)
 */
export const darkShadows = {
  none: 'none',
  sm: '0 1px 2px 0 rgb(var(--color-shadow-rgb) / 0.3)',
  base: '0 1px 3px 0 rgb(var(--color-shadow-rgb) / 0.4), 0 1px 2px -1px rgb(var(--color-shadow-rgb) / 0.4)',
  md: '0 4px 6px -1px rgb(var(--color-shadow-rgb) / 0.4), 0 2px 4px -2px rgb(var(--color-shadow-rgb) / 0.4)',
  lg: '0 10px 15px -3px rgb(var(--color-shadow-rgb) / 0.4), 0 4px 6px -4px rgb(var(--color-shadow-rgb) / 0.4)',
  xl: '0 20px 25px -5px rgb(var(--color-shadow-rgb) / 0.4), 0 8px 10px -6px rgb(var(--color-shadow-rgb) / 0.4)',
  '2xl': '0 25px 50px -12px rgb(var(--color-shadow-rgb) / 0.5)',
  inner: 'inset 0 2px 4px 0 rgb(var(--color-shadow-rgb) / 0.3)',
} as const

/**
 * Type definitions
 */
export type ShadowSize = keyof typeof shadows
export type ColoredShadowType = keyof typeof coloredShadows
export type FocusRingType = keyof typeof focusRings
export type SemanticShadowCategory = keyof typeof semanticShadows

/**
 * Shadow utilities
 */

/**
 * Get shadow by size
 */
export const getShadow = (size: ShadowSize): string => {
  return shadows[size]
}

export const getDarkShadow = (size: ShadowSize): string => {
  return darkShadows[size]
}

/**
 * Get colored shadow
 */
export const getColoredShadow = (color: ColoredShadowType, size: 'sm' | 'md' | 'lg'): string => {
  return coloredShadows[color][size]
}

/**
 * Get focus ring
 */
export const getFocusRing = (type: FocusRingType = 'default'): string => {
  return focusRings[type]
}

/**
 * Combine multiple shadows
 */
export const combineShadows = (...shadowValues: string[]): string => {
  return shadowValues.filter(s => s !== 'none').join(', ')
}

/**
 * Create custom shadow
 */
export const createShadow = (
  offsetX: number,
  offsetY: number,
  blur: number,
  spread: number,
  color: string,
  inset = false
): string => {
  const insetStr = inset ? 'inset ' : ''
  return `${insetStr}${offsetX}px ${offsetY}px ${blur}px ${spread}px ${color}`
}

/**
 * Export all shadow utilities
 */
export const shadowSystem = {
  shadows,
  coloredShadows,
  focusRings,
  semanticShadows,
  darkShadows,
  getShadow,
  getDarkShadow,
  getColoredShadow,
  getFocusRing,
  combineShadows,
  createShadow,
} as const

export default shadowSystem
