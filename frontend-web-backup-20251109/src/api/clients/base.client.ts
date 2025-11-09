/**
 * Base API Client
 *
 * Axios instance with interceptors configured
 */

import axios, { type AxiosInstance } from 'axios'
import { apiConfig } from '@/config/api.config'
import { authInterceptor } from '../interceptors/auth.interceptor'
import { errorInterceptor } from '../interceptors/error.interceptor'
import { loggerInterceptor } from '../interceptors/logger.interceptor'
import { retryInterceptor } from '../interceptors/retry.interceptor'

/**
 * Create base axios instance
 */
const createApiClient = (): AxiosInstance => {
  const instance = axios.create({
    baseURL: apiConfig.baseURL,
    timeout: apiConfig.timeout,
    headers: apiConfig.headers,
  })

  // Request/Response interceptors
  authInterceptor(instance)
  loggerInterceptor(instance)
  retryInterceptor(instance)
  errorInterceptor(instance)

  return instance
}

/**
 * API client instance
 */
export const apiClient = createApiClient()

export default apiClient
