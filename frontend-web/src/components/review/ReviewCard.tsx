/**
 * Review Card Component
 * 
 * Displays a card during review session with flip animation
 * 
 * Features:
 * - Front/back flip
 * - Shows answer button
 * - Keyboard shortcuts (Enter to show answer)
 * - Timer tracking
 */

import * as React from 'react'
import { Card, CardContent } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Eye } from 'lucide-react'
import { cn } from '@/lib/utils'

export interface ReviewCardProps {
  front: string
  back: string
  isRevealed?: boolean
  onReveal?: () => void
  className?: string
  showFlipButton?: boolean
}

export const ReviewCard = React.memo<ReviewCardProps>(
  ({
    front,
    back,
    isRevealed = false,
    onReveal,
    className,
    showFlipButton = true,
  }) => {
    React.useEffect(() => {
      if (isRevealed) return

      const handleKeyDown = (e: KeyboardEvent) => {
        const target = e.target as HTMLElement
        if (target.tagName === 'INPUT' || target.tagName === 'TEXTAREA') {
          return
        }

        if (e.key === 'Enter' && !isRevealed) {
          e.preventDefault()
          onReveal?.()
        }
      }

      window.addEventListener('keydown', handleKeyDown)
      return () => window.removeEventListener('keydown', handleKeyDown)
    }, [isRevealed, onReveal])

    return (
      <Card className={cn('w-full min-h-64', className)}>
        <CardContent className="flex flex-col items-center justify-center p-6 min-h-64">
          {!isRevealed ? (
            <div className="text-center space-y-4 w-full">
              <p className="text-lg font-medium whitespace-pre-wrap break-words">
                {front}
              </p>
              {showFlipButton && onReveal && (
                <Button
                  onClick={onReveal}
                  variant="outline"
                  size="lg"
                  className="mt-4"
                  aria-label="Show answer"
                >
                  <Eye className="h-4 w-4 mr-2" />
                  Show Answer
                  <span className="ml-2 text-xs opacity-70" aria-hidden="true">
                    (Enter)
                  </span>
                </Button>
              )}
            </div>
          ) : (
            <div className="text-center space-y-4 w-full">
              <div className="mb-4">
                <p className="text-sm text-muted-foreground mb-2">Question:</p>
                <p className="text-base text-muted-foreground whitespace-pre-wrap break-words">
                  {front}
                </p>
              </div>
              <div className="border-t pt-4">
                <p className="text-sm text-muted-foreground mb-2">Answer:</p>
                <p className="text-lg font-medium whitespace-pre-wrap break-words">
                  {back}
                </p>
              </div>
            </div>
          )}
        </CardContent>
      </Card>
    )
  },
)

ReviewCard.displayName = 'ReviewCard'

