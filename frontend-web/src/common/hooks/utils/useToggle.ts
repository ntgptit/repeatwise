/**
 * useToggle Hook
 *
 * Provides a boolean state with toggle, set, and reset functions.
 * Useful for modals, dropdowns, accordions, etc.
 *
 * @module common/hooks/utils/useToggle
 */

import { useCallback, useState } from 'react';

/**
 * Return type for useToggle hook
 */
export interface UseToggleReturn {
  /**
   * Current boolean state
   */
  value: boolean;

  /**
   * Toggle the boolean value
   */
  toggle: () => void;

  /**
   * Set to true
   */
  setTrue: () => void;

  /**
   * Set to false
   */
  setFalse: () => void;

  /**
   * Set to specific value
   */
  setValue: (value: boolean) => void;

  /**
   * Reset to initial value
   */
  reset: () => void;
}

/**
 * Hook to manage boolean state with toggle functionality
 *
 * @param initialValue - Initial boolean value (default: false)
 * @returns Toggle state and control functions
 *
 * @example
 * ```tsx
 * const modal = useToggle(false);
 *
 * return (
 *   <>
 *     <button onClick={modal.toggle}>Toggle Modal</button>
 *     <button onClick={modal.setTrue}>Open Modal</button>
 *     <button onClick={modal.setFalse}>Close Modal</button>
 *
 *     {modal.value && (
 *       <Modal onClose={modal.setFalse}>
 *         Modal Content
 *       </Modal>
 *     )}
 *   </>
 * );
 * ```
 */
export function useToggle(initialValue = false): UseToggleReturn {
  const [value, setValue] = useState<boolean>(initialValue);

  const toggle = useCallback(() => {
    setValue((prev) => !prev);
  }, []);

  const setTrue = useCallback(() => {
    setValue(true);
  }, []);

  const setFalse = useCallback(() => {
    setValue(false);
  }, []);

  const setValueCallback = useCallback((newValue: boolean) => {
    setValue(newValue);
  }, []);

  const reset = useCallback(() => {
    setValue(initialValue);
  }, [initialValue]);

  return {
    value,
    toggle,
    setTrue,
    setFalse,
    setValue: setValueCallback,
    reset,
  };
}

/**
 * Alternative hook that returns array tuple (similar to useState)
 *
 * @param initialValue - Initial boolean value
 * @returns Tuple of [value, toggle, setTrue, setFalse]
 *
 * @example
 * ```tsx
 * const [isOpen, toggle, open, close] = useToggleArray(false);
 * ```
 */
export function useToggleArray(
  initialValue = false
): [boolean, () => void, () => void, () => void] {
  const { value, toggle, setTrue, setFalse } = useToggle(initialValue);
  return [value, toggle, setTrue, setFalse];
}

export default useToggle;
