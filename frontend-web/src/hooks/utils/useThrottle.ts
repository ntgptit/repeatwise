/**
 * useThrottle Hook
 * 
 * Throttle a value with a delay
 * 
 * ✅ Chuẩn: Trả về throttledValue hoặc { value, cancel }
 */

import { useState, useEffect, useRef, useCallback } from 'react'

export interface UseThrottleReturn<T> {
  value: T
  cancel: () => void
}

/**
 * useThrottle - Throttle a value (simple version)
 * 
 * @template T Value type
 * @param value Value to throttle
 * @param delay Delay in milliseconds
 * @returns Throttled value
 */
export function useThrottle<T>(value: T, delay: number): T

/**
 * useThrottle - Throttle a value (with cancel option)
 * 
 * @template T Value type
 * @param value Value to throttle
 * @param delay Delay in milliseconds
 * @param options Options object
 * @returns Object with throttled value and cancel function
 */
export function useThrottle<T>(
  value: T,
  delay: number,
  options: { cancelable: true },
): UseThrottleReturn<T>

export function useThrottle<T>(
  value: T,
  delay: number,
  options?: { cancelable: true },
): T | UseThrottleReturn<T> {
  const [throttledValue, setThrottledValue] = useState<T>(value)
  const lastRanRef = useRef<number>(Date.now())
  const timeoutRef = useRef<NodeJS.Timeout>()

  useEffect(() => {
    const handler = () => {
      if (Date.now() - lastRanRef.current >= delay) {
        setThrottledValue(value)
        lastRanRef.current = Date.now()
      }
    }

    if (Date.now() - lastRanRef.current >= delay) {
      setThrottledValue(value)
      lastRanRef.current = Date.now()
    } else {
      if (timeoutRef.current) {
        clearTimeout(timeoutRef.current)
      }
      timeoutRef.current = setTimeout(handler, delay - (Date.now() - lastRanRef.current))
    }

    return () => {
      if (timeoutRef.current) {
        clearTimeout(timeoutRef.current)
      }
    }
  }, [value, delay])

  const cancel = useCallback(() => {
    if (timeoutRef.current) {
      clearTimeout(timeoutRef.current)
      timeoutRef.current = undefined
    }
  }, [])

  if (options?.cancelable) {
    return {
      value: throttledValue,
      cancel,
    }
  }

  return throttledValue
}
