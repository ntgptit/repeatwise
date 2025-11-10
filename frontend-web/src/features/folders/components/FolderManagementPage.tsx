import { useEffect, useMemo, useState } from 'react'
import { Alert, Box, Button, Grid, Stack } from '@mui/material'
import PageTitle from '@/components/PageTitle'
import SuspenseLoader from '@/components/SuspenseLoader'
import { notificationService } from '@/common/services/notification.service'
import type {
  CopyFolderRequest,
  CreateFolderRequest,
  FolderTreeNode,
  MoveFolderRequest,
  UpdateFolderRequest,
} from '@/api/types/folder.types'
import { FolderTree } from '@/features/folders/components/FolderTree'
import { FolderActionsPanel } from '@/features/folders/components/FolderActionsPanel'
import { FolderDetailsPanel } from '@/features/folders/components/FolderDetailsPanel'
import { FolderStatsPanel } from '@/features/folders/components/FolderStatsPanel'
import { FolderDeckListPlaceholder } from '@/features/folders/components/FolderDeckListPlaceholder'
import {
  CopyFolderModal,
  CreateFolderModal,
  DeleteFolderDialog,
  MoveFolderModal,
  RenameFolderModal,
} from '@/features/folders/components/modals'
import { useFolderTree } from '@/features/folders/hooks/useFolderQueries'
import {
  useCopyFolder,
  useCreateFolder,
  useDeleteFolder,
  useMoveFolder,
  useUpdateFolder,
} from '@/features/folders/hooks/useFolderMutations'
import { findFolderNode } from '@/features/folders/utils/tree'

type ModalType = 'create' | 'rename' | 'move' | 'copy' | 'delete' | null

const EMPTY_TREE: FolderTreeNode[] = []

const resolveErrorMessage = (error: unknown, fallback: string) => {
  if (error instanceof Error && error.message) {
    return error.message
  }

  if (typeof error === 'string') {
    return error
  }

  if (typeof error === 'object' && error !== null && 'message' in error) {
    const message = (error as { message?: unknown }).message
    if (typeof message === 'string') {
      return message
    }
  }

  return fallback
}

const useInitialSelection = (nodes: FolderTreeNode[], currentId: string | null) => {
  const [value, setValue] = useState<string | null>(currentId)

  useEffect(() => {
    if (!value && nodes.length > 0) {
      setValue(nodes[0].id)
    }
  }, [nodes, value])

  return [value, setValue] as const
}

export const FolderManagementPage = () => {
  const { data, isLoading, isError, refetch } = useFolderTree()
  const tree = data?.tree ?? EMPTY_TREE
  const [selectedId, setSelectedId] = useInitialSelection(tree, null)
  const selectedFolder = useMemo(
    () => findFolderNode(tree, selectedId ?? null),
    [tree, selectedId]
  )
  const [activeModal, setActiveModal] = useState<ModalType>(null)
  const [createParent, setCreateParent] = useState<FolderTreeNode | null>(null)

  const createFolder = useCreateFolder()
  const updateFolder = useUpdateFolder()
  const moveFolder = useMoveFolder()
  const copyFolder = useCopyFolder()
  const deleteFolder = useDeleteFolder()

  const closeModal = () => {
    setActiveModal(null)
    setCreateParent(null)
  }

  const handleSelect = (folderId: string | null) => {
    setSelectedId(folderId)
  }

  const handleCreateRoot = () => {
    setCreateParent(null)
    setActiveModal('create')
  }

  const handleCreateChild = () => {
    if (!selectedFolder) {
      return
    }
    setCreateParent(selectedFolder)
    setActiveModal('create')
  }

  const openModal = (type: ModalType) => {
    if (!selectedFolder) {
      return
    }
    setActiveModal(type)
  }

  const handleCreateSubmit = async (values: CreateFolderRequest) => {
    try {
      const result = await createFolder.mutateAsync(values)
      notificationService.success('Folder created successfully')
      setSelectedId(result.id)
      closeModal()
    } catch (error) {
      notificationService.error(resolveErrorMessage(error, 'Unable to create folder'))
    }
  }

  const handleRenameSubmit = async (payload: UpdateFolderRequest) => {
    if (!selectedFolder) {
      return
    }

    try {
      await updateFolder.mutateAsync({
        folderId: selectedFolder.id,
        payload,
      })
      notificationService.success('Folder updated successfully')
      closeModal()
    } catch (error) {
      notificationService.error(resolveErrorMessage(error, 'Unable to update folder'))
    }
  }

  const handleMoveSubmit = async (payload: MoveFolderRequest) => {
    if (!selectedFolder) {
      return
    }

    try {
      const result = await moveFolder.mutateAsync({
        folderId: selectedFolder.id,
        payload,
      })
      notificationService.success('Folder moved successfully')
      setSelectedId(result.id)
      closeModal()
    } catch (error) {
      notificationService.error(resolveErrorMessage(error, 'Unable to move folder'))
    }
  }

  const handleCopySubmit = async (payload: CopyFolderRequest) => {
    if (!selectedFolder) {
      return
    }

    try {
      const result = await copyFolder.mutateAsync({
        folderId: selectedFolder.id,
        payload,
      })
      notificationService.success('Folder copied successfully')
      setSelectedId(result.id)
      closeModal()
    } catch (error) {
      notificationService.error(resolveErrorMessage(error, 'Unable to copy folder'))
    }
  }

  const handleDeleteConfirm = async () => {
    if (!selectedFolder) {
      return
    }

    try {
      await deleteFolder.mutateAsync(selectedFolder.id)
      notificationService.success('Folder deleted successfully')
      setSelectedId(selectedFolder.parentFolderId ?? null)
      closeModal()
    } catch (error) {
      notificationService.error(resolveErrorMessage(error, 'Unable to delete folder'))
    }
  }

  if (isLoading) {
    return (
      <Box sx={{ p: 4 }}>
        <SuspenseLoader />
      </Box>
    )
  }

  if (isError) {
    return (
      <Box sx={{ p: 4 }}>
        <Stack spacing={2}>
          <Alert severity="error">
            Failed to load folder data. Please retry or check the backend connection.
          </Alert>
          <Button variant="contained" onClick={() => refetch()}>
            Retry
          </Button>
        </Stack>
      </Box>
    )
  }

  return (
    <Stack spacing={3}>
      <PageTitle
        heading="Folder management"
        subHeading="Organize and manage folders with full support for UC-007 through UC-011."
      />
      <Grid container spacing={3}>
        <Grid item xs={12} md={4}>
          <Stack spacing={2}>
            <FolderTree nodes={tree} selectedId={selectedId} onSelect={handleSelect} />
            <FolderActionsPanel
              selected={selectedFolder ?? null}
              onCreateRoot={handleCreateRoot}
              onCreateChild={handleCreateChild}
              onRename={() => openModal('rename')}
              onMove={() => openModal('move')}
              onCopy={() => openModal('copy')}
              onDelete={() => openModal('delete')}
            />
          </Stack>
        </Grid>
        <Grid item xs={12} md={8}>
          <Stack spacing={3}>
            <FolderStatsPanel />
            <FolderDetailsPanel folder={selectedFolder ?? null} />
            <FolderDeckListPlaceholder />
          </Stack>
        </Grid>
      </Grid>

      <CreateFolderModal
        open={activeModal === 'create'}
        parent={createParent}
        isSubmitting={createFolder.isLoading}
        onClose={closeModal}
        onSubmit={handleCreateSubmit}
      />
      <RenameFolderModal
        open={activeModal === 'rename'}
        folder={selectedFolder ?? null}
        isSubmitting={updateFolder.isLoading}
        onClose={closeModal}
        onSubmit={handleRenameSubmit}
      />
      <MoveFolderModal
        open={activeModal === 'move'}
        folder={selectedFolder ?? null}
        tree={tree}
        isSubmitting={moveFolder.isLoading}
        onClose={closeModal}
        onSubmit={handleMoveSubmit}
      />
      <CopyFolderModal
        open={activeModal === 'copy'}
        folder={selectedFolder ?? null}
        tree={tree}
        isSubmitting={copyFolder.isLoading}
        onClose={closeModal}
        onSubmit={handleCopySubmit}
      />
      <DeleteFolderDialog
        open={activeModal === 'delete'}
        folder={selectedFolder ?? null}
        isSubmitting={deleteFolder.isLoading}
        onClose={closeModal}
        onConfirm={handleDeleteConfirm}
      />
    </Stack>
  )
}

export default FolderManagementPage

