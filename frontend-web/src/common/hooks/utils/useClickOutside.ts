/**
 * useClickOutside Hook
 *
 * Detects clicks outside a referenced element.
 * Useful for closing dropdowns, modals, popovers, etc.
 *
 * @module common/hooks/utils/useClickOutside
 */

import { type RefObject, useEffect, useRef } from 'react';

/**
 * Hook to detect clicks outside an element
 *
 * @param ref - React ref to the element
 * @param handler - Callback when click outside occurs
 * @param enabled - Whether the hook is enabled (default: true)
 *
 * @example
 * ```tsx
 * function Dropdown() {
 *   const [isOpen, setIsOpen] = useState(false);
 *   const dropdownRef = useRef<HTMLDivElement>(null);
 *
 *   useClickOutside(dropdownRef, () => setIsOpen(false), isOpen);
 *
 *   return (
 *     <div ref={dropdownRef}>
 *       <button onClick={() => setIsOpen(!isOpen)}>Toggle</button>
 *       {isOpen && <DropdownMenu />}
 *     </div>
 *   );
 * }
 * ```
 */
export function useClickOutside<T extends HTMLElement = HTMLElement>(
  ref: RefObject<T>,
  handler: (event: MouseEvent | TouchEvent) => void,
  enabled = true
): void {
  useEffect(() => {
    if (!enabled) {
      return;
    }

    const listener = (event: MouseEvent | TouchEvent) => {
      const element = ref.current;

      // Do nothing if clicking ref's element or descendent elements
      if (!element || element.contains(event.target as Node)) {
        return;
      }

      handler(event);
    };

    // Add event listeners
    document.addEventListener('mousedown', listener);
    document.addEventListener('touchstart', listener);

    // Cleanup
    return () => {
      document.removeEventListener('mousedown', listener);
      document.removeEventListener('touchstart', listener);
    };
  }, [ref, handler, enabled]);
}

/**
 * Hook that returns a ref and handles click outside
 * Alternative API that doesn't require passing a ref
 *
 * @param handler - Callback when click outside occurs
 * @param enabled - Whether the hook is enabled
 * @returns Ref to attach to element
 *
 * @example
 * ```tsx
 * function Dropdown() {
 *   const [isOpen, setIsOpen] = useState(false);
 *   const dropdownRef = useClickOutsideRef(() => setIsOpen(false), isOpen);
 *
 *   return (
 *     <div ref={dropdownRef}>
 *       <button onClick={() => setIsOpen(!isOpen)}>Toggle</button>
 *       {isOpen && <DropdownMenu />}
 *     </div>
 *   );
 * }
 * ```
 */
export function useClickOutsideRef<T extends HTMLElement = HTMLElement>(
  handler: (event: MouseEvent | TouchEvent) => void,
  enabled = true
): RefObject<T> {
  const ref = useRef<T>(null);
  useClickOutside(ref, handler, enabled);
  return ref;
}

/**
 * Hook to detect clicks outside multiple elements
 *
 * @param refs - Array of React refs
 * @param handler - Callback when click outside occurs
 * @param enabled - Whether the hook is enabled
 *
 * @example
 * ```tsx
 * function MultiDropdown() {
 *   const buttonRef = useRef<HTMLButtonElement>(null);
 *   const menuRef = useRef<HTMLDivElement>(null);
 *   const [isOpen, setIsOpen] = useState(false);
 *
 *   useClickOutsideMultiple(
 *     [buttonRef, menuRef],
 *     () => setIsOpen(false),
 *     isOpen
 *   );
 *
 *   return (
 *     <>
 *       <button ref={buttonRef}>Toggle</button>
 *       {isOpen && <div ref={menuRef}>Menu</div>}
 *     </>
 *   );
 * }
 * ```
 */
export function useClickOutsideMultiple<T extends HTMLElement = HTMLElement>(
  refs: Array<RefObject<T>>,
  handler: (event: MouseEvent | TouchEvent) => void,
  enabled = true
): void {
  useEffect(() => {
    if (!enabled) {
      return;
    }

    const listener = (event: MouseEvent | TouchEvent) => {
      // Check if click is inside any of the refs
      const clickedInside = refs.some((ref) => {
        const element = ref.current;
        return element && element.contains(event.target as Node);
      });

      if (!clickedInside) {
        handler(event);
      }
    };

    document.addEventListener('mousedown', listener);
    document.addEventListener('touchstart', listener);

    return () => {
      document.removeEventListener('mousedown', listener);
      document.removeEventListener('touchstart', listener);
    };
  }, [refs, handler, enabled]);
}

export default useClickOutside;
