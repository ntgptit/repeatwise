/**
 * Loading Spinner Component
 * 
 * Displays a loading spinner
 * 
 * Features:
 * - Accessible
 * - Customizable size
 * - Optional label
 */

import * as React from 'react'
import { Loader2 } from 'lucide-react'
import { cn } from '@/lib/utils'

export interface LoadingSpinnerProps {
  size?: 'sm' | 'md' | 'lg'
  label?: string
  className?: string
}

const sizeClasses = {
  sm: 'h-4 w-4',
  md: 'h-8 w-8',
  lg: 'h-12 w-12',
}

export const LoadingSpinner = React.memo<LoadingSpinnerProps>(
  ({ size = 'md', label, className }) => {
    return (
      <div
        className={cn('flex flex-col items-center justify-center gap-2', className)}
        role="status"
        aria-live="polite"
        aria-label={label || 'Loading'}
      >
        <Loader2
          className={cn('animate-spin text-muted-foreground', sizeClasses[size])}
          aria-hidden="true"
        />
        {label && (
          <p className="text-sm text-muted-foreground" aria-hidden="true">
            {label}
          </p>
        )}
      </div>
    )
  },
)

LoadingSpinner.displayName = 'LoadingSpinner'

