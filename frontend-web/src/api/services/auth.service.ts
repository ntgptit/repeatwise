/**
 * Authentication API Service
 * Implements UC-001 to UC-006
 */

import { apiClient } from '../clients/base.client'
import type {
  RegisterRequest,
  RegisterResponse,
  LoginRequest,
  LoginResponse,
  RefreshTokenResponse,
  LogoutResponse,
  UpdateProfileRequest,
  UpdateProfileResponse,
  ChangePasswordRequest,
  ChangePasswordResponse,
  User,
} from '../types/auth.types'

/**
 * Auth API Service
 */
export const authService = {
  /**
   * UC-001: User Registration
   * @throws {AuthError} if registration fails
   */
  register: async (data: RegisterRequest): Promise<RegisterResponse> => {
    const response = await apiClient.post<RegisterResponse>('/v1/auth/register', {
      email: data.email,
      username: data.username || undefined, // Send undefined instead of empty string
      password: data.password,
      confirmPassword: data.confirmPassword,
      name: data.name || undefined,
    })
    return response.data
  },

  /**
   * UC-002: User Login
   * Sets refresh_token in HTTP-only cookie automatically
   * @throws {AuthError} if login fails
   */
  login: async (data: LoginRequest): Promise<LoginResponse> => {
    const response = await apiClient.post<LoginResponse>('/v1/auth/login', {
      identifier: data.usernameOrEmail, // Backend expects 'identifier'
      password: data.password,
    })
    return response.data
  },

  /**
   * UC-003: Refresh Access Token
   * Uses refresh_token from HTTP-only cookie
   * @throws {AuthError} if refresh fails
   */
  refreshToken: async (): Promise<RefreshTokenResponse> => {
    const response = await apiClient.post<RefreshTokenResponse>('/v1/auth/refresh')
    return response.data
  },

  /**
   * UC-004: User Logout
   * Revokes current refresh token on server
   * @throws {AuthError} if logout fails (but client should logout anyway)
   */
  logout: async (): Promise<LogoutResponse> => {
    const response = await apiClient.post<LogoutResponse>('/v1/auth/logout')
    return response.data
  },

  /**
   * UC-004: Logout All Devices
   * Revokes all refresh tokens for user
   */
  logoutAll: async (): Promise<LogoutResponse> => {
    const response = await apiClient.post<LogoutResponse>('/v1/auth/logout-all')
    return response.data
  },

  /**
   * Get Current User Profile
   * @throws {AuthError} if not authenticated
   */
  getMe: async (): Promise<User> => {
    const response = await apiClient.get<User>('/v1/users/me')
    return response.data
  },

  /**
   * UC-005: Update User Profile
   * @throws {AuthError} if update fails
   */
  updateProfile: async (data: UpdateProfileRequest): Promise<UpdateProfileResponse> => {
    const response = await apiClient.patch<UpdateProfileResponse>('/v1/users/profile', data)
    return response.data
  },

  /**
   * UC-006: Change Password
   * Revokes all refresh tokens and requires re-login
   * @throws {AuthError} if password change fails
   */
  changePassword: async (data: ChangePasswordRequest): Promise<ChangePasswordResponse> => {
    const response = await apiClient.post<ChangePasswordResponse>(
      '/v1/users/change-password',
      {
        currentPassword: data.currentPassword,
        newPassword: data.newPassword,
      }
    )
    return response.data
  },
}

export default authService
