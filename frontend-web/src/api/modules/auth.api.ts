import { BaseApi } from '../http/base.api'
import { API_ENDPOINTS } from '@/constants/api'
import type { PaginatedResponse } from '@/api/types/pagination'

/**
 * Auth API Module
 * Follows consistent convention: BaseApi + TypeScript Generics
 */
export interface LoginRequest {
  usernameOrEmail: string
  password: string
}

export interface LoginResponse {
  access_token: string
  expires_in: number
  user: {
    id: string
    email: string
    username?: string
    name?: string
    language: string
    theme: string
    timezone: string
  }
}

export interface RegisterRequest {
  email: string
  username?: string
  password: string
  confirmPassword: string
  name?: string
}

export interface RegisterResponse {
  id: string
}

export interface RefreshTokenRequest {
  // Refresh token is sent via HTTP-only cookie, no need for body
}

export interface RefreshTokenResponse {
  access_token: string
  expires_in: number
}

export interface UserProfile {
  id: string
  email: string
  username?: string
  name?: string
  language: string
  theme: string
  timezone: string
  createdAt: string
  updatedAt?: string
}

class AuthApi extends BaseApi {
  constructor() {
    super(API_ENDPOINTS.AUTH.LOGIN.replace('/login', ''))
  }

  /**
   * Login user
   */
  async login(data: LoginRequest): Promise<LoginResponse> {
    return this.customPost<LoginResponse, LoginRequest>(
      '/login',
      data,
      { skipAuth: true },
    )
  }

  /**
   * Register new user
   */
  async register(data: RegisterRequest): Promise<RegisterResponse> {
    return this.customPost<RegisterResponse, RegisterRequest>(
      '/register',
      data,
      { skipAuth: true },
    )
  }

  /**
   * Logout user
   */
  async logout(): Promise<void> {
    await this.customPost('/logout')
  }

  /**
   * Refresh access token
   * Refresh token is automatically sent via HTTP-only cookie
   */
  async refreshToken(): Promise<RefreshTokenResponse> {
    return this.customPost<RefreshTokenResponse, RefreshTokenRequest>(
      '/refresh',
      {},
      { skipAuth: true },
    )
  }

  /**
   * Get current user profile
   */
  async getProfile(): Promise<UserProfile> {
    return this.customGet<UserProfile>('/profile')
  }
}

// Export singleton instance
export const authApi = new AuthApi()