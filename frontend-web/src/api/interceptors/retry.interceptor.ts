/**
 * Retry Interceptor
 */

import { type AxiosInstance, AxiosError } from 'axios'
import { apiConfig } from '@/config/api.config'

export const retryInterceptor = (instance: AxiosInstance): void => {
  instance.interceptors.response.use(
    response => response,
    async (error: AxiosError) => {
      const config = error.config as typeof error.config & { _retry?: number }

      if (!config || !apiConfig.enableRetry) {
        throw error
      }

      const retryCount = config._retry || 0
      const statusCode = error.response?.status || 0

      if (retryCount < apiConfig.maxRetries && apiConfig.retryStatusCodes.includes(statusCode)) {
        config._retry = retryCount + 1

        await new Promise(resolve => setTimeout(resolve, apiConfig.retryDelay * (retryCount + 1)))

        return instance(config)
      }
      throw error
    }
  )
}

export default retryInterceptor
