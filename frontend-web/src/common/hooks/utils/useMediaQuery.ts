/**
 * useMediaQuery Hook
 *
 * Match CSS media queries in React
 */

import { useEffect, useState } from 'react'
import { breakpointValues } from '@/design-system/foundations/breakpoints'

export const useMediaQuery = (query: string): boolean => {
  const [matches, setMatches] = useState<boolean>(
    globalThis.window === undefined ? false : globalThis.window.matchMedia(query).matches
  )

  useEffect(() => {
    const mediaQuery = globalThis.window.matchMedia(query)

    const handleChange = (event: MediaQueryListEvent) => {
      setMatches(event.matches)
    }

    mediaQuery.addEventListener('change', handleChange)

    // Set initial value
    setMatches(mediaQuery.matches)

    return () => {
      mediaQuery.removeEventListener('change', handleChange)
    }
  }, [query])

  return matches
}

// Convenience hooks for common breakpoints
export const useIsMobile = (): boolean => {
  return useMediaQuery(`(max-width: ${breakpointValues.md - 1}px)`)
}

export const useIsTablet = (): boolean => {
  return useMediaQuery(
    `(min-width: ${breakpointValues.md}px) and (max-width: ${breakpointValues.lg - 1}px)`
  )
}

export const useIsDesktop = (): boolean => {
  return useMediaQuery(`(min-width: ${breakpointValues.lg}px)`)
}

export default useMediaQuery
