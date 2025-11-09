import { useEffect, useState } from 'react'

import {
  type BreakpointKey,
  breakpointValues,
  getCurrentBreakpoint,
} from '../foundations/breakpoints'

const computeBreakpoint = (): BreakpointKey => getCurrentBreakpoint()

const getWindow = () => {
  const global = globalThis as typeof globalThis & { window?: Window }
  return global.window ?? undefined
}

export const useBreakpoint = (): BreakpointKey => {
  const [breakpoint, setBreakpoint] = useState<BreakpointKey>(computeBreakpoint)

  useEffect(() => {
    const win = getWindow()
    if (!win) {
      return
    }

    const updateBreakpoint = () => {
      setBreakpoint(prev => {
        const next = getCurrentBreakpoint()
        return prev === next ? prev : next
      })
    }

    if (typeof win.matchMedia !== 'function') {
      win.addEventListener('resize', updateBreakpoint)
      updateBreakpoint()
      return () => {
        const cleanupWin = getWindow()
        if (!cleanupWin) {
          return
        }

        cleanupWin.removeEventListener('resize', updateBreakpoint)
      }
    }

    const mediaQueryLists = Object.values(breakpointValues).map(value =>
      win.matchMedia(`(min-width: ${value}px)`)
    )

    for (const mediaQuery of mediaQueryLists) {
      if (typeof mediaQuery.addEventListener === 'function') {
        mediaQuery.addEventListener('change', updateBreakpoint)
      } else if ('onchange' in mediaQuery) {
        mediaQuery.onchange = updateBreakpoint
      }
    }

    updateBreakpoint()

    return () => {
      for (const mediaQuery of mediaQueryLists) {
        if (typeof mediaQuery.removeEventListener === 'function') {
          mediaQuery.removeEventListener('change', updateBreakpoint)
        } else if ('onchange' in mediaQuery) {
          mediaQuery.onchange = null
        }
      }
    }
  }, [])

  return breakpoint
}
