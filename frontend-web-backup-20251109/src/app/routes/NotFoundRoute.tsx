import { Navigate } from 'react-router-dom'
import type { JSX } from 'react'

import { APP_ROUTES } from '@/config/app.config'

export const NotFoundRoute = (): JSX.Element => {
  return <Navigate to={APP_ROUTES.HOME} replace />
}

export default NotFoundRoute

