/**
 * useToggle Hook
 *
 * Manage boolean state with toggle functionality
 * Useful for modals, dropdowns, accordions, etc.
 */

import { useCallback, useState } from 'react';

/**
 * Toggle hook return type
 */
export interface UseToggleReturn {
  /**
   * Current boolean value
   */
  value: boolean;

  /**
   * Toggle the value (true <-> false)
   */
  toggle: () => void;

  /**
   * Set value to true
   */
  setTrue: () => void;

  /**
   * Set value to false
   */
  setFalse: () => void;

  /**
   * Set specific value
   */
  setValue: (value: boolean) => void;
}

/**
 * Toggle boolean state
 *
 * @param initialValue - Initial boolean value (default: false)
 * @returns Toggle state and controls
 *
 * @example
 * ```tsx
 * const modal = useToggle();
 *
 * <Button onClick={modal.toggle}>Open Modal</Button>
 * <Modal open={modal.value} onClose={modal.setFalse}>
 *   ...
 * </Modal>
 * ```
 *
 * @example
 * ```tsx
 * const { value: isOpen, toggle, setTrue, setFalse } = useToggle(false);
 *
 * <Accordion open={isOpen} onToggle={toggle} />
 * ```
 */
export const useToggle = (initialValue = false): UseToggleReturn => {
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

  return {
    value,
    toggle,
    setTrue,
    setFalse,
    setValue: setValueCallback,
  };
};

export default useToggle;
