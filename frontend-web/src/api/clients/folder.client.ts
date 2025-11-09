/**
 * Folder API Client
 * Implements API calls for UC-007 to UC-011 (Folder Management)
 */

import { apiClient } from './base.client'
import type {
  FolderResponse,
  CreateFolderRequest,
  UpdateFolderRequest,
  MoveFolderRequest,
  CopyFolderRequest,
  DeleteFolderResponse,
} from '../types/folder.types'

const FOLDER_BASE_PATH = '/api/folders'

/**
 * UC-007: Create a new folder
 */
export const createFolder = async (
  request: CreateFolderRequest
): Promise<FolderResponse> => {
  const response = await apiClient.post<FolderResponse>(FOLDER_BASE_PATH, request)
  return response.data
}

/**
 * UC-008: Update folder (rename/change description)
 */
export const updateFolder = async (
  folderId: string,
  request: UpdateFolderRequest
): Promise<FolderResponse> => {
  const response = await apiClient.patch<FolderResponse>(
    `${FOLDER_BASE_PATH}/${folderId}`,
    request
  )
  return response.data
}

/**
 * UC-009: Move folder to a different parent
 */
export const moveFolder = async (
  folderId: string,
  request: MoveFolderRequest
): Promise<FolderResponse> => {
  const response = await apiClient.post<FolderResponse>(
    `${FOLDER_BASE_PATH}/${folderId}/move`,
    request
  )
  return response.data
}

/**
 * UC-010: Copy folder and its subtree
 */
export const copyFolder = async (
  folderId: string,
  request: CopyFolderRequest
): Promise<FolderResponse> => {
  const response = await apiClient.post<FolderResponse>(
    `${FOLDER_BASE_PATH}/${folderId}/copy`,
    request
  )
  return response.data
}

/**
 * UC-011: Delete folder (soft delete)
 */
export const deleteFolder = async (folderId: string): Promise<DeleteFolderResponse> => {
  const response = await apiClient.delete<DeleteFolderResponse>(
    `${FOLDER_BASE_PATH}/${folderId}`
  )
  return response.data
}

/**
 * Restore a soft-deleted folder
 */
export const restoreFolder = async (folderId: string): Promise<FolderResponse> => {
  const response = await apiClient.post<FolderResponse>(
    `${FOLDER_BASE_PATH}/${folderId}/restore`
  )
  return response.data
}

/**
 * Get a single folder by ID
 */
export const getFolderById = async (folderId: string): Promise<FolderResponse> => {
  const response = await apiClient.get<FolderResponse>(`${FOLDER_BASE_PATH}/${folderId}`)
  return response.data
}

/**
 * Get all folders for current user (hierarchical tree)
 */
export const getAllFolders = async (): Promise<FolderResponse[]> => {
  const response = await apiClient.get<FolderResponse[]>(FOLDER_BASE_PATH)
  return response.data
}

/**
 * Get root-level folders
 */
export const getRootFolders = async (): Promise<FolderResponse[]> => {
  const response = await apiClient.get<FolderResponse[]>(`${FOLDER_BASE_PATH}/root`)
  return response.data
}

/**
 * Get child folders of a parent
 */
export const getChildFolders = async (parentId: string): Promise<FolderResponse[]> => {
  const response = await apiClient.get<FolderResponse[]>(
    `${FOLDER_BASE_PATH}/${parentId}/children`
  )
  return response.data
}

export const folderClient = {
  createFolder,
  updateFolder,
  moveFolder,
  copyFolder,
  deleteFolder,
  restoreFolder,
  getFolderById,
  getAllFolders,
  getRootFolders,
  getChildFolders,
}

export default folderClient
