import axios, { type AxiosError, type InternalAxiosRequestConfig } from 'axios'
import { toast } from 'sonner'
import { API_CONFIG, APP_CONFIG } from '@/constants'
import { HttpStatus, type ApiError, type ApiResponse } from '@/api/types/api-response'

/**
 * Error Interceptor
 * Handles error responses, token refresh, and error notifications
 */
export class ErrorInterceptor {
  private isRefreshing = false
  private failedQueue: Array<{
    resolve: (value?: unknown) => void
    reject: (error?: unknown) => void
  }> = []

  onRejected = async (error: AxiosError<ApiError>): Promise<never> => {
    const config = error.config as InternalAxiosRequestConfig & {
      skipErrorHandler?: boolean
      _retry?: boolean
    }

    // Skip error handler if specified
    if (config?.skipErrorHandler) {
      return Promise.reject(error)
    }

    // Handle 401 Unauthorized - Token expired
    if (
      error.response?.status === HttpStatus.UNAUTHORIZED &&
      !config?._retry
    ) {
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
            // Return axios instance from error to retry
            return axios.request(config)
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
      // Refresh token is sent via HTTP-only cookie automatically
      // Call refresh token endpoint (no body needed)
      const response = await axios.post<ApiResponse<{ access_token: string }>>(
        `${API_CONFIG.BASE_URL}/auth/refresh`,
        {},
        {
          withCredentials: true, // Include cookies
        },
      )

      const { access_token } = response.data.data
      this.setAccessToken(access_token)

      // Retry failed requests
      this.processQueue(null)

      // Retry original request
      if (config && config.headers) {
        config.headers.Authorization = `Bearer ${access_token}`
        return axios.request(config)
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
    localStorage.removeItem(APP_CONFIG.STORAGE_KEYS.USER)
    window.location.href = '/login'
  }
}

// Export singleton instance
export const errorInterceptor = new ErrorInterceptor()
