import { apiClient } from '@/api/clients/base.client'
import type {
  CopyDeckRequest,
  CopyDeckResponse,
  CreateDeckRequest,
  DeckDto,
  DeleteDeckResponse,
  MoveDeckRequest,
  UpdateDeckRequest,
} from '@/api/types/deck.types'

const basePath = '/v1/decks'

const normalizeOptional = <T>(value: T | null | undefined): T | null | undefined => {
  if (value === undefined) {
    return undefined
  }
  return value ?? null
}

export const deckService = {
  getByFolder: async (folderId: string | null): Promise<DeckDto[]> => {
    const response = await apiClient.get<DeckDto[]>(basePath, {
      params: {
        folderId: normalizeOptional(folderId),
      },
    })
    return response.data
  },

  getById: async (deckId: string): Promise<DeckDto> => {
    const response = await apiClient.get<DeckDto>(`${basePath}/${deckId}`)
    return response.data
  },

  create: async (payload: CreateDeckRequest): Promise<DeckDto> => {
    const response = await apiClient.post<DeckDto>(basePath, {
      name: payload.name,
      description: payload.description?.trim() ?? undefined,
      folderId: normalizeOptional(payload.folderId ?? null),
    })
    return response.data
  },

  update: async (deckId: string, payload: UpdateDeckRequest): Promise<DeckDto> => {
    const response = await apiClient.patch<DeckDto>(`${basePath}/${deckId}`, {
      name: payload.name,
      description: payload.description?.trim() ?? undefined,
    })
    return response.data
  },

  move: async (deckId: string, payload: MoveDeckRequest): Promise<DeckDto> => {
    const response = await apiClient.post<DeckDto>(`${basePath}/${deckId}/move`, {
      targetFolderId: normalizeOptional(payload.targetFolderId ?? null),
    })
    return response.data
  },

  copy: async (deckId: string, payload: CopyDeckRequest): Promise<CopyDeckResponse> => {
    const response = await apiClient.post<CopyDeckResponse>(`${basePath}/${deckId}/copy`, {
      destinationFolderId: normalizeOptional(payload.destinationFolderId ?? null),
      newName: payload.newName?.trim() ?? undefined,
      appendCopySuffix: payload.appendCopySuffix ?? true,
    })
    return response.data
  },

  delete: async (deckId: string): Promise<DeleteDeckResponse> => {
    const response = await apiClient.delete<DeleteDeckResponse>(`${basePath}/${deckId}`)
    return response.data
  },
}

export default deckService

