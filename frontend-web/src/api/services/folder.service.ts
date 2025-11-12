import { apiClient } from '@/api/clients/base.client'
import type {
  CopyFolderRequest,
  CreateFolderRequest,
  DeleteFolderResponse,
  FolderDto,
  MoveFolderRequest,
  UpdateFolderRequest,
} from '@/api/types/folder.types'

const basePath = '/v1/folders'

const toNullable = (value: string | null | undefined): string | null | undefined => {
  if (value === undefined) {
    return undefined
  }

  return value ?? null
}

export const folderService = {
  getAll: async (): Promise<FolderDto[]> => {
    const response = await apiClient.get<FolderDto[]>(basePath)
    return response.data
  },

  getRoot: async (): Promise<FolderDto[]> => {
    const response = await apiClient.get<FolderDto[]>(`${basePath}/root`)
    return response.data
  },

  getChildren: async (parentId: string): Promise<FolderDto[]> => {
    const response = await apiClient.get<FolderDto[]>(`${basePath}/${parentId}/children`)
    return response.data
  },

  getById: async (folderId: string): Promise<FolderDto> => {
    const response = await apiClient.get<FolderDto>(`${basePath}/${folderId}`)
    return response.data
  },

  create: async (payload: CreateFolderRequest): Promise<FolderDto> => {
    const response = await apiClient.post<FolderDto>(basePath, {
      name: payload.name,
      description: payload.description ?? undefined,
      parentFolderId: toNullable(payload.parentFolderId),
    })

    return response.data
  },

  update: async (folderId: string, payload: UpdateFolderRequest): Promise<FolderDto> => {
    const response = await apiClient.patch<FolderDto>(`${basePath}/${folderId}`, {
      name: payload.name,
      description: payload.description ?? undefined,
    })

    return response.data
  },

  move: async (folderId: string, payload: MoveFolderRequest): Promise<FolderDto> => {
    const response = await apiClient.post<FolderDto>(`${basePath}/${folderId}/move`, {
      targetParentFolderId: toNullable(payload.targetParentFolderId),
    })

    return response.data
  },

  copy: async (folderId: string, payload: CopyFolderRequest): Promise<FolderDto> => {
    const response = await apiClient.post<FolderDto>(`${basePath}/${folderId}/copy`, {
      destinationFolderId: toNullable(payload.destinationFolderId),
      newName: payload.newName ?? undefined,
      renamePolicy: payload.renamePolicy ?? undefined,
    })

    return response.data
  },

  restore: async (folderId: string): Promise<FolderDto> => {
    const response = await apiClient.post<FolderDto>(`${basePath}/${folderId}/restore`)
    return response.data
  },

  delete: async (folderId: string): Promise<DeleteFolderResponse> => {
    const response = await apiClient.delete<DeleteFolderResponse>(`${basePath}/${folderId}`)
    return response.data
  },
}

export default folderService

