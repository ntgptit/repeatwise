/**
 * Date Utilities
 *
 * Common date manipulation and formatting functions
 * Note: Consider using date-fns or dayjs for production
 */

/**
 * Format date to string
 * @example formatDate(new Date(), 'yyyy-MM-dd') => '2024-01-15'
 */
export const formatDate = (date: Date | string | number, format = 'yyyy-MM-dd'): string => {
  const d = new Date(date);

  if (isNaN(d.getTime())) {
    return '';
  }

  const pad = (n: number) => String(n).padStart(2, '0');

  const replacements: Record<string, string> = {
    yyyy: String(d.getFullYear()),
    yy: String(d.getFullYear()).slice(-2),
    MM: pad(d.getMonth() + 1),
    M: String(d.getMonth() + 1),
    dd: pad(d.getDate()),
    d: String(d.getDate()),
    HH: pad(d.getHours()),
    H: String(d.getHours()),
    hh: pad(d.getHours() % 12 || 12),
    h: String(d.getHours() % 12 || 12),
    mm: pad(d.getMinutes()),
    m: String(d.getMinutes()),
    ss: pad(d.getSeconds()),
    s: String(d.getSeconds()),
  };

  return format.replace(/yyyy|yy|MM|M|dd|d|HH|H|hh|h|mm|m|ss|s/g, (match) => replacements[match]);
};

/**
 * Parse date from string
 */
export const parseDate = (dateString: string): Date | null => {
  const date = new Date(dateString);
  return isNaN(date.getTime()) ? null : date;
};

/**
 * Check if value is a valid date
 */
export const isValidDate = (date: unknown): boolean => {
  if (date instanceof Date) {
    return !isNaN(date.getTime());
  }
  if (typeof date === 'string' || typeof date === 'number') {
    return !isNaN(new Date(date).getTime());
  }
  return false;
};

/**
 * Get current date/time
 */
export const now = (): Date => new Date();

/**
 * Get today at midnight
 */
export const today = (): Date => {
  const date = new Date();
  date.setHours(0, 0, 0, 0);
  return date;
};

/**
 * Add days to date
 */
export const addDays = (date: Date, days: number): Date => {
  const result = new Date(date);
  result.setDate(result.getDate() + days);
  return result;
};

/**
 * Get difference between two dates in days
 */
export const differenceInDays = (date1: Date, date2: Date): number => {
  const ms = Math.abs(date1.getTime() - date2.getTime());
  return Math.floor(ms / (1000 * 60 * 60 * 24));
};

/**
 * Check if date is today
 */
export const isToday = (date: Date): boolean => {
  const todayDate = new Date();
  return (
    date.getDate() === todayDate.getDate() &&
    date.getMonth() === todayDate.getMonth() &&
    date.getFullYear() === todayDate.getFullYear()
  );
};

/**
 * Format relative time (e.g., "2 hours ago")
 */
export const formatRelative = (date: Date): string => {
  const nowDate = new Date();
  const seconds = Math.floor((nowDate.getTime() - date.getTime()) / 1000);

  if (seconds < 60) return 'just now';
  if (seconds < 120) return '1 minute ago';
  if (seconds < 3600) return `${Math.floor(seconds / 60)} minutes ago`;
  if (seconds < 7200) return '1 hour ago';
  if (seconds < 86400) return `${Math.floor(seconds / 3600)} hours ago`;
  if (seconds < 172800) return 'yesterday';
  if (seconds < 604800) return `${Math.floor(seconds / 86400)} days ago`;
  if (seconds < 1209600) return '1 week ago';
  if (seconds < 2592000) return `${Math.floor(seconds / 604800)} weeks ago`;
  if (seconds < 5184000) return '1 month ago';
  if (seconds < 31536000) return `${Math.floor(seconds / 2592000)} months ago`;
  if (seconds < 63072000) return '1 year ago';
  return `${Math.floor(seconds / 31536000)} years ago`;
};

export default {
  formatDate,
  parseDate,
  isValidDate,
  now,
  today,
  addDays,
  differenceInDays,
  isToday,
  formatRelative,
};
