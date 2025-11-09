import type { AxiosInstance, InternalAxiosRequestConfig } from 'axios'
import { useAuthStore } from '@/store/slices/auth.slice'

let isRefreshing = false
let failedQueue: Array<{
  resolve: (token: string) => void
  reject: (error: any) => void
}> = []

const processQueue = (error: any, token: string | null = null) => {
  failedQueue.forEach((prom) => {
    if (error) {
      prom.reject(error)
    } else if (token) {
      prom.resolve(token)
    }
  })

  failedQueue = []
}

export const authInterceptor = (instance: AxiosInstance): void => {
  // Request interceptor - Add access token to headers
  instance.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
      const { accessToken } = useAuthStore.getState()

      if (accessToken && config.headers) {
        config.headers.Authorization = `Bearer ${accessToken}`
      }

      return config
    },
    (error) => Promise.reject(error)
  )

  // Response interceptor - Handle token refresh on 401
  instance.interceptors.response.use(
    (response) => response,
    async (error) => {
      const originalRequest = error.config

      // If error is not 401 or request already retried, reject
      if (error.response?.status !== 401 || originalRequest._retry) {
        return Promise.reject(error)
      }

      // Don't retry refresh endpoint
      if (originalRequest.url?.includes('/auth/refresh')) {
        useAuthStore.getState().clearAuth()
        return Promise.reject(error)
      }

      if (isRefreshing) {
        // Queue the request while refresh is in progress
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject })
        })
          .then((token) => {
            originalRequest.headers.Authorization = `Bearer ${token}`
            return instance(originalRequest)
          })
          .catch((err) => Promise.reject(err))
      }

      originalRequest._retry = true
      isRefreshing = true

      try {
        // Attempt to refresh token
        await useAuthStore.getState().refreshToken()
        const { accessToken } = useAuthStore.getState()

        if (!accessToken) {
          throw new Error('No access token after refresh')
        }

        // Process queued requests with new token
        processQueue(null, accessToken)

        // Retry original request with new token
        originalRequest.headers.Authorization = `Bearer ${accessToken}`
        return instance(originalRequest)
      } catch (refreshError) {
        // Refresh failed - clear auth and reject all queued requests
        processQueue(refreshError, null)
        useAuthStore.getState().clearAuth()

        // Redirect to login page
        if (typeof globalThis.window !== 'undefined') {
          globalThis.location.href = '/login'
        }

        return Promise.reject(refreshError)
      } finally {
        isRefreshing = false
      }
    }
  )
}

export default authInterceptor
