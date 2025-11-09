/**
 * Custom hooks for Folder operations
 * Implements UC-007 to UC-011
 */

import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { folderClient } from '@/api/clients/folder.client'
import type {
  CreateFolderRequest,
  UpdateFolderRequest,
  MoveFolderRequest,
  CopyFolderRequest,
  FolderResponse,
  DeleteFolderResponse,
} from '@/api/types/folder.types'

export const FOLDER_QUERY_KEYS = {
  all: ['folders'] as const,
  lists: () => [...FOLDER_QUERY_KEYS.all, 'list'] as const,
  list: (filters?: Record<string, unknown>) =>
    [...FOLDER_QUERY_KEYS.lists(), filters] as const,
  details: () => [...FOLDER_QUERY_KEYS.all, 'detail'] as const,
  detail: (id: string) => [...FOLDER_QUERY_KEYS.details(), id] as const,
  roots: () => [...FOLDER_QUERY_KEYS.all, 'roots'] as const,
  children: (parentId: string) => [...FOLDER_QUERY_KEYS.all, 'children', parentId] as const,
}

/**
 * Hook to fetch all folders
 */
export const useFolders = () => {
  return useQuery({
    queryKey: FOLDER_QUERY_KEYS.lists(),
    queryFn: () => folderClient.getAllFolders(),
  })
}

/**
 * Hook to fetch a single folder
 */
export const useFolder = (folderId: string) => {
  return useQuery({
    queryKey: FOLDER_QUERY_KEYS.detail(folderId),
    queryFn: () => folderClient.getFolderById(folderId),
    enabled: !!folderId,
  })
}

/**
 * Hook to fetch root folders
 */
export const useRootFolders = () => {
  return useQuery({
    queryKey: FOLDER_QUERY_KEYS.roots(),
    queryFn: () => folderClient.getRootFolders(),
  })
}

/**
 * Hook to fetch child folders
 */
export const useChildFolders = (parentId: string) => {
  return useQuery({
    queryKey: FOLDER_QUERY_KEYS.children(parentId),
    queryFn: () => folderClient.getChildFolders(parentId),
    enabled: !!parentId,
  })
}

/**
 * UC-007: Hook to create a folder
 */
export const useCreateFolder = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (request: CreateFolderRequest) => folderClient.createFolder(request),
    onSuccess: () => {
      // Invalidate all folder queries to refetch updated data
      queryClient.invalidateQueries({ queryKey: FOLDER_QUERY_KEYS.all })
    },
  })
}

/**
 * UC-008: Hook to update a folder (rename/change description)
 */
export const useUpdateFolder = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ folderId, request }: { folderId: string; request: UpdateFolderRequest }) =>
      folderClient.updateFolder(folderId, request),
    onSuccess: (data) => {
      // Invalidate all folder queries
      queryClient.invalidateQueries({ queryKey: FOLDER_QUERY_KEYS.all })
      // Update the specific folder cache
      queryClient.setQueryData(FOLDER_QUERY_KEYS.detail(data.id), data)
    },
  })
}

/**
 * UC-009: Hook to move a folder
 */
export const useMoveFolder = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ folderId, request }: { folderId: string; request: MoveFolderRequest }) =>
      folderClient.moveFolder(folderId, request),
    onSuccess: () => {
      // Invalidate all folder queries to refetch tree structure
      queryClient.invalidateQueries({ queryKey: FOLDER_QUERY_KEYS.all })
    },
  })
}

/**
 * UC-010: Hook to copy a folder
 */
export const useCopyFolder = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ folderId, request }: { folderId: string; request: CopyFolderRequest }) =>
      folderClient.copyFolder(folderId, request),
    onSuccess: () => {
      // Invalidate all folder queries to show copied folder
      queryClient.invalidateQueries({ queryKey: FOLDER_QUERY_KEYS.all })
    },
  })
}

/**
 * UC-011: Hook to delete a folder
 */
export const useDeleteFolder = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (folderId: string) => folderClient.deleteFolder(folderId),
    onSuccess: () => {
      // Invalidate all folder queries to remove deleted folder
      queryClient.invalidateQueries({ queryKey: FOLDER_QUERY_KEYS.all })
    },
  })
}

/**
 * Hook to restore a deleted folder
 */
export const useRestoreFolder = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (folderId: string) => folderClient.restoreFolder(folderId),
    onSuccess: () => {
      // Invalidate all folder queries to show restored folder
      queryClient.invalidateQueries({ queryKey: FOLDER_QUERY_KEYS.all })
    },
  })
}
