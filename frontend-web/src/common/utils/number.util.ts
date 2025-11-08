/**
 * Number Utilities
 *
 * Helper functions for number formatting and manipulation.
 *
 * @module common/utils/number
 */

/**
 * Format number with commas
 *
 * @param num - Number to format
 * @param locale - Locale code (default: 'en-US')
 * @returns Formatted number string
 *
 * @example
 * ```ts
 * formatNumber(1234567.89) // '1,234,567.89'
 * ```
 */
export const formatNumber = (num: number, locale = 'en-US'): string => {
  return new Intl.NumberFormat(locale).format(num);
};

/**
 * Format number as currency
 *
 * @param amount - Amount to format
 * @param currency - Currency code (default: 'USD')
 * @param locale - Locale code (default: 'en-US')
 * @returns Formatted currency string
 *
 * @example
 * ```ts
 * formatCurrency(1234.56) // '$1,234.56'
 * formatCurrency(1234.56, 'EUR', 'de-DE') // '1.234,56 ¬'
 * ```
 */
export const formatCurrency = (
  amount: number,
  currency = 'USD',
  locale = 'en-US'
): string => {
  return new Intl.NumberFormat(locale, {
    style: 'currency',
    currency,
  }).format(amount);
};

/**
 * Format number as percentage
 *
 * @param value - Number between 0 and 1 (or 0-100 if asDecimal is false)
 * @param decimals - Number of decimal places (default: 0)
 * @param asDecimal - Whether value is decimal (0-1) or percentage (0-100)
 * @returns Formatted percentage string
 *
 * @example
 * ```ts
 * formatPercentage(0.1564, 2) // '15.64%'
 * formatPercentage(15.64, 2, false) // '15.64%'
 * ```
 */
export const formatPercentage = (
  value: number,
  decimals = 0,
  asDecimal = true
): string => {
  const percent = asDecimal ? value * 100 : value;
  return `${percent.toFixed(decimals)}%`;
};

/**
 * Format file size in human-readable format
 *
 * @param bytes - Size in bytes
 * @param decimals - Number of decimal places (default: 2)
 * @returns Formatted size string
 *
 * @example
 * ```ts
 * formatFileSize(1024) // '1.00 KB'
 * formatFileSize(1536000) // '1.46 MB'
 * ```
 */
export const formatFileSize = (bytes: number, decimals = 2): string => {
  if (bytes === 0) return '0 Bytes';

  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));

  return `${parseFloat((bytes / Math.pow(k, i)).toFixed(decimals))} ${sizes[i]}`;
};

/**
 * Clamp number between min and max
 *
 * @param num - Number to clamp
 * @param min - Minimum value
 * @param max - Maximum value
 * @returns Clamped number
 *
 * @example
 * ```ts
 * clamp(15, 0, 10) // 10
 * clamp(-5, 0, 10) // 0
 * clamp(5, 0, 10) // 5
 * ```
 */
export const clamp = (num: number, min: number, max: number): number => {
  return Math.min(Math.max(num, min), max);
};

/**
 * Round number to specified decimal places
 *
 * @param num - Number to round
 * @param decimals - Number of decimal places
 * @returns Rounded number
 *
 * @example
 * ```ts
 * roundTo(1.2345, 2) // 1.23
 * ```
 */
export const roundTo = (num: number, decimals: number): number => {
  const factor = Math.pow(10, decimals);
  return Math.round(num * factor) / factor;
};

/**
 * Generate random number between min and max
 *
 * @param min - Minimum value
 * @param max - Maximum value
 * @param float - Whether to return float (default: false)
 * @returns Random number
 *
 * @example
 * ```ts
 * randomNumber(1, 10) // 7
 * randomNumber(1, 10, true) // 7.3421
 * ```
 */
export const randomNumber = (min: number, max: number, float = false): number => {
  const rand = Math.random() * (max - min) + min;
  return float ? rand : Math.floor(rand);
};

/**
 * Check if number is in range
 *
 * @param num - Number to check
 * @param min - Minimum value
 * @param max - Maximum value
 * @param inclusive - Include min and max (default: true)
 * @returns True if in range
 *
 * @example
 * ```ts
 * inRange(5, 1, 10) // true
 * inRange(10, 1, 10, false) // false
 * ```
 */
export const inRange = (
  num: number,
  min: number,
  max: number,
  inclusive = true
): boolean => {
  if (inclusive) {
    return num >= min && num <= max;
  }
  return num > min && num < max;
};

/**
 * Calculate percentage of value from total
 *
 * @param value - Part value
 * @param total - Total value
 * @param decimals - Number of decimal places
 * @returns Percentage (0-100)
 *
 * @example
 * ```ts
 * toPercentage(25, 100) // 25
 * toPercentage(1, 3, 2) // 33.33
 * ```
 */
export const toPercentage = (value: number, total: number, decimals = 2): number => {
  if (total === 0) return 0;
  return roundTo((value / total) * 100, decimals);
};

/**
 * Format number with compact notation
 *
 * @param num - Number to format
 * @param locale - Locale code
 * @returns Compact number string
 *
 * @example
 * ```ts
 * formatCompact(1000) // '1K'
 * formatCompact(1500000) // '1.5M'
 * ```
 */
export const formatCompact = (num: number, locale = 'en-US'): string => {
  return new Intl.NumberFormat(locale, {
    notation: 'compact',
    compactDisplay: 'short',
  }).format(num);
};
