/**
 * Card Item Component
 * 
 * Displays a single card in a list with front/back preview
 * 
 * Features:
 * - Shows front and back preview
 * - Click to view/edit
 * - Delete action
 * - Character count display
 */

import * as React from 'react'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Edit2, Trash2, Eye } from 'lucide-react'
import { cn } from '@/lib/utils'

export interface CardItemData {
  id: string
  deckId: string
  front: string
  back: string
  createdAt?: string
  updatedAt?: string
  currentBox?: number | null
  dueDate?: string | null
}

export interface CardItemProps {
  card: CardItemData
  onEdit?: (cardId: string) => void
  onDelete?: (cardId: string) => void
  onPreview?: (cardId: string) => void
  className?: string
  showActions?: boolean
  compact?: boolean
}

export const CardItem = React.memo<CardItemProps>(
  ({
    card,
    onEdit,
    onDelete,
    onPreview,
    className,
    showActions = true,
    compact = false,
  }) => {
    const frontPreview = card.front.length > 100 
      ? `${card.front.substring(0, 100)}...` 
      : card.front
    const backPreview = card.back.length > 100 
      ? `${card.back.substring(0, 100)}...` 
      : card.back

    return (
      <Card
        className={cn(
          'hover:shadow-md transition-shadow',
          className,
        )}
        role="article"
        aria-label={`Card: ${card.front.substring(0, 50)}`}
      >
        <CardHeader className={cn(compact && 'pb-3')}>
          <div className="flex items-start justify-between gap-2">
            <CardTitle className="text-base font-medium line-clamp-2 flex-1">
              {frontPreview}
            </CardTitle>
            {showActions && (
              <div className="flex gap-1 shrink-0">
                {onPreview && (
                  <Button
                    variant="ghost"
                    size="icon"
                    className="h-8 w-8"
                    onClick={() => onPreview(card.id)}
                    aria-label="Preview card"
                  >
                    <Eye className="h-4 w-4" />
                  </Button>
                )}
                {onEdit && (
                  <Button
                    variant="ghost"
                    size="icon"
                    className="h-8 w-8"
                    onClick={() => onEdit(card.id)}
                    aria-label="Edit card"
                  >
                    <Edit2 className="h-4 w-4" />
                  </Button>
                )}
                {onDelete && (
                  <Button
                    variant="ghost"
                    size="icon"
                    className="h-8 w-8 text-destructive hover:text-destructive"
                    onClick={() => onDelete(card.id)}
                    aria-label="Delete card"
                  >
                    <Trash2 className="h-4 w-4" />
                  </Button>
                )}
              </div>
            )}
          </div>
        </CardHeader>
        <CardContent className={cn(compact && 'pt-0')}>
          <div className="space-y-2">
            <div className="text-sm text-muted-foreground line-clamp-2">
              {backPreview}
            </div>
            <div className="flex items-center justify-between text-xs text-muted-foreground">
              <div className="flex gap-2">
                {card.currentBox !== null && card.currentBox !== undefined && (
                  <Badge variant="secondary" className="text-xs">
                    Box {card.currentBox}
                  </Badge>
                )}
                {card.dueDate && (
                  <Badge variant="outline" className="text-xs">
                    Due: {new Date(card.dueDate).toLocaleDateString()}
                  </Badge>
                )}
              </div>
              <div className="text-xs">
                {card.front.length + card.back.length} chars
              </div>
            </div>
          </div>
        </CardContent>
      </Card>
    )
  },
)

CardItem.displayName = 'CardItem'

