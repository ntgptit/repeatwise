import { APP_CONFIG } from '@/constants/config'

/**
 * Token Management Utilities
 */
export const tokenManager = {
  /**
   * Get access token from storage
   */
  getAccessToken(): string | null {
    return localStorage.getItem(APP_CONFIG.STORAGE_KEYS.ACCESS_TOKEN)
  },

  /**
   * Get refresh token from storage
   */
  getRefreshToken(): string | null {
    return localStorage.getItem(APP_CONFIG.STORAGE_KEYS.REFRESH_TOKEN)
  },

  /**
   * Set access token to storage
   */
  setAccessToken(token: string): void {
    localStorage.setItem(APP_CONFIG.STORAGE_KEYS.ACCESS_TOKEN, token)
  },

  /**
   * Set refresh token to storage
   */
  setRefreshToken(token: string): void {
    localStorage.setItem(APP_CONFIG.STORAGE_KEYS.REFRESH_TOKEN, token)
  },

  /**
   * Set both tokens
   */
  setTokens(accessToken: string, refreshToken: string): void {
    this.setAccessToken(accessToken)
    this.setRefreshToken(refreshToken)
  },

  /**
   * Remove all tokens
   */
  clearTokens(): void {
    localStorage.removeItem(APP_CONFIG.STORAGE_KEYS.ACCESS_TOKEN)
    localStorage.removeItem(APP_CONFIG.STORAGE_KEYS.REFRESH_TOKEN)
  },

  /**
   * Check if user is authenticated
   */
  isAuthenticated(): boolean {
    return Boolean(this.getAccessToken())
  },
}
