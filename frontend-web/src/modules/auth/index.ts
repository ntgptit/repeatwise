/**
 * Auth Module
 * 
 * Authentication and authorization UI
 * 
 * Components:
 * - RegisterForm: User registration form
 * - LoginForm: Login form (username/email + password)
 * - ProtectedRoute: Route wrapper for authenticated routes
 * - AuthContext: Authentication state management
 * 
 * Features:
 * - Form validation (email format, password strength, username format)
 * - Auto token refresh interceptor
 * - Token storage (access token in memory, refresh token in HTTP-only cookie)
 * - Redirect handling after login/registration
 */

export * from './components'
export * from './hooks'
export * from './types'
