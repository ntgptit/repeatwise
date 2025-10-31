/**
 * App Component
 * 
 * Main application component with routing
 */

import * as React from 'react'
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { QueryClientProvider } from '@tanstack/react-query'
import { ReactQueryDevtools } from '@tanstack/react-query-devtools'
import { Toaster } from 'sonner'
import { ThemeProvider } from 'next-themes'

import { queryClient } from '@/lib/queryClient'
import { ErrorBoundary } from '@/components/common/ErrorBoundary'
import { ProtectedRoute } from '@/components/common/ProtectedRoute'
import { LoadingSpinner } from '@/components/common/LoadingSpinner'
import { useAuth } from '@/hooks/domain/useAuth'

// Pages
import { HomePage } from '@/pages/Home/HomePage'
import { LoginPage } from '@/pages/Auth/LoginPage'
import { RegisterPage } from '@/pages/Auth/RegisterPage'
import { DashboardPage } from '@/pages/Dashboard/DashboardPage'
import { DeckListPage } from '@/pages/Deck/DeckListPage'
import { DeckDetailPage } from '@/pages/Deck/DeckDetailPage'
import { FolderListPage } from '@/pages/Folder/FolderListPage'
import { FolderDetailPage } from '@/pages/Folder/FolderDetailPage'
import { ReviewSessionPage } from '@/pages/Review/ReviewSessionPage'
import { StatsPage } from '@/pages/Stats/StatsPage'
import { SettingsPage } from '@/pages/Settings/SettingsPage'

import { ROUTES } from '@/constants/routes'

function AppRoutes() {
  const { isAuthenticated, isLoading } = useAuth()

  // Determine what to render for the home route
  const renderHomeRoute = () => {
    if (isLoading) {
      return (
        <div className="flex items-center justify-center min-h-screen">
          <LoadingSpinner label="Loading..." />
        </div>
      )
    }
    
    if (isAuthenticated) {
      return <Navigate to={ROUTES.DASHBOARD} replace />
    }
    
    return <Navigate to={ROUTES.LOGIN} replace />
  }

  return (
    <Routes>
      {/* Public Routes */}
      <Route
        path={ROUTES.HOME}
        element={renderHomeRoute()}
      />
      <Route path={ROUTES.LOGIN} element={<LoginPage />} />
      <Route path={ROUTES.REGISTER} element={<RegisterPage />} />

      {/* Protected Routes */}
      <Route
        path={ROUTES.DASHBOARD}
        element={
          <ProtectedRoute
            isAuthenticated={isAuthenticated}
            isLoading={isLoading}
          >
            <DashboardPage />
          </ProtectedRoute>
        }
      />
      <Route
        path={ROUTES.DECKS}
        element={
          <ProtectedRoute
            isAuthenticated={isAuthenticated}
            isLoading={isLoading}
          >
            <DeckListPage />
          </ProtectedRoute>
        }
      />
      <Route
        path={`${ROUTES.DECKS}/:id`}
        element={
          <ProtectedRoute
            isAuthenticated={isAuthenticated}
            isLoading={isLoading}
          >
            <DeckDetailPage />
          </ProtectedRoute>
        }
      />
      <Route
        path={ROUTES.FOLDERS}
        element={
          <ProtectedRoute
            isAuthenticated={isAuthenticated}
            isLoading={isLoading}
          >
            <FolderListPage />
          </ProtectedRoute>
        }
      />
      <Route
        path={`${ROUTES.FOLDERS}/:id`}
        element={
          <ProtectedRoute
            isAuthenticated={isAuthenticated}
            isLoading={isLoading}
          >
            <FolderDetailPage />
          </ProtectedRoute>
        }
      />
      <Route
        path={ROUTES.REVIEW}
        element={
          <ProtectedRoute
            isAuthenticated={isAuthenticated}
            isLoading={isLoading}
          >
            <ReviewSessionPage />
          </ProtectedRoute>
        }
      />
      <Route
        path={ROUTES.STATS}
        element={
          <ProtectedRoute
            isAuthenticated={isAuthenticated}
            isLoading={isLoading}
          >
            <StatsPage />
          </ProtectedRoute>
        }
      />
      <Route
        path={ROUTES.SETTINGS}
        element={
          <ProtectedRoute
            isAuthenticated={isAuthenticated}
            isLoading={isLoading}
          >
            <SettingsPage />
          </ProtectedRoute>
        }
      />

      {/* Default redirect */}
      <Route path="*" element={<Navigate to={ROUTES.HOME} replace />} />
    </Routes>
  )
}

function App() {
  return (
    <ErrorBoundary>
      <QueryClientProvider client={queryClient}>
        <ThemeProvider attribute="class" defaultTheme="system" enableSystem>
          <BrowserRouter>
            <AppRoutes />
          </BrowserRouter>
          <Toaster position="top-right" />
        </ThemeProvider>
        <ReactQueryDevtools initialIsOpen={false} />
      </QueryClientProvider>
    </ErrorBoundary>
  )
}

export default App
