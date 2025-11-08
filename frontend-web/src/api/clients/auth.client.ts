import type { AxiosInstance } from 'axios'
import { apiClient } from './base.client'
import { API_ENDPOINTS } from '@/config/app.config'
import { isApiResponse, type ApiResponse } from '@/api/types'

const { AUTH, USER } = API_ENDPOINTS

export interface AuthTokens {
  accessToken: string
  refreshToken: string
  expiresIn?: number
  tokenType?: string
}

export interface AuthUser {
  id: string
  email: string
  fullName: string
  avatarUrl?: string
  roles?: string[]
  createdAt?: string
  updatedAt?: string
}

export interface AuthSession {
  user: AuthUser
  tokens: AuthTokens
}

export interface LoginRequest {
  email: string
  password: string
  rememberMe?: boolean
}

export type LoginResponse = AuthSession

export interface RegisterRequest {
  fullName: string
  email: string
  password: string
  confirmPassword?: string
}

export type RegisterResponse = AuthSession

export interface RefreshTokenRequest {
  refreshToken: string
}

export type RefreshTokenResponse = AuthTokens

export interface ForgotPasswordRequest {
  email: string
}

export interface ResetPasswordRequest {
  token: string
  password: string
  confirmPassword?: string
}

export interface VerifyEmailRequest {
  token: string
}

export interface BaseMessageResponse {
  message: string
}

export class AuthClient {
  private readonly http: AxiosInstance

  constructor(http: AxiosInstance = apiClient) {
    this.http = http
  }

  private unwrap<T>(payload: ApiResponse<T> | T): T {
    if (isApiResponse<T>(payload)) {
      if (!payload.success) {
        throw new Error(payload.message ?? 'Request failed')
      }
      return payload.data
    }

    return payload
  }

  async login(payload: LoginRequest): Promise<LoginResponse> {
    const response = await this.http.post<ApiResponse<LoginResponse>>(AUTH.LOGIN, payload)
    return this.unwrap<LoginResponse>(response.data)
  }

  async register(payload: RegisterRequest): Promise<RegisterResponse> {
    const response = await this.http.post<ApiResponse<RegisterResponse>>(AUTH.REGISTER, payload)
    return this.unwrap<RegisterResponse>(response.data)
  }

  async logout(): Promise<void> {
    await this.http.post(AUTH.LOGOUT)
  }

  async refreshToken(payload: RefreshTokenRequest): Promise<RefreshTokenResponse> {
    const response = await this.http.post<ApiResponse<RefreshTokenResponse>>(
      AUTH.REFRESH_TOKEN,
      payload
    )
    return this.unwrap<RefreshTokenResponse>(response.data)
  }

  async forgotPassword(payload: ForgotPasswordRequest): Promise<BaseMessageResponse> {
    const response = await this.http.post<ApiResponse<BaseMessageResponse>>(
      AUTH.FORGOT_PASSWORD,
      payload
    )
    return this.unwrap<BaseMessageResponse>(response.data)
  }

  async resetPassword(payload: ResetPasswordRequest): Promise<BaseMessageResponse> {
    const response = await this.http.post<ApiResponse<BaseMessageResponse>>(
      AUTH.RESET_PASSWORD,
      payload
    )
    return this.unwrap<BaseMessageResponse>(response.data)
  }

  async verifyEmail(payload: VerifyEmailRequest): Promise<BaseMessageResponse> {
    const response = await this.http.post<ApiResponse<BaseMessageResponse>>(
      AUTH.VERIFY_EMAIL,
      payload
    )
    return this.unwrap<BaseMessageResponse>(response.data)
  }

  async getCurrentUser(): Promise<AuthUser> {
    const response = await this.http.get<ApiResponse<AuthUser>>(USER.PROFILE)
    return this.unwrap<AuthUser>(response.data)
  }
}

export const authClient = new AuthClient()

export default authClient
