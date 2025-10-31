/**
 * Card List Component
 * 
 * Displays a list of cards with pagination and empty state
 * 
 * Features:
 * - Paginated card list
 * - Empty state when no cards
 * - Loading state
 * - Selection support (optional)
 */

import * as React from 'react'
import { CardItem, type CardItemData, type CardItemProps } from './CardItem'
import { EmptyState } from '@/components/common/EmptyState'
import { LoadingSpinner } from '@/components/common/LoadingSpinner'
import { cn } from '@/lib/utils'

export interface CardListProps {
  cards: CardItemData[]
  isLoading?: boolean
  onEdit?: (cardId: string) => void
  onDelete?: (cardId: string) => void
  onPreview?: (cardId: string) => void
  className?: string
  emptyMessage?: string
  emptyDescription?: string
  gridCols?: 1 | 2 | 3 | 4
  compact?: boolean
}

export const CardList = React.memo<CardListProps>(
  ({
    cards,
    isLoading = false,
    onEdit,
    onDelete,
    onPreview,
    className,
    emptyMessage = 'No cards found',
    emptyDescription = 'Create your first card to get started',
    gridCols = 3,
    compact = false,
  }) => {
    if (isLoading) {
      return (
        <div className="flex items-center justify-center py-12">
          <LoadingSpinner />
        </div>
      )
    }

    if (cards.length === 0) {
      return (
        <EmptyState
          message={emptyMessage}
          description={emptyDescription}
        />
      )
    }

    const gridColsClass = {
      1: 'grid-cols-1',
      2: 'grid-cols-1 md:grid-cols-2',
      3: 'grid-cols-1 md:grid-cols-2 lg:grid-cols-3',
      4: 'grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4',
    }

    return (
      <div
        className={cn(
          'grid gap-4',
          gridColsClass[gridCols],
          className,
        )}
        role="list"
        aria-label="Card list"
      >
        {cards.map((card) => (
          <CardItem
            key={card.id}
            card={card}
            onEdit={onEdit}
            onDelete={onDelete}
            onPreview={onPreview}
            compact={compact}
          />
        ))}
      </div>
    )
  },
)

CardList.displayName = 'CardList'

