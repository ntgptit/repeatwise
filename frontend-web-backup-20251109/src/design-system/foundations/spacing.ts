/**
 * Spacing System - RepeatWise Design System
 *
 * Consistent spacing scale for:
 * - Margins
 * - Padding
 * - Gaps
 * - Insets
 *
 * Based on 4px base unit (0.25rem)
 * Scale follows Tailwind's spacing scale for consistency
 */

/**
 * Base spacing unit (4px)
 */
const BASE_UNIT = 4

/**
 * Spacing Scale
 * Each value is a multiple of the base unit (4px)
 */
export const spacing = {
  0: '0px', // 0px
  px: '1px', // 1px (special case)
  0.5: '0.125rem', // 2px  (0.5 � 4px)
  1: '0.25rem', // 4px  (1 � 4px)
  1.5: '0.375rem', // 6px  (1.5 � 4px)
  2: '0.5rem', // 8px  (2 � 4px)
  2.5: '0.625rem', // 10px (2.5 � 4px)
  3: '0.75rem', // 12px (3 � 4px)
  3.5: '0.875rem', // 14px (3.5 � 4px)
  4: '1rem', // 16px (4 � 4px)
  5: '1.25rem', // 20px (5 � 4px)
  6: '1.5rem', // 24px (6 � 4px)
  7: '1.75rem', // 28px (7 � 4px)
  8: '2rem', // 32px (8 � 4px)
  9: '2.25rem', // 36px (9 � 4px)
  10: '2.5rem', // 40px (10 � 4px)
  11: '2.75rem', // 44px (11 � 4px)
  12: '3rem', // 48px (12 � 4px)
  14: '3.5rem', // 56px (14 � 4px)
  16: '4rem', // 64px (16 � 4px)
  20: '5rem', // 80px (20 � 4px)
  24: '6rem', // 96px (24 � 4px)
  28: '7rem', // 112px (28 � 4px)
  32: '8rem', // 128px (32 � 4px)
  36: '9rem', // 144px (36 � 4px)
  40: '10rem', // 160px (40 � 4px)
  44: '11rem', // 176px (44 � 4px)
  48: '12rem', // 192px (48 � 4px)
  52: '13rem', // 208px (52 � 4px)
  56: '14rem', // 224px (56 � 4px)
  60: '15rem', // 240px (60 � 4px)
  64: '16rem', // 256px (64 � 4px)
  72: '18rem', // 288px (72 � 4px)
  80: '20rem', // 320px (80 � 4px)
  96: '24rem', // 384px (96 � 4px)
} as const

/**
 * Semantic Spacing - Named spacing for specific use cases
 */
export const semanticSpacing = {
  // Component internal spacing
  component: {
    xs: spacing[1], // 4px - Very tight spacing
    sm: spacing[2], // 8px - Tight spacing (button padding, small gaps)
    md: spacing[4], // 16px - Default spacing (input padding, card padding)
    lg: spacing[6], // 24px - Loose spacing
    xl: spacing[8], // 32px - Very loose spacing
  },

  // Layout spacing (between sections, containers)
  layout: {
    xs: spacing[4], // 16px - Tight layout spacing
    sm: spacing[6], // 24px - Small layout spacing
    md: spacing[8], // 32px - Medium layout spacing
    lg: spacing[12], // 48px - Large layout spacing
    xl: spacing[16], // 64px - Extra large layout spacing
    '2xl': spacing[24], // 96px - Section spacing
    '3xl': spacing[32], // 128px - Page section spacing
  },

  // Container padding
  container: {
    mobile: spacing[4], // 16px - Mobile container padding
    tablet: spacing[6], // 24px - Tablet container padding
    desktop: spacing[8], // 32px - Desktop container padding
  },

  // Card spacing
  card: {
    padding: {
      sm: spacing[3], // 12px - Small card padding
      md: spacing[4], // 16px - Medium card padding
      lg: spacing[6], // 24px - Large card padding
    },
    gap: {
      sm: spacing[2], // 8px - Small card gap
      md: spacing[4], // 16px - Medium card gap
      lg: spacing[6], // 24px - Large card gap
    },
  },

  // Form spacing
  form: {
    fieldGap: spacing[4], // 16px - Gap between form fields
    labelGap: spacing[2], // 8px - Gap between label and input
    helperTextGap: spacing[1], // 4px - Gap between input and helper text
    buttonGap: spacing[3], // 12px - Gap between buttons
    sectionGap: spacing[6], // 24px - Gap between form sections
  },

  // Grid/Flex gaps
  grid: {
    xs: spacing[2], // 8px
    sm: spacing[4], // 16px
    md: spacing[6], // 24px
    lg: spacing[8], // 32px
    xl: spacing[12], // 48px
  },

  // Table spacing
  table: {
    cellPadding: {
      sm: spacing[2], // 8px - Small cell padding
      md: spacing[3], // 12px - Medium cell padding
      lg: spacing[4], // 16px - Large cell padding
    },
    rowGap: spacing[0], // 0px - No gap between rows (border handles this)
  },

  // Modal/Drawer spacing
  modal: {
    padding: spacing[6], // 24px - Modal content padding
    headerPadding: spacing[4], // 16px - Modal header padding
    footerPadding: spacing[4], // 16px - Modal footer padding
    gap: spacing[4], // 16px - Gap between modal sections
  },

  // Navigation spacing
  nav: {
    itemGap: spacing[1], // 4px - Gap between nav items
    sectionGap: spacing[6], // 24px - Gap between nav sections
    padding: spacing[4], // 16px - Nav padding
  },

  // Button spacing
  button: {
    padding: {
      sm: `${spacing[2]} ${spacing[3]}`, // 8px 12px - Small button
      md: `${spacing[2.5]} ${spacing[4]}`, // 10px 16px - Medium button
      lg: `${spacing[3]} ${spacing[6]}`, // 12px 24px - Large button
    },
    gap: spacing[2], // 8px - Gap between icon and text
  },

  // Input spacing
  input: {
    padding: {
      sm: `${spacing[2]} ${spacing[3]}`, // 8px 12px - Small input
      md: `${spacing[2.5]} ${spacing[3.5]}`, // 10px 14px - Medium input
      lg: `${spacing[3]} ${spacing[4]}`, // 12px 16px - Large input
    },
  },

  // Stack spacing (vertical/horizontal component stacking)
  stack: {
    xs: spacing[1], // 4px
    sm: spacing[2], // 8px
    md: spacing[4], // 16px
    lg: spacing[6], // 24px
    xl: spacing[8], // 32px
  },

  // Icon spacing
  icon: {
    gap: spacing[2], // 8px - Gap between icon and text
    margin: spacing[1], // 4px - Icon margin
  },
} as const

export const space = spacing
export const pad = semanticSpacing.component
export const gap = semanticSpacing.stack

/**
 * Inset Spacing (for absolute/fixed positioned elements)
 */
export const insets = {
  0: spacing[0],
  xs: spacing[2], // 8px
  sm: spacing[4], // 16px
  md: spacing[6], // 24px
  lg: spacing[8], // 32px
  xl: spacing[12], // 48px
} as const

/**
 * Responsive Spacing
 * Different spacing values based on breakpoint
 */
export const responsiveSpacing = {
  containerPadding: {
    mobile: spacing[4], // 16px
    tablet: spacing[6], // 24px
    desktop: spacing[8], // 32px
  },
  sectionSpacing: {
    mobile: spacing[8], // 32px
    tablet: spacing[12], // 48px
    desktop: spacing[16], // 64px
  },
  cardSpacing: {
    mobile: spacing[4], // 16px
    tablet: spacing[5], // 20px
    desktop: spacing[6], // 24px
  },
} as const

/**
 * Negative Spacing (for negative margins)
 */
type NegativeSpacing = Record<string, string>

export const negativeSpacing = Object.entries(spacing).reduce<NegativeSpacing>(
  (acc, [key, value]) => {
    if (key !== '0' && key !== 'px') {
      acc[`-${key}`] = `-${value}`
    }
    return acc
  },
  {}
)

/**
 * Type definitions
 */
export type SpacingScale = keyof typeof spacing
export type SemanticSpacingCategory = keyof typeof semanticSpacing
export type InsetScale = keyof typeof insets

/**
 * Spacing utilities
 */

/**
 * Get spacing value by scale
 * Example: getSpacing(4) => '1rem'
 */
export const getSpacing = (scale: SpacingScale): string => {
  return spacing[scale]
}

/**
 * Get multiple spacing values
 * Example: getSpacingMultiple(2, 4) => '0.5rem 1rem'
 */
export const getSpacingMultiple = (...scales: SpacingScale[]): string => {
  return scales.map(scale => spacing[scale]).join(' ')
}

/**
 * Calculate custom spacing (multiple of base unit)
 * Example: calculateSpacing(3.25) => '0.8125rem' (13px)
 */
export const calculateSpacing = (multiplier: number): string => {
  return `${multiplier * 0.25}rem`
}

/**
 * Convert spacing to pixels
 * Example: spacingToPx(4) => 16
 */
export const spacingToPx = (scale: SpacingScale): number => {
  if (scale === 0) {
    return 0
  }

  if (scale === 'px') {
    return 1
  }

  return Number(scale) * BASE_UNIT
}

/**
 * Export all spacing utilities
 */
export const spacingSystem = {
  spacing,
  semanticSpacing,
  insets,
  responsiveSpacing,
  negativeSpacing,
  getSpacing,
  getSpacingMultiple,
  calculateSpacing,
  spacingToPx,
  space,
  pad,
  gap,
  BASE_UNIT,
} as const

export default spacingSystem
