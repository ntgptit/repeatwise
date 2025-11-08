/**
 * Color System
 *
 * Centralized color definitions using HSL CSS variables for theme support.
 * Colors are defined in Tailwind config and accessed via CSS variables.
 *
 * @module design-system/foundations/colors
 */

/**
 * Semantic color tokens
 * These map to CSS variables defined in the global styles
 */
export const colors = {
  /**
   * Primary brand colors
   */
  primary: {
    DEFAULT: 'hsl(var(--primary))',
    foreground: 'hsl(var(--primary-foreground))',
  },

  /**
   * Secondary brand colors
   */
  secondary: {
    DEFAULT: 'hsl(var(--secondary))',
    foreground: 'hsl(var(--secondary-foreground))',
  },

  /**
   * Accent colors for highlights and call-to-actions
   */
  accent: {
    DEFAULT: 'hsl(var(--accent))',
    foreground: 'hsl(var(--accent-foreground))',
  },

  /**
   * Muted/subtle colors for less prominent elements
   */
  muted: {
    DEFAULT: 'hsl(var(--muted))',
    foreground: 'hsl(var(--muted-foreground))',
  },

  /**
   * Background and foreground colors
   */
  background: 'hsl(var(--background))',
  foreground: 'hsl(var(--foreground))',

  /**
   * Card component colors
   */
  card: {
    DEFAULT: 'hsl(var(--card))',
    foreground: 'hsl(var(--card-foreground))',
  },

  /**
   * Popover/dropdown colors
   */
  popover: {
    DEFAULT: 'hsl(var(--popover))',
    foreground: 'hsl(var(--popover-foreground))',
  },

  /**
   * Border colors
   */
  border: 'hsl(var(--border))',

  /**
   * Input field border colors
   */
  input: 'hsl(var(--input))',

  /**
   * Focus ring colors
   */
  ring: 'hsl(var(--ring))',

  /**
   * Status colors
   */
  status: {
    /**
     * Success state colors (green)
     */
    success: {
      DEFAULT: 'hsl(var(--success))',
      foreground: 'hsl(var(--success-foreground))',
    },

    /**
     * Warning state colors (yellow/orange)
     */
    warning: {
      DEFAULT: 'hsl(var(--warning))',
      foreground: 'hsl(var(--warning-foreground))',
    },

    /**
     * Error/destructive state colors (red)
     */
    destructive: {
      DEFAULT: 'hsl(var(--destructive))',
      foreground: 'hsl(var(--destructive-foreground))',
    },

    /**
     * Info state colors (blue)
     */
    info: {
      DEFAULT: 'hsl(var(--info))',
      foreground: 'hsl(var(--info-foreground))',
    },
  },
} as const;

/**
 * Type for color keys
 */
export type ColorKey = keyof typeof colors;

/**
 * Helper function to get color value
 *
 * @param path - Dot notation path to color (e.g., 'status.success.DEFAULT')
 * @returns Color value
 *
 * @example
 * ```ts
 * getColor('primary.DEFAULT') // 'hsl(var(--primary))'
 * getColor('status.success.DEFAULT') // 'hsl(var(--success))'
 * ```
 */
export const getColor = (path: string): string => {
  const keys = path.split('.');
  let value: unknown = colors;

  for (const key of keys) {
    if (typeof value === 'object' && value !== null && key in value) {
      value = (value as Record<string, unknown>)[key];
    } else {
      return '';
    }
  }

  return typeof value === 'string' ? value : '';
};

/**
 * Re-export for Tailwind compatibility
 */
export default colors;
