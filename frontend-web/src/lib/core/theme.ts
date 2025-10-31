/**
 * Theme Configuration
 * 
 * Centralized theme configuration and utilities
 * 
 * Features:
 * - Theme color definitions
 * - Theme utilities
 * - Type-safe theme access
 */

export type ThemeMode = 'light' | 'dark' | 'system'

export interface ThemeColors {
  background: string
  foreground: string
  primary: string
  primaryForeground: string
  secondary: string
  secondaryForeground: string
  muted: string
  mutedForeground: string
  accent: string
  accentForeground: string
  destructive: string
  destructiveForeground: string
  border: string
  input: string
  ring: string
  card: string
  cardForeground: string
  popover: string
  popoverForeground: string
}

export const themeConfig = {
  // Light theme colors (HSL values)
  light: {
    background: '0 0% 100%',
    foreground: '222.2 84% 4.9%',
    primary: '221.2 83.2% 53.3%',
    primaryForeground: '210 40% 98%',
    secondary: '210 40% 96.1%',
    secondaryForeground: '222.2 47.4% 11.2%',
    muted: '210 40% 96.1%',
    mutedForeground: '215.4 16.3% 46.9%',
    accent: '210 40% 96.1%',
    accentForeground: '222.2 47.4% 11.2%',
    destructive: '0 84.2% 60.2%',
    destructiveForeground: '210 40% 98%',
    border: '214.3 31.8% 91.4%',
    input: '214.3 31.8% 91.4%',
    ring: '221.2 83.2% 53.3%',
    card: '0 0% 100%',
    cardForeground: '222.2 84% 4.9%',
    popover: '0 0% 100%',
    popoverForeground: '222.2 84% 4.9%',
  },
  // Dark theme colors (HSL values)
  dark: {
    background: '222.2 84% 4.9%',
    foreground: '210 40% 98%',
    primary: '217.2 91.2% 59.8%',
    primaryForeground: '222.2 47.4% 11.2%',
    secondary: '217.2 32.6% 17.5%',
    secondaryForeground: '210 40% 98%',
    muted: '217.2 32.6% 17.5%',
    mutedForeground: '215 20.2% 65.1%',
    accent: '217.2 32.6% 17.5%',
    accentForeground: '210 40% 98%',
    destructive: '0 62.8% 30.6%',
    destructiveForeground: '210 40% 98%',
    border: '217.2 32.6% 17.5%',
    input: '217.2 32.6% 17.5%',
    ring: '224.3 76.3% 48%',
    card: '222.2 84% 4.9%',
    cardForeground: '210 40% 98%',
    popover: '222.2 84% 4.9%',
    popoverForeground: '210 40% 98%',
  },
  // Border radius
  radius: {
    sm: '0.125rem',
    md: '0.375rem',
    lg: '0.5rem',
    xl: '0.75rem',
    full: '9999px',
  },
} as const

/**
 * Get CSS variable name for a theme color
 */
export function getThemeVar(color: keyof ThemeColors): string {
  return `--${color.replace(/([A-Z])/g, '-$1').toLowerCase()}`
}

/**
 * Theme utility functions
 */
export const themeUtils = {
  /**
   * Get HSL color value as CSS variable
   */
  hsl: (color: string): string => `hsl(${color})`,
  
  /**
   * Get HSL color value with opacity
   */
  hsla: (color: string, opacity: number): string => `hsla(${color} / ${opacity})`,
  
  /**
   * Get CSS variable reference
   */
  var: (name: string): string => `var(--${name})`,
}

/**
 * Theme tokens for use in components
 */
export const themeTokens = {
  colors: {
    light: themeConfig.light,
    dark: themeConfig.dark,
  },
  radius: themeConfig.radius,
} as const

