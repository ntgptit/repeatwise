export interface DeckDto {
  id: string
  name: string
  description: string | null
  folderId: string | null
  cardCount: number
  createdAt: string
  updatedAt: string
}

export interface CreateDeckRequest {
  name: string
  description?: string | null
  folderId?: string | null
}

export interface UpdateDeckRequest {
  name?: string
  description?: string | null
}

export interface MoveDeckRequest {
  targetFolderId?: string | null
}

export interface CopyDeckRequest {
  destinationFolderId?: string | null
  newName?: string
  appendCopySuffix?: boolean
}

export interface CopyDeckResponse {
  deck: DeckDto
  message: string
  copiedCards: number
}

export interface DeleteDeckResponse {
  deckId: string
  message: string
  deletedAt: string
}

