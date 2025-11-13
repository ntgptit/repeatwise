import { useMutation, useQueryClient } from '@tanstack/react-query'
import { cardService } from '@/api/services/card.service'
import type {
  CardDto,
  CreateCardRequest,
  DeleteCardResponse,
  UpdateCardRequest,
} from '@/api/types/card.types'
import { cardQueryKeys } from '@/features/cards/hooks/useCardQueries'
import { deckQueryKeys } from '@/features/decks/hooks/useDeckQueries'

interface UpdateCardVariables {
  cardId: string
  deckId: string
  payload: UpdateCardRequest
}

interface DeleteCardVariables {
  cardId: string
  deckId: string
}

const invalidateDeckData = (queryClient: ReturnType<typeof useQueryClient>, deckId: string) => {
  queryClient.invalidateQueries({ queryKey: deckQueryKeys.all })
  queryClient.invalidateQueries({ queryKey: deckQueryKeys.detail(deckId) })
  queryClient.invalidateQueries({ queryKey: cardQueryKeys.listByDeck(deckId) })
}

export const useCreateCard = () => {
  const queryClient = useQueryClient()

  return useMutation<CardDto, unknown, CreateCardRequest>({
    mutationFn: payload => cardService.create(payload),
    onSuccess: (_card, variables) => {
      invalidateDeckData(queryClient, variables.deckId)
    },
  })
}

export const useUpdateCard = () => {
  const queryClient = useQueryClient()

  return useMutation<CardDto, unknown, UpdateCardVariables>({
    mutationFn: ({ cardId, payload }) => cardService.update(cardId, payload),
    onSuccess: (card, variables) => {
      queryClient.setQueryData(cardQueryKeys.listByDeck(variables.deckId), (current?: CardDto[]) => {
        if (!current) {
          return current
        }
        return current.map(item => (item.id === card.id ? card : item))
      })
      invalidateDeckData(queryClient, variables.deckId)
    },
  })
}

export const useDeleteCard = () => {
  const queryClient = useQueryClient()

  return useMutation<DeleteCardResponse, unknown, DeleteCardVariables>({
    mutationFn: ({ cardId }) => cardService.delete(cardId),
    onSuccess: (_response, variables) => {
      queryClient.setQueryData(cardQueryKeys.listByDeck(variables.deckId), (current?: CardDto[]) => {
        if (!current) {
          return current
        }
        return current.filter(item => item.id !== variables.cardId)
      })
      invalidateDeckData(queryClient, variables.deckId)
    },
  })
}

