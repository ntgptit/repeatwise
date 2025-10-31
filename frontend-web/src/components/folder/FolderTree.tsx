/**
 * Folder Tree Component
 * 
 * Displays hierarchical folder tree structure
 * 
 * Features:
 * - Expand/collapse all
 * - Search/filter
 * - Selection
 * - Loading state
 * - Empty state
 */

import * as React from 'react'
import { FolderTreeNode, type FolderTreeNodeData } from './FolderTreeNode'
import { SearchBar } from '@/components/common/Navigation'
import { EmptyState } from '@/components/common/EmptyState'
import { LoadingSpinner } from '@/components/common/LoadingSpinner'
import { Button } from '@/components/ui/button'
import { ChevronDown, ChevronRight, FolderTree as FolderTreeIcon } from 'lucide-react'
import { cn } from '@/lib/utils'

export interface FolderTreeProps {
  /** Root folders */
  folders: FolderTreeNodeData[]
  /** Selected folder ID */
  selectedId?: string | null
  /** Expanded folder IDs */
  expandedIds?: Set<string>
  /** Expand/collapse handler */
  onToggleExpand?: (folderId: string) => void
  /** Click handler */
  onClick?: (folderId: string) => void
  /** Loading state */
  isLoading?: boolean
  /** Search query */
  searchQuery?: string
  /** Search handler */
  onSearch?: (query: string) => void
  /** Show search bar */
  showSearch?: boolean
  /** Additional className */
  className?: string
  /** Custom content renderer */
  renderContent?: (folder: FolderTreeNodeData) => React.ReactNode
}

export const FolderTree = React.memo<FolderTreeProps>(
  ({
    folders,
    selectedId,
    expandedIds = new Set(),
    onToggleExpand,
    onClick,
    isLoading = false,
    searchQuery,
    onSearch,
    showSearch = true,
    className,
    renderContent,
  }) => {
    const [localExpanded, setLocalExpanded] = React.useState<Set<string>>(
      expandedIds,
    )

    React.useEffect(() => {
      setLocalExpanded(expandedIds)
    }, [expandedIds])

    const handleToggle = (folderId: string) => {
      const newExpanded = new Set(localExpanded)
      if (newExpanded.has(folderId)) {
        newExpanded.delete(folderId)
      } else {
        newExpanded.add(folderId)
      }
      setLocalExpanded(newExpanded)
      onToggleExpand?.(folderId)
    }

    const expandAll = () => {
      const allIds = new Set<string>()
      const collectIds = (folders: FolderTreeNodeData[]) => {
        folders.forEach((folder) => {
          if (folder.children && folder.children.length > 0) {
            allIds.add(folder.id)
            collectIds(folder.children)
          }
        })
      }
      collectIds(folders)
      setLocalExpanded(allIds)
    }

    const collapseAll = () => {
      setLocalExpanded(new Set())
    }

    // Filter folders based on search query
    const filterFolders = (
      folders: FolderTreeNodeData[],
      query: string,
    ): FolderTreeNodeData[] => {
      if (!query) return folders

      const lowerQuery = query.toLowerCase()
      return folders
        .map((folder) => {
          const matches = folder.name.toLowerCase().includes(lowerQuery)
          const filteredChildren =
            folder.children && folder.children.length > 0
              ? filterFolders(folder.children, query)
              : []

          if (matches || filteredChildren.length > 0) {
            return {
              ...folder,
              children: filteredChildren.length > 0 ? filteredChildren : folder.children,
            }
          }
          return null
        })
        .filter((f): f is FolderTreeNodeData => f !== null)
    }

    const filteredFolders = searchQuery
      ? filterFolders(folders, searchQuery)
      : folders

    if (isLoading) {
      return (
        <div className="flex items-center justify-center py-8">
          <LoadingSpinner />
        </div>
      )
    }

    if (folders.length === 0) {
      return (
        <EmptyState
          message="No folders"
          description="Create your first folder to organize your decks"
          icon={<FolderTreeIcon className="h-12 w-12" />}
        />
      )
    }

    return (
      <div className={cn('space-y-2', className)} role="tree" aria-label="Folder tree">
        {/* Search and Controls */}
        {showSearch && (
          <div className="space-y-2 p-2">
            {onSearch && (
              <SearchBar
                placeholder="Search folders..."
                onSearch={onSearch}
                className="w-full"
              />
            )}
            <div className="flex gap-2">
              <Button
                variant="ghost"
                size="sm"
                onClick={expandAll}
                className="text-xs"
              >
                <ChevronDown className="h-3 w-3 mr-1" />
                Expand All
              </Button>
              <Button
                variant="ghost"
                size="sm"
                onClick={collapseAll}
                className="text-xs"
              >
                <ChevronRight className="h-3 w-3 mr-1" />
                Collapse All
              </Button>
            </div>
          </div>
        )}

        {/* Tree Nodes */}
        <div className="space-y-0.5">
          {filteredFolders.map((folder) => (
            <FolderTreeNode
              key={folder.id}
              folder={folder}
              isExpanded={localExpanded.has(folder.id)}
              onToggle={handleToggle}
              onClick={onClick}
              selectedId={selectedId}
              renderContent={renderContent}
            />
          ))}
        </div>
      </div>
    )
  },
)

FolderTree.displayName = 'FolderTree'

