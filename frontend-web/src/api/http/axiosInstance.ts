import axios from 'axios'
import type {
  AxiosInstance,
  AxiosRequestConfig,
  AxiosResponse,
  InternalAxiosRequestConfig,
} from 'axios'
import { API_CONFIG } from '@/constants/api'
import { requestInterceptor } from './interceptors/request.interceptor'
import { responseInterceptor } from './interceptors/response.interceptor'
import { errorInterceptor } from './interceptors/error.interceptor'
import { loggingInterceptor } from './interceptors/logging.interceptor'
import type {
  ApiRequestConfig,
  ApiRequestMetadata,
  ApiResponse,
} from '@/api/types/api-response'

/**
 * Enhanced Axios Instance Configuration
 * Centralized HTTP client with interceptors
 */
class AxiosInstanceConfig {
  private readonly instance: AxiosInstance

  constructor() {
    this.instance = axios.create({
      baseURL: API_CONFIG.BASE_URL,
      timeout: API_CONFIG.TIMEOUT,
      withCredentials: true, // Include cookies for refresh token
      headers: {
        'Content-Type': 'application/json',
      },
    })

    this.setupInterceptors()
  }

  /**
   * Setup request and response interceptors
   */
  private setupInterceptors(): void {
    // Request Interceptor - Add auth token, headers
    this.instance.interceptors.request.use(
      (config: InternalAxiosRequestConfig) => {
        const customConfig = config as InternalAxiosRequestConfig & {
          skipLogging?: boolean
          _requestMetadata?: ApiRequestMetadata
        }

        // Log request and store metadata (unless skipped)
        if (!customConfig.skipLogging) {
          customConfig._requestMetadata = loggingInterceptor.logRequest(config)
        }

        // Add auth token and headers
        return requestInterceptor.onFulfilled(config)
      },
      (error: unknown) => {
        requestInterceptor.onRejected(error)
        return Promise.reject(error)
      },
    )

    // Response Interceptor - Handle successful responses
    this.instance.interceptors.response.use(
      <T>(response: AxiosResponse<ApiResponse<T>>) => {
        const config = response.config as InternalAxiosRequestConfig & {
          skipLogging?: boolean
          _requestMetadata?: ApiRequestMetadata
        }

        // Log response (unless skipped)
        if (!config.skipLogging && config._requestMetadata) {
          loggingInterceptor.logResponse(config._requestMetadata, response.data)
        }

        return responseInterceptor.onFulfilled<T>(response)
      },
      async (error: unknown) => {
        // Log error if axios error
        if (axios.isAxiosError(error) && error.config) {
          const config = error.config as InternalAxiosRequestConfig & {
            skipLogging?: boolean
            _requestMetadata?: ApiRequestMetadata
          }

          if (!config.skipLogging && config._requestMetadata) {
            loggingInterceptor.logError(config._requestMetadata, error)
          }
        }

        // Handle error - cast to AxiosError for interceptor
        if (axios.isAxiosError(error)) {
          return errorInterceptor.onRejected(error)
        }
        throw error
      },
    )
  }

  /**
   * GET request
   */
  async get<T = unknown>(
    url: string,
    config?: AxiosRequestConfig & ApiRequestConfig,
  ): Promise<T> {
    const response = await this.instance.get<ApiResponse<T>>(url, config)
    return response.data.data
  }

  /**
   * POST request
   */
  async post<T = unknown, D = unknown>(
    url: string,
    data?: D,
    config?: AxiosRequestConfig & ApiRequestConfig,
  ): Promise<T> {
    const response = await this.instance.post<ApiResponse<T>>(
      url,
      data,
      config,
    )
    return response.data.data
  }

  /**
   * PUT request
   */
  async put<T = unknown, D = unknown>(
    url: string,
    data?: D,
    config?: AxiosRequestConfig & ApiRequestConfig,
  ): Promise<T> {
    const response = await this.instance.put<ApiResponse<T>>(
      url,
      data,
      config,
    )
    return response.data.data
  }

  /**
   * PATCH request
   */
  async patch<T = unknown, D = unknown>(
    url: string,
    data?: D,
    config?: AxiosRequestConfig & ApiRequestConfig,
  ): Promise<T> {
    const response = await this.instance.patch<ApiResponse<T>>(
      url,
      data,
      config,
    )
    return response.data.data
  }

  /**
   * DELETE request
   */
  async delete<T = unknown>(
    url: string,
    config?: AxiosRequestConfig & ApiRequestConfig,
  ): Promise<T> {
    const response = await this.instance.delete<ApiResponse<T>>(url, config)
    return response.data.data
  }

  /**
   * Get axios instance (for advanced usage)
   */
  getInstance(): AxiosInstance {
    return this.instance
  }
}

// Export singleton instance
export const axiosInstance = new AxiosInstanceConfig()

/**
 * HTTP Client - Type-safe API methods
 * Returns data directly (not wrapped in ApiResponse)
 */
export const http = {
  get: <T = unknown>(
    url: string,
    config?: AxiosRequestConfig & ApiRequestConfig,
  ) => axiosInstance.get<T>(url, config),

  post: <T = unknown, D = unknown>(
    url: string,
    data?: D,
    config?: AxiosRequestConfig & ApiRequestConfig,
  ) => axiosInstance.post<T, D>(url, data, config),

  put: <T = unknown, D = unknown>(
    url: string,
    data?: D,
    config?: AxiosRequestConfig & ApiRequestConfig,
  ) => axiosInstance.put<T, D>(url, data, config),

  patch: <T = unknown, D = unknown>(
    url: string,
    data?: D,
    config?: AxiosRequestConfig & ApiRequestConfig,
  ) => axiosInstance.patch<T, D>(url, data, config),

  delete: <T = unknown>(
    url: string,
    config?: AxiosRequestConfig & ApiRequestConfig,
  ) => axiosInstance.delete<T>(url, config),
}