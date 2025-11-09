/**
 * Folder API Types
 * Corresponds to backend DTOs for folder operations (UC-007 to UC-011)
 */

export interface FolderResponse {
  id: string
  userId: string
  name: string
  description: string | null
  parentId: string | null
  path: string
  depth: number
  createdAt: string
  updatedAt: string
  deletedAt: string | null
  children?: FolderResponse[]
  deckCount?: number
}

export interface CreateFolderRequest {
  name: string
  description?: string | null
  parentId?: string | null
}

export interface UpdateFolderRequest {
  name?: string
  description?: string | null
}

export interface MoveFolderRequest {
  targetParentFolderId: string | null
}

export interface CopyFolderRequest {
  destinationFolderId: string | null
  newName?: string | null
}

export interface DeleteFolderResponse {
  message: string
  deletedFolders: number
  deletedDecks: number
  deletedCards?: number
  recoverableUntil?: string
}

export interface FolderTreeNode extends FolderResponse {
  children: FolderTreeNode[]
  expanded?: boolean
  selected?: boolean
}
