import { useMutation, useQueryClient } from '@tanstack/react-query'
import { folderService } from '@/api/services/folder.service'
import type {
  CopyFolderRequest,
  CreateFolderRequest,
  DeleteFolderResponse,
  FolderDto,
  MoveFolderRequest,
  UpdateFolderRequest,
} from '@/api/types/folder.types'
import { folderQueryKeys } from '@/features/folders/hooks/useFolderQueries'

const invalidateFolders = (queryClient: ReturnType<typeof useQueryClient>) => {
  queryClient.invalidateQueries({ queryKey: folderQueryKeys.all })
}

export const useCreateFolder = () => {
  const queryClient = useQueryClient()

  return useMutation<FolderDto, unknown, CreateFolderRequest>({
    mutationFn: (payload) => folderService.create(payload),
    onSuccess: () => {
      invalidateFolders(queryClient)
    },
  })
}

interface UpdateFolderVariables {
  folderId: string
  payload: UpdateFolderRequest
}

export const useUpdateFolder = () => {
  const queryClient = useQueryClient()

  return useMutation<FolderDto, unknown, UpdateFolderVariables>({
    mutationFn: ({ folderId, payload }) => folderService.update(folderId, payload),
    onSuccess: () => {
      invalidateFolders(queryClient)
    },
  })
}

interface MoveFolderVariables {
  folderId: string
  payload: MoveFolderRequest
}

export const useMoveFolder = () => {
  const queryClient = useQueryClient()

  return useMutation<FolderDto, unknown, MoveFolderVariables>({
    mutationFn: ({ folderId, payload }) => folderService.move(folderId, payload),
    onSuccess: () => {
      invalidateFolders(queryClient)
    },
  })
}

interface CopyFolderVariables {
  folderId: string
  payload: CopyFolderRequest
}

export const useCopyFolder = () => {
  const queryClient = useQueryClient()

  return useMutation<FolderDto, unknown, CopyFolderVariables>({
    mutationFn: ({ folderId, payload }) => folderService.copy(folderId, payload),
    onSuccess: () => {
      invalidateFolders(queryClient)
    },
  })
}

export const useDeleteFolder = () => {
  const queryClient = useQueryClient()

  return useMutation<DeleteFolderResponse, unknown, string>({
    mutationFn: (folderId) => folderService.delete(folderId),
    onSuccess: () => {
      invalidateFolders(queryClient)
    },
  })
}

