/**
 * useOutsideClick Hook
 * 
 * Detect clicks outside an element
 * 
 * ✅ Performance: Uses useRef to avoid re-renders
 * ✅ Side-effect isolation: Only manages event listeners in scope
 */

import { useEffect, useRef, useCallback, type RefObject } from 'react'

/**
 * useOutsideClick - Detect clicks outside element
 * 
 * @template T Element type (defaults to HTMLElement)
 * @param handler Callback function when click outside
 * @param enabled Whether the hook is enabled
 * @returns Ref to attach to element
 */
export function useOutsideClick<T extends HTMLElement = HTMLElement>(
  handler: () => void,
  enabled = true,
): RefObject<T> {
  const ref = useRef<T>(null)
  // ✅ Performance: Store handler in ref to avoid re-creating event listeners
  const handlerRef = useRef(handler)

  // Update handler ref when handler changes
  useEffect(() => {
    handlerRef.current = handler
  }, [handler])

  useEffect(() => {
    if (!enabled) return

    const handleClickOutside = (event: MouseEvent | TouchEvent) => {
      if (ref.current && !ref.current.contains(event.target as Node)) {
        handlerRef.current()
      }
    }

    document.addEventListener('mousedown', handleClickOutside)
    document.addEventListener('touchstart', handleClickOutside)

    return () => {
      document.removeEventListener('mousedown', handleClickOutside)
      document.removeEventListener('touchstart', handleClickOutside)
    }
  }, [enabled])

  return ref
}
