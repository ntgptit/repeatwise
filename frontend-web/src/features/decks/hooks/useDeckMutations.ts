import { useMutation, useQueryClient } from '@tanstack/react-query'
import { deckService } from '@/api/services/deck.service'
import type {
  CopyDeckRequest,
  CopyDeckResponse,
  CreateDeckRequest,
  DeckDto,
  DeleteDeckResponse,
  MoveDeckRequest,
  UpdateDeckRequest,
} from '@/api/types/deck.types'
import { deckQueryKeys } from '@/features/decks/hooks/useDeckQueries'

const invalidateAllDeckLists = (queryClient: ReturnType<typeof useQueryClient>) => {
  queryClient.invalidateQueries({ queryKey: deckQueryKeys.all })
}

interface UpdateDeckVariables {
  deckId: string
  payload: UpdateDeckRequest
}

interface MoveDeckVariables {
  deckId: string
  payload: MoveDeckRequest
}

interface CopyDeckVariables {
  deckId: string
  payload: CopyDeckRequest
}

export const useCreateDeck = () => {
  const queryClient = useQueryClient()

  return useMutation<DeckDto, unknown, CreateDeckRequest>({
    mutationFn: payload => deckService.create(payload),
    onSuccess: deck => {
      invalidateAllDeckLists(queryClient)
      queryClient.setQueryData(deckQueryKeys.detail(deck.id), deck)
    },
  })
}

export const useUpdateDeck = () => {
  const queryClient = useQueryClient()

  return useMutation<DeckDto, unknown, UpdateDeckVariables>({
    mutationFn: ({ deckId, payload }) => deckService.update(deckId, payload),
    onSuccess: deck => {
      invalidateAllDeckLists(queryClient)
      queryClient.setQueryData(deckQueryKeys.detail(deck.id), deck)
    },
  })
}

export const useMoveDeck = () => {
  const queryClient = useQueryClient()

  return useMutation<DeckDto, unknown, MoveDeckVariables>({
    mutationFn: ({ deckId, payload }) => deckService.move(deckId, payload),
    onSuccess: deck => {
      invalidateAllDeckLists(queryClient)
      queryClient.setQueryData(deckQueryKeys.detail(deck.id), deck)
    },
  })
}

export const useCopyDeck = () => {
  const queryClient = useQueryClient()

  return useMutation<CopyDeckResponse, unknown, CopyDeckVariables>({
    mutationFn: ({ deckId, payload }) => deckService.copy(deckId, payload),
    onSuccess: response => {
      invalidateAllDeckLists(queryClient)
      queryClient.setQueryData(deckQueryKeys.detail(response.deck.id), response.deck)
    },
  })
}

export const useDeleteDeck = () => {
  const queryClient = useQueryClient()

  return useMutation<DeleteDeckResponse, unknown, string>({
    mutationFn: deckId => deckService.delete(deckId),
    onSuccess: response => {
      invalidateAllDeckLists(queryClient)
      queryClient.removeQueries({ queryKey: deckQueryKeys.detail(response.deckId) })
    },
  })
}

