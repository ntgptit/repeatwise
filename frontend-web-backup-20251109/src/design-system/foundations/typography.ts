/**
 * Typography System - RepeatWise Design System
 *
 * Complete typography system including:
 * - Font families (sans, serif, mono)
 * - Font sizes with line heights
 * - Font weights
 * - Letter spacing
 * - Text styles (headings, body, captions)
 *
 * Based on responsive breakpoints:
 * - Desktop (> 1024px)
 * - Tablet (768px - 1024px)
 * - Mobile (< 768px)
 */

/**
 * Font Families
 */
export const fontFamilies = {
  sans: [
    'Inter',
    '-apple-system',
    'BlinkMacSystemFont',
    'Segoe UI',
    'Roboto',
    'Helvetica Neue',
    'Arial',
    'sans-serif',
  ].join(', '),

  serif: ['Georgia', 'Cambria', 'Times New Roman', 'Times', 'serif'].join(', '),

  mono: [
    'JetBrains Mono',
    'Monaco',
    'Consolas',
    'Liberation Mono',
    'Courier New',
    'monospace',
  ].join(', '),
} as const

/**
 * Font Weights
 */
export const fontWeights = {
  thin: 100,
  extralight: 200,
  light: 300,
  normal: 400,
  medium: 500,
  semibold: 600,
  bold: 700,
  extrabold: 800,
  black: 900,
} as const

/**
 * Font Sizes with corresponding line heights
 * Scale: xs, sm, base, lg, xl, 2xl, 3xl, 4xl, 5xl, 6xl, 7xl, 8xl, 9xl
 */
export const fontSizes = {
  xs: {
    fontSize: '0.75rem', // 12px
    lineHeight: '1rem', // 16px
  },
  sm: {
    fontSize: '0.875rem', // 14px
    lineHeight: '1.25rem', // 20px
  },
  base: {
    fontSize: '1rem', // 16px
    lineHeight: '1.5rem', // 24px
  },
  lg: {
    fontSize: '1.125rem', // 18px
    lineHeight: '1.75rem', // 28px
  },
  xl: {
    fontSize: '1.25rem', // 20px
    lineHeight: '1.75rem', // 28px
  },
  '2xl': {
    fontSize: '1.5rem', // 24px
    lineHeight: '2rem', // 32px
  },
  '3xl': {
    fontSize: '1.875rem', // 30px
    lineHeight: '2.25rem', // 36px
  },
  '4xl': {
    fontSize: '2.25rem', // 36px
    lineHeight: '2.5rem', // 40px
  },
  '5xl': {
    fontSize: '3rem', // 48px
    lineHeight: '1', // 48px
  },
  '6xl': {
    fontSize: '3.75rem', // 60px
    lineHeight: '1', // 60px
  },
  '7xl': {
    fontSize: '4.5rem', // 72px
    lineHeight: '1', // 72px
  },
  '8xl': {
    fontSize: '6rem', // 96px
    lineHeight: '1', // 96px
  },
  '9xl': {
    fontSize: '8rem', // 128px
    lineHeight: '1', // 128px
  },
} as const

/**
 * Letter Spacing
 */
export const letterSpacing = {
  tighter: '-0.05em',
  tight: '-0.025em',
  normal: '0em',
  wide: '0.025em',
  wider: '0.05em',
  widest: '0.1em',
} as const

/**
 * Line Heights (standalone)
 */
export const lineHeights = {
  none: '1',
  tight: '1.25',
  snug: '1.375',
  normal: '1.5',
  relaxed: '1.625',
  loose: '2',
} as const

/**
 * Fluid typography helper
 * Generates clamp-based font sizes that scale with viewport width
 */
export const fluid = (minSize: string, maxSize: string): string => {
  return `clamp(${minSize}, 1vw + ${minSize}, ${maxSize})`
}

/**
 * Text Styles - Pre-configured text combinations
 * Usage: Apply these to components for consistent typography
 */
export const textStyles = {
  // Display Styles (Large headings, hero text)
  display: {
    large: {
      fontFamily: fontFamilies.sans,
      fontSize: fluid('2.5rem', '3.75rem'),
      lineHeight: 1.1,
      fontWeight: fontWeights.bold,
      letterSpacing: letterSpacing.tight,
    },
    medium: {
      fontFamily: fontFamilies.sans,
      fontSize: fontSizes['5xl'].fontSize,
      lineHeight: fontSizes['5xl'].lineHeight,
      fontWeight: fontWeights.bold,
      letterSpacing: letterSpacing.tight,
    },
    small: {
      fontFamily: fontFamilies.sans,
      fontSize: fontSizes['4xl'].fontSize,
      lineHeight: fontSizes['4xl'].lineHeight,
      fontWeight: fontWeights.bold,
      letterSpacing: letterSpacing.tight,
    },
  },

  // Headings (H1-H6)
  heading: {
    h1: {
      fontFamily: fontFamilies.sans,
      fontSize: fontSizes['3xl'].fontSize,
      lineHeight: fontSizes['3xl'].lineHeight,
      fontWeight: fontWeights.bold,
      letterSpacing: letterSpacing.tight,
    },
    h2: {
      fontFamily: fontFamilies.sans,
      fontSize: fontSizes['2xl'].fontSize,
      lineHeight: fontSizes['2xl'].lineHeight,
      fontWeight: fontWeights.semibold,
      letterSpacing: letterSpacing.tight,
    },
    h3: {
      fontFamily: fontFamilies.sans,
      fontSize: fontSizes.xl.fontSize,
      lineHeight: fontSizes.xl.lineHeight,
      fontWeight: fontWeights.semibold,
      letterSpacing: letterSpacing.normal,
    },
    h4: {
      fontFamily: fontFamilies.sans,
      fontSize: fontSizes.lg.fontSize,
      lineHeight: fontSizes.lg.lineHeight,
      fontWeight: fontWeights.semibold,
      letterSpacing: letterSpacing.normal,
    },
    h5: {
      fontFamily: fontFamilies.sans,
      fontSize: fontSizes.base.fontSize,
      lineHeight: fontSizes.base.lineHeight,
      fontWeight: fontWeights.semibold,
      letterSpacing: letterSpacing.normal,
    },
    h6: {
      fontFamily: fontFamilies.sans,
      fontSize: fontSizes.sm.fontSize,
      lineHeight: fontSizes.sm.lineHeight,
      fontWeight: fontWeights.semibold,
      letterSpacing: letterSpacing.wide,
    },
  },

  // Body Text
  body: {
    large: {
      fontFamily: fontFamilies.sans,
      fontSize: fontSizes.lg.fontSize,
      lineHeight: fontSizes.lg.lineHeight,
      fontWeight: fontWeights.normal,
      letterSpacing: letterSpacing.normal,
    },
    base: {
      fontFamily: fontFamilies.sans,
      fontSize: fontSizes.base.fontSize,
      lineHeight: fontSizes.base.lineHeight,
      fontWeight: fontWeights.normal,
      letterSpacing: letterSpacing.normal,
    },
    small: {
      fontFamily: fontFamilies.sans,
      fontSize: fontSizes.sm.fontSize,
      lineHeight: fontSizes.sm.lineHeight,
      fontWeight: fontWeights.normal,
      letterSpacing: letterSpacing.normal,
    },
  },

  // Caption/Helper Text
  caption: {
    large: {
      fontFamily: fontFamilies.sans,
      fontSize: fontSizes.sm.fontSize,
      lineHeight: fontSizes.sm.lineHeight,
      fontWeight: fontWeights.medium,
      letterSpacing: letterSpacing.normal,
    },
    base: {
      fontFamily: fontFamilies.sans,
      fontSize: fontSizes.xs.fontSize,
      lineHeight: fontSizes.xs.lineHeight,
      fontWeight: fontWeights.medium,
      letterSpacing: letterSpacing.normal,
    },
  },

  // Label Text (for form inputs, etc.)
  label: {
    large: {
      fontFamily: fontFamilies.sans,
      fontSize: fontSizes.base.fontSize,
      lineHeight: fontSizes.base.lineHeight,
      fontWeight: fontWeights.medium,
      letterSpacing: letterSpacing.normal,
    },
    base: {
      fontFamily: fontFamilies.sans,
      fontSize: fontSizes.sm.fontSize,
      lineHeight: fontSizes.sm.lineHeight,
      fontWeight: fontWeights.medium,
      letterSpacing: letterSpacing.normal,
    },
    small: {
      fontFamily: fontFamilies.sans,
      fontSize: fontSizes.xs.fontSize,
      lineHeight: fontSizes.xs.lineHeight,
      fontWeight: fontWeights.medium,
      letterSpacing: letterSpacing.wide,
    },
  },

  // Button Text
  button: {
    large: {
      fontFamily: fontFamilies.sans,
      fontSize: fontSizes.base.fontSize,
      lineHeight: fontSizes.base.lineHeight,
      fontWeight: fontWeights.semibold,
      letterSpacing: letterSpacing.normal,
    },
    base: {
      fontFamily: fontFamilies.sans,
      fontSize: fontSizes.sm.fontSize,
      lineHeight: fontSizes.sm.lineHeight,
      fontWeight: fontWeights.semibold,
      letterSpacing: letterSpacing.normal,
    },
    small: {
      fontFamily: fontFamilies.sans,
      fontSize: fontSizes.xs.fontSize,
      lineHeight: fontSizes.xs.lineHeight,
      fontWeight: fontWeights.semibold,
      letterSpacing: letterSpacing.wide,
    },
  },

  // Code/Monospace
  code: {
    large: {
      fontFamily: fontFamilies.mono,
      fontSize: fontSizes.base.fontSize,
      lineHeight: fontSizes.base.lineHeight,
      fontWeight: fontWeights.normal,
      letterSpacing: letterSpacing.normal,
    },
    base: {
      fontFamily: fontFamilies.mono,
      fontSize: fontSizes.sm.fontSize,
      lineHeight: fontSizes.sm.lineHeight,
      fontWeight: fontWeights.normal,
      letterSpacing: letterSpacing.normal,
    },
    small: {
      fontFamily: fontFamilies.mono,
      fontSize: fontSizes.xs.fontSize,
      lineHeight: fontSizes.xs.lineHeight,
      fontWeight: fontWeights.normal,
      letterSpacing: letterSpacing.normal,
    },
  },

  // Link Text
  link: {
    large: {
      fontFamily: fontFamilies.sans,
      fontSize: fontSizes.base.fontSize,
      lineHeight: fontSizes.base.lineHeight,
      fontWeight: fontWeights.medium,
      letterSpacing: letterSpacing.normal,
      textDecoration: 'underline',
    },
    base: {
      fontFamily: fontFamilies.sans,
      fontSize: fontSizes.sm.fontSize,
      lineHeight: fontSizes.sm.lineHeight,
      fontWeight: fontWeights.medium,
      letterSpacing: letterSpacing.normal,
      textDecoration: 'underline',
    },
    small: {
      fontFamily: fontFamilies.sans,
      fontSize: fontSizes.xs.fontSize,
      lineHeight: fontSizes.xs.lineHeight,
      fontWeight: fontWeights.medium,
      letterSpacing: letterSpacing.normal,
      textDecoration: 'underline',
    },
  },
} as const

/**
 * Responsive Typography Utilities
 * Apply different typography based on screen size
 */
export const responsiveTextStyles = {
  hero: {
    mobile: textStyles.display.small,
    tablet: textStyles.display.medium,
    desktop: textStyles.display.large,
  },
  pageTitle: {
    mobile: textStyles.heading.h2,
    tablet: textStyles.heading.h1,
    desktop: textStyles.heading.h1,
  },
  sectionTitle: {
    mobile: textStyles.heading.h3,
    tablet: textStyles.heading.h2,
    desktop: textStyles.heading.h2,
  },
  cardTitle: {
    mobile: textStyles.heading.h5,
    tablet: textStyles.heading.h4,
    desktop: textStyles.heading.h4,
  },
} as const

/**
 * Type definitions
 */
export type FontFamily = keyof typeof fontFamilies
export type FontWeight = keyof typeof fontWeights
export type FontSize = keyof typeof fontSizes
export type LetterSpacing = keyof typeof letterSpacing
export type LineHeight = keyof typeof lineHeights
export type TextStyleCategory = keyof typeof textStyles
export type TextStylePath = {
  [Category in TextStyleCategory]: Category extends string
    ? `${Category}.${Extract<keyof (typeof textStyles)[Category], string>}`
    : never
}[TextStyleCategory]

/**
 * Create text style token
 * Returns a copy of the requested text style for safe mutation/override
 */
export const createTextStyle = (path: TextStylePath) => {
  const [category, variant] = path.split('.') as [TextStyleCategory, string]
  const categoryStyles = textStyles[category]

  if (!categoryStyles) {
    throw new Error(`Unknown text style category: ${category}`)
  }

  const styleKey = variant as keyof typeof categoryStyles
  const baseStyle = categoryStyles[styleKey]

  if (!baseStyle) {
    throw new Error(`Unknown text style variant: ${path}`)
  }

  if (typeof baseStyle !== 'object' || baseStyle === null) {
    throw new Error(`Text style "${path}" is not an object and cannot be cloned`)
  }

  const styleObject = baseStyle as Record<string, unknown>

  return { ...styleObject } as (typeof categoryStyles)[typeof styleKey]
}

/**
 * Typography utilities
 */
export const typography = {
  fontFamilies,
  fontWeights,
  fontSizes,
  letterSpacing,
  lineHeights,
  textStyles,
  responsiveTextStyles,
  createTextStyle,
} as const

export const t = textStyles

export default typography
