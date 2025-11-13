import { useQuery, type UseQueryResult } from '@tanstack/react-query'
import { deckService } from '@/api/services/deck.service'
import type { DeckDto } from '@/api/types/deck.types'

export const deckQueryKeys = {
  all: ['decks'] as const,
  list: (folderId: string | null) => [...deckQueryKeys.all, folderId ?? 'root'] as const,
  detail: (deckId: string) => [...deckQueryKeys.all, 'detail', deckId] as const,
}

export const useDecks = (folderId: string | null): UseQueryResult<DeckDto[]> => {
  return useQuery({
    queryKey: deckQueryKeys.list(folderId),
    queryFn: () => deckService.getByFolder(folderId),
    staleTime: 60 * 1000,
  })
}

