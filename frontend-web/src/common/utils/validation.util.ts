/**
 * Validation Utilities
 *
 * Common validation functions
 */

import { VALIDATION_RULES } from '@/config/app.config';

/**
 * Validate email format
 */
export const isValidEmail = (email: string): boolean => {
  return VALIDATION_RULES.EMAIL.pattern.test(email);
};

/**
 * Validate username format
 */
export const isValidUsername = (username: string): boolean => {
  return VALIDATION_RULES.USERNAME.pattern.test(username);
};

/**
 * Validate password strength
 */
export const isValidPassword = (password: string): boolean => {
  return (
    password.length >= VALIDATION_RULES.PASSWORD.minLength &&
    password.length <= VALIDATION_RULES.PASSWORD.maxLength
  );
};

/**
 * Validate URL format
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
 * Validate phone number (simple)
 */
export const isValidPhone = (phone: string): boolean => {
  const phoneRegex = /^\+?[\d\s-()]{10,}$/;
  return phoneRegex.test(phone);
};

/**
 * Check if string is empty or only whitespace
 */
export const isEmpty = (value: string | null | undefined): boolean => {
  return !value || value.trim().length === 0;
};

/**
 * Check if value is not empty
 */
export const isNotEmpty = (value: string | null | undefined): boolean => {
  return !isEmpty(value);
};

/**
 * Validate required field
 */
export const isRequired = (value: unknown): boolean => {
  if (value === null || value === undefined) return false;
  if (typeof value === 'string') return value.trim().length > 0;
  if (Array.isArray(value)) return value.length > 0;
  return true;
};

/**
 * Validate min length
 */
export const minLength = (value: string, min: number): boolean => {
  return value.length >= min;
};

/**
 * Validate max length
 */
export const maxLength = (value: string, max: number): boolean => {
  return value.length <= max;
};

/**
 * Validate length range
 */
export const lengthBetween = (value: string, min: number, max: number): boolean => {
  return value.length >= min && value.length <= max;
};

/**
 * Validate min value
 */
export const minValue = (value: number, min: number): boolean => {
  return value >= min;
};

/**
 * Validate max value
 */
export const maxValue = (value: number, max: number): boolean => {
  return value <= max;
};

/**
 * Validate value range
 */
export const valueBetween = (value: number, min: number, max: number): boolean => {
  return value >= min && value <= max;
};

/**
 * Validate pattern match
 */
export const matchesPattern = (value: string, pattern: RegExp): boolean => {
  return pattern.test(value);
};

/**
 * Validate values are equal
 */
export const equals = (value1: unknown, value2: unknown): boolean => {
  return value1 === value2;
};

/**
 * Validate alphanumeric
 */
export const isAlphanumeric = (value: string): boolean => {
  return /^[a-zA-Z0-9]+$/.test(value);
};

/**
 * Validate numeric
 */
export const isNumeric = (value: string): boolean => {
  return /^\d+$/.test(value);
};

/**
 * Validate alphabetic
 */
export const isAlphabetic = (value: string): boolean => {
  return /^[a-zA-Z]+$/.test(value);
};

export default {
  isValidEmail,
  isValidUsername,
  isValidPassword,
  isValidUrl,
  isValidPhone,
  isEmpty,
  isNotEmpty,
  isRequired,
  minLength,
  maxLength,
  lengthBetween,
  minValue,
  maxValue,
  valueBetween,
  matchesPattern,
  equals,
  isAlphanumeric,
  isNumeric,
  isAlphabetic,
};
