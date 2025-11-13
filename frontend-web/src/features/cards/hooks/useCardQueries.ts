import { useQuery, type UseQueryResult } from '@tanstack/react-query'
import { cardService } from '@/api/services/card.service'
import type { CardDto } from '@/api/types/card.types'

export const cardQueryKeys = {
  all: ['cards'] as const,
  listByDeck: (deckId: string) => [...cardQueryKeys.all, 'deck', deckId] as const,
}

export const useCardsByDeck = (deckId: string | null): UseQueryResult<CardDto[]> => {
  return useQuery({
    queryKey: deckId ? cardQueryKeys.listByDeck(deckId) : [...cardQueryKeys.all, 'deck', 'none'],
    queryFn: () => (deckId ? cardService.listByDeck(deckId) : Promise.resolve([])),
    enabled: Boolean(deckId),
    staleTime: 30 * 1000,
  })
}

