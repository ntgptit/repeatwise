import { apiClient } from '@/api/clients/base.client'
import type {
  CardDto,
  CreateCardRequest,
  DeleteCardResponse,
  UpdateCardRequest,
} from '@/api/types/card.types'

const basePath = '/v1/cards'

export const cardService = {
  listByDeck: async (deckId: string): Promise<CardDto[]> => {
    const response = await apiClient.get<CardDto[]>(`${basePath}/deck/${deckId}`)
    return response.data
  },

  create: async (payload: CreateCardRequest): Promise<CardDto> => {
    const response = await apiClient.post<CardDto>(basePath, {
      deckId: payload.deckId,
      front: payload.front.trim(),
      back: payload.back.trim(),
    })
    return response.data
  },

  update: async (cardId: string, payload: UpdateCardRequest): Promise<CardDto> => {
    const response = await apiClient.patch<CardDto>(`${basePath}/${cardId}`, {
      front: payload.front?.trim(),
      back: payload.back?.trim(),
    })
    return response.data
  },

  delete: async (cardId: string): Promise<DeleteCardResponse> => {
    const response = await apiClient.delete<DeleteCardResponse>(`${basePath}/${cardId}`)
    return response.data
  },
}

export default cardService

