/**
 * String Utilities
 *
 * Common string manipulation and formatting functions
 */

/**
 * Capitalize first letter of a string
 * @example capitalize('hello') => 'Hello'
 */
export const capitalize = (str: string): string => {
  if (!str) return '';
  return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
};

/**
 * Capitalize first letter of each word
 * @example capitalizeWords('hello world') => 'Hello World'
 */
export const capitalizeWords = (str: string): string => {
  if (!str) return '';
  return str
    .split(' ')
    .map((word) => capitalize(word))
    .join(' ');
};

/**
 * Convert string to lowercase
 */
export const lowercase = (str: string): string => {
  return str?.toLowerCase() || '';
};

/**
 * Convert string to uppercase
 */
export const uppercase = (str: string): string => {
  return str?.toUpperCase() || '';
};

/**
 * Truncate string to specified length with ellipsis
 * @example truncate('Hello World', 5) => 'Hello...'
 */
export const truncate = (str: string, maxLength: number, suffix = '...'): string => {
  if (!str || str.length <= maxLength) return str;
  return str.substring(0, maxLength) + suffix;
};

/**
 * Truncate string in the middle
 * @example truncateMiddle('verylongfilename.txt', 15) => 'verylo...me.txt'
 */
export const truncateMiddle = (str: string, maxLength: number, separator = '...'): string => {
  if (!str || str.length <= maxLength) return str;

  const separatorLength = separator.length;
  const charsToShow = maxLength - separatorLength;
  const frontChars = Math.ceil(charsToShow / 2);
  const backChars = Math.floor(charsToShow / 2);

  return str.substring(0, frontChars) + separator + str.substring(str.length - backChars);
};

/**
 * Remove extra whitespace
 * @example removeExtraSpaces('hello  world   ') => 'hello world'
 */
export const removeExtraSpaces = (str: string): string => {
  return str?.trim().replace(/\s+/g, ' ') || '';
};

/**
 * Convert string to slug (URL-friendly)
 * @example slugify('Hello World!') => 'hello-world'
 */
export const slugify = (str: string): string => {
  return str
    .toLowerCase()
    .trim()
    .replace(/[^\w\s-]/g, '') // Remove non-word chars except spaces and hyphens
    .replace(/[\s_-]+/g, '-') // Replace spaces, underscores with single hyphen
    .replace(/^-+|-+$/g, ''); // Remove leading/trailing hyphens
};

/**
 * Convert slug to title
 * @example unslugify('hello-world') => 'Hello World'
 */
export const unslugify = (slug: string): string => {
  return capitalizeWords(slug.replace(/-/g, ' '));
};

/**
 * Convert string to camelCase
 * @example camelCase('hello world') => 'helloWorld'
 */
export const camelCase = (str: string): string => {
  return str
    .replace(/(?:^\w|[A-Z]|\b\w)/g, (word, index) =>
      index === 0 ? word.toLowerCase() : word.toUpperCase()
    )
    .replace(/\s+/g, '');
};

/**
 * Convert string to PascalCase
 * @example pascalCase('hello world') => 'HelloWorld'
 */
export const pascalCase = (str: string): string => {
  return str
    .replace(/(?:^\w|[A-Z]|\b\w)/g, (word) => word.toUpperCase())
    .replace(/\s+/g, '');
};

/**
 * Convert string to snake_case
 * @example snakeCase('Hello World') => 'hello_world'
 */
export const snakeCase = (str: string): string => {
  return str
    .replace(/\W+/g, ' ')
    .split(/ |\B(?=[A-Z])/)
    .map((word) => word.toLowerCase())
    .join('_');
};

/**
 * Convert string to kebab-case
 * @example kebabCase('Hello World') => 'hello-world'
 */
export const kebabCase = (str: string): string => {
  return str
    .replace(/\W+/g, ' ')
    .split(/ |\B(?=[A-Z])/)
    .map((word) => word.toLowerCase())
    .join('-');
};

/**
 * Check if string is empty or only whitespace
 */
export const isEmpty = (str: string | null | undefined): boolean => {
  return !str || str.trim().length === 0;
};

/**
 * Check if string is not empty
 */
export const isNotEmpty = (str: string | null | undefined): boolean => {
  return !isEmpty(str);
};

/**
 * Pad string to specified length
 * @example padStart('5', 3, '0') => '005'
 */
export const padStart = (str: string, length: number, char = ' '): string => {
  return str.padStart(length, char);
};

/**
 * Pad string to specified length (end)
 * @example padEnd('5', 3, '0') => '500'
 */
export const padEnd = (str: string, length: number, char = ' '): string => {
  return str.padEnd(length, char);
};

/**
 * Reverse a string
 * @example reverse('hello') => 'olleh'
 */
export const reverse = (str: string): string => {
  return str.split('').reverse().join('');
};

/**
 * Count occurrences of substring
 * @example countOccurrences('hello world', 'l') => 3
 */
export const countOccurrences = (str: string, substring: string): number => {
  if (!str || !substring) return 0;
  return (str.match(new RegExp(substring, 'g')) || []).length;
};

/**
 * Check if string contains substring (case-insensitive)
 */
export const containsIgnoreCase = (str: string, substring: string): boolean => {
  if (!str || !substring) return false;
  return str.toLowerCase().includes(substring.toLowerCase());
};

/**
 * Check if string starts with substring (case-insensitive)
 */
export const startsWithIgnoreCase = (str: string, substring: string): boolean => {
  if (!str || !substring) return false;
  return str.toLowerCase().startsWith(substring.toLowerCase());
};

/**
 * Check if string ends with substring (case-insensitive)
 */
export const endsWithIgnoreCase = (str: string, substring: string): boolean => {
  if (!str || !substring) return false;
  return str.toLowerCase().endsWith(substring.toLowerCase());
};

/**
 * Extract initials from name
 * @example getInitials('John Doe') => 'JD'
 */
export const getInitials = (name: string, maxLength = 2): string => {
  if (!name) return '';

  const parts = name.trim().split(/\s+/);
  const initials = parts.map((part) => part.charAt(0).toUpperCase());

  return initials.slice(0, maxLength).join('');
};

/**
 * Mask string (for sensitive data)
 * @example mask('1234567890', 4) => '******7890'
 */
export const mask = (str: string, visibleChars = 4, maskChar = '*'): string => {
  if (!str || str.length <= visibleChars) return str;

  const masked = maskChar.repeat(str.length - visibleChars);
  return masked + str.slice(-visibleChars);
};

/**
 * Mask email
 * @example maskEmail('john.doe@example.com') => 'j*****e@example.com'
 */
export const maskEmail = (email: string): string => {
  if (!email || !email.includes('@')) return email;

  const [localPart, domain] = email.split('@');
  if (localPart.length <= 2) return email;

  const maskedLocal = localPart.charAt(0) + '*'.repeat(localPart.length - 2) + localPart.slice(-1);
  return `${maskedLocal}@${domain}`;
};

/**
 * Generate random string
 * @param length Length of the string
 * @param charset Character set to use
 */
export const randomString = (
  length: number,
  charset = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'
): string => {
  let result = '';
  for (let i = 0; i < length; i++) {
    result += charset.charAt(Math.floor(Math.random() * charset.length));
  }
  return result;
};

/**
 * Convert string to boolean
 * @example parseBoolean('true') => true
 */
export const parseBoolean = (str: string): boolean => {
  const normalized = str?.toLowerCase().trim();
  return normalized === 'true' || normalized === '1' || normalized === 'yes';
};

/**
 * Escape HTML characters
 */
export const escapeHtml = (str: string): string => {
  const htmlEscapes: Record<string, string> = {
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    '"': '&quot;',
    "'": '&#x27;',
    '/': '&#x2F;',
  };

  return str.replace(/[&<>"'/]/g, (char) => htmlEscapes[char]);
};

/**
 * Unescape HTML characters
 */
export const unescapeHtml = (str: string): string => {
  const htmlUnescapes: Record<string, string> = {
    '&amp;': '&',
    '&lt;': '<',
    '&gt;': '>',
    '&quot;': '"',
    '&#x27;': "'",
    '&#x2F;': '/',
  };

  return str.replace(/&(?:amp|lt|gt|quot|#x27|#x2F);/g, (entity) => htmlUnescapes[entity]);
};

/**
 * Compare strings (case-insensitive)
 */
export const equalsIgnoreCase = (str1: string, str2: string): boolean => {
  if (!str1 || !str2) return str1 === str2;
  return str1.toLowerCase() === str2.toLowerCase();
};

/**
 * Word count
 */
export const wordCount = (str: string): number => {
  if (!str) return 0;
  return str.trim().split(/\s+/).length;
};

/**
 * Character count (excluding spaces)
 */
export const charCount = (str: string, excludeSpaces = false): number => {
  if (!str) return 0;
  return excludeSpaces ? str.replace(/\s/g, '').length : str.length;
};

/**
 * Highlight search terms in text
 * @example highlight('Hello World', 'world') => 'Hello <mark>World</mark>'
 */
export const highlight = (text: string, search: string, tag = 'mark'): string => {
  if (!text || !search) return text;

  const regex = new RegExp(`(${search})`, 'gi');
  return text.replace(regex, `<${tag}>$1</${tag}>`);
};

/**
 * Remove accents/diacritics
 * @example removeAccents('café') => 'cafe'
 */
export const removeAccents = (str: string): string => {
  return str.normalize('NFD').replace(/[\u0300-\u036f]/g, '');
};

/**
 * Format template string
 * @example template('Hello {name}!', { name: 'John' }) => 'Hello John!'
 */
export const template = (str: string, params: Record<string, string | number>): string => {
  return str.replace(/\{(\w+)\}/g, (match, key) => String(params[key] ?? match));
};

export default {
  capitalize,
  capitalizeWords,
  lowercase,
  uppercase,
  truncate,
  truncateMiddle,
  removeExtraSpaces,
  slugify,
  unslugify,
  camelCase,
  pascalCase,
  snakeCase,
  kebabCase,
  isEmpty,
  isNotEmpty,
  padStart,
  padEnd,
  reverse,
  countOccurrences,
  containsIgnoreCase,
  startsWithIgnoreCase,
  endsWithIgnoreCase,
  getInitials,
  mask,
  maskEmail,
  randomString,
  parseBoolean,
  escapeHtml,
  unescapeHtml,
  equalsIgnoreCase,
  wordCount,
  charCount,
  highlight,
  removeAccents,
  template,
};
