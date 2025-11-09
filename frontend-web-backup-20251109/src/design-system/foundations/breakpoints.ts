/**
 * Breakpoints System - RepeatWise Design System
 *
 * Responsive breakpoints for different screen sizes:
 * - Mobile: < 768px
 * - Tablet: 768px - 1024px
 * - Desktop: > 1024px
 *
 * Based on wireframes specification in 00_docs
 */

/**
 * Breakpoint values (in pixels)
 */
export const breakpointValues = {
  xs: 0, // Extra small devices (portrait phones)
  sm: 640, // Small devices (landscape phones)
  md: 768, // Medium devices (tablets)
  lg: 1024, // Large devices (desktops)
  xl: 1280, // Extra large devices (large desktops)
  '2xl': 1536, // 2X large devices (larger desktops)
} as const

export type BreakpointKey = keyof typeof breakpointValues

const breakpointEntries = Object.entries(breakpointValues) as [BreakpointKey, number][]

const createUnitValues = (transform: (value: number) => string) =>
  Object.freeze(
    breakpointEntries.reduce<Record<BreakpointKey, string>>(
      (acc, [key, value]) => {
        acc[key] = transform(value)
        return acc
      },
      {} as Record<BreakpointKey, string>
    )
  )

const getWindow = () => {
  const global = globalThis as typeof globalThis & { window?: Window }
  return global.window ?? undefined
}

/**
 * Breakpoints in different units
 */
export const breakpoints = {
  // Pixel values
  px: createUnitValues(value => `${value}px`),
  // Em values (for better accessibility)
  em: createUnitValues(value => `${value / 16}em`),
  // Rem values
  rem: createUnitValues(value => `${value / 16}rem`),
} as const

/**
 * Semantic breakpoints based on device type
 */
export const deviceBreakpoints = {
  mobile: {
    min: breakpointValues.xs,
    max: breakpointValues.md - 1, // 0 - 767px
  },
  tablet: {
    min: breakpointValues.md,
    max: breakpointValues.lg - 1, // 768 - 1023px
  },
  desktop: {
    min: breakpointValues.lg,
    max: Infinity, // 1024px+
  },
} as const

/**
 * Media query strings for CSS-in-JS
 */
const mediaMin = createUnitValues(value => `@media (min-width: ${value}px)`)

const mediaMax = Object.freeze({
  xs: `@media (max-width: ${breakpointValues.sm - 1}px)`,
  sm: `@media (max-width: ${breakpointValues.md - 1}px)`,
  md: `@media (max-width: ${breakpointValues.lg - 1}px)`,
  lg: `@media (max-width: ${breakpointValues.xl - 1}px)`,
  xl: `@media (max-width: ${breakpointValues['2xl'] - 1}px)`,
} satisfies Partial<Record<BreakpointKey, string>>)

export const media = {
  min: mediaMin,
  max: mediaMax,
} as const

export const mediaQueries = {
  // Min-width (mobile-first)
  min: media.min,
  // Max-width (desktop-first)
  max: media.max,
  // Range queries (between two breakpoints)
  between: {
    smMd: `@media (min-width: ${breakpoints.px.sm}) and (max-width: ${breakpointValues.md - 1}px)`,
    mdLg: `@media (min-width: ${breakpoints.px.md}) and (max-width: ${breakpointValues.lg - 1}px)`,
    lgXl: `@media (min-width: ${breakpoints.px.lg}) and (max-width: ${breakpointValues.xl - 1}px)`,
  },
  // Device-specific queries
  device: {
    mobile: `@media (max-width: ${breakpointValues.md - 1}px)`,
    tablet: `@media (min-width: ${breakpoints.px.md}) and (max-width: ${breakpointValues.lg - 1}px)`,
    desktop: `@media (min-width: ${breakpoints.px.lg})`,
    tabletUp: `@media (min-width: ${breakpoints.px.md})`,
    desktopUp: `@media (min-width: ${breakpoints.px.lg})`,
  },
  // Special queries
  special: {
    portrait: '@media (orientation: portrait)',
    landscape: '@media (orientation: landscape)',
    retina: '@media (-webkit-min-device-pixel-ratio: 2), (min-resolution: 192dpi)',
    dark: '@media (prefers-color-scheme: dark)',
    light: '@media (prefers-color-scheme: light)',
    reducedMotion: '@media (prefers-reduced-motion: reduce)',
    print: '@media print',
  },
} as const

/**
 * Container max-widths for each breakpoint
 */
export const containerMaxWidths = {
  sm: '640px',
  md: '768px',
  lg: '1024px',
  xl: '1280px',
  '2xl': '1536px',
} as const

/**
 * Grid columns for different breakpoints
 */
export const gridColumns = {
  mobile: 4, // 4 columns on mobile
  tablet: 8, // 8 columns on tablet
  desktop: 12, // 12 columns on desktop
} as const

export const grid = {
  columns: gridColumns,
  layout: {
    mobile: 'grid-cols-4',
    tablet: 'grid-cols-8',
    desktop: 'grid-cols-12',
  },
} as const

/**
 * Type definitions
 */
export type DeviceType = keyof typeof deviceBreakpoints
export type MediaQueryType = 'min' | 'max' | 'between' | 'device' | 'special'

/**
 * Utility functions
 */

/**
 * Check if current viewport matches a breakpoint
 * Usage in JS: isBreakpoint('md') - checks if viewport is >= md
 */
export const isBreakpoint = (breakpoint: BreakpointKey): boolean => {
  const win = getWindow()
  if (!win) {
    return false
  }

  return win.matchMedia(`(min-width: ${breakpointValues[breakpoint]}px)`).matches
}

/**
 * Check if current viewport is within a device type
 * Usage: isDevice('mobile')
 */
export const isDevice = (device: DeviceType): boolean => {
  const win = getWindow()
  if (!win) {
    return false
  }

  const { min, max } = deviceBreakpoints[device]
  const minQuery = `(min-width: ${min}px)`
  const query = Number.isFinite(max) ? `${minQuery} and (max-width: ${max}px)` : minQuery
  return win.matchMedia(query).matches
}

/**
 * Get current breakpoint based on viewport width
 * Returns the largest breakpoint that matches
 */
export const getCurrentBreakpoint = (): BreakpointKey => {
  const win = getWindow()
  if (!win) {
    return 'xs'
  }

  for (let i = breakpointEntries.length - 1; i >= 0; i -= 1) {
    const entry = breakpointEntries[i]
    if (!entry) {
      continue
    }

    const [key, value] = entry
    if (win.matchMedia(`(min-width: ${value}px)`).matches) {
      return key
    }
  }

  return 'xs'
}

/**
 * Get current device type
 */
export const getCurrentDevice = (): DeviceType => {
  const win = getWindow()
  if (!win) {
    return 'mobile'
  }

  if (win.matchMedia(`(min-width: ${deviceBreakpoints.desktop.min}px)`).matches) {
    return 'desktop'
  }

  if (win.matchMedia(`(min-width: ${deviceBreakpoints.tablet.min}px)`).matches) {
    return 'tablet'
  }

  return 'mobile'
}

/**
 * Create a custom media query
 * Example: createMediaQuery(768, 1024) => '@media (min-width: 768px) and (max-width: 1024px)'
 */
export const createMediaQuery = (
  min?: number,
  max?: number,
  unit: 'px' | 'em' | 'rem' = 'px'
): string => {
  const queries: string[] = []

  if (min !== undefined) {
    const value = unit === 'px' ? `${min}px` : `${min / 16}${unit}`
    queries.push(`(min-width: ${value})`)
  }

  if (max !== undefined) {
    const value = unit === 'px' ? `${max}px` : `${max / 16}${unit}`
    queries.push(`(max-width: ${value})`)
  }

  return queries.length > 0 ? `@media ${queries.join(' and ')}` : ''
}

/**
 * Responsive value helper
 * Returns appropriate value based on current breakpoint
 *
 * Example:
 * const padding = useResponsiveValue({
 *   mobile: '16px',
 *   tablet: '24px',
 *   desktop: '32px'
 * });
 */
export const getResponsiveValue = <T>(values: Partial<Record<DeviceType, T>>): T | undefined => {
  const device = getCurrentDevice()
  return values[device] ?? values.mobile
}

/**
 * Hook-like utility for responsive values (can be used in React components)
 * Note: This is a helper - actual React hook should use useMediaQuery
 */
export const responsiveValue = <T>(mobile: T, tablet?: T, desktop?: T): T => {
  const device = getCurrentDevice()

  if (device === 'desktop' && desktop !== undefined) {
    return desktop
  }

  if (device === 'tablet' && tablet !== undefined) {
    return tablet
  }

  return mobile
}

export const classByDevice = <T extends string>(values: Record<DeviceType, T>): T =>
  values[getCurrentDevice()]

/**
 * Breakpoint debugging helper
 */
export const debugBreakpoint = (): void => {
  const win = getWindow()
  if (!win) {
    return
  }

  console.warn('Current Breakpoint:', getCurrentBreakpoint())
  console.warn('Current Device:', getCurrentDevice())
  console.warn('Window Width:', win.innerWidth)
}

/**
 * Export all breakpoint utilities
 */
export const breakpointSystem = {
  values: breakpointValues,
  breakpoints,
  deviceBreakpoints,
  mediaQueries,
  containerMaxWidths,
  gridColumns,
  grid,
  media,
  isBreakpoint,
  isDevice,
  getCurrentBreakpoint,
  getCurrentDevice,
  createMediaQuery,
  getResponsiveValue,
  responsiveValue,
  classByDevice,
  debugBreakpoint,
} as const

export default breakpointSystem
