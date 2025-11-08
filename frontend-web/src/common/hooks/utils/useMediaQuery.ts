/**
 * useMediaQuery Hook
 *
 * Tracks the state of a CSS media query.
 * Useful for responsive design and conditional rendering.
 *
 * @module common/hooks/utils/useMediaQuery
 */

import { useEffect, useState } from 'react';

/**
 * Hook to track media query matches
 *
 * @param query - Media query string
 * @param defaultValue - Default value for SSR (default: false)
 * @returns Boolean indicating if media query matches
 *
 * @example
 * ```tsx
 * const isMobile = useMediaQuery('(max-width: 768px)');
 * const isDesktop = useMediaQuery('(min-width: 1024px)');
 * const prefersDark = useMediaQuery('(prefers-color-scheme: dark)');
 *
 * return (
 *   <div>
 *     {isMobile ? <MobileNav /> : <DesktopNav />}
 *   </div>
 * );
 * ```
 */
export function useMediaQuery(query: string, defaultValue = false): boolean {
  const [matches, setMatches] = useState<boolean>(() => {
    // Return default value during SSR
    if (typeof window === 'undefined') {
      return defaultValue;
    }

    return window.matchMedia(query).matches;
  });

  useEffect(() => {
    if (typeof window === 'undefined') {
      return;
    }

    const mediaQueryList = window.matchMedia(query);

    // Update state when media query changes
    const handleChange = (event: MediaQueryListEvent) => {
      setMatches(event.matches);
    };

    // Set initial value
    setMatches(mediaQueryList.matches);

    // Add event listener
    // Use addEventListener for modern browsers
    if (mediaQueryList.addEventListener) {
      mediaQueryList.addEventListener('change', handleChange);
    } else {
      // Fallback for older browsers
      mediaQueryList.addListener(handleChange);
    }

    // Cleanup
    return () => {
      if (mediaQueryList.removeEventListener) {
        mediaQueryList.removeEventListener('change', handleChange);
      } else {
        mediaQueryList.removeListener(handleChange);
      }
    };
  }, [query]);

  return matches;
}

/**
 * Predefined breakpoint hooks based on common screen sizes
 */

/**
 * Check if viewport is mobile (max-width: 767px)
 */
export function useIsMobile(): boolean {
  return useMediaQuery('(max-width: 767px)');
}

/**
 * Check if viewport is tablet (min-width: 768px and max-width: 1023px)
 */
export function useIsTablet(): boolean {
  return useMediaQuery('(min-width: 768px) and (max-width: 1023px)');
}

/**
 * Check if viewport is desktop (min-width: 1024px)
 */
export function useIsDesktop(): boolean {
  return useMediaQuery('(min-width: 1024px)');
}

/**
 * Check if user prefers dark color scheme
 */
export function usePrefersDarkMode(): boolean {
  return useMediaQuery('(prefers-color-scheme: dark)');
}

/**
 * Check if user prefers reduced motion
 */
export function usePrefersReducedMotion(): boolean {
  return useMediaQuery('(prefers-reduced-motion: reduce)');
}

/**
 * Check if device is in portrait orientation
 */
export function useIsPortrait(): boolean {
  return useMediaQuery('(orientation: portrait)');
}

/**
 * Check if device is in landscape orientation
 */
export function useIsLandscape(): boolean {
  return useMediaQuery('(orientation: landscape)');
}

/**
 * Hook to get current breakpoint
 *
 * @returns Current breakpoint name
 *
 * @example
 * ```tsx
 * const breakpoint = useBreakpoint();
 * // Returns 'xs' | 'sm' | 'md' | 'lg' | 'xl' | '2xl'
 * ```
 */
export function useBreakpoint(): 'xs' | 'sm' | 'md' | 'lg' | 'xl' | '2xl' {
  const is2xl = useMediaQuery('(min-width: 1536px)');
  const isXl = useMediaQuery('(min-width: 1280px)');
  const isLg = useMediaQuery('(min-width: 1024px)');
  const isMd = useMediaQuery('(min-width: 768px)');
  const isSm = useMediaQuery('(min-width: 640px)');

  if (is2xl) return '2xl';
  if (isXl) return 'xl';
  if (isLg) return 'lg';
  if (isMd) return 'md';
  if (isSm) return 'sm';
  return 'xs';
}

export default useMediaQuery;
