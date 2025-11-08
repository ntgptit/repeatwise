/**
 * Web Storage Service
 *
 * Centralized service for localStorage and sessionStorage
 * with error handling and type safety
 */

import { storageConfig } from '@/config/services.config';

/**
 * Storage type
 */
type StorageType = 'local' | 'session';

/**
 * Storage service class
 */
class WebStorageService {
  private readonly prefix: string;

  constructor(private readonly type: StorageType = 'local') {
    this.prefix =
      type === 'local'
        ? storageConfig.localStorage.prefix
        : storageConfig.sessionStorage.prefix;
  }

  /**
   * Get storage instance
   */
  private getStorage(): Storage {
    return this.type === 'local' ? localStorage : sessionStorage;
  }

  /**
   * Get prefixed key
   */
  private getPrefixedKey(key: string): string {
    return `${this.prefix}${key}`;
  }

  /**
   * Get item from storage
   */
  get<T>(key: string, defaultValue?: T): T | null {
    try {
      const storage = this.getStorage();
      const item = storage.getItem(this.getPrefixedKey(key));

      if (item === null) {
        return defaultValue ?? null;
      }

      return JSON.parse(item) as T;
    } catch (error) {
      console.error(`Error getting item "${key}" from ${this.type}Storage:`, error);
      return defaultValue ?? null;
    }
  }

  /**
   * Set item in storage
   */
  set<T>(key: string, value: T): boolean {
    try {
      const storage = this.getStorage();
      storage.setItem(this.getPrefixedKey(key), JSON.stringify(value));
      return true;
    } catch (error) {
      console.error(`Error setting item "${key}" in ${this.type}Storage:`, error);
      return false;
    }
  }

  /**
   * Remove item from storage
   */
  remove(key: string): boolean {
    try {
      const storage = this.getStorage();
      storage.removeItem(this.getPrefixedKey(key));
      return true;
    } catch (error) {
      console.error(`Error removing item "${key}" from ${this.type}Storage:`, error);
      return false;
    }
  }

  /**
   * Clear all items from storage
   */
  clear(): boolean {
    try {
      const storage = this.getStorage();

      // Only clear items with our prefix
      const keysToRemove: string[] = [];
      for (let i = 0; i < storage.length; i++) {
        const key = storage.key(i);
        if (key?.startsWith(this.prefix)) {
          keysToRemove.push(key);
        }
      }

      keysToRemove.forEach((key) => storage.removeItem(key));
      return true;
    } catch (error) {
      console.error(`Error clearing ${this.type}Storage:`, error);
      return false;
    }
  }

  /**
   * Check if key exists
   */
  has(key: string): boolean {
    try {
      const storage = this.getStorage();
      return storage.getItem(this.getPrefixedKey(key)) !== null;
    } catch (error) {
      console.error(`Error checking if "${key}" exists in ${this.type}Storage:`, error);
      return false;
    }
  }

  /**
   * Get all keys (with prefix)
   */
  keys(): string[] {
    try {
      const storage = this.getStorage();
      const keys: string[] = [];

      for (let i = 0; i < storage.length; i++) {
        const key = storage.key(i);
        if (key?.startsWith(this.prefix)) {
          keys.push(key.replace(this.prefix, ''));
        }
      }

      return keys;
    } catch (error) {
      console.error(`Error getting keys from ${this.type}Storage:`, error);
      return [];
    }
  }

  /**
   * Get storage size in bytes (approximate)
   */
  getSize(): number {
    try {
      const storage = this.getStorage();
      let size = 0;

      for (let i = 0; i < storage.length; i++) {
        const key = storage.key(i);
        if (key?.startsWith(this.prefix)) {
          const value = storage.getItem(key);
          size += key.length + (value?.length || 0);
        }
      }

      return size;
    } catch (error) {
      console.error(`Error getting ${this.type}Storage size:`, error);
      return 0;
    }
  }
}

/**
 * LocalStorage instance
 */
export const localStorageService = new WebStorageService('local');

/**
 * SessionStorage instance
 */
export const sessionStorageService = new WebStorageService('session');

/**
 * Check if storage is available
 */
export const isStorageAvailable = (type: StorageType = 'local'): boolean => {
  try {
    const storage = type === 'local' ? localStorage : sessionStorage;
    const testKey = '__storage_test__';
    storage.setItem(testKey, 'test');
    storage.removeItem(testKey);
    return true;
  } catch {
    return false;
  }
};

export default {
  local: localStorageService,
  session: sessionStorageService,
  isAvailable: isStorageAvailable,
};
