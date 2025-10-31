/**
 * useDebounce Hook
 * 
 * Debounce a value with a delay
 * 
 * ✅ Chuẩn: Trả về debouncedValue hoặc { value, cancel }
 * ✅ Type-safe: Sử dụng TypeScript Generic
 */

import { useState, useEffect, useRef, useCallback } from 'react'

export interface UseDebounceReturn<T> {
  value: T
  cancel: () => void
}

/**
 * useDebounce - Debounce a value (simple version)
 * 
 * @template T Value type
 * @param value Value to debounce
 * @param delay Delay in milliseconds
 * @returns Debounced value
 */
export function useDebounce<T>(value: T, delay: number): T

/**
 * useDebounce - Debounce a value (with cancel option)
 * 
 * @template T Value type
 * @param value Value to debounce
 * @param delay Delay in milliseconds
 * @param options Options object
 * @returns Object with debounced value and cancel function
 */
export function useDebounce<T>(
  value: T,
  delay: number,
  options: { cancelable: true },
): UseDebounceReturn<T>

export function useDebounce<T>(
  value: T,
  delay: number,
  options?: { cancelable: true },
): T | UseDebounceReturn<T> {
  const [debouncedValue, setDebouncedValue] = useState<T>(value)
  const timeoutRef = useRef<NodeJS.Timeout>()

  const cancel = useCallback(() => {
    if (timeoutRef.current) {
      clearTimeout(timeoutRef.current)
      timeoutRef.current = undefined
    }
  }, [])

  useEffect(() => {
    if (timeoutRef.current) {
      clearTimeout(timeoutRef.current)
    }

    timeoutRef.current = setTimeout(() => {
      setDebouncedValue(value)
    }, delay)

    return () => {
      if (timeoutRef.current) {
        clearTimeout(timeoutRef.current)
      }
    }
  }, [value, delay])

  if (options?.cancelable) {
    return {
      value: debouncedValue,
      cancel,
    }
  }

  return debouncedValue
}
