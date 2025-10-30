import { BaseApi } from '../http/base.api'
import { API_ENDPOINTS } from '@/constants/api'
import type { PaginatedResponse } from '@/api/types/pagination'

/**
 * Folder API Module
 * Follows consistent convention: BaseApi + TypeScript Generics
 */
export interface Folder {
  id: string
  name: string
  parentId: string | null
  userId: string
  createdAt: string
  updatedAt: string
  deckCount?: number
  cardCount?: number
}

export interface CreateFolderRequest {
  name: string
  parentId?: string | null
}

export interface UpdateFolderRequest {
  name?: string
  parentId?: string | null
}

export interface FolderTreeNode extends Folder {
  children?: FolderTreeNode[]
}

class FolderApi extends BaseApi {
  constructor() {
    super(API_ENDPOINTS.FOLDERS.BASE)
  }

  /**
   * Get all folders
   */
  async getFolders(parentId?: string | null): Promise<Folder[]> {
    const params = parentId !== undefined ? { parentId } : {}
    return this.getList<Folder>(params)
  }

  /**
   * Get folder tree
   */
  async getFolderTree(): Promise<FolderTreeNode[]> {
    return this.customGet<FolderTreeNode[]>('/tree')
  }

  /**
   * Get folder by ID
   */
  async getById(id: string): Promise<Folder> {
    return super.getById<Folder>(id)
  }

  /**
   * Create folder
   */
  async create(data: CreateFolderRequest): Promise<Folder> {
    return super.create<Folder, CreateFolderRequest>(data)
  }

  /**
   * Update folder
   */
  async update(id: string, data: UpdateFolderRequest): Promise<Folder> {
    return super.update<Folder, UpdateFolderRequest>(id, data)
  }

  /**
   * Delete folder
   */
  async delete(id: string): Promise<void> {
    await super.delete(id)
  }

  /**
   * Move folder
   */
  async move(id: string, parentId: string | null): Promise<Folder> {
    return this.customPatch<Folder, { parentId: string | null }>(
      `/${id}/move`,
      { parentId },
    )
  }
}

// Export singleton instance
export const folderApi = new FolderApi()