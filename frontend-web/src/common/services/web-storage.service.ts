/**
 * Web Storage Service
 *
 * Unified service for localStorage and sessionStorage operations.
 * Handles JSON serialization, error handling, and provides type-safe API.
 *
 * @module common/services/web-storage
 */

import { servicesConfig } from '@/config';

/**
 * Storage type
 */
export type StorageType = 'localStorage' | 'sessionStorage';

/**
 * Storage options
 */
export interface StorageOptions {
  /**
   * Storage type (default: localStorage)
   */
  type?: StorageType;

  /**
   * Key prefix (default: from config)
   */
  prefix?: string;

  /**
   * Whether to use JSON serialization (default: true)
   */
  useJSON?: boolean;
}

/**
 * Web Storage Service class
 */
class WebStorageService {
  private readonly prefix: string;

  constructor() {
    this.prefix = servicesConfig.storage.keyPrefix;
  }

  /**
   * Get storage instance
   */
  private getStorage(type: StorageType = 'localStorage'): Storage | null {
    if (typeof window === 'undefined') {
      return null;
    }

    return type === 'localStorage' ? window.localStorage : window.sessionStorage;
  }

  /**
   * Generate prefixed key
   */
  private getPrefixedKey(key: string, customPrefix?: string): string {
    const prefix = customPrefix ?? this.prefix;
    return `${prefix}${key}`;
  }

  /**
   * Set item in storage
   *
   * @param key - Storage key
   * @param value - Value to store
   * @param options - Storage options
   * @returns True if successful
   *
   * @example
   * ```ts
   * storageService.setItem('user', { id: 1, name: 'John' });
   * storageService.setItem('token', 'abc123', { type: 'sessionStorage' });
   * ```
   */
  setItem<T>(key: string, value: T, options: StorageOptions = {}): boolean {
    const { type = 'localStorage', prefix, useJSON = true } = options;
    const storage = this.getStorage(type);

    if (!storage) {
      console.warn('Storage not available');
      return false;
    }

    try {
      const prefixedKey = this.getPrefixedKey(key, prefix);
      const valueToStore = useJSON ? JSON.stringify(value) : String(value);
      storage.setItem(prefixedKey, valueToStore);
      return true;
    } catch (error) {
      console.error(`Error setting storage item "${key}":`, error);
      return false;
    }
  }

  /**
   * Get item from storage
   *
   * @param key - Storage key
   * @param options - Storage options
   * @returns Stored value or null
   *
   * @example
   * ```ts
   * const user = storageService.getItem<User>('user');
   * const token = storageService.getItem<string>('token', { type: 'sessionStorage' });
   * ```
   */
  getItem<T>(key: string, options: StorageOptions = {}): T | null {
    const { type = 'localStorage', prefix, useJSON = true } = options;
    const storage = this.getStorage(type);

    if (!storage) {
      return null;
    }

    try {
      const prefixedKey = this.getPrefixedKey(key, prefix);
      const item = storage.getItem(prefixedKey);

      if (item === null) {
        return null;
      }

      return useJSON ? (JSON.parse(item) as T) : (item as T);
    } catch (error) {
      console.error(`Error getting storage item "${key}":`, error);
      return null;
    }
  }

  /**
   * Remove item from storage
   *
   * @param key - Storage key
   * @param options - Storage options
   * @returns True if successful
   *
   * @example
   * ```ts
   * storageService.removeItem('user');
   * ```
   */
  removeItem(key: string, options: StorageOptions = {}): boolean {
    const { type = 'localStorage', prefix } = options;
    const storage = this.getStorage(type);

    if (!storage) {
      return false;
    }

    try {
      const prefixedKey = this.getPrefixedKey(key, prefix);
      storage.removeItem(prefixedKey);
      return true;
    } catch (error) {
      console.error(`Error removing storage item "${key}":`, error);
      return false;
    }
  }

  /**
   * Clear all items from storage
   *
   * @param options - Storage options
   * @returns True if successful
   *
   * @example
   * ```ts
   * storageService.clear();
   * storageService.clear({ type: 'sessionStorage' });
   * ```
   */
  clear(options: StorageOptions = {}): boolean {
    const { type = 'localStorage' } = options;
    const storage = this.getStorage(type);

    if (!storage) {
      return false;
    }

    try {
      storage.clear();
      return true;
    } catch (error) {
      console.error('Error clearing storage:', error);
      return false;
    }
  }

  /**
   * Check if key exists in storage
   *
   * @param key - Storage key
   * @param options - Storage options
   * @returns True if key exists
   *
   * @example
   * ```ts
   * if (storageService.hasItem('user')) {
   *   // User is logged in
   * }
   * ```
   */
  hasItem(key: string, options: StorageOptions = {}): boolean {
    return this.getItem(key, options) !== null;
  }

  /**
   * Get all keys from storage
   *
   * @param options - Storage options
   * @returns Array of keys (without prefix)
   *
   * @example
   * ```ts
   * const keys = storageService.getKeys();
   * ```
   */
  getKeys(options: StorageOptions = {}): string[] {
    const { type = 'localStorage', prefix } = options;
    const storage = this.getStorage(type);

    if (!storage) {
      return [];
    }

    try {
      const prefixToMatch = prefix ?? this.prefix;
      const keys: string[] = [];

      for (let i = 0; i < storage.length; i++) {
        const key = storage.key(i);
        if (key?.startsWith(prefixToMatch)) {
          keys.push(key.substring(prefixToMatch.length));
        }
      }

      return keys;
    } catch (error) {
      console.error('Error getting storage keys:', error);
      return [];
    }
  }

  /**
   * Get storage size in bytes
   *
   * @param options - Storage options
   * @returns Approximate size in bytes
   */
  getSize(options: StorageOptions = {}): number {
    const { type = 'localStorage' } = options;
    const storage = this.getStorage(type);

    if (!storage) {
      return 0;
    }

    try {
      let size = 0;
      for (let i = 0; i < storage.length; i++) {
        const key = storage.key(i);
        if (key) {
          const value = storage.getItem(key);
          size += key.length + (value?.length ?? 0);
        }
      }
      return size;
    } catch (error) {
      console.error('Error calculating storage size:', error);
      return 0;
    }
  }
}

/**
 * Singleton instance of WebStorageService
 */
export const storageService = new WebStorageService();

/**
 * Export class for testing purposes
 */
export { WebStorageService };

/**
 * Re-export as default
 */
export default storageService;
