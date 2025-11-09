/**
 * useLocalStorage Hook
 *
 * Sync state with localStorage with automatic serialization/deserialization
 */

import { useCallback, useEffect, useState } from 'react'

export interface UseLocalStorageOptions<T> {
  serializer?: (value: T) => string
  deserializer?: (value: string) => T
  syncData?: boolean
}

export const useLocalStorage = <T>(
  key: string,
  initialValue: T,
  options?: UseLocalStorageOptions<T>
): [T, (value: T | ((prev: T) => T)) => void, () => void] => {
  const { serializer = JSON.stringify, deserializer = JSON.parse, syncData = true } = options || {}

  const [storedValue, setStoredValue] = useState<T>(() => {
    try {
      const item = globalThis.localStorage?.getItem(key)
      return item ? deserializer(item) : initialValue
    } catch (error) {
      console.error(`Error reading localStorage key "${key}":`, error)
      return initialValue
    }
  })

  const setValue = useCallback(
    (value: T | ((prev: T) => T)) => {
      try {
        const valueToStore =
          typeof value === 'function' ? (value as (prev: T) => T)(storedValue) : value
        setStoredValue(valueToStore)
        globalThis.localStorage?.setItem(key, serializer(valueToStore))
      } catch (error) {
        console.error(`Error setting localStorage key "${key}":`, error)
      }
    },
    [key, storedValue, serializer]
  )

  const removeValue = useCallback(() => {
    try {
      globalThis.localStorage?.removeItem(key)
      setStoredValue(initialValue)
    } catch (error) {
      console.error(`Error removing localStorage key "${key}":`, error)
    }
  }, [key, initialValue])

  useEffect(() => {
    if (!syncData) {
      return undefined
    }

    const handleStorageChange = (e: StorageEvent) => {
      if (e.key === key && e.newValue !== null) {
        try {
          setStoredValue(deserializer(e.newValue))
        } catch (error) {
          console.error(`Error syncing localStorage key "${key}":`, error)
        }
      }
    }

    globalThis.addEventListener('storage', handleStorageChange)
    return () => {
      globalThis.removeEventListener('storage', handleStorageChange)
    }
  }, [key, deserializer, syncData])

  return [storedValue, setValue, removeValue]
}

export default useLocalStorage
