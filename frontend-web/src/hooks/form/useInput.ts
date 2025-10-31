/**
 * useInput Hook
 * 
 * Single input field state management hook
 * 
 * ✅ Chuẩn: { value, handleChange, reset }
 * ✅ Type-safe: Sử dụng TypeScript Generic
 */

import { useState, useCallback } from 'react'

export interface UseInputReturn<T> {
  value: T
  handleChange: (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => void
  handleChangeValue: (value: T) => void
  reset: () => void
  setValue: (value: T) => void
}

/**
 * useInput - Single input state hook
 * 
 * @template T Input value type
 * @param initialValue Initial input value
 * @returns Object with input value and handlers
 */
export function useInput<T>(initialValue: T): UseInputReturn<T> {
  const [value, setValue] = useState<T>(initialValue)

  const handleChange = useCallback(
    (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
      setValue(e.target.value as T)
    },
    [],
  )

  const handleChangeValue = useCallback((newValue: T) => {
    setValue(newValue)
  }, [])

  const reset = useCallback(() => {
    setValue(initialValue)
  }, [initialValue])

  return {
    value,
    handleChange,
    handleChangeValue,
    reset,
    setValue,
  }
}
