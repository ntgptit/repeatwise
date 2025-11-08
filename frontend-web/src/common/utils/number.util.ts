/**
 * Number Utilities
 *
 * Common number manipulation and formatting functions
 */

/**
 * Format number with thousand separators
 * @example formatNumber(1234567.89) => '1,234,567.89'
 */
export const formatNumber = (
  num: number,
  locale = 'en-US',
  options?: Intl.NumberFormatOptions
): string => {
  return new Intl.NumberFormat(locale, options).format(num);
};

/**
 * Format number as currency
 * @example formatCurrency(1234.56, 'USD') => '$1,234.56'
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
 * @example formatPercentage(0.1234) => '12.34%'
 */
export const formatPercentage = (
  value: number,
  decimals = 2,
  locale = 'en-US'
): string => {
  return new Intl.NumberFormat(locale, {
    style: 'percent',
    minimumFractionDigits: decimals,
    maximumFractionDigits: decimals,
  }).format(value);
};

/**
 * Format number with compact notation (K, M, B)
 * @example formatCompact(1234567) => '1.2M'
 */
export const formatCompact = (num: number, locale = 'en-US'): string => {
  return new Intl.NumberFormat(locale, {
    notation: 'compact',
    compactDisplay: 'short',
  }).format(num);
};

/**
 * Round number to specified decimal places
 * @example round(1.2345, 2) => 1.23
 */
export const round = (num: number, decimals = 0): number => {
  const factor = Math.pow(10, decimals);
  return Math.round(num * factor) / factor;
};

/**
 * Floor number to specified decimal places
 * @example floor(1.2345, 2) => 1.23
 */
export const floor = (num: number, decimals = 0): number => {
  const factor = Math.pow(10, decimals);
  return Math.floor(num * factor) / factor;
};

/**
 * Ceil number to specified decimal places
 * @example ceil(1.2345, 2) => 1.24
 */
export const ceil = (num: number, decimals = 0): number => {
  const factor = Math.pow(10, decimals);
  return Math.ceil(num * factor) / factor;
};

/**
 * Clamp number between min and max
 * @example clamp(15, 0, 10) => 10
 */
export const clamp = (num: number, min: number, max: number): number => {
  return Math.min(Math.max(num, min), max);
};

/**
 * Check if number is between min and max (inclusive)
 */
export const isBetween = (num: number, min: number, max: number): boolean => {
  return num >= min && num <= max;
};

/**
 * Check if number is even
 */
export const isEven = (num: number): boolean => {
  return num % 2 === 0;
};

/**
 * Check if number is odd
 */
export const isOdd = (num: number): boolean => {
  return num % 2 !== 0;
};

/**
 * Check if number is positive
 */
export const isPositive = (num: number): boolean => {
  return num > 0;
};

/**
 * Check if number is negative
 */
export const isNegative = (num: number): boolean => {
  return num < 0;
};

/**
 * Check if number is zero
 */
export const isZero = (num: number): boolean => {
  return num === 0;
};

/**
 * Generate random integer between min and max (inclusive)
 * @example randomInt(1, 10) => 7
 */
export const randomInt = (min: number, max: number): number => {
  return Math.floor(Math.random() * (max - min + 1)) + min;
};

/**
 * Generate random float between min and max
 * @example randomFloat(1, 10) => 7.234
 */
export const randomFloat = (min: number, max: number): number => {
  return Math.random() * (max - min) + min;
};

/**
 * Sum of numbers
 * @example sum(1, 2, 3, 4, 5) => 15
 */
export const sum = (...numbers: number[]): number => {
  return numbers.reduce((acc, num) => acc + num, 0);
};

/**
 * Average of numbers
 * @example average(1, 2, 3, 4, 5) => 3
 */
export const average = (...numbers: number[]): number => {
  if (numbers.length === 0) return 0;
  return sum(...numbers) / numbers.length;
};

/**
 * Median of numbers
 * @example median(1, 2, 3, 4, 5) => 3
 */
export const median = (...numbers: number[]): number => {
  if (numbers.length === 0) return 0;

  const sorted = [...numbers].sort((a, b) => a - b);
  const middle = Math.floor(sorted.length / 2);

  if (sorted.length % 2 === 0) {
    return (sorted[middle - 1] + sorted[middle]) / 2;
  }

  return sorted[middle];
};

/**
 * Min of numbers
 * @example min(1, 2, 3, 4, 5) => 1
 */
export const min = (...numbers: number[]): number => {
  return Math.min(...numbers);
};

/**
 * Max of numbers
 * @example max(1, 2, 3, 4, 5) => 5
 */
export const max = (...numbers: number[]): number => {
  return Math.max(...numbers);
};

/**
 * Calculate percentage
 * @example percentage(25, 100) => 25
 */
export const percentage = (value: number, total: number): number => {
  if (total === 0) return 0;
  return (value / total) * 100;
};

/**
 * Calculate percentage change
 * @example percentageChange(100, 150) => 50
 */
export const percentageChange = (oldValue: number, newValue: number): number => {
  if (oldValue === 0) return 0;
  return ((newValue - oldValue) / Math.abs(oldValue)) * 100;
};

/**
 * Parse string to number (safe)
 * Returns fallback if parsing fails
 */
export const parseNumber = (value: string | number, fallback = 0): number => {
  if (typeof value === 'number') return value;

  const parsed = Number(value);
  return isNaN(parsed) ? fallback : parsed;
};

/**
 * Parse string to integer (safe)
 */
export const parseInt = (value: string | number, fallback = 0): number => {
  if (typeof value === 'number') return Math.floor(value);

  const parsed = Number.parseInt(value, 10);
  return isNaN(parsed) ? fallback : parsed;
};

/**
 * Parse string to float (safe)
 */
export const parseFloat = (value: string | number, fallback = 0): number => {
  if (typeof value === 'number') return value;

  const parsed = Number.parseFloat(value);
  return isNaN(parsed) ? fallback : parsed;
};

/**
 * Format file size in bytes to human readable
 * @example formatBytes(1024) => '1 KB'
 */
export const formatBytes = (bytes: number, decimals = 2): string => {
  if (bytes === 0) return '0 Bytes';

  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));

  return `${parseFloat((bytes / Math.pow(k, i)).toFixed(decimals))} ${sizes[i]}`;
};

/**
 * Parse human readable file size to bytes
 * @example parseBytes('1 KB') => 1024
 */
export const parseBytes = (size: string): number => {
  const units: Record<string, number> = {
    B: 1,
    Bytes: 1,
    KB: 1024,
    MB: 1024 * 1024,
    GB: 1024 * 1024 * 1024,
    TB: 1024 * 1024 * 1024 * 1024,
  };

  const match = size.match(/^(\d+(?:\.\d+)?)\s*([A-Z]+)$/i);
  if (!match) return 0;

  const [, value, unit] = match;
  const multiplier = units[unit.toUpperCase()] || 1;

  return parseFloat(value) * multiplier;
};

/**
 * Format duration in milliseconds to human readable
 * @example formatDuration(90000) => '1m 30s'
 */
export const formatDuration = (ms: number): string => {
  const seconds = Math.floor(ms / 1000);
  const minutes = Math.floor(seconds / 60);
  const hours = Math.floor(minutes / 60);
  const days = Math.floor(hours / 24);

  if (days > 0) {
    return `${days}d ${hours % 24}h`;
  }
  if (hours > 0) {
    return `${hours}h ${minutes % 60}m`;
  }
  if (minutes > 0) {
    return `${minutes}m ${seconds % 60}s`;
  }
  return `${seconds}s`;
};

/**
 * Convert degrees to radians
 */
export const toRadians = (degrees: number): number => {
  return (degrees * Math.PI) / 180;
};

/**
 * Convert radians to degrees
 */
export const toDegrees = (radians: number): number => {
  return (radians * 180) / Math.PI;
};

/**
 * Calculate factorial
 * @example factorial(5) => 120
 */
export const factorial = (n: number): number => {
  if (n < 0) return 0;
  if (n === 0 || n === 1) return 1;

  let result = 1;
  for (let i = 2; i <= n; i++) {
    result *= i;
  }
  return result;
};

/**
 * Check if number is prime
 */
export const isPrime = (num: number): boolean => {
  if (num <= 1) return false;
  if (num <= 3) return true;
  if (num % 2 === 0 || num % 3 === 0) return false;

  for (let i = 5; i * i <= num; i += 6) {
    if (num % i === 0 || num % (i + 2) === 0) return false;
  }

  return true;
};

/**
 * Pad number with leading zeros
 * @example padZero(5, 3) => '005'
 */
export const padZero = (num: number, length: number): string => {
  return String(num).padStart(length, '0');
};

/**
 * Ordinal suffix for number
 * @example ordinal(1) => '1st', ordinal(2) => '2nd'
 */
export const ordinal = (num: number): string => {
  const suffixes = ['th', 'st', 'nd', 'rd'];
  const value = num % 100;
  const suffix = suffixes[(value - 20) % 10] || suffixes[value] || suffixes[0];
  return `${num}${suffix}`;
};

/**
 * Linear interpolation between two numbers
 * @param a Start value
 * @param b End value
 * @param t Interpolation factor (0 to 1)
 */
export const lerp = (a: number, b: number, t: number): number => {
  return a + (b - a) * t;
};

/**
 * Inverse linear interpolation
 * Returns the interpolation factor for a value between a and b
 */
export const inverseLerp = (a: number, b: number, value: number): number => {
  return (value - a) / (b - a);
};

/**
 * Map value from one range to another
 * @example mapRange(5, 0, 10, 0, 100) => 50
 */
export const mapRange = (
  value: number,
  inMin: number,
  inMax: number,
  outMin: number,
  outMax: number
): number => {
  return ((value - inMin) * (outMax - outMin)) / (inMax - inMin) + outMin;
};

export default {
  formatNumber,
  formatCurrency,
  formatPercentage,
  formatCompact,
  round,
  floor,
  ceil,
  clamp,
  isBetween,
  isEven,
  isOdd,
  isPositive,
  isNegative,
  isZero,
  randomInt,
  randomFloat,
  sum,
  average,
  median,
  min,
  max,
  percentage,
  percentageChange,
  parseNumber,
  parseInt,
  parseFloat,
  formatBytes,
  parseBytes,
  formatDuration,
  toRadians,
  toDegrees,
  factorial,
  isPrime,
  padZero,
  ordinal,
  lerp,
  inverseLerp,
  mapRange,
};
