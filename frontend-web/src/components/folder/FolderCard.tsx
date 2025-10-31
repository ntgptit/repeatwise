/**
 * Folder Card Component
 * 
 * Displays a single folder card with statistics and actions
 * 
 * Features:
 * - Folder name and description
 * - Statistics (cards, decks, due cards)
 * - Quick actions (edit, delete, move)
 * - Click to navigate
 */

import * as React from 'react'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Edit2, Trash2, Folder, FolderOpen, MoreVertical } from 'lucide-react'
import { cn } from '@/lib/utils'

export interface FolderCardData {
  id: string
  name: string
  description?: string | null
  parentId?: string | null
  depth?: number
  childrenCount?: number
  deckCount?: number
  totalCards?: number
  dueCards?: number
  newCards?: number
  matureCards?: number
  createdAt?: string
  updatedAt?: string
}

export interface FolderCardProps {
  folder: FolderCardData
  onEdit?: (folderId: string) => void
  onDelete?: (folderId: string) => void
  onMove?: (folderId: string) => void
  onClick?: (folderId: string) => void
  className?: string
  showActions?: boolean
  isExpanded?: boolean
}

export const FolderCard = React.memo<FolderCardProps>(
  ({
    folder,
    onEdit,
    onDelete,
    onMove,
    onClick,
    className,
    showActions = true,
    isExpanded = false,
  }) => {
    const handleClick = () => {
      onClick?.(folder.id)
    }

    const FolderIcon = isExpanded ? FolderOpen : Folder

    return (
      <Card
        className={cn(
          'hover:shadow-md transition-shadow cursor-pointer',
          className,
        )}
        onClick={handleClick}
        role="article"
        aria-label={`Folder: ${folder.name}`}
      >
        <CardHeader>
          <div className="flex items-start justify-between gap-2">
            <div className="flex items-center gap-2 flex-1 min-w-0">
              <FolderIcon className="h-5 w-5 text-muted-foreground shrink-0" />
              <CardTitle className="text-lg font-semibold line-clamp-2">
                {folder.name}
              </CardTitle>
            </div>
            {showActions && (
              <div className="flex gap-1 shrink-0">
                {onMove && (
                  <Button
                    variant="ghost"
                    size="icon"
                    className="h-8 w-8"
                    onClick={(e) => {
                      e.stopPropagation()
                      onMove(folder.id)
                    }}
                    aria-label="Move folder"
                  >
                    <MoreVertical className="h-4 w-4" />
                  </Button>
                )}
                {onEdit && (
                  <Button
                    variant="ghost"
                    size="icon"
                    className="h-8 w-8"
                    onClick={(e) => {
                      e.stopPropagation()
                      onEdit(folder.id)
                    }}
                    aria-label="Edit folder"
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
                      onDelete(folder.id)
                    }}
                    aria-label="Delete folder"
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
            {folder.description && (
              <p className="text-sm text-muted-foreground line-clamp-2">
                {folder.description}
              </p>
            )}
            <div className="flex items-center gap-2 flex-wrap">
              {folder.childrenCount !== undefined && folder.childrenCount > 0 && (
                <Badge variant="secondary" className="text-xs">
                  {folder.childrenCount} folders
                </Badge>
              )}
              {folder.deckCount !== undefined && folder.deckCount > 0 && (
                <Badge variant="outline" className="text-xs">
                  {folder.deckCount} decks
                </Badge>
              )}
              {folder.totalCards !== undefined && folder.totalCards > 0 && (
                <Badge variant="default" className="text-xs">
                  {folder.totalCards} cards
                </Badge>
              )}
              {folder.dueCards !== undefined && folder.dueCards > 0 && (
                <Badge variant="default" className="text-xs bg-orange-500">
                  {folder.dueCards} due
                </Badge>
              )}
            </div>
          </div>
        </CardContent>
      </Card>
    )
  },
)

FolderCard.displayName = 'FolderCard'

