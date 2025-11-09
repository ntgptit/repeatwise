/**
 * Array Utilities
 *
 * Common array manipulation functions
 */

/**
 * Remove duplicates from array
 */
export const unique = <T>(array: T[]): T[] => {
  return [...new Set(array)]
}

/**
 * Remove duplicates by key
 */
export const uniqueBy = <T>(array: T[], key: keyof T): T[] => {
  const seen = new Set()
  return array.filter(item => {
    const value = item[key]
    if (seen.has(value)) {
      return false
    }
    seen.add(value)
    return true
  })
}

/**
 * Chunk array into smaller arrays
 */
export const chunk = <T>(array: T[], size: number): T[][] => {
  const chunks: T[][] = []
  for (let i = 0; i < array.length; i += size) {
    chunks.push(array.slice(i, i + size))
  }
  return chunks
}

/**
 * Flatten array one level deep
 */
export const flatten = <T>(array: (T | T[])[]): T[] => {
  return array.flat() as T[]
}

/**
 * Flatten array recursively
 */
export const flattenDeep = <T>(array: unknown[]): T[] => {
  return array.flat(Infinity) as T[]
}

/**
 * Group array by key
 */
export const groupBy = <T>(array: T[], key: keyof T): Record<string, T[]> => {
  return array.reduce(
    (groups, item) => {
      const value = String(item[key])
      groups[value] = groups[value] || []
      groups[value].push(item)
      return groups
    },
    {} as Record<string, T[]>
  )
}

/**
 * Sort array by key
 */
export const sortBy = <T>(array: T[], key: keyof T, order: 'asc' | 'desc' = 'asc'): T[] => {
  return [...array].sort((a, b) => {
    const aVal = a[key]
    const bVal = b[key]

    if (aVal < bVal) {
      return order === 'asc' ? -1 : 1
    }
    if (aVal > bVal) {
      return order === 'asc' ? 1 : -1
    }
    return 0
  })
}

/**
 * Shuffle array randomly
 */
export const shuffle = <T>(array: T[]): T[] => {
  const result = [...array]
  for (let i = result.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1))
    const temp = result[i]
    result[i] = result[j]
    result[j] = temp
  }
  return result
}

/**
 * Get random item from array
 */
export const sample = <T>(array: T[]): T | undefined => {
  return array[Math.floor(Math.random() * array.length)]
}

/**
 * Get N random items from array
 */
export const sampleSize = <T>(array: T[], n: number): T[] => {
  const shuffled = shuffle(array)
  return shuffled.slice(0, n)
}

/**
 * Sum array of numbers
 */
export const sum = (array: number[]): number => {
  return array.reduce((acc, num) => acc + num, 0)
}

/**
 * Average of array of numbers
 */
export const average = (array: number[]): number => {
  if (array.length === 0) {
    return 0
  }
  return sum(array) / array.length
}

/**
 * Find min in array
 */
export const min = (array: number[]): number => {
  return Math.min(...array)
}

/**
 * Find max in array
 */
export const max = (array: number[]): number => {
  return Math.max(...array)
}

/**
 * Count occurrences of value
 */
export const count = <T>(array: T[], value: T): number => {
  return array.filter(item => item === value).length
}

/**
 * Count by predicate
 */
export const countBy = <T>(array: T[], predicate: (item: T) => boolean): number => {
  return array.filter(predicate).length
}

/**
 * Partition array by predicate
 */
export const partition = <T>(array: T[], predicate: (item: T) => boolean): [T[], T[]] => {
  const pass: T[] = []
  const fail: T[] = []

  array.forEach(item => {
    if (predicate(item)) {
      pass.push(item)
    } else {
      fail.push(item)
    }
  })

  return [pass, fail]
}

/**
 * Difference between two arrays
 */
export const difference = <T>(array1: T[], array2: T[]): T[] => {
  return array1.filter(item => !array2.includes(item))
}

/**
 * Intersection of two arrays
 */
export const intersection = <T>(array1: T[], array2: T[]): T[] => {
  return array1.filter(item => array2.includes(item))
}

/**
 * Union of two arrays (unique values)
 */
export const union = <T>(array1: T[], array2: T[]): T[] => {
  return unique([...array1, ...array2])
}

/**
 * Check if array is empty
 */
export const isEmpty = <T>(array: T[]): boolean => {
  return array.length === 0
}

/**
 * Check if array is not empty
 */
export const isNotEmpty = <T>(array: T[]): boolean => {
  return array.length > 0
}

/**
 * Get first item
 */
export const first = <T>(array: T[]): T | undefined => {
  return array[0]
}

/**
 * Get last item
 */
export const last = <T>(array: T[]): T | undefined => {
  return array[array.length - 1]
}

/**
 * Get nth item
 */
export const nth = <T>(array: T[], index: number): T | undefined => {
  return array[index]
}

/**
 * Remove item at index
 */
export const removeAt = <T>(array: T[], index: number): T[] => {
  return [...array.slice(0, index), ...array.slice(index + 1)]
}

/**
 * Insert item at index
 */
export const insertAt = <T>(array: T[], index: number, item: T): T[] => {
  return [...array.slice(0, index), item, ...array.slice(index)]
}

/**
 * Move item from one index to another
 */
export const move = <T>(array: T[], fromIndex: number, toIndex: number): T[] => {
  const result = [...array]
  const [removed] = result.splice(fromIndex, 1)
  result.splice(toIndex, 0, removed)
  return result
}

/**
 * Compact array (remove falsy values)
 */
export const compact = <T>(array: (T | null | undefined | false | 0 | '')[]): T[] => {
  return array.filter(Boolean) as T[]
}

/**
 * Range of numbers
 */
export const range = (start: number, end: number, step = 1): number[] => {
  const result: number[] = []
  for (let i = start; i <= end; i += step) {
    result.push(i)
  }
  return result
}

export default {
  unique,
  uniqueBy,
  chunk,
  flatten,
  flattenDeep,
  groupBy,
  sortBy,
  shuffle,
  sample,
  sampleSize,
  sum,
  average,
  min,
  max,
  count,
  countBy,
  partition,
  difference,
  intersection,
  union,
  isEmpty,
  isNotEmpty,
  first,
  last,
  nth,
  removeAt,
  insertAt,
  move,
  compact,
  range,
}
