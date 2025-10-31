/**
 * Review Summary Component
 * 
 * Displays summary after completing a review session
 * 
 * Features:
 * - Statistics display
 * - Rating breakdown
 * - Time spent
 * - Accessible
 */

import * as React from 'react'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { CheckCircle2 } from 'lucide-react'
import { cn } from '@/lib/utils'
import type { CardRating } from './RatingButtons'

export interface ReviewSummaryProps {
  totalCards: number
  ratings?: Record<CardRating, number>
  timeSpentMs?: number
  onRestart?: () => void
  onFinish?: () => void
  className?: string
}

export const ReviewSummary = React.memo<ReviewSummaryProps>(
  ({
    totalCards,
    ratings,
    timeSpentMs,
    onRestart,
    onFinish,
    className,
  }) => {
    const formatTime = (ms: number) => {
      const seconds = Math.floor(ms / 1000)
      const minutes = Math.floor(seconds / 60)
      const remainingSeconds = seconds % 60

      if (minutes > 0) {
        return `${minutes}m ${remainingSeconds}s`
      }
      return `${seconds}s`
    }

    const ratingLabels: Record<CardRating, string> = {
      AGAIN: 'Again',
      HARD: 'Hard',
      GOOD: 'Good',
      EASY: 'Easy',
    }

    const totalRatings = ratings
      ? Object.values(ratings).reduce((sum, count) => sum + count, 0)
      : 0

    return (
      <Card className={cn('w-full max-w-2xl mx-auto', className)}>
        <CardHeader>
          <div className="flex items-center gap-2">
            <CheckCircle2 className="h-6 w-6 text-green-500" />
            <CardTitle>Review Complete!</CardTitle>
          </div>
        </CardHeader>
        <CardContent className="space-y-6">
          {/* Statistics */}
          <div className="grid grid-cols-2 gap-4">
            <div>
              <p className="text-sm text-muted-foreground">Cards Reviewed</p>
              <p className="text-2xl font-semibold">{totalCards}</p>
            </div>
            {timeSpentMs !== undefined && (
              <div>
                <p className="text-sm text-muted-foreground">Time Spent</p>
                <p className="text-2xl font-semibold">
                  {formatTime(timeSpentMs)}
                </p>
              </div>
            )}
          </div>

          {/* Rating Breakdown */}
          {ratings && totalRatings > 0 && (
            <div>
              <h3 className="text-sm font-semibold mb-3">Rating Breakdown</h3>
              <div className="grid grid-cols-2 md:grid-cols-4 gap-2">
                {(Object.keys(ratings) as CardRating[]).map((rating) => {
                  const count = ratings[rating]
                  const percentage =
                    totalRatings > 0
                      ? Math.round((count / totalRatings) * 100)
                      : 0

                  return (
                    <div
                      key={rating}
                      className="flex flex-col items-center p-3 border rounded-md"
                    >
                      <Badge variant="outline" className="mb-2">
                        {ratingLabels[rating]}
                      </Badge>
                      <p className="text-2xl font-semibold">{count}</p>
                      <p className="text-xs text-muted-foreground">
                        {percentage}%
                      </p>
                    </div>
                  )
                })}
              </div>
            </div>
          )}

          {/* Actions */}
          <div className="flex justify-end gap-2 pt-4 border-t">
            {onRestart && (
              <Button variant="outline" onClick={onRestart}>
                Review Again
              </Button>
            )}
            {onFinish && (
              <Button onClick={onFinish}>Finish</Button>
            )}
          </div>
        </CardContent>
      </Card>
    )
  },
)

ReviewSummary.displayName = 'ReviewSummary'

