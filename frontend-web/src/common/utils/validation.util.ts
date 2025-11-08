/**
 * Validation Utilities
 *
 * Helper functions for common validation patterns.
 *
 * @module common/utils/validation
 */

/**
 * Validate email address
 *
 * @param email - Email string to validate
 * @returns True if valid email
 *
 * @example
 * ```ts
 * isValidEmail('user@example.com') // true
 * isValidEmail('invalid') // false
 * ```
 */
export const isValidEmail = (email: string): boolean => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
};

/**
 * Validate URL
 *
 * @param url - URL string to validate
 * @returns True if valid URL
 *
 * @example
 * ```ts
 * isValidUrl('https://example.com') // true
 * isValidUrl('not-a-url') // false
 * ```
 */
export const isValidUrl = (url: string): boolean => {
  try {
    new URL(url);
    return true;
  } catch {
    return false;
  }
};

/**
 * Validate phone number (basic)
 *
 * @param phone - Phone number string
 * @returns True if valid phone
 *
 * @example
 * ```ts
 * isValidPhone('+1234567890') // true
 * ```
 */
export const isValidPhone = (phone: string): boolean => {
  const phoneRegex = /^[+]?[(]?[0-9]{1,4}[)]?[-\s.]?[(]?[0-9]{1,4}[)]?[-\s.]?[0-9]{1,9}$/;
  return phoneRegex.test(phone);
};

/**
 * Validate password strength
 *
 * @param password - Password string
 * @param minLength - Minimum length (default: 8)
 * @returns True if strong password
 *
 * @example
 * ```ts
 * isStrongPassword('Pass123!') // true
 * isStrongPassword('weak') // false
 * ```
 */
export const isStrongPassword = (password: string, minLength = 8): boolean => {
  if (password.length < minLength) return false;

  const hasUpper = /[A-Z]/.test(password);
  const hasLower = /[a-z]/.test(password);
  const hasNumber = /\d/.test(password);
  const hasSpecial = /[!@#$%^&*(),.?":{}|<>]/.test(password);

  return hasUpper && hasLower && hasNumber && hasSpecial;
};

/**
 * Validate credit card number (Luhn algorithm)
 *
 * @param cardNumber - Card number string
 * @returns True if valid card number
 */
export const isValidCreditCard = (cardNumber: string): boolean => {
  const cleaned = cardNumber.replace(/\D/g, '');
  if (cleaned.length < 13 || cleaned.length > 19) return false;

  let sum = 0;
  let isEven = false;

  for (let i = cleaned.length - 1; i >= 0; i--) {
    let digit = parseInt(cleaned.charAt(i), 10);

    if (isEven) {
      digit *= 2;
      if (digit > 9) digit -= 9;
    }

    sum += digit;
    isEven = !isEven;
  }

  return sum % 10 === 0;
};

/**
 * Validate number is in range
 *
 * @param value - Number to validate
 * @param min - Minimum value
 * @param max - Maximum value
 * @returns True if in range
 */
export const isInRange = (value: number, min: number, max: number): boolean => {
  return value >= min && value <= max;
};

/**
 * Validate required field
 *
 * @param value - Value to check
 * @returns True if not empty
 */
export const isRequired = (value: unknown): boolean => {
  if (value === null || value === undefined) return false;
  if (typeof value === 'string') return value.trim().length > 0;
  if (Array.isArray(value)) return value.length > 0;
  return true;
};

/**
 * Validate minimum length
 *
 * @param value - String to validate
 * @param minLength - Minimum length
 * @returns True if meets minimum
 */
export const hasMinLength = (value: string, minLength: number): boolean => {
  return value.length >= minLength;
};

/**
 * Validate maximum length
 *
 * @param value - String to validate
 * @param maxLength - Maximum length
 * @returns True if within maximum
 */
export const hasMaxLength = (value: string, maxLength: number): boolean => {
  return value.length <= maxLength;
};

/**
 * Validate alphanumeric string
 *
 * @param value - String to validate
 * @returns True if alphanumeric
 */
export const isAlphanumeric = (value: string): boolean => {
  return /^[a-zA-Z0-9]+$/.test(value);
};

/**
 * Validate numeric string
 *
 * @param value - String to validate
 * @returns True if numeric
 */
export const isNumeric = (value: string): boolean => {
  return /^\d+$/.test(value);
};
