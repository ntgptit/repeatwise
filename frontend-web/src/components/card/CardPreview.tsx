/**
 * Card Preview Component
 * 
 * Displays a card with flip animation to show front/back
 * 
 * Features:
 * - Flip animation
 * - Keyboard navigation (Space to flip)
 * - Click to flip
 */

import * as React from 'react'
import { Card, CardContent } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { RotateCcw } from 'lucide-react'
import { cn } from '@/lib/utils'

export interface CardPreviewProps {
  front: string
  back: string
  className?: string
  onFlip?: () => void
  showFlipButton?: boolean
  initialSide?: 'front' | 'back'
}

export const CardPreview = React.memo<CardPreviewProps>(
  ({
    front,
    back,
    className,
    onFlip,
    showFlipButton = true,
    initialSide = 'front',
  }) => {
    const [isFlipped, setIsFlipped] = React.useState(initialSide === 'back')
    const [isAnimating, setIsAnimating] = React.useState(false)

    const handleFlip = React.useCallback(() => {
      if (isAnimating) return
      
      setIsAnimating(true)
      setIsFlipped((prev) => !prev)
      
      setTimeout(() => {
        setIsAnimating(false)
        onFlip?.()
      }, 300)
    }, [isAnimating, onFlip])

    React.useEffect(() => {
      const handleKeyDown = (e: KeyboardEvent) => {
        if (e.key === ' ' || e.key === 'Enter') {
          e.preventDefault()
          handleFlip()
        }
      }

      window.addEventListener('keydown', handleKeyDown)
      return () => window.removeEventListener('keydown', handleKeyDown)
    }, [handleFlip])

    return (
      <div
        className={cn('relative', className)}
        role="button"
        tabIndex={0}
        onClick={handleFlip}
        onKeyDown={(e) => {
          if (e.key === 'Enter' || e.key === ' ') {
            e.preventDefault()
            handleFlip()
          }
        }}
        aria-label={isFlipped ? 'Card back' : 'Card front'}
      >
        <Card
          className={cn(
            'relative w-full min-h-64 transition-opacity duration-300 cursor-pointer',
            isAnimating && 'pointer-events-none',
          )}
        >
          {/* Front Side */}
          {!isFlipped && (
            <CardContent className="flex flex-col items-center justify-center p-6 min-h-64">
              <div className="text-center space-y-4 w-full">
                <p className="text-lg font-medium whitespace-pre-wrap break-words">
                  {front}
                </p>
                {showFlipButton && (
                  <p className="text-sm text-muted-foreground">
                    Click or press Space to flip
                  </p>
                )}
              </div>
            </CardContent>
          )}

          {/* Back Side */}
          {isFlipped && (
            <CardContent className="flex flex-col items-center justify-center p-6 min-h-64">
              <div className="text-center space-y-4 w-full">
                <p className="text-lg font-medium whitespace-pre-wrap break-words">
                  {back}
                </p>
                {showFlipButton && (
                  <div className="flex flex-col items-center gap-2">
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={(e) => {
                        e.stopPropagation()
                        handleFlip()
                      }}
                      aria-label="Flip card"
                    >
                      <RotateCcw className="h-4 w-4 mr-2" />
                      Flip back
                    </Button>
                  </div>
                )}
              </div>
            </CardContent>
          )}
        </Card>
      </div>
    )
  },
)

CardPreview.displayName = 'CardPreview'

