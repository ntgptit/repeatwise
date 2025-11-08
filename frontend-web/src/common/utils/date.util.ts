/**
 * Date Utilities
 *
 * Helper functions for date formatting and manipulation.
 *
 * @module common/utils/date
 */

/**
 * Format date to string
 *
 * @param date - Date to format
 * @param format - Format string (default: 'MMM dd, yyyy')
 * @returns Formatted date string
 *
 * @example
 * ```ts
 * formatDate(new Date()) // 'Nov 08, 2025'
 * ```
 */
export const formatDate = (date: Date | string | number, format = 'MMM dd, yyyy'): string => {
  const d = new Date(date);
  if (isNaN(d.getTime())) return '';

  const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
  const fullMonths = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'];

  return format
    .replace('MMMM', fullMonths[d.getMonth()] ?? '')
    .replace('MMM', months[d.getMonth()] ?? '')
    .replace('MM', String(d.getMonth() + 1).padStart(2, '0'))
    .replace('dd', String(d.getDate()).padStart(2, '0'))
    .replace('d', String(d.getDate()))
    .replace('yyyy', String(d.getFullYear()))
    .replace('yy', String(d.getFullYear()).slice(-2))
    .replace('HH', String(d.getHours()).padStart(2, '0'))
    .replace('mm', String(d.getMinutes()).padStart(2, '0'))
    .replace('ss', String(d.getSeconds()).padStart(2, '0'));
};

/**
 * Format date as relative time
 *
 * @param date - Date to format
 * @returns Relative time string
 *
 * @example
 * ```ts
 * formatRelative(new Date()) // 'just now'
 * formatRelative(subDays(new Date(), 2)) // '2 days ago'
 * ```
 */
export const formatRelative = (date: Date | string | number): string => {
  const d = new Date(date);
  const now = new Date();
  const diffMs = now.getTime() - d.getTime();
  const diffSec = Math.floor(diffMs / 1000);
  const diffMin = Math.floor(diffSec / 60);
  const diffHour = Math.floor(diffMin / 60);
  const diffDay = Math.floor(diffHour / 24);
  const diffWeek = Math.floor(diffDay / 7);
  const diffMonth = Math.floor(diffDay / 30);
  const diffYear = Math.floor(diffDay / 365);

  if (diffSec < 60) return 'just now';
  if (diffMin < 60) return `${diffMin} ${diffMin === 1 ? 'minute' : 'minutes'} ago`;
  if (diffHour < 24) return `${diffHour} ${diffHour === 1 ? 'hour' : 'hours'} ago`;
  if (diffDay < 7) return `${diffDay} ${diffDay === 1 ? 'day' : 'days'} ago`;
  if (diffWeek < 4) return `${diffWeek} ${diffWeek === 1 ? 'week' : 'weeks'} ago`;
  if (diffMonth < 12) return `${diffMonth} ${diffMonth === 1 ? 'month' : 'months'} ago`;
  return `${diffYear} ${diffYear === 1 ? 'year' : 'years'} ago`;
};

/**
 * Check if date is today
 *
 * @param date - Date to check
 * @returns True if today
 */
export const isToday = (date: Date | string | number): boolean => {
  const d = new Date(date);
  const today = new Date();
  return (
    d.getDate() === today.getDate() &&
    d.getMonth() === today.getMonth() &&
    d.getFullYear() === today.getFullYear()
  );
};

/**
 * Check if date is in past
 *
 * @param date - Date to check
 * @returns True if in past
 */
export const isPast = (date: Date | string | number): boolean => {
  return new Date(date).getTime() < Date.now();
};

/**
 * Check if date is in future
 *
 * @param date - Date to check
 * @returns True if in future
 */
export const isFuture = (date: Date | string | number): boolean => {
  return new Date(date).getTime() > Date.now();
};

/**
 * Add days to date
 *
 * @param date - Base date
 * @param days - Number of days to add
 * @returns New date
 */
export const addDays = (date: Date, days: number): Date => {
  const result = new Date(date);
  result.setDate(result.getDate() + days);
  return result;
};

/**
 * Subtract days from date
 *
 * @param date - Base date
 * @param days - Number of days to subtract
 * @returns New date
 */
export const subDays = (date: Date, days: number): Date => {
  return addDays(date, -days);
};

/**
 * Get start of day
 *
 * @param date - Input date
 * @returns Start of day (00:00:00)
 */
export const startOfDay = (date: Date): Date => {
  const d = new Date(date);
  d.setHours(0, 0, 0, 0);
  return d;
};

/**
 * Get end of day
 *
 * @param date - Input date
 * @returns End of day (23:59:59)
 */
export const endOfDay = (date: Date): Date => {
  const d = new Date(date);
  d.setHours(23, 59, 59, 999);
  return d;
};

/**
 * Parse ISO date string
 *
 * @param dateString - ISO date string
 * @returns Date object
 */
export const parseISO = (dateString: string): Date => {
  return new Date(dateString);
};

/**
 * Format date to ISO string
 *
 * @param date - Date to format
 * @returns ISO date string
 */
export const toISO = (date: Date): string => {
  return date.toISOString();
};
