/**
 * Deck List Component
 * 
 * Displays a list of decks with empty state
 * 
 * Features:
 * - Grid layout
 * - Empty state
 * - Loading state
 */

import * as React from 'react'
import { DeckCard, type DeckCardData, type DeckCardProps } from './DeckCard'
import { EmptyState } from '@/components/common/EmptyState'
import { LoadingSpinner } from '@/components/common/LoadingSpinner'
import { cn } from '@/lib/utils'

export interface DeckListProps {
  decks: DeckCardData[]
  isLoading?: boolean
  onEdit?: (deckId: string) => void
  onDelete?: (deckId: string) => void
  onReview?: (deckId: string) => void
  onClick?: (deckId: string) => void
  className?: string
  emptyMessage?: string
  emptyDescription?: string
  gridCols?: 1 | 2 | 3 | 4
}

export const DeckList = React.memo<DeckListProps>(
  ({
    decks,
    isLoading = false,
    onEdit,
    onDelete,
    onReview,
    onClick,
    className,
    emptyMessage = 'No decks found',
    emptyDescription = 'Create your first deck to get started',
    gridCols = 3,
  }) => {
    if (isLoading) {
      return (
        <div className="flex items-center justify-center py-12">
          <LoadingSpinner />
        </div>
      )
    }

    if (decks.length === 0) {
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
        className={cn('grid gap-4', gridColsClass[gridCols], className)}
        role="list"
        aria-label="Deck list"
      >
        {decks.map((deck) => (
          <DeckCard
            key={deck.id}
            deck={deck}
            onEdit={onEdit}
            onDelete={onDelete}
            onReview={onReview}
            onClick={onClick}
          />
        ))}
      </div>
    )
  },
)

DeckList.displayName = 'DeckList'

