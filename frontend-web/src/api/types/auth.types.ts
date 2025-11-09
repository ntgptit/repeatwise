/**
 * Authentication API Types
 * Based on UC-001 to UC-006 specifications
 */

// ==================== Request Types ====================

/**
 * UC-001: User Registration Request
 */
export interface RegisterRequest {
  email: string // Required, unique, valid email format
  username?: string // Optional, 3-30 chars, alphanumeric + underscore/hyphen, unique if provided
  password: string // Required, min 8 characters
  confirmPassword: string // Required, must match password
  name?: string // Optional, max 100 characters
}

/**
 * UC-002: User Login Request
 */
export interface LoginRequest {
  usernameOrEmail: string // Username or email
  password: string
}

/**
 * UC-005: Update User Profile Request
 */
export interface UpdateProfileRequest {
  name?: string // Optional, 1-100 chars
  username?: string // Optional, 3-30 chars, unique if set
  timezone?: string // Optional, valid IANA timezone
  language?: 'VI' | 'EN' // Optional
  theme?: 'LIGHT' | 'DARK' | 'SYSTEM' // Optional
}

/**
 * UC-006: Change Password Request
 */
export interface ChangePasswordRequest {
  currentPassword: string
  newPassword: string
  confirmNewPassword: string
}

// ==================== Response Types ====================

/**
 * User data returned from API
 */
export interface User {
  id: string
  email: string
  username?: string | null
  name?: string | null
  timezone: string
  language: 'VI' | 'EN'
  theme: 'LIGHT' | 'DARK' | 'SYSTEM'
  createdAt: string
  updatedAt: string
}

/**
 * UC-002: Login Response
 */
export interface LoginResponse {
  accessToken: string
  expiresIn: number // seconds
  user: User
  // refresh_token is set in HTTP-only cookie, not in response body
}

/**
 * UC-001: Registration Response
 */
export interface RegisterResponse {
  message: string
  userId?: string
}

/**
 * UC-003: Refresh Token Response
 */
export interface RefreshTokenResponse {
  accessToken: string
  expiresIn: number // seconds
  // new refresh_token is set in HTTP-only cookie
}

/**
 * UC-004: Logout Response
 */
export interface LogoutResponse {
  message: string
}

/**
 * UC-005: Update Profile Response
 */
export interface UpdateProfileResponse {
  message: string
  user: User
}

/**
 * UC-006: Change Password Response
 */
export interface ChangePasswordResponse {
  message: string
}

// ==================== Error Types ====================

export interface ValidationError {
  field: string
  message: string
}

export interface AuthError {
  error: string
  message: string
  details?: ValidationError[]
}
