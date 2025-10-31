/**
 * Theme Provider Component
 * 
 * Enhanced theme provider with better configuration
 */

import * as React from 'react'
import { ThemeProvider as NextThemeProvider } from 'next-themes'
import type { ThemeProviderProps } from 'next-themes'

export interface ThemeProviderConfig extends Omit<ThemeProviderProps, 'children'> {
  children: React.ReactNode
  storageKey?: string
  enableSystem?: boolean
  disableTransitionOnChange?: boolean
}

/**
 * Enhanced Theme Provider
 * 
 * Wraps next-themes ThemeProvider with better defaults
 */
export function ThemeProvider({
  children,
  storageKey = 'repeatwise-theme',
  enableSystem = true,
  disableTransitionOnChange = false,
  defaultTheme = 'system',
  attribute = 'class',
  ...props
}: ThemeProviderConfig) {
  const [mounted, setMounted] = React.useState(false)

  React.useEffect(() => {
    setMounted(true)
    
    // Disable transitions on theme change if requested
    if (disableTransitionOnChange) {
      const observer = new MutationObserver(() => {
        const root = document.documentElement
        root.classList.add('disable-transitions')
        setTimeout(() => {
          root.classList.remove('disable-transitions')
        }, 1)
      })
      
      observer.observe(document.documentElement, {
        attributes: true,
        attributeFilter: ['class'],
      })
      
      return () => observer.disconnect()
    }
  }, [disableTransitionOnChange])

  // Prevent hydration mismatch
  if (!mounted) {
    return <>{children}</>
  }

  return (
    <NextThemeProvider
      storageKey={storageKey}
      enableSystem={enableSystem}
      defaultTheme={defaultTheme}
      attribute={attribute}
      {...props}
    >
      {children}
    </NextThemeProvider>
  )
}

