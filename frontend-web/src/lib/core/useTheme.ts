/**
 * Theme Hook
 * 
 * Custom hook for theme management
 */

import { useTheme as useNextTheme } from 'next-themes'
import type { ThemeMode } from './theme'

export interface UseThemeReturn {
  theme: ThemeMode | undefined
  setTheme: (theme: ThemeMode | 'system') => void
  resolvedTheme: 'light' | 'dark' | undefined
  systemTheme: 'light' | 'dark' | undefined
}

/**
 * Custom hook for theme management
 * Wraps next-themes useTheme with better typing
 */
export function useTheme(): UseThemeReturn {
  const { theme, setTheme, resolvedTheme, systemTheme } = useNextTheme()
  
  return {
    theme: theme as ThemeMode | undefined,
    setTheme: setTheme as (theme: ThemeMode | 'system') => void,
    resolvedTheme: resolvedTheme as 'light' | 'dark' | undefined,
    systemTheme: systemTheme as 'light' | 'dark' | undefined,
  }
}

