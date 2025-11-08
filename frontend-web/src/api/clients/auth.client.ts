import type { AxiosInstance } from 'axios'
import { apiClient } from './base.client'
import { API_ENDPOINTS } from '@/config/app.config'

const { AUTH, USER } = API_ENDPOINTS

// ==================== Types ====================

export enum Language {
  VI = 'VI',
  EN = 'EN',
}

export enum Theme {
  LIGHT = 'LIGHT',
  DARK = 'DARK',
  SYSTEM = 'SYSTEM',
}

// Request DTOs
export interface RegisterRequest {
  email: string
  username?: string
  password: string
  confirmPassword: string
  name?: string
}

export interface LoginRequest {
  identifier: string // username or email
  password: string
}

export interface RefreshTokenRequest {
  // Refresh token is sent via HTTP-only cookie automatically
}

export interface UpdateProfileRequest {
  name?: string
  username?: string
  timezone?: string
  language?: Language
  theme?: Theme
}

export interface ChangePasswordRequest {
  currentPassword: string
  newPassword: string
  confirmNewPassword: string
}

// Response DTOs
export interface UserResponse {
  id: string
  email: string
  username?: string
  name?: string
  timezone?: string
  language: Language
  theme: Theme
  createdAt: string
  updatedAt: string
}

export interface AuthResponse {
  accessToken: string
  expiresIn: number // seconds (900 for 15 minutes)
  user: UserResponse
}

export interface RegisterResponse {
  message: string
  userId: string
}

export interface LogoutResponse {
  message: string
}

export interface RefreshTokenResponse {
  access_token: string
  expires_in: number
}

export interface UpdateProfileResponse {
  message: string
  user: UserResponse
}

export interface ChangePasswordResponse {
  message: string
}

// ==================== Auth Client ====================

export class AuthClient {
  private readonly http: AxiosInstance

  constructor(http: AxiosInstance = apiClient) {
    this.http = http
  }

  /**
   * UC-001: User Registration
   * Register a new user account
   */
  async register(payload: RegisterRequest): Promise<RegisterResponse> {
    const response = await this.http.post<RegisterResponse>(AUTH.REGISTER, payload)
    return response.data
  }

  /**
   * UC-002: User Login
   * Login with username/email and password
   * Returns access token and sets refresh token in HTTP-only cookie
   */
  async login(payload: LoginRequest): Promise<AuthResponse> {
    const response = await this.http.post<AuthResponse>(AUTH.LOGIN, payload)
    return response.data
  }

  /**
   * UC-003: Refresh Access Token
   * Refresh access token using refresh token from HTTP-only cookie
   */
  async refreshToken(): Promise<RefreshTokenResponse> {
    const response = await this.http.post<RefreshTokenResponse>(AUTH.REFRESH_TOKEN)
    return response.data
  }

  /**
   * UC-004: User Logout
   * Logout and revoke all refresh tokens
   */
  async logout(): Promise<LogoutResponse> {
    const response = await this.http.post<LogoutResponse>(AUTH.LOGOUT)
    return response.data
  }

  /**
   * UC-005: Update User Profile
   * Update name, timezone, language, and theme
   */
  async updateProfile(payload: UpdateProfileRequest): Promise<UpdateProfileResponse> {
    const response = await this.http.patch<UpdateProfileResponse>(USER.UPDATE_PROFILE, payload)
    return response.data
  }

  /**
   * UC-006: Change Password
   * Change password and logout from all devices
   */
  async changePassword(payload: ChangePasswordRequest): Promise<ChangePasswordResponse> {
    const response = await this.http.post<ChangePasswordResponse>(USER.CHANGE_PASSWORD, payload)
    return response.data
  }

  /**
   * Get current user profile
   */
  async getCurrentUser(): Promise<UserResponse> {
    const response = await this.http.get<{ user: UserResponse }>(USER.PROFILE)
    return response.data.user
  }
}

export const authClient = new AuthClient()

export default authClient
