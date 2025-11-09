/**
 * URL Utilities
 *
 * Common URL manipulation functions
 */

/**
 * Build URL with query parameters
 */
export const buildUrl = (
  baseUrl: string,
  params?: Record<string, string | number | boolean | undefined | null>
): string => {
  if (!params || Object.keys(params).length === 0) {
    return baseUrl;
  }

  const queryParams = new URLSearchParams();

  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null) {
      queryParams.append(key, String(value));
    }
  });

  const queryString = queryParams.toString();
  if (!queryString) return baseUrl;

  const separator = baseUrl.includes('?') ? '&' : '?';
  return `${baseUrl}${separator}${queryString}`;
};

/**
 * Parse query string to object
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
 * Get query parameter by name
 */
export const getQueryParam = (url: string, param: string): string | null => {
  try {
    const urlObj = new URL(url);
    return urlObj.searchParams.get(param);
  } catch {
    return null;
  }
};

/**
 * Add or update query parameter
 */
export const setQueryParam = (url: string, param: string, value: string): string => {
  try {
    const urlObj = new URL(url);
    urlObj.searchParams.set(param, value);
    return urlObj.toString();
  } catch {
    return url;
  }
};

/**
 * Remove query parameter
 */
export const removeQueryParam = (url: string, param: string): string => {
  try {
    const urlObj = new URL(url);
    urlObj.searchParams.delete(param);
    return urlObj.toString();
  } catch {
    return url;
  }
};

/**
 * Get all query parameters as object
 */
export const getAllQueryParams = (url: string): Record<string, string> => {
  try {
    const urlObj = new URL(url);
    const params: Record<string, string> = {};

    urlObj.searchParams.forEach((value, key) => {
      params[key] = value;
    });

    return params;
  } catch {
    return {};
  }
};

/**
 * Get pathname from URL
 */
export const getPathname = (url: string): string => {
  try {
    const urlObj = new URL(url);
    return urlObj.pathname;
  } catch {
    return '';
  }
};

/**
 * Get domain from URL
 */
export const getDomain = (url: string): string => {
  try {
    const urlObj = new URL(url);
    return urlObj.hostname;
  } catch {
    return '';
  }
};

/**
 * Get protocol from URL
 */
export const getProtocol = (url: string): string => {
  try {
    const urlObj = new URL(url);
    return urlObj.protocol;
  } catch {
    return '';
  }
};

/**
 * Check if URL is absolute
 */
export const isAbsoluteUrl = (url: string): boolean => {
  return /^https?:\/\//i.test(url);
};

/**
 * Check if URL is relative
 */
export const isRelativeUrl = (url: string): boolean => {
  return !isAbsoluteUrl(url);
};

/**
 * Join URL paths
 */
export const joinPaths = (...paths: string[]): string => {
  return paths
    .map((path, index) => {
      if (index === 0) {
        return path.replace(/\/$/, '');
      }
      return path.replace(/^\//, '').replace(/\/$/, '');
    })
    .filter(Boolean)
    .join('/');
};

/**
 * Normalize URL path
 */
export const normalizePath = (path: string): string => {
  return path.replace(/\/+/g, '/').replace(/\/$/, '') || '/';
};

/**
 * Encode URL component safely
 */
export const encodeUrl = (str: string): string => {
  return encodeURIComponent(str);
};

/**
 * Decode URL component safely
 */
export const decodeUrl = (str: string): string => {
  try {
    return decodeURIComponent(str);
  } catch {
    return str;
  }
};

/**
 * Check if string is valid URL
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
 * Get base URL (protocol + domain)
 */
export const getBaseUrl = (url: string): string => {
  try {
    const urlObj = new URL(url);
    return `${urlObj.protocol}//${urlObj.host}`;
  } catch {
    return '';
  }
};

/**
 * Add trailing slash to URL
 */
export const addTrailingSlash = (url: string): string => {
  return url.endsWith('/') ? url : `${url}/`;
};

/**
 * Remove trailing slash from URL
 */
export const removeTrailingSlash = (url: string): string => {
  return url.replace(/\/$/, '');
};

/**
 * Get file extension from URL
 */
export const getFileExtension = (url: string): string => {
  const pathname = getPathname(url);
  const match = pathname.match(/\.([^.]+)$/);
  return match ? match[1] : '';
};

/**
 * Get filename from URL
 */
export const getFilename = (url: string): string => {
  const pathname = getPathname(url);
  return pathname.split('/').pop() || '';
};

export default {
  buildUrl,
  parseQueryString,
  getQueryParam,
  setQueryParam,
  removeQueryParam,
  getAllQueryParams,
  getPathname,
  getDomain,
  getProtocol,
  isAbsoluteUrl,
  isRelativeUrl,
  joinPaths,
  normalizePath,
  encodeUrl,
  decodeUrl,
  isValidUrl,
  getBaseUrl,
  addTrailingSlash,
  removeTrailingSlash,
  getFileExtension,
  getFilename,
};
