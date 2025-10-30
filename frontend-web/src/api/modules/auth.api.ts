import { BaseApi } from '../http/base.api'
import { API_ENDPOINTS } from '@/constants/api'
import type { PaginatedResponse } from '@/api/types/pagination'

/**
 * Auth API Module
 * Follows consistent convention: BaseApi + TypeScript Generics
 */
export interface LoginRequest {
  email: string
  password: string
}

export interface LoginResponse {
  accessToken: string
  refreshToken: string
  user: {
    id: string
    email: string
    name: string
  }
}

export interface RegisterRequest {
  email: string
  password: string
  name: string
}

export interface RegisterResponse {
  id: string
}

export interface RefreshTokenRequest {
  refreshToken: string
}

export interface RefreshTokenResponse {
  accessToken: string
}

export interface UserProfile {
  id: string
  email: string
  name: string
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
   */
  async refreshToken(
    data: RefreshTokenRequest,
  ): Promise<RefreshTokenResponse> {
    return this.customPost<RefreshTokenResponse, RefreshTokenRequest>(
      '/refresh',
      data,
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