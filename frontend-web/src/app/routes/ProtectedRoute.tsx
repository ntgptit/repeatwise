import { Navigate } from 'react-router-dom'
import { useAuthStore } from '@/store/slices/auth.slice'
import { APP_ROUTES } from '@/config/app.config'

interface ProtectedRouteProps {
  children: React.ReactNode
}

/**
 * ProtectedRoute component
 * Redirects to login if user is not authenticated
 */
export default function ProtectedRoute({ children }: ProtectedRouteProps) {
  const { isAuthenticated } = useAuthStore()

  if (!isAuthenticated) {
    // Redirect to login page
    return <Navigate to={APP_ROUTES.LOGIN} replace />
  }

  return <>{children}</>
}
