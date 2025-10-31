/**
 * Deck Card Component
 * 
 * Displays a single deck card with stats and actions
 * 
 * Features:
 * - Deck name and description
 * - Card count, due cards, new cards
 * - Quick actions (edit, delete, review)
 * - Click to navigate
 */

import * as React from 'react'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Edit2, Trash2, Play, MoreVertical } from 'lucide-react'
import { cn } from '@/lib/utils'

export interface DeckCardData {
  id: string
  name: string
  description?: string | null
  folderId?: string | null
  cardCount?: number
  dueCards?: number
  newCards?: number
  createdAt?: string
  updatedAt?: string
}

export interface DeckCardProps {
  deck: DeckCardData
  onEdit?: (deckId: string) => void
  onDelete?: (deckId: string) => void
  onReview?: (deckId: string) => void
  onClick?: (deckId: string) => void
  className?: string
  showActions?: boolean
}

export const DeckCard = React.memo<DeckCardProps>(
  ({
    deck,
    onEdit,
    onDelete,
    onReview,
    onClick,
    className,
    showActions = true,
  }) => {
    const handleClick = () => {
      onClick?.(deck.id)
    }

    return (
      <Card
        className={cn(
          'hover:shadow-md transition-shadow cursor-pointer',
          className,
        )}
        onClick={handleClick}
        role="article"
        aria-label={`Deck: ${deck.name}`}
      >
        <CardHeader>
          <div className="flex items-start justify-between gap-2">
            <CardTitle className="text-lg font-semibold line-clamp-2 flex-1">
              {deck.name}
            </CardTitle>
            {showActions && (
              <div className="flex gap-1 shrink-0">
                {onReview && (
                  <Button
                    variant="ghost"
                    size="icon"
                    className="h-8 w-8"
                    onClick={(e) => {
                      e.stopPropagation()
                      onReview(deck.id)
                    }}
                    aria-label="Start review"
                  >
                    <Play className="h-4 w-4" />
                  </Button>
                )}
                {onEdit && (
                  <Button
                    variant="ghost"
                    size="icon"
                    className="h-8 w-8"
                    onClick={(e) => {
                      e.stopPropagation()
                      onEdit(deck.id)
                    }}
                    aria-label="Edit deck"
                  >
                    <Edit2 className="h-4 w-4" />
                  </Button>
                )}
                {onDelete && (
                  <Button
                    variant="ghost"
                    size="icon"
                    className="h-8 w-8 text-destructive hover:text-destructive"
                    onClick={(e) => {
                      e.stopPropagation()
                      onDelete(deck.id)
                    }}
                    aria-label="Delete deck"
                  >
                    <Trash2 className="h-4 w-4" />
                  </Button>
                )}
              </div>
            )}
          </div>
        </CardHeader>
        <CardContent>
          <div className="space-y-3">
            {deck.description && (
              <p className="text-sm text-muted-foreground line-clamp-2">
                {deck.description}
              </p>
            )}
            <div className="flex items-center gap-2 flex-wrap">
              {deck.cardCount !== undefined && (
                <Badge variant="secondary" className="text-xs">
                  {deck.cardCount} cards
                </Badge>
              )}
              {deck.dueCards !== undefined && deck.dueCards > 0 && (
                <Badge variant="default" className="text-xs">
                  {deck.dueCards} due
                </Badge>
              )}
              {deck.newCards !== undefined && deck.newCards > 0 && (
                <Badge variant="outline" className="text-xs">
                  {deck.newCards} new
                </Badge>
              )}
            </div>
          </div>
        </CardContent>
      </Card>
    )
  },
)

DeckCard.displayName = 'DeckCard'

