import type { InternalAxiosRequestConfig } from 'axios'
import { APP_CONFIG } from '@/constants/config'

/**
 * Request Interceptor
 * Handles adding authentication tokens and request headers
 */
export const requestInterceptor = {
  onFulfilled: (config: InternalAxiosRequestConfig) => {
    // Skip auth token for public endpoints
    const customConfig = config as InternalAxiosRequestConfig & {
      skipAuth?: boolean
    }

    if (!customConfig.skipAuth) {
      // Add auth token
      const token = localStorage.getItem(APP_CONFIG.STORAGE_KEYS.ACCESS_TOKEN)
      if (token) {
        config.headers.Authorization = `Bearer ${token}`
      }
    }

    // Add request ID for tracking
    config.headers['X-Request-ID'] = generateRequestId()

    return config
  },

  onRejected: (error: unknown) => {
    return Promise.reject(error)
  },
}

/**
 * Generate unique request ID
 */
function generateRequestId(): string {
  return `${Date.now()}-${Math.random().toString(36).substr(2, 9)}`
}