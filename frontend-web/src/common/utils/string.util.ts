/**
 * String Utilities
 *
 * Helper functions for string manipulation and formatting.
 *
 * @module common/utils/string
 */

/**
 * Capitalize first letter of string
 *
 * @param str - Input string
 * @returns String with first letter capitalized
 *
 * @example
 * ```ts
 * capitalize('hello') // 'Hello'
 * ```
 */
export const capitalize = (str: string): string => {
  if (!str) return '';
  return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
};

/**
 * Capitalize first letter of each word
 *
 * @param str - Input string
 * @returns String with each word capitalized
 *
 * @example
 * ```ts
 * capitalizeWords('hello world') // 'Hello World'
 * ```
 */
export const capitalizeWords = (str: string): string => {
  if (!str) return '';
  return str.replace(/\b\w/g, (char) => char.toUpperCase());
};

/**
 * Convert string to kebab-case
 *
 * @param str - Input string
 * @returns kebab-case string
 *
 * @example
 * ```ts
 * toKebabCase('Hello World') // 'hello-world'
 * ```
 */
export const toKebabCase = (str: string): string => {
  return str
    .replace(/([a-z])([A-Z])/g, '$1-$2')
    .replace(/[\s_]+/g, '-')
    .toLowerCase();
};

/**
 * Convert string to camelCase
 *
 * @param str - Input string
 * @returns camelCase string
 *
 * @example
 * ```ts
 * toCamelCase('hello-world') // 'helloWorld'
 * ```
 */
export const toCamelCase = (str: string): string => {
  return str
    .replace(/[-_\s]+(.)?/g, (_, char) => (char ? char.toUpperCase() : ''))
    .replace(/^[A-Z]/, (char) => char.toLowerCase());
};

/**
 * Convert string to PascalCase
 *
 * @param str - Input string
 * @returns PascalCase string
 *
 * @example
 * ```ts
 * toPascalCase('hello-world') // 'HelloWorld'
 * ```
 */
export const toPascalCase = (str: string): string => {
  const camel = toCamelCase(str);
  return camel.charAt(0).toUpperCase() + camel.slice(1);
};

/**
 * Convert string to snake_case
 *
 * @param str - Input string
 * @returns snake_case string
 *
 * @example
 * ```ts
 * toSnakeCase('helloWorld') // 'hello_world'
 * ```
 */
export const toSnakeCase = (str: string): string => {
  return str
    .replace(/([a-z])([A-Z])/g, '$1_$2')
    .replace(/[\s-]+/g, '_')
    .toLowerCase();
};

/**
 * Truncate string to specified length
 *
 * @param str - Input string
 * @param length - Maximum length
 * @param suffix - Suffix to append (default: '...')
 * @returns Truncated string
 *
 * @example
 * ```ts
 * truncate('Hello World', 8) // 'Hello...'
 * ```
 */
export const truncate = (str: string, length: number, suffix = '...'): string => {
  if (str.length <= length) return str;
  return str.slice(0, length - suffix.length) + suffix;
};

/**
 * Remove extra whitespace from string
 *
 * @param str - Input string
 * @returns String with single spaces
 *
 * @example
 * ```ts
 * removeExtraSpaces('hello   world') // 'hello world'
 * ```
 */
export const removeExtraSpaces = (str: string): string => {
  return str.replace(/\s+/g, ' ').trim();
};

/**
 * Generate slug from string
 *
 * @param str - Input string
 * @returns URL-safe slug
 *
 * @example
 * ```ts
 * slugify('Hello World!') // 'hello-world'
 * ```
 */
export const slugify = (str: string): string => {
  return str
    .toLowerCase()
    .trim()
    .replace(/[^\w\s-]/g, '')
    .replace(/[\s_-]+/g, '-')
    .replace(/^-+|-+$/g, '');
};

/**
 * Generate random string
 *
 * @param length - Length of string
 * @param chars - Character set to use
 * @returns Random string
 *
 * @example
 * ```ts
 * randomString(8) // 'a7b3c9d2'
 * ```
 */
export const randomString = (
  length: number,
  chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'
): string => {
  let result = '';
  for (let i = 0; i < length; i++) {
    result += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  return result;
};

/**
 * Check if string is empty or whitespace
 *
 * @param str - Input string
 * @returns True if empty or whitespace
 *
 * @example
 * ```ts
 * isEmpty('   ') // true
 * isEmpty('hello') // false
 * ```
 */
export const isEmpty = (str: string | null | undefined): boolean => {
  return !str || str.trim().length === 0;
};

/**
 * Format phone number
 *
 * @param phone - Phone number string
 * @param format - Format pattern (default: US format)
 * @returns Formatted phone number
 *
 * @example
 * ```ts
 * formatPhone('1234567890') // '(123) 456-7890'
 * ```
 */
export const formatPhone = (phone: string, format = '(###) ###-####'): string => {
  const cleaned = phone.replace(/\D/g, '');
  let formatted = format;
  for (const digit of cleaned) {
    formatted = formatted.replace('#', digit);
  }
  return formatted;
};

/**
 * Escape HTML special characters
 *
 * @param str - Input string
 * @returns Escaped string
 *
 * @example
 * ```ts
 * escapeHtml('<div>Hello</div>') // '&lt;div&gt;Hello&lt;/div&gt;'
 * ```
 */
export const escapeHtml = (str: string): string => {
  const map: Record<string, string> = {
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    '"': '&quot;',
    "'": '&#039;',
  };
  return str.replace(/[&<>"']/g, (char) => map[char] ?? char);
};

/**
 * Highlight search term in text
 *
 * @param text - Input text
 * @param searchTerm - Term to highlight
 * @param className - CSS class for highlight
 * @returns Text with highlighted term
 *
 * @example
 * ```ts
 * highlightText('Hello World', 'World', 'highlight')
 * // 'Hello <mark class="highlight">World</mark>'
 * ```
 */
export const highlightText = (
  text: string,
  searchTerm: string,
  className = 'highlight'
): string => {
  if (!searchTerm) return text;
  const regex = new RegExp(`(${escapeRegex(searchTerm)})`, 'gi');
  return text.replace(regex, `<mark class="${className}">$1</mark>`);
};

/**
 * Escape regex special characters
 *
 * @param str - Input string
 * @returns Escaped string
 *
 * @example
 * ```ts
 * escapeRegex('hello.world') // 'hello\\.world'
 * ```
 */
export const escapeRegex = (str: string): string => {
  return str.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
};

/**
 * Get initials from name
 *
 * @param name - Full name
 * @param maxLength - Maximum number of initials (default: 2)
 * @returns Initials
 *
 * @example
 * ```ts
 * getInitials('John Doe') // 'JD'
 * getInitials('John Paul Smith', 3) // 'JPS'
 * ```
 */
export const getInitials = (name: string, maxLength = 2): string => {
  return name
    .split(/\s+/)
    .filter(Boolean)
    .map((word) => word[0])
    .slice(0, maxLength)
    .join('')
    .toUpperCase();
};

/**
 * Pluralize word based on count
 *
 * @param count - Number count
 * @param singular - Singular form
 * @param plural - Plural form (optional)
 * @returns Pluralized string
 *
 * @example
 * ```ts
 * pluralize(1, 'item') // '1 item'
 * pluralize(5, 'item') // '5 items'
 * pluralize(2, 'box', 'boxes') // '2 boxes'
 * ```
 */
export const pluralize = (count: number, singular: string, plural?: string): string => {
  const word = count === 1 ? singular : plural ?? `${singular}s`;
  return `${count} ${word}`;
};
