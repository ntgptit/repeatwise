/**
 * Review Progress Component
 * 
 * Displays progress bar for review session
 * 
 * Features:
 * - Progress bar
 * - Card count display
 * - Percentage
 * - Accessible
 */

import * as React from 'react'
import { Progress } from '@/components/ui/progress'
import { cn } from '@/lib/utils'

export interface ReviewProgressProps {
  current: number
  total: number
  className?: string
  showCount?: boolean
  showPercentage?: boolean
}

export const ReviewProgress = React.memo<ReviewProgressProps>(
  ({ current, total, className, showCount = true, showPercentage = true }) => {
    const percentage = total > 0 ? Math.round((current / total) * 100) : 0

    return (
      <div className={cn('space-y-2', className)} role="progressbar" aria-valuenow={current} aria-valuemin={0} aria-valuemax={total} aria-label={`Review progress: ${current} of ${total} cards`}>
        <div className="flex items-center justify-between text-sm">
          {showCount && (
            <span className="text-muted-foreground">
              Card {current} of {total}
            </span>
          )}
          {showPercentage && (
            <span className="text-muted-foreground font-medium">
              {percentage}%
            </span>
          )}
        </div>
        <Progress value={percentage} className="h-2" />
      </div>
    )
  },
)

ReviewProgress.displayName = 'ReviewProgress'

