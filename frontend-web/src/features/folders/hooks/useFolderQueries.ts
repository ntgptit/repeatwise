import { useQuery, type UseQueryResult } from '@tanstack/react-query'
import { folderService } from '@/api/services/folder.service'
import type { FolderDto, FolderTreeNode } from '@/api/types/folder.types'
import { buildFolderTree } from '@/features/folders/utils/tree'

export const folderQueryKeys = {
  all: ['folders'] as const,
  tree: () => [...folderQueryKeys.all, 'tree'] as const,
  detail: (folderId: string) => [...folderQueryKeys.all, folderId] as const,
}

interface FolderTreeResult {
  list: FolderDto[]
  tree: FolderTreeNode[]
}

export const useFolderTree = (): UseQueryResult<FolderTreeResult> => {
  return useQuery({
    queryKey: folderQueryKeys.tree(),
    queryFn: async () => {
      const folders = await folderService.getAll()
      return {
        list: folders,
        tree: buildFolderTree(folders),
      }
    },
  })
}

