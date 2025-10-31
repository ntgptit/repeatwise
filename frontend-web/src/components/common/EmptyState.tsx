/**
 * Empty State Component
 * 
 * Displays an empty state message when there's no data
 * 
 * Features:
 * - Customizable message and description
 * - Optional action button
 * - Accessible
 */

import * as React from 'react'
import { Button } from '@/components/ui/button'
import { cn } from '@/lib/utils'

export interface EmptyStateProps {
  message?: string
  description?: string
  actionLabel?: string
  onAction?: () => void
  icon?: React.ReactNode
  className?: string
}

export const EmptyState = React.memo<EmptyStateProps>(
  ({
    message = 'No data available',
    description,
    actionLabel,
    onAction,
    icon,
    className,
  }) => {
    return (
      <div
        className={cn(
          'flex flex-col items-center justify-center py-12 px-4 text-center',
          className,
        )}
        role="status"
        aria-live="polite"
      >
        {icon && (
          <div className="mb-4 text-muted-foreground" aria-hidden="true">
            {icon}
          </div>
        )}
        <h3 className="text-lg font-semibold mb-2">{message}</h3>
        {description && (
          <p className="text-sm text-muted-foreground max-w-sm mb-4">
            {description}
          </p>
        )}
        {actionLabel && onAction && (
          <Button onClick={onAction} variant="outline" aria-label={actionLabel}>
            {actionLabel}
          </Button>
        )}
      </div>
    )
  },
)

EmptyState.displayName = 'EmptyState'

