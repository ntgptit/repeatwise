export interface FolderDto {
  id: string
  name: string
  description: string | null
  parentFolderId: string | null
  depth: number
  path: string
  sortOrder: number
  createdAt: string
  updatedAt: string
}

export interface FolderTreeNode extends FolderDto {
  children: FolderTreeNode[]
}

export interface CreateFolderRequest {
  name: string
  description?: string | null
  parentFolderId?: string | null
}

export interface UpdateFolderRequest {
  name?: string
  description?: string | null
}

export interface MoveFolderRequest {
  targetParentFolderId?: string | null
}

export interface CopyFolderRequest {
  destinationFolderId?: string | null
  newName?: string
  renamePolicy?: string
}

export interface DeleteFolderResponse {
  message: string
  deletedFolders: number
  deletedDecks: number
}

export interface FolderStatsDto {
  folderId: string
  folderName: string
  totalFolders: number
  totalDecks: number
  totalCards: number
  dueCards: number
  newCards: number
  learningCards: number
  reviewCards: number
  masteredCards: number
  completionRate: number
  cached: boolean
  lastUpdatedAt: string | null
}

