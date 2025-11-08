/**
 * URL Utilities
 *
 * Helper functions for URL manipulation and query string handling.
 *
 * @module common/utils/url
 */

/**
 * Parse query string to object
 *
 * @param queryString - Query string (with or without ?)
 * @returns Object with query parameters
 *
 * @example
 * ```ts
 * parseQueryString('?name=John&age=30') // { name: 'John', age: '30' }
 * ```
 */
export const parseQueryString = (queryString: string): Record<string, string> => {
  const params = new URLSearchParams(queryString);
  const result: Record<string, string> = {};

  params.forEach((value, key) => {
    result[key] = value;
  });

  return result;
};

/**
 * Build query string from object
 *
 * @param params - Parameters object
 * @returns Query string
 *
 * @example
 * ```ts
 * buildQueryString({ name: 'John', age: 30 }) // 'name=John&age=30'
 * ```
 */
export const buildQueryString = (params: Record<string, unknown>): string => {
  const searchParams = new URLSearchParams();

  Object.entries(params).forEach(([key, value]) => {
    if (value !== null && value !== undefined) {
      searchParams.append(key, String(value));
    }
  });

  return searchParams.toString();
};

/**
 * Add query parameters to URL
 *
 * @param url - Base URL
 * @param params - Parameters to add
 * @returns URL with query parameters
 *
 * @example
 * ```ts
 * addQueryParams('/api/users', { page: 1, limit: 10 })
 * // '/api/users?page=1&limit=10'
 * ```
 */
export const addQueryParams = (url: string, params: Record<string, unknown>): string => {
  const queryString = buildQueryString(params);
  if (!queryString) return url;

  const separator = url.includes('?') ? '&' : '?';
  return `${url}${separator}${queryString}`;
};

/**
 * Remove query parameters from URL
 *
 * @param url - URL with query parameters
 * @returns URL without query parameters
 *
 * @example
 * ```ts
 * removeQueryParams('/api/users?page=1&limit=10') // '/api/users'
 * ```
 */
export const removeQueryParams = (url: string): string => {
  return url.split('?')[0] ?? url;
};

/**
 * Get query parameter value
 *
 * @param url - URL or query string
 * @param param - Parameter name
 * @returns Parameter value or null
 *
 * @example
 * ```ts
 * getQueryParam('/api?page=1', 'page') // '1'
 * ```
 */
export const getQueryParam = (url: string, param: string): string | null => {
  const queryString = url.includes('?') ? url.split('?')[1] : url;
  const params = new URLSearchParams(queryString);
  return params.get(param);
};

/**
 * Update query parameter in URL
 *
 * @param url - Base URL
 * @param param - Parameter name
 * @param value - Parameter value
 * @returns Updated URL
 *
 * @example
 * ```ts
 * updateQueryParam('/api?page=1', 'page', '2') // '/api?page=2'
 * ```
 */
export const updateQueryParam = (url: string, param: string, value: string): string => {
  const [baseUrl, queryString] = url.split('?');
  const params = new URLSearchParams(queryString);
  params.set(param, value);
  return `${baseUrl ?? ''}?${params.toString()}`;
};

/**
 * Join URL parts safely
 *
 * @param parts - URL parts to join
 * @returns Joined URL
 *
 * @example
 * ```ts
 * joinUrl('api', 'users', '123') // 'api/users/123'
 * joinUrl('api/', '/users', 'profile/') // 'api/users/profile'
 * ```
 */
export const joinUrl = (...parts: string[]): string => {
  return parts
    .map((part, index) => {
      if (index === 0) {
        return part.replace(/\/+$/, '');
      }
      if (index === parts.length - 1) {
        return part.replace(/^\/+/, '');
      }
      return part.replace(/^\/+|\/+$/g, '');
    })
    .filter(Boolean)
    .join('/');
};

/**
 * Check if URL is absolute
 *
 * @param url - URL to check
 * @returns True if absolute URL
 *
 * @example
 * ```ts
 * isAbsoluteUrl('https://example.com') // true
 * isAbsoluteUrl('/api/users') // false
 * ```
 */
export const isAbsoluteUrl = (url: string): boolean => {
  return /^https?:\/\//i.test(url);
};

/**
 * Get domain from URL
 *
 * @param url - Full URL
 * @returns Domain
 *
 * @example
 * ```ts
 * getDomain('https://example.com/path') // 'example.com'
 * ```
 */
export const getDomain = (url: string): string => {
  try {
    return new URL(url).hostname;
  } catch {
    return '';
  }
};
