/**
 * Folder Tree Node Component
 * 
 * Recursive tree node component for folder hierarchy
 * 
 * Features:
 * - Expand/collapse functionality
 * - Recursive rendering
 * - Indentation based on depth
 * - Click handlers
 */

import * as React from 'react'
import { ChevronRight, ChevronDown, Folder, FolderOpen } from 'lucide-react'
import { cn } from '@/lib/utils'

export interface FolderTreeNodeData {
  id: string
  name: string
  parentId?: string | null
  depth?: number
  childrenCount?: number
  deckCount?: number
  totalCards?: number
  dueCards?: number
  children?: FolderTreeNodeData[]
}

export interface FolderTreeNodeProps {
  /** Folder data */
  folder: FolderTreeNodeData
  /** Whether node is expanded */
  isExpanded?: boolean
  /** Toggle expansion handler */
  onToggle?: (folderId: string) => void
  /** Click handler */
  onClick?: (folderId: string) => void
  /** Selected folder ID */
  selectedId?: string | null
  /** Maximum depth (default: 10) */
  maxDepth?: number
  /** Additional className */
  className?: string
  /** Render custom content */
  renderContent?: (folder: FolderTreeNodeData) => React.ReactNode
}

export const FolderTreeNode = React.memo<FolderTreeNodeProps>(
  ({
    folder,
    isExpanded = false,
    onToggle,
    onClick,
    selectedId,
    maxDepth = 10,
    className,
    renderContent,
  }) => {
    const hasChildren = folder.children && folder.children.length > 0
    const isSelected = selectedId === folder.id
    const canExpand = hasChildren && (folder.depth ?? 0) < maxDepth

    const handleToggle = (e: React.MouseEvent) => {
      e.stopPropagation()
      if (canExpand && onToggle) {
        onToggle(folder.id)
      }
    }

    const handleClick = () => {
      onClick?.(folder.id)
    }

    const depth = folder.depth ?? 0
    const indent = depth * 20

    const FolderIcon = isExpanded ? FolderOpen : Folder

    return (
      <div className={cn('select-none', className)}>
        <div
          className={cn(
            'flex items-center gap-1 px-2 py-1.5 rounded-md cursor-pointer transition-colors',
            'hover:bg-accent hover:text-accent-foreground',
            isSelected && 'bg-accent text-accent-foreground font-medium',
            !isSelected && 'text-muted-foreground',
          )}
          onClick={handleClick}
          style={{ paddingLeft: `${12 + indent}px` }}
          role="treeitem"
          aria-expanded={canExpand ? isExpanded : undefined}
          aria-selected={isSelected}
        >
          {/* Expand/Collapse Icon */}
          {canExpand ? (
            <button
              onClick={handleToggle}
              className="p-0.5 hover:bg-accent rounded"
              aria-label={isExpanded ? 'Collapse' : 'Expand'}
            >
              {isExpanded ? (
                <ChevronDown className="h-4 w-4" />
              ) : (
                <ChevronRight className="h-4 w-4" />
              )}
            </button>
          ) : (
            <span className="w-5" />
          )}

          {/* Folder Icon */}
          <FolderIcon className="h-4 w-4 shrink-0" />

          {/* Folder Name */}
          <span className="flex-1 text-sm truncate">{folder.name}</span>

          {/* Badges */}
          <div className="flex items-center gap-1 shrink-0">
            {folder.dueCards !== undefined && folder.dueCards > 0 && (
              <span className="text-xs text-orange-500 font-medium">
                {folder.dueCards}
              </span>
            )}
            {renderContent && renderContent(folder)}
          </div>
        </div>

        {/* Children */}
        {isExpanded && hasChildren && folder.children && (
          <div role="group">
            {folder.children.map((child) => (
              <FolderTreeNode
                key={child.id}
                folder={child}
                isExpanded={isExpanded}
                onToggle={onToggle}
                onClick={onClick}
                selectedId={selectedId}
                maxDepth={maxDepth}
                renderContent={renderContent}
              />
            ))}
          </div>
        )}
      </div>
    )
  },
)

FolderTreeNode.displayName = 'FolderTreeNode'

