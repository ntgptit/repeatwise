/**
 * Typography System
 *
 * Defines font families, sizes, weights, line heights, and letter spacing
 * for consistent typography across the application.
 *
 * @module design-system/foundations/typography
 */

/**
 * Font family definitions
 */
export const fontFamily = {
  /**
   * Primary sans-serif font stack
   */
  sans: [
    'Inter',
    'system-ui',
    '-apple-system',
    'BlinkMacSystemFont',
    'Segoe UI',
    'Roboto',
    'Helvetica Neue',
    'Arial',
    'sans-serif',
  ].join(', '),

  /**
   * Monospace font stack for code
   */
  mono: [
    'JetBrains Mono',
    'Fira Code',
    'Consolas',
    'Monaco',
    'Courier New',
    'monospace',
  ].join(', '),
} as const;

/**
 * Font size scale (rem-based for accessibility)
 */
export const fontSize = {
  /**
   * Extra small text (10px)
   */
  xs: '0.625rem',

  /**
   * Small text (12px)
   */
  sm: '0.75rem',

  /**
   * Base text size (14px)
   */
  base: '0.875rem',

  /**
   * Medium text (16px)
   */
  md: '1rem',

  /**
   * Large text (18px)
   */
  lg: '1.125rem',

  /**
   * Extra large text (20px)
   */
  xl: '1.25rem',

  /**
   * Heading sizes
   */
  '2xl': '1.5rem',    // 24px
  '3xl': '1.875rem',  // 30px
  '4xl': '2.25rem',   // 36px
  '5xl': '3rem',      // 48px
  '6xl': '3.75rem',   // 60px
  '7xl': '4.5rem',    // 72px
} as const;

/**
 * Font weight scale
 */
export const fontWeight = {
  /**
   * Thin weight (100)
   */
  thin: '100',

  /**
   * Extra light weight (200)
   */
  extralight: '200',

  /**
   * Light weight (300)
   */
  light: '300',

  /**
   * Normal/regular weight (400)
   */
  normal: '400',

  /**
   * Medium weight (500)
   */
  medium: '500',

  /**
   * Semibold weight (600)
   */
  semibold: '600',

  /**
   * Bold weight (700)
   */
  bold: '700',

  /**
   * Extra bold weight (800)
   */
  extrabold: '800',

  /**
   * Black weight (900)
   */
  black: '900',
} as const;

/**
 * Line height scale
 */
export const lineHeight = {
  /**
   * Tighter line height (1.25)
   */
  tight: '1.25',

  /**
   * Slightly tight line height (1.375)
   */
  snug: '1.375',

  /**
   * Normal line height (1.5)
   */
  normal: '1.5',

  /**
   * Relaxed line height (1.625)
   */
  relaxed: '1.625',

  /**
   * Loose line height (2)
   */
  loose: '2',
} as const;

/**
 * Letter spacing scale
 */
export const letterSpacing = {
  /**
   * Tighter letter spacing (-0.05em)
   */
  tighter: '-0.05em',

  /**
   * Tight letter spacing (-0.025em)
   */
  tight: '-0.025em',

  /**
   * Normal letter spacing (0)
   */
  normal: '0',

  /**
   * Wide letter spacing (0.025em)
   */
  wide: '0.025em',

  /**
   * Wider letter spacing (0.05em)
   */
  wider: '0.05em',

  /**
   * Widest letter spacing (0.1em)
   */
  widest: '0.1em',
} as const;

/**
 * Typography presets for common text styles
 */
export const textStyles = {
  /**
   * Display heading (largest)
   */
  displayLg: {
    fontSize: fontSize['7xl'],
    fontWeight: fontWeight.bold,
    lineHeight: lineHeight.tight,
    letterSpacing: letterSpacing.tight,
  },

  /**
   * Display heading (medium)
   */
  displayMd: {
    fontSize: fontSize['6xl'],
    fontWeight: fontWeight.bold,
    lineHeight: lineHeight.tight,
    letterSpacing: letterSpacing.tight,
  },

  /**
   * Display heading (small)
   */
  displaySm: {
    fontSize: fontSize['5xl'],
    fontWeight: fontWeight.bold,
    lineHeight: lineHeight.tight,
    letterSpacing: letterSpacing.tight,
  },

  /**
   * H1 heading
   */
  h1: {
    fontSize: fontSize['4xl'],
    fontWeight: fontWeight.bold,
    lineHeight: lineHeight.tight,
    letterSpacing: letterSpacing.tight,
  },

  /**
   * H2 heading
   */
  h2: {
    fontSize: fontSize['3xl'],
    fontWeight: fontWeight.semibold,
    lineHeight: lineHeight.snug,
    letterSpacing: letterSpacing.tight,
  },

  /**
   * H3 heading
   */
  h3: {
    fontSize: fontSize['2xl'],
    fontWeight: fontWeight.semibold,
    lineHeight: lineHeight.snug,
    letterSpacing: letterSpacing.normal,
  },

  /**
   * H4 heading
   */
  h4: {
    fontSize: fontSize.xl,
    fontWeight: fontWeight.semibold,
    lineHeight: lineHeight.normal,
    letterSpacing: letterSpacing.normal,
  },

  /**
   * H5 heading
   */
  h5: {
    fontSize: fontSize.lg,
    fontWeight: fontWeight.medium,
    lineHeight: lineHeight.normal,
    letterSpacing: letterSpacing.normal,
  },

  /**
   * H6 heading
   */
  h6: {
    fontSize: fontSize.base,
    fontWeight: fontWeight.medium,
    lineHeight: lineHeight.normal,
    letterSpacing: letterSpacing.normal,
  },

  /**
   * Body large text
   */
  bodyLg: {
    fontSize: fontSize.md,
    fontWeight: fontWeight.normal,
    lineHeight: lineHeight.relaxed,
    letterSpacing: letterSpacing.normal,
  },

  /**
   * Body regular text
   */
  body: {
    fontSize: fontSize.base,
    fontWeight: fontWeight.normal,
    lineHeight: lineHeight.normal,
    letterSpacing: letterSpacing.normal,
  },

  /**
   * Body small text
   */
  bodySm: {
    fontSize: fontSize.sm,
    fontWeight: fontWeight.normal,
    lineHeight: lineHeight.normal,
    letterSpacing: letterSpacing.normal,
  },

  /**
   * Caption text
   */
  caption: {
    fontSize: fontSize.xs,
    fontWeight: fontWeight.normal,
    lineHeight: lineHeight.normal,
    letterSpacing: letterSpacing.wide,
  },

  /**
   * Overline text
   */
  overline: {
    fontSize: fontSize.xs,
    fontWeight: fontWeight.semibold,
    lineHeight: lineHeight.normal,
    letterSpacing: letterSpacing.widest,
    textTransform: 'uppercase' as const,
  },

  /**
   * Code text
   */
  code: {
    fontSize: fontSize.sm,
    fontFamily: fontFamily.mono,
    fontWeight: fontWeight.normal,
    lineHeight: lineHeight.normal,
    letterSpacing: letterSpacing.normal,
  },
} as const;

/**
 * Type definitions
 */
export type FontSize = keyof typeof fontSize;
export type FontWeight = keyof typeof fontWeight;
export type LineHeight = keyof typeof lineHeight;
export type LetterSpacing = keyof typeof letterSpacing;
export type TextStyle = keyof typeof textStyles;

/**
 * Typography configuration object
 */
export const typography = {
  fontFamily,
  fontSize,
  fontWeight,
  lineHeight,
  letterSpacing,
  textStyles,
} as const;

/**
 * Re-export for convenience
 */
export default typography;
