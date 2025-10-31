/**
 * Rating Buttons Component
 * 
 * Displays SRS rating buttons (AGAIN, HARD, GOOD, EASY)
 * 
 * Features:
 * - Four rating options
 * - Keyboard shortcuts support
 * - Accessible
 * - Visual feedback
 */

import * as React from 'react'
import { Button } from '@/components/ui/button'
import { cn } from '@/lib/utils'

export type CardRating = 'AGAIN' | 'HARD' | 'GOOD' | 'EASY'

export interface RatingButtonsProps {
  onRate: (rating: CardRating) => void
  disabled?: boolean
  className?: string
  showShortcuts?: boolean
}

const ratingConfig: Record<CardRating, { label: string; shortcut: string; variant: 'destructive' | 'secondary' | 'default' | 'outline' }> = {
  AGAIN: {
    label: 'Again',
    shortcut: '1',
    variant: 'destructive',
  },
  HARD: {
    label: 'Hard',
    shortcut: '2',
    variant: 'secondary',
  },
  GOOD: {
    label: 'Good',
    shortcut: '3',
    variant: 'default',
  },
  EASY: {
    label: 'Easy',
    shortcut: '4',
    variant: 'outline',
  },
}

export const RatingButtons = React.memo<RatingButtonsProps>(
  ({ onRate, disabled = false, className, showShortcuts = true }) => {
    React.useEffect(() => {
      if (disabled) return

      const handleKeyDown = (e: KeyboardEvent) => {
        // Prevent default if focus is not on input/textarea
        const target = e.target as HTMLElement
        if (target.tagName === 'INPUT' || target.tagName === 'TEXTAREA') {
          return
        }

        switch (e.key) {
          case '1':
          case 'z':
          case 'Z':
            e.preventDefault()
            onRate('AGAIN')
            break
          case '2':
          case 'x':
          case 'X':
            e.preventDefault()
            onRate('HARD')
            break
          case '3':
          case ' ':
            e.preventDefault()
            onRate('GOOD')
            break
          case '4':
          case 'c':
          case 'C':
            e.preventDefault()
            onRate('EASY')
            break
        }
      }

      window.addEventListener('keydown', handleKeyDown)
      return () => window.removeEventListener('keydown', handleKeyDown)
    }, [onRate, disabled])

    return (
      <div
        className={cn(
          'flex flex-wrap items-center justify-center gap-2',
          className,
        )}
        role="group"
        aria-label="Card rating buttons"
      >
        {(Object.keys(ratingConfig) as CardRating[]).map((rating) => {
          const config = ratingConfig[rating]
          return (
            <Button
              key={rating}
              variant={config.variant}
              size="lg"
              onClick={() => onRate(rating)}
              disabled={disabled}
              className="min-w-[100px]"
              aria-label={`Rate as ${config.label}`}
            >
              {config.label}
              {showShortcuts && (
                <span className="ml-2 text-xs opacity-70" aria-hidden="true">
                  ({config.shortcut})
                </span>
              )}
            </Button>
          )
        })}
      </div>
    )
  },
)

RatingButtons.displayName = 'RatingButtons'

