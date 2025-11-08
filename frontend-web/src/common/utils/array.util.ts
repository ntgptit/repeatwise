/**
 * Array Utilities
 *
 * Helper functions for array manipulation.
 *
 * @module common/utils/array
 */

/**
 * Remove duplicates from array
 *
 * @param arr - Input array
 * @returns Array without duplicates
 *
 * @example
 * ```ts
 * unique([1, 2, 2, 3]) // [1, 2, 3]
 * ```
 */
export const unique = <T>(arr: T[]): T[] => {
  return [...new Set(arr)];
};

/**
 * Group array items by key
 *
 * @param arr - Input array
 * @param key - Key function or property name
 * @returns Grouped object
 *
 * @example
 * ```ts
 * groupBy([{type: 'a'}, {type: 'b'}, {type: 'a'}], 'type')
 * // { a: [{type: 'a'}, {type: 'a'}], b: [{type: 'b'}] }
 * ```
 */
export const groupBy = <T>(
  arr: T[],
  key: keyof T | ((item: T) => string)
): Record<string, T[]> => {
  return arr.reduce(
    (groups, item) => {
      const groupKey = typeof key === 'function' ? key(item) : String(item[key]);
      if (!groups[groupKey]) {
        groups[groupKey] = [];
      }
      groups[groupKey]?.push(item);
      return groups;
    },
    {} as Record<string, T[]>
  );
};

/**
 * Chunk array into smaller arrays
 *
 * @param arr - Input array
 * @param size - Chunk size
 * @returns Array of chunks
 *
 * @example
 * ```ts
 * chunk([1, 2, 3, 4, 5], 2) // [[1, 2], [3, 4], [5]]
 * ```
 */
export const chunk = <T>(arr: T[], size: number): T[][] => {
  const chunks: T[][] = [];
  for (let i = 0; i < arr.length; i += size) {
    chunks.push(arr.slice(i, i + size));
  }
  return chunks;
};

/**
 * Shuffle array randomly
 *
 * @param arr - Input array
 * @returns Shuffled array
 *
 * @example
 * ```ts
 * shuffle([1, 2, 3, 4, 5]) // [3, 1, 5, 2, 4]
 * ```
 */
export const shuffle = <T>(arr: T[]): T[] => {
  const shuffled = [...arr];
  for (let i = shuffled.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [shuffled[i], shuffled[j]] = [shuffled[j] ?? shuffled[i]!, shuffled[i] ?? shuffled[j]!];
  }
  return shuffled;
};

/**
 * Get random item from array
 *
 * @param arr - Input array
 * @returns Random item
 */
export const randomItem = <T>(arr: T[]): T | undefined => {
  return arr[Math.floor(Math.random() * arr.length)];
};

/**
 * Sort array by property
 *
 * @param arr - Input array
 * @param key - Property key or getter function
 * @param order - Sort order (default: 'asc')
 * @returns Sorted array
 */
export const sortBy = <T>(
  arr: T[],
  key: keyof T | ((item: T) => unknown),
  order: 'asc' | 'desc' = 'asc'
): T[] => {
  return [...arr].sort((a, b) => {
    const aVal = typeof key === 'function' ? key(a) : a[key];
    const bVal = typeof key === 'function' ? key(b) : b[key];

    if (aVal < bVal) return order === 'asc' ? -1 : 1;
    if (aVal > bVal) return order === 'asc' ? 1 : -1;
    return 0;
  });
};

/**
 * Remove falsy values from array
 *
 * @param arr - Input array
 * @returns Array without falsy values
 */
export const compact = <T>(arr: (T | null | undefined | false | 0 | '')[]): T[] => {
  return arr.filter(Boolean) as T[];
};

/**
 * Flatten nested array
 *
 * @param arr - Nested array
 * @param depth - Depth to flatten (default: 1)
 * @returns Flattened array
 */
export const flatten = <T>(arr: unknown[], depth = 1): T[] => {
  return arr.flat(depth) as T[];
};

/**
 * Get intersection of two arrays
 *
 * @param arr1 - First array
 * @param arr2 - Second array
 * @returns Intersection array
 */
export const intersection = <T>(arr1: T[], arr2: T[]): T[] => {
  const set2 = new Set(arr2);
  return arr1.filter((item) => set2.has(item));
};

/**
 * Get difference of two arrays
 *
 * @param arr1 - First array
 * @param arr2 - Second array
 * @returns Difference array
 */
export const difference = <T>(arr1: T[], arr2: T[]): T[] => {
  const set2 = new Set(arr2);
  return arr1.filter((item) => !set2.has(item));
};

/**
 * Move array item from one index to another
 *
 * @param arr - Input array
 * @param fromIndex - Source index
 * @param toIndex - Destination index
 * @returns New array with moved item
 */
export const move = <T>(arr: T[], fromIndex: number, toIndex: number): T[] => {
  const result = [...arr];
  const [removed] = result.splice(fromIndex, 1);
  if (removed !== undefined) {
    result.splice(toIndex, 0, removed);
  }
  return result;
};
