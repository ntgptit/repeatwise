/**
 * Error Boundary Component
 * 
 * Catches JavaScript errors in child components and displays fallback UI
 * 
 * Features:
 * - Error catching
 * - Fallback UI
 * - Error reporting (optional)
 * - Reset functionality
 */

import * as React from 'react'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { AlertTriangle, RefreshCw } from 'lucide-react'

export interface ErrorBoundaryProps {
  /** Child components */
  children: React.ReactNode
  /** Fallback component */
  fallback?: React.ComponentType<ErrorBoundaryState>
  /** Error handler callback */
  onError?: (error: Error, errorInfo: React.ErrorInfo) => void
  /** Show reset button */
  showReset?: boolean
  /** Reset handler */
  onReset?: () => void
}

export interface ErrorBoundaryState {
  /** Error object */
  error: Error | null
  /** Whether error boundary has caught an error */
  hasError: boolean
  /** Reset error boundary */
  reset: () => void
}

export class ErrorBoundary extends React.Component<
  ErrorBoundaryProps,
  ErrorBoundaryState
> {
  constructor(props: ErrorBoundaryProps) {
    super(props)
    this.state = {
      error: null,
      hasError: false,
      reset: () => this.reset(),
    }
  }

  static getDerivedStateFromError(error: Error): Partial<ErrorBoundaryState> {
    return {
      error,
      hasError: true,
    }
  }

  componentDidCatch(error: Error, errorInfo: React.ErrorInfo) {
    // Log error to error reporting service
    console.error('ErrorBoundary caught an error:', error, errorInfo)
    this.props.onError?.(error, errorInfo)
  }

  reset = () => {
    this.setState({
      error: null,
      hasError: false,
    })
    this.props.onReset?.()
  }

  render() {
    if (this.state.hasError && this.state.error) {
      if (this.props.fallback) {
        const Fallback = this.props.fallback
        return (
          <Fallback
            error={this.state.error}
            hasError={this.state.hasError}
            reset={this.reset}
          />
        )
      }

      return (
        <div className="flex items-center justify-center min-h-[400px] p-4">
          <Card className="w-full max-w-md">
            <CardHeader>
              <div className="flex items-center gap-2">
                <AlertTriangle className="h-5 w-5 text-destructive" />
                <CardTitle>Something went wrong</CardTitle>
              </div>
            </CardHeader>
            <CardContent className="space-y-4">
              <p className="text-sm text-muted-foreground">
                {this.state.error.message ||
                  'An unexpected error occurred. Please try again.'}
              </p>
              {this.props.showReset !== false && (
                <div className="flex gap-2">
                  <Button onClick={this.reset} variant="outline" className="flex-1">
                    <RefreshCw className="mr-2 h-4 w-4" />
                    Try Again
                  </Button>
                  <Button
                    onClick={() => window.location.reload()}
                    variant="default"
                    className="flex-1"
                  >
                    Reload Page
                  </Button>
                </div>
              )}
            </CardContent>
          </Card>
        </div>
      )
    }

    return this.props.children
  }
}

/**
 * Default error fallback component
 */
export function DefaultErrorFallback({
  error,
  reset,
}: ErrorBoundaryState) {
  return (
    <div className="flex items-center justify-center min-h-[400px] p-4">
      <Card className="w-full max-w-md">
        <CardHeader>
          <div className="flex items-center gap-2">
            <AlertTriangle className="h-5 w-5 text-destructive" />
            <CardTitle>Something went wrong</CardTitle>
          </div>
        </CardHeader>
        <CardContent className="space-y-4">
          <p className="text-sm text-muted-foreground">
            {error?.message || 'An unexpected error occurred. Please try again.'}
          </p>
          <div className="flex gap-2">
            <Button onClick={reset} variant="outline" className="flex-1">
              <RefreshCw className="mr-2 h-4 w-4" />
              Try Again
            </Button>
            <Button
              onClick={() => window.location.reload()}
              variant="default"
              className="flex-1"
            >
              Reload Page
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}

