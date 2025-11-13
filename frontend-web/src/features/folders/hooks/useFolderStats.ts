import { useCallback } from 'react'
import { useQuery, useQueryClient } from '@tanstack/react-query'
import { folderService } from '@/api/services/folder.service'
import type { FolderStatsDto } from '@/api/types/folder.types'

const folderStatsQueryKey = (folderId: string) => ['folders', 'stats', folderId] as const

export const useFolderStats = (folderId: string | null) => {
  const queryClient = useQueryClient()

  const queryResult = useQuery<FolderStatsDto>({
    queryKey: folderId ? folderStatsQueryKey(folderId) : ['folders', 'stats', 'none'],
    enabled: Boolean(folderId),
    queryFn: () => {
      if (!folderId) {
        return Promise.reject(new Error('Folder ID is required'))
      }
      return folderService.getStats(folderId)
    },
    staleTime: 60 * 1000,
  })

  const refresh = useCallback(async () => {
    if (!folderId) {
      return null
    }
    const data = await folderService.getStats(folderId, { refresh: true })
    queryClient.setQueryData(folderStatsQueryKey(folderId), data)
    return data
  }, [folderId, queryClient])

  return {
    ...queryResult,
    refresh,
  }
}

export default useFolderStats

