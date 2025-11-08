/**
 * Spacing System
 *
 * Defines consistent spacing scale for margins, padding, and gaps.
 * Based on 4px base unit for a harmonious spacing system.
 *
 * @module design-system/foundations/spacing
 */

/**
 * Spacing scale (4px base unit)
 * Values are in rem for scalability
 */
export const spacing = {
  /**
   * 0px
   */
  0: '0',

  /**
   * 2px (0.125rem)
   */
  0.5: '0.125rem',

  /**
   * 4px (0.25rem)
   */
  1: '0.25rem',

  /**
   * 6px (0.375rem)
   */
  1.5: '0.375rem',

  /**
   * 8px (0.5rem)
   */
  2: '0.5rem',

  /**
   * 10px (0.625rem)
   */
  2.5: '0.625rem',

  /**
   * 12px (0.75rem)
   */
  3: '0.75rem',

  /**
   * 14px (0.875rem)
   */
  3.5: '0.875rem',

  /**
   * 16px (1rem)
   */
  4: '1rem',

  /**
   * 20px (1.25rem)
   */
  5: '1.25rem',

  /**
   * 24px (1.5rem)
   */
  6: '1.5rem',

  /**
   * 28px (1.75rem)
   */
  7: '1.75rem',

  /**
   * 32px (2rem)
   */
  8: '2rem',

  /**
   * 36px (2.25rem)
   */
  9: '2.25rem',

  /**
   * 40px (2.5rem)
   */
  10: '2.5rem',

  /**
   * 44px (2.75rem)
   */
  11: '2.75rem',

  /**
   * 48px (3rem)
   */
  12: '3rem',

  /**
   * 56px (3.5rem)
   */
  14: '3.5rem',

  /**
   * 64px (4rem)
   */
  16: '4rem',

  /**
   * 80px (5rem)
   */
  20: '5rem',

  /**
   * 96px (6rem)
   */
  24: '6rem',

  /**
   * 112px (7rem)
   */
  28: '7rem',

  /**
   * 128px (8rem)
   */
  32: '8rem',

  /**
   * 144px (9rem)
   */
  36: '9rem',

  /**
   * 160px (10rem)
   */
  40: '10rem',

  /**
   * 176px (11rem)
   */
  44: '11rem',

  /**
   * 192px (12rem)
   */
  48: '12rem',

  /**
   * 208px (13rem)
   */
  52: '13rem',

  /**
   * 224px (14rem)
   */
  56: '14rem',

  /**
   * 240px (15rem)
   */
  60: '15rem',

  /**
   * 256px (16rem)
   */
  64: '16rem',

  /**
   * 288px (18rem)
   */
  72: '18rem',

  /**
   * 320px (20rem)
   */
  80: '20rem',

  /**
   * 384px (24rem)
   */
  96: '24rem',
} as const;

/**
 * Semantic spacing tokens for common use cases
 */
export const semanticSpacing = {
  /**
   * Component internal spacing
   */
  component: {
    xs: spacing[1],      // 4px
    sm: spacing[2],      // 8px
    md: spacing[4],      // 16px
    lg: spacing[6],      // 24px
    xl: spacing[8],      // 32px
  },

  /**
   * Layout spacing
   */
  layout: {
    xs: spacing[4],      // 16px
    sm: spacing[6],      // 24px
    md: spacing[8],      // 32px
    lg: spacing[12],     // 48px
    xl: spacing[16],     // 64px
  },

  /**
   * Section spacing
   */
  section: {
    sm: spacing[12],     // 48px
    md: spacing[16],     // 64px
    lg: spacing[24],     // 96px
    xl: spacing[32],     // 128px
  },

  /**
   * Container padding
   */
  container: {
    sm: spacing[4],      // 16px
    md: spacing[6],      // 24px
    lg: spacing[8],      // 32px
    xl: spacing[12],     // 48px
  },
} as const;

/**
 * Type definitions
 */
export type SpacingKey = keyof typeof spacing;
export type SemanticSpacingKey = keyof typeof semanticSpacing;

/**
 * Helper function to get spacing value
 *
 * @param key - Spacing key
 * @returns Spacing value in rem
 *
 * @example
 * ```ts
 * getSpacing(4) // '1rem'
 * getSpacing(8) // '2rem'
 * ```
 */
export const getSpacing = (key: SpacingKey): string => {
  return spacing[key];
};

/**
 * Re-export for convenience
 */
export default spacing;
