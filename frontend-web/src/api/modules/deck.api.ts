import { BaseApi } from '../http/base.api'
import { API_ENDPOINTS } from '@/constants/api'

/**
 * Deck API Module
 * Follows consistent convention: BaseApi + TypeScript Generics
 */
export interface Deck {
  id: string
  name: string
  description?: string | null
  folderId?: string | null
  folderName?: string | null
  cardCount?: number
  dueCards?: number
  newCards?: number
  createdAt?: string
  updatedAt?: string
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
  folderId: string | null
}

class DeckApi extends BaseApi {
  constructor() {
    super(API_ENDPOINTS.DECKS.BASE)
  }

  /**
   * Get all decks
   */
  async getAll(): Promise<Deck[]> {
    return this.getList<Deck>()
  }

  /**
   * Get deck by ID
   */
  async getById(id: string): Promise<Deck> {
    return super.getById<Deck>(id)
  }

  /**
   * Get decks by folder
   */
  async getByFolder(folderId: string): Promise<Deck[]> {
    return this.customGet<Deck[]>(`/folder/${folderId}`)
  }

  /**
   * Create deck
   */
  async create(data: CreateDeckRequest): Promise<Deck> {
    return super.create<Deck, CreateDeckRequest>(data)
  }

  /**
   * Update deck
   */
  async update(id: string, data: UpdateDeckRequest): Promise<Deck> {
    return super.update<Deck, UpdateDeckRequest>(id, data)
  }

  /**
   * Delete deck
   */
  async delete(id: string): Promise<void> {
    await super.delete(id)
  }

  /**
   * Move deck
   */
  async move(id: string, folderId: string | null): Promise<Deck> {
    return this.customPatch<Deck, MoveDeckRequest>(
      `/${id}/move`,
      { folderId },
    )
  }
}

// Export singleton instance
export const deckApi = new DeckApi()

