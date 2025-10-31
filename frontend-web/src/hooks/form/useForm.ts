/**
 * useForm Hook
 * 
 * Form state management hook
 * 
 * ✅ Chuẩn: { values, handleChange, reset }
 * ✅ Type-safe: Sử dụng TypeScript Generic
 */

import { useState, useCallback } from 'react'

export interface UseFormReturn<T extends Record<string, unknown>> {
  values: T
  handleChange: (
    name: keyof T,
  ) => (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => void
  handleChangeValue: (name: keyof T, value: unknown) => void
  reset: () => void
  setValues: (values: T | ((prev: T) => T)) => void
}

/**
 * useForm - Form state management hook
 * 
 * @template T Form values type
 * @param initialValues Initial form values
 * @returns Object with form values and handlers
 */
export function useForm<T extends Record<string, unknown>>(
  initialValues: T,
): UseFormReturn<T> {
  const [values, setValues] = useState<T>(initialValues)

  const handleChange = useCallback(
    (name: keyof T) =>
      (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        setValues((prev) => ({
          ...prev,
          [name]: e.target.value,
        }))
      },
    [],
  )

  const handleChangeValue = useCallback((name: keyof T, value: unknown) => {
    setValues((prev) => ({
      ...prev,
      [name]: value,
    }))
  }, [])

  const reset = useCallback(() => {
    setValues(initialValues)
  }, [initialValues])

  return {
    values,
    handleChange,
    handleChangeValue,
    reset,
    setValues,
  }
}
