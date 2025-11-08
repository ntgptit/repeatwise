/**
 * useLocalStorage Hook
 *
 * Syncs state with localStorage for persistent data.
 * Automatically handles JSON serialization and parsing.
 *
 * @module common/hooks/utils/useLocalStorage
 */

import { useCallback, useEffect, useState } from 'react';

/**
 * Options for useLocalStorage hook
 */
export interface UseLocalStorageOptions<T> {
  /**
   * Serializer function (default: JSON.stringify)
   */
  serializer?: (value: T) => string;

  /**
   * Deserializer function (default: JSON.parse)
   */
  deserializer?: (value: string) => T;

  /**
   * Whether to sync across tabs/windows (default: true)
   */
  syncTabs?: boolean;
}

/**
 * Hook to persist state in localStorage
 *
 * @param key - LocalStorage key
 * @param initialValue - Initial value if key doesn't exist
 * @param options - Configuration options
 * @returns Tuple of [value, setValue, removeValue]
 *
 * @example
 * ```tsx
 * const [theme, setTheme, removeTheme] = useLocalStorage('theme', 'light');
 *
 * return (
 *   <button onClick={() => setTheme(theme === 'light' ? 'dark' : 'light')}>
 *     Toggle Theme
 *   </button>
 * );
 * ```
 */
export function useLocalStorage<T>(
  key: string,
  initialValue: T,
  options: UseLocalStorageOptions<T> = {}
): [T, (value: T | ((prev: T) => T)) => void, () => void] {
  const {
    serializer = JSON.stringify,
    deserializer = JSON.parse,
    syncTabs = true,
  } = options;

  // Get initial value from localStorage or use provided initialValue
  const readValue = useCallback((): T => {
    // Prevent build error during SSR
    if (typeof window === 'undefined') {
      return initialValue;
    }

    try {
      const item = window.localStorage.getItem(key);
      return item ? (deserializer(item) as T) : initialValue;
    } catch (error) {
      console.warn(`Error reading localStorage key "${key}":`, error);
      return initialValue;
    }
  }, [key, initialValue, deserializer]);

  const [storedValue, setStoredValue] = useState<T>(readValue);

  // Return a wrapped version of useState's setter function
  const setValue = useCallback(
    (value: T | ((prev: T) => T)) => {
      // Prevent build error during SSR
      if (typeof window === 'undefined') {
        console.warn('useLocalStorage: window is undefined');
        return;
      }

      try {
        // Allow value to be a function (same API as useState)
        const valueToStore = value instanceof Function ? value(storedValue) : value;

        // Save state
        setStoredValue(valueToStore);

        // Save to localStorage
        window.localStorage.setItem(key, serializer(valueToStore));

        // Dispatch custom event for cross-tab synchronization
        if (syncTabs) {
          window.dispatchEvent(
            new CustomEvent('local-storage-change', {
              detail: { key, value: valueToStore },
            })
          );
        }
      } catch (error) {
        console.warn(`Error setting localStorage key "${key}":`, error);
      }
    },
    [key, storedValue, serializer, syncTabs]
  );

  // Remove value from localStorage
  const removeValue = useCallback(() => {
    if (typeof window === 'undefined') {
      console.warn('useLocalStorage: window is undefined');
      return;
    }

    try {
      window.localStorage.removeItem(key);
      setStoredValue(initialValue);

      if (syncTabs) {
        window.dispatchEvent(
          new CustomEvent('local-storage-change', {
            detail: { key, value: null },
          })
        );
      }
    } catch (error) {
      console.warn(`Error removing localStorage key "${key}":`, error);
    }
  }, [key, initialValue, syncTabs]);

  // Listen for storage changes in other tabs
  useEffect(() => {
    if (!syncTabs || typeof window === 'undefined') {
      return;
    }

    const handleStorageChange = (e: StorageEvent | CustomEvent) => {
      if (e instanceof StorageEvent) {
        if (e.key === key && e.newValue !== null) {
          try {
            setStoredValue(deserializer(e.newValue) as T);
          } catch (error) {
            console.warn(`Error parsing storage change for key "${key}":`, error);
          }
        }
      } else if (e instanceof CustomEvent) {
        const detail = e.detail as { key: string; value: T | null };
        if (detail.key === key) {
          if (detail.value === null) {
            setStoredValue(initialValue);
          } else {
            setStoredValue(detail.value);
          }
        }
      }
    };

    window.addEventListener('storage', handleStorageChange as EventListener);
    window.addEventListener('local-storage-change', handleStorageChange as EventListener);

    return () => {
      window.removeEventListener('storage', handleStorageChange as EventListener);
      window.removeEventListener('local-storage-change', handleStorageChange as EventListener);
    };
  }, [key, initialValue, deserializer, syncTabs]);

  return [storedValue, setValue, removeValue];
}

export default useLocalStorage;
