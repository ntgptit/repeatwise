/**
 * Color System - RepeatWise Design System
 *
 * Complete color palette including:
 * - Brand colors (primary, secondary)
 * - Semantic colors (success, warning, error, info)
 * - Neutral colors (grayscale)
 * - UI colors (background, foreground, border)
 *
 * All colors support both light and dark themes using CSS variables
 */

const brand = {
  // Brand Colors - Primary (Blue)
  primary: {
    50: '#eff6ff',
    100: '#dbeafe',
    200: '#bfdbfe',
    300: '#93c5fd',
    400: '#60a5fa',
    500: '#3b82f6', // Main brand color
    600: '#2563eb',
    700: '#1d4ed8',
    800: '#1e40af',
    900: '#1e3a8a',
    950: '#172554',
  },
  // Secondary (Purple/Indigo)
  secondary: {
    50: '#eef2ff',
    100: '#e0e7ff',
    200: '#c7d2fe',
    300: '#a5b4fc',
    400: '#818cf8',
    500: '#6366f1', // Main secondary color
    600: '#4f46e5',
    700: '#4338ca',
    800: '#3730a3',
    900: '#312e81',
    950: '#1e1b4b',
  },
} as const

const semantic = {
  // Success (Green)
  success: {
    50: '#f0fdf4',
    100: '#dcfce7',
    200: '#bbf7d0',
    300: '#86efac',
    400: '#4ade80',
    500: '#22c55e', // Main success color
    600: '#16a34a',
    700: '#15803d',
    800: '#166534',
    900: '#14532d',
    950: '#052e16',
  },
  // Warning (Amber)
  warning: {
    50: '#fffbeb',
    100: '#fef3c7',
    200: '#fde68a',
    300: '#fcd34d',
    400: '#fbbf24',
    500: '#f59e0b', // Main warning color
    600: '#d97706',
    700: '#b45309',
    800: '#92400e',
    900: '#78350f',
    950: '#451a03',
  },
  // Error/Destructive (Red)
  error: {
    50: '#fef2f2',
    100: '#fee2e2',
    200: '#fecaca',
    300: '#fca5a5',
    400: '#f87171',
    500: '#ef4444', // Main error color
    600: '#dc2626',
    700: '#b91c1c',
    800: '#991b1b',
    900: '#7f1d1d',
    950: '#450a0a',
  },
  // Info (Cyan)
  info: {
    50: '#ecfeff',
    100: '#cffafe',
    200: '#a5f3fc',
    300: '#67e8f9',
    400: '#22d3ee',
    500: '#06b6d4', // Main info color
    600: '#0891b2',
    700: '#0e7490',
    800: '#155e75',
    900: '#164e63',
    950: '#083344',
  },
} as const

const neutral = {
  // Slate (Cool Gray)
  slate: {
    50: '#f8fafc',
    100: '#f1f5f9',
    200: '#e2e8f0',
    300: '#cbd5e1',
    400: '#94a3b8',
    500: '#64748b',
    600: '#475569',
    700: '#334155',
    800: '#1e293b',
    900: '#0f172a',
    950: '#020617',
  },
  // Gray (Neutral)
  gray: {
    50: '#f9fafb',
    100: '#f3f4f6',
    200: '#e5e7eb',
    300: '#d1d5db',
    400: '#9ca3af',
    500: '#6b7280',
    600: '#4b5563',
    700: '#374151',
    800: '#1f2937',
    900: '#111827',
    950: '#030712',
  },
} as const

const ui = {
  // Light theme defaults
  light: {
    background: '0 0% 100%', // White
    foreground: '222.2 84% 4.9%', // Near black

    primary: '221.2 83.2% 53.3%', // Blue 500
    primaryForeground: '210 40% 98%', // Near white

    secondary: '210 40% 96.1%', // Blue-gray 100
    secondaryForeground: '222.2 47.4% 11.2%', // Dark gray

    muted: '210 40% 96.1%', // Light gray
    mutedForeground: '215.4 16.3% 46.9%', // Medium gray

    accent: '210 40% 96.1%', // Light blue-gray
    accentForeground: '222.2 47.4% 11.2%', // Dark text

    destructive: '0 84.2% 60.2%', // Red 500
    destructiveForeground: '210 40% 98%', // Near white

    success: '142 71% 45%', // Green 600
    successForeground: '210 40% 98%', // Near white

    warning: '38 92% 50%', // Amber 500
    warningForeground: '222.2 47.4% 11.2%', // Dark text

    info: '188 94% 42%', // Cyan 600
    infoForeground: '210 40% 98%', // Near white

    border: '214.3 31.8% 91.4%', // Light border
    input: '214.3 31.8% 91.4%', // Input border
    ring: '221.2 83.2% 53.3%', // Focus ring (primary)

    card: '0 0% 100%', // White
    cardForeground: '222.2 84% 4.9%', // Dark text

    popover: '0 0% 100%', // White
    popoverForeground: '222.2 84% 4.9%', // Dark text
  },

  // Dark theme defaults
  dark: {
    background: '222.2 84% 4.9%', // Very dark blue-gray
    foreground: '210 40% 98%', // Near white

    primary: '217.2 91.2% 59.8%', // Lighter blue
    primaryForeground: '222.2 47.4% 11.2%', // Dark text

    secondary: '217.2 32.6% 17.5%', // Dark blue-gray
    secondaryForeground: '210 40% 98%', // Light text

    muted: '217.2 32.6% 17.5%', // Dark muted
    mutedForeground: '215 20.2% 65.1%', // Medium light gray

    accent: '217.2 32.6% 17.5%', // Dark accent
    accentForeground: '210 40% 98%', // Light text

    destructive: '0 62.8% 30.6%', // Dark red
    destructiveForeground: '210 40% 98%', // Light text

    success: '142 71% 35%', // Dark green
    successForeground: '210 40% 98%', // Light text

    warning: '38 92% 40%', // Dark amber
    warningForeground: '210 40% 98%', // Light text

    info: '188 94% 32%', // Dark cyan
    infoForeground: '210 40% 98%', // Light text

    border: '217.2 32.6% 17.5%', // Dark border
    input: '217.2 32.6% 17.5%', // Input border
    ring: '224.3 76.3% 48%', // Focus ring

    card: '222.2 84% 4.9%', // Very dark
    cardForeground: '210 40% 98%', // Light text

    popover: '222.2 84% 4.9%', // Very dark
    popoverForeground: '210 40% 98%', // Light text
  },
} as const

const createColors = () => {
  const srs = {
    get box1(): string {
      return semantic.error[300]
    },
    get box2(): string {
      return semantic.warning[400]
    },
    get box3(): string {
      return semantic.warning[300]
    },
    get box4(): string {
      return semantic.success[300]
    },
    get box5(): string {
      return semantic.success[200]
    },
    get box6(): string {
      return semantic.info[300]
    },
    get box7(): string {
      return brand.secondary[300]
    },
  }

  const special = {
    get white(): string {
      return ui.light.background
    },
    get black(): string {
      return ui.dark.background
    },
    get transparent(): string {
      return 'transparent'
    },
    get current(): string {
      return 'currentColor'
    },
  }

  return {
    brand,
    semantic,
    neutral,
    ui,
    srs,
    special,
  } as const
}

export const colors = createColors()

export const tailwindColors = {
  primary: colors.brand.primary,
  secondary: colors.brand.secondary,
  success: colors.semantic.success,
  warning: colors.semantic.warning,
  error: colors.semantic.error,
  info: colors.semantic.info,
  slate: colors.neutral.slate,
  gray: colors.neutral.gray,
} as const

/**
 * Type-safe color utilities
 */
export type ColorPalette = typeof colors
export type BrandColor = keyof typeof colors.brand
export type SemanticColor = keyof typeof colors.semantic
export type NeutralColor = keyof typeof colors.neutral

/**
 * Brand color shades type
 */
type BrandColorShades = (typeof colors.brand)[BrandColor]
type BrandColorShade = keyof BrandColorShades

/**
 * Semantic color shades type
 */
type SemanticColorShades = (typeof colors.semantic)[SemanticColor]
type SemanticColorShade = keyof SemanticColorShades

/**
 * Neutral color shades type
 */
type NeutralColorShades = (typeof colors.neutral)[NeutralColor]
type NeutralColorShade = keyof NeutralColorShades

type ColorCategoryMap = Pick<typeof colors, 'brand' | 'semantic' | 'neutral'>

/**
 * Get brand color (type-safe)
 * @example getBrandColor('primary', 500) => '#3b82f6'
 */
export const getBrandColor = (color: BrandColor, shade: BrandColorShade): string => {
  return colors.brand[color][shade]
}

/**
 * Get semantic color (type-safe)
 * @example getSemanticColor('success', 500) => '#22c55e'
 */
export const getSemanticColor = (color: SemanticColor, shade: SemanticColorShade): string => {
  return colors.semantic[color][shade]
}

/**
 * Get neutral color (type-safe)
 * @example getNeutralColor('gray', 500) => '#6b7280'
 */
export const getNeutralColor = (color: NeutralColor, shade: NeutralColorShade): string => {
  return colors.neutral[color][shade]
}

/**
 * Get SRS box color (type-safe)
 * @example getSRSColor('box1') => '#fca5a5'
 */
export const getSRSColor = (box: keyof typeof colors.srs): string => {
  return colors.srs[box]
}

/**
 * Legacy getColor - deprecated, use specific functions instead
 * @deprecated Use getBrandColor, getSemanticColor, or getNeutralColor instead
 */
export function getColor(category: 'brand', color: BrandColor, shade?: BrandColorShade): string
export function getColor(
  category: 'semantic',
  color: SemanticColor,
  shade?: SemanticColorShade
): string
export function getColor(
  category: 'neutral',
  color: NeutralColor,
  shade?: NeutralColorShade
): string
export function getColor(category: keyof ColorCategoryMap, color: string, shade?: number): string {
  const palette = colors[category]
  const colorEntry = palette[color as keyof typeof palette]

  if (!colorEntry) {
    throw new Error(`[colors] Invalid color "${color}" for category "${category}"`)
  }

  if (typeof colorEntry === 'string') {
    return colorEntry
  }

  const resolvedShade = (shade ?? 500) as keyof typeof colorEntry
  const value = colorEntry[resolvedShade]

  if (!value) {
    throw new Error(
      `[colors] Invalid shade "${String(resolvedShade)}" for ${String(category)}.${String(color)}`
    )
  }

  return value
}

/**
 * Generate CSS variables for theme
 */
export const generateCSSVariables = (theme: 'light' | 'dark' = 'light') => {
  const themeColors = colors.ui[theme]
  return Object.entries(themeColors)
    .map(([key, value]) => `--color-${key.replaceAll(/([A-Z])/g, '-$1').toLowerCase()}: ${value};`)
    .join('\n  ')
}

export default colors
