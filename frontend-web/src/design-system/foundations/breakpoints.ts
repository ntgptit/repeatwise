/**
 * Breakpoints System
 *
 * Defines responsive breakpoints for mobile-first design.
 * Follows common device widths and industry standards.
 *
 * @module design-system/foundations/breakpoints
 */

/**
 * Breakpoint values in pixels
 */
export const breakpoints = {
  /**
   * Extra small devices (phones, portrait)
   * 0px - 639px
   */
  xs: 0,

  /**
   * Small devices (phones, landscape / small tablets)
   * 640px and up
   */
  sm: 640,

  /**
   * Medium devices (tablets)
   * 768px and up
   */
  md: 768,

  /**
   * Large devices (desktops)
   * 1024px and up
   */
  lg: 1024,

  /**
   * Extra large devices (large desktops)
   * 1280px and up
   */
  xl: 1280,

  /**
   * 2XL devices (larger desktops)
   * 1536px and up
   */
  '2xl': 1536,
} as const;

/**
 * Breakpoint keys type
 */
export type BreakpointKey = keyof typeof breakpoints;

/**
 * Media query helpers
 */
export const mediaQueries = {
  /**
   * Min-width media queries (mobile-first)
   */
  up: {
    xs: `@media (min-width: ${breakpoints.xs}px)`,
    sm: `@media (min-width: ${breakpoints.sm}px)`,
    md: `@media (min-width: ${breakpoints.md}px)`,
    lg: `@media (min-width: ${breakpoints.lg}px)`,
    xl: `@media (min-width: ${breakpoints.xl}px)`,
    '2xl': `@media (min-width: ${breakpoints['2xl']}px)`,
  },

  /**
   * Max-width media queries (desktop-first)
   */
  down: {
    xs: `@media (max-width: ${breakpoints.sm - 1}px)`,
    sm: `@media (max-width: ${breakpoints.md - 1}px)`,
    md: `@media (max-width: ${breakpoints.lg - 1}px)`,
    lg: `@media (max-width: ${breakpoints.xl - 1}px)`,
    xl: `@media (max-width: ${breakpoints['2xl'] - 1}px)`,
  },

  /**
   * Range media queries (between two breakpoints)
   */
  between: {
    'xs-sm': `@media (min-width: ${breakpoints.xs}px) and (max-width: ${breakpoints.sm - 1}px)`,
    'sm-md': `@media (min-width: ${breakpoints.sm}px) and (max-width: ${breakpoints.md - 1}px)`,
    'md-lg': `@media (min-width: ${breakpoints.md}px) and (max-width: ${breakpoints.lg - 1}px)`,
    'lg-xl': `@media (min-width: ${breakpoints.lg}px) and (max-width: ${breakpoints.xl - 1}px)`,
    'xl-2xl': `@media (min-width: ${breakpoints.xl}px) and (max-width: ${breakpoints['2xl'] - 1}px)`,
  },
} as const;

/**
 * Container max widths for each breakpoint
 */
export const containerMaxWidths = {
  sm: '640px',
  md: '768px',
  lg: '1024px',
  xl: '1280px',
  '2xl': '1536px',
} as const;

/**
 * Helper to create min-width media query
 *
 * @param breakpoint - Breakpoint key
 * @returns Media query string
 *
 * @example
 * ```ts
 * up('md') // '@media (min-width: 768px)'
 * ```
 */
export const up = (breakpoint: BreakpointKey): string => {
  return `@media (min-width: ${breakpoints[breakpoint]}px)`;
};

/**
 * Helper to create max-width media query
 *
 * @param breakpoint - Breakpoint key
 * @returns Media query string
 *
 * @example
 * ```ts
 * down('md') // '@media (max-width: 1023px)'
 * ```
 */
export const down = (breakpoint: BreakpointKey): string => {
  const value = breakpoints[breakpoint];
  return `@media (max-width: ${value - 1}px)`;
};

/**
 * Helper to create range media query
 *
 * @param min - Minimum breakpoint key
 * @param max - Maximum breakpoint key
 * @returns Media query string
 *
 * @example
 * ```ts
 * between('sm', 'lg') // '@media (min-width: 640px) and (max-width: 1023px)'
 * ```
 */
export const between = (min: BreakpointKey, max: BreakpointKey): string => {
  return `@media (min-width: ${breakpoints[min]}px) and (max-width: ${breakpoints[max] - 1}px)`;
};

/**
 * Re-export for convenience
 */
export default breakpoints;
