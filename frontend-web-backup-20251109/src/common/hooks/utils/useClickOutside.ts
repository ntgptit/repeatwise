/**
 * useClickOutside Hook
 *
 * Detect clicks outside of a ref element
 */

import { type RefObject, useEffect } from 'react'

export const useClickOutside = <T extends HTMLElement = HTMLElement>(
  ref: RefObject<T>,
  handler: (event: MouseEvent | TouchEvent) => void,
  enabled = true
): void => {
  useEffect(() => {
    if (!enabled) {
      return
    }

    const listener = (event: MouseEvent | TouchEvent) => {
      const element = ref.current

      // Do nothing if clicking ref's element or descendent elements
      if (!element || element.contains(event.target as Node)) {
        return
      }

      handler(event)
    }

    document.addEventListener('mousedown', listener)
    document.addEventListener('touchstart', listener)

    return () => {
      document.removeEventListener('mousedown', listener)
      document.removeEventListener('touchstart', listener)
    }
  }, [ref, handler, enabled])
}

export default useClickOutside
