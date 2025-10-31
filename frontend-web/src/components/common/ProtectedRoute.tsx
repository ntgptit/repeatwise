/**
 * Protected Route Component
 * 
 * Route wrapper that requires authentication
 * 
 * Features:
 * - Authentication check
 * - Redirect to login if not authenticated
 * - Loading state
 * - Optional role-based access
 */

import * as React from 'react'
import { Navigate, useLocation } from 'react-router-dom'
import { LoadingSpinner } from './LoadingSpinner'

export interface ProtectedRouteProps {
  /** Child components */
  children: React.ReactNode
  /** Whether user is authenticated */
  isAuthenticated: boolean
  /** Whether authentication is being checked */
  isLoading?: boolean
  /** Login redirect path */
  loginPath?: string
  /** Required roles (optional) */
  requiredRoles?: string[]
  /** User roles */
  userRoles?: string[]
  /** Fallback component while loading */
  loadingFallback?: React.ReactNode
}

export const ProtectedRoute = React.memo<ProtectedRouteProps>(
  ({
    children,
    isAuthenticated,
    isLoading = false,
    loginPath = '/login',
    requiredRoles,
    userRoles = [],
    loadingFallback,
  }) => {
    const location = useLocation()

    // Show loading state
    if (isLoading) {
      return (
        loadingFallback || (
          <div className="flex items-center justify-center min-h-screen">
            <LoadingSpinner label="Checking authentication..." />
          </div>
        )
      )
    }

    // Redirect to login if not authenticated
    if (!isAuthenticated) {
      return (
        <Navigate
          to={loginPath}
          state={{ from: location }}
          replace
        />
      )
    }

    // Check role-based access
    if (requiredRoles && requiredRoles.length > 0) {
      const hasRequiredRole = requiredRoles.some((role) =>
        userRoles.includes(role),
      )

      if (!hasRequiredRole) {
        // Redirect to unauthorized page or home
        return <Navigate to="/" replace />
      }
    }

    return <>{children}</>
  },
)

ProtectedRoute.displayName = 'ProtectedRoute'

