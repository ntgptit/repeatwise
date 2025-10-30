import axios, {
  AxiosError,
  AxiosInstance,
  AxiosRequestConfig,
  AxiosResponse,
  InternalAxiosRequestConfig,
} from 'axios'
import { toast } from 'sonner'
import { API_CONFIG, APP_CONFIG } from '@/constants'
import type { ApiError, ApiRequestConfig, ApiResponse, HttpStatus } from '@/types/api'

/**
 * HTTP Client with interceptors for API calls
 */
class ApiClient {
  private instance: AxiosInstance
  private isRefreshing = false
  private failedQueue: Array<{
    resolve: (value?: unknown) => void
    reject: (error?: unknown) => void
  }> = []

  constructor() {
    this.instance = axios.create({
      baseURL: API_CONFIG.BASE_URL,
      timeout: API_CONFIG.TIMEOUT,
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
    // Request Interceptor
    this.instance.interceptors.request.use(
      (config: InternalAxiosRequestConfig) => {
        // Add auth token
        const customConfig = config as InternalAxiosRequestConfig & {
          skipAuth?: boolean
        }

        if (!customConfig.skipAuth) {
          const token = this.getAccessToken()
          if (token) {
            config.headers.Authorization = `Bearer ${token}`
          }
        }

        // Add request ID for tracking
        config.headers['X-Request-ID'] = this.generateRequestId()

        return config
      },
      (error: AxiosError) => {
        return Promise.reject(error)
      },
    )

    // Response Interceptor
    this.instance.interceptors.response.use(
      (response: AxiosResponse<ApiResponse>) => {
        // Handle successful response
        return this.handleSuccessResponse(response)
      },
      async (error: AxiosError<ApiError>) => {
        // Handle error response
        return this.handleErrorResponse(error)
      },
    )
  }

  /**
   * Handle successful response
   */
  private handleSuccessResponse<T>(
    response: AxiosResponse<ApiResponse<T>>,
  ): AxiosResponse<ApiResponse<T>> {
    // Extract data from response if needed
    if (response.data?.data !== undefined) {
      return {
        ...response,
        data: response.data,
      }
    }
    return response
  }

  /**
   * Handle error response
   */
  private handleErrorResponse(
    error: AxiosError<ApiError>,
  ): Promise<never> {
    const config = error.config as InternalAxiosRequestConfig & {
      skipErrorHandler?: boolean
      _retry?: boolean
    }

    // Skip error handler if specified
    if (config?.skipErrorHandler) {
      return Promise.reject(error)
    }

    // Handle 401 Unauthorized - Token expired
    if (error.response?.status === HttpStatus.UNAUTHORIZED && !config?._retry) {
      return this.handleUnauthorizedError(error)
    }

    // Handle other errors
    const apiError = this.extractError(error)
    this.showErrorToast(apiError)

    return Promise.reject(apiError)
  }

  /**
   * Handle 401 Unauthorized - Refresh token
   */
  private async handleUnauthorizedError(
    error: AxiosError<ApiError>,
  ): Promise<never> {
    const config = error.config as InternalAxiosRequestConfig & {
      _retry?: boolean
      skipAuth?: boolean
    }

    // Skip auth endpoints
    if (config?.skipAuth) {
      return Promise.reject(this.extractError(error))
    }

    // If already refreshing, queue the request
    if (this.isRefreshing) {
      return new Promise((resolve, reject) => {
        this.failedQueue.push({ resolve, reject })
      })
        .then(() => {
          if (config) {
            const token = this.getAccessToken()
            if (token && config.headers) {
              config.headers.Authorization = `Bearer ${token}`
            }
            return this.instance.request(config)
          }
          return Promise.reject(this.extractError(error))
        })
        .catch((err) => {
          return Promise.reject(err)
        })
    }

    if (config) {
      config._retry = true
    }
    this.isRefreshing = true

    try {
      const refreshToken = this.getRefreshToken()
      if (!refreshToken) {
        this.logout()
        return Promise.reject(this.extractError(error))
      }

      // Call refresh token endpoint
      const response = await axios.post<ApiResponse<{ accessToken: string }>>(
        `${API_CONFIG.BASE_URL}/auth/refresh`,
        { refreshToken },
      )

      const { accessToken } = response.data.data
      this.setAccessToken(accessToken)

      // Retry failed requests
      this.processQueue(null)

      // Retry original request
      if (config && config.headers) {
        config.headers.Authorization = `Bearer ${accessToken}`
        return this.instance.request(config)
      }

      return Promise.reject(this.extractError(error))
    } catch (refreshError) {
      // Refresh failed, logout user
      this.processQueue(refreshError)
      this.logout()
      return Promise.reject(this.extractError(error))
    } finally {
      this.isRefreshing = false
    }
  }

  /**
   * Process queued requests after token refresh
   */
  private processQueue(error: unknown): void {
    this.failedQueue.forEach((promise) => {
      if (error) {
        promise.reject(error)
      } else {
        promise.resolve()
      }
    })
    this.failedQueue = []
  }

  /**
   * Extract error from axios error
   */
  private extractError(error: AxiosError<ApiError>): ApiError {
    if (error.response?.data) {
      return {
        message: error.response.data.message || 'An error occurred',
        code: error.response.data.code,
        status: error.response.status,
        errors: error.response.data.errors,
        timestamp: error.response.data.timestamp,
      }
    }

    if (error.request) {
      return {
        message: 'Network error. Please check your connection.',
        status: 0,
      }
    }

    return {
      message: error.message || 'An unexpected error occurred',
    }
  }

  /**
   * Show error toast notification
   */
  private showErrorToast(error: ApiError): void {
    // Don't show toast for auth errors (handled separately)
    if (error.status === HttpStatus.UNAUTHORIZED) {
      return
    }

    const message =
      error.errors && Object.keys(error.errors).length > 0
        ? Object.values(error.errors).flat().join(', ')
        : error.message

    toast.error(message || 'An error occurred')
  }

  /**
   * Get access token from storage
   */
  private getAccessToken(): string | null {
    return localStorage.getItem(APP_CONFIG.STORAGE_KEYS.ACCESS_TOKEN)
  }

  /**
   * Get refresh token from storage
   */
  private getRefreshToken(): string | null {
    return localStorage.getItem(APP_CONFIG.STORAGE_KEYS.REFRESH_TOKEN)
  }

  /**
   * Set access token to storage
   */
  private setAccessToken(token: string): void {
    localStorage.setItem(APP_CONFIG.STORAGE_KEYS.ACCESS_TOKEN, token)
  }

  /**
   * Logout user
   */
  private logout(): void {
    localStorage.removeItem(APP_CONFIG.STORAGE_KEYS.ACCESS_TOKEN)
    localStorage.removeItem(APP_CONFIG.STORAGE_KEYS.REFRESH_TOKEN)
    localStorage.removeItem(APP_CONFIG.STORAGE_KEYS.USER)
    window.location.href = '/login'
  }

  /**
   * Generate unique request ID
   */
  private generateRequestId(): string {
    return `${Date.now()}-${Math.random().toString(36).substr(2, 9)}`
  }

  /**
   * GET request
   */
  async get<T = unknown>(
    url: string,
    config?: AxiosRequestConfig & ApiRequestConfig,
  ): Promise<ApiResponse<T>> {
    const response = await this.instance.get<ApiResponse<T>>(url, config)
    return response.data
  }

  /**
   * POST request
   */
  async post<T = unknown, D = unknown>(
    url: string,
    data?: D,
    config?: AxiosRequestConfig & ApiRequestConfig,
  ): Promise<ApiResponse<T>> {
    const response = await this.instance.post<ApiResponse<T>>(
      url,
      data,
      config,
    )
    return response.data
  }

  /**
   * PUT request
   */
  async put<T = unknown, D = unknown>(
    url: string,
    data?: D,
    config?: AxiosRequestConfig & ApiRequestConfig,
  ): Promise<ApiResponse<T>> {
    const response = await this.instance.put<ApiResponse<T>>(
      url,
      data,
      config,
    )
    return response.data
  }

  /**
   * PATCH request
   */
  async patch<T = unknown, D = unknown>(
    url: string,
    data?: D,
    config?: AxiosRequestConfig & ApiRequestConfig,
  ): Promise<ApiResponse<T>> {
    const response = await this.instance.patch<ApiResponse<T>>(
      url,
      data,
      config,
    )
    return response.data
  }

  /**
   * DELETE request
   */
  async delete<T = unknown>(
    url: string,
    config?: AxiosRequestConfig & ApiRequestConfig,
  ): Promise<ApiResponse<T>> {
    const response = await this.instance.delete<ApiResponse<T>>(url, config)
    return response.data
  }

  /**
   * Get axios instance (for advanced usage)
   */
  getInstance(): AxiosInstance {
    return this.instance
  }
}

// Export singleton instance
export const apiClient = new ApiClient()

// Export convenience methods
export const api = {
  get: <T = unknown>(
    url: string,
    config?: AxiosRequestConfig & ApiRequestConfig,
  ) => apiClient.get<T>(url, config),
  post: <T = unknown, D = unknown>(
    url: string,
    data?: D,
    config?: AxiosRequestConfig & ApiRequestConfig,
  ) => apiClient.post<T, D>(url, data, config),
  put: <T = unknown, D = unknown>(
    url: string,
    data?: D,
    config?: AxiosRequestConfig & ApiRequestConfig,
  ) => apiClient.put<T, D>(url, data, config),
  patch: <T = unknown, D = unknown>(
    url: string,
    data?: D,
    config?: AxiosRequestConfig & ApiRequestConfig,
  ) => apiClient.patch<T, D>(url, data, config),
  delete: <T = unknown>(
    url: string,
    config?: AxiosRequestConfig & ApiRequestConfig,
  ) => apiClient.delete<T>(url, config),
}
