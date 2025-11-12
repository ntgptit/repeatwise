import { useEffect, useMemo, useState } from 'react'
import { Alert, Button, Container, Grid, Stack } from '@mui/material'
import PageHelmet from '@/components/PageHelmet'
import PageTitleWrapper from '@/components/PageTitleWrapper'
import Footer from '@/components/Footer'
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
    const { message } = error as { message?: unknown }
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

  const { mutateAsync: createFolder, isPending: isCreating } = useCreateFolder()
  const { mutateAsync: updateFolder, isPending: isUpdating } = useUpdateFolder()
  const { mutateAsync: moveFolder, isPending: isMoving } = useMoveFolder()
  const { mutateAsync: copyFolder, isPending: isCopying } = useCopyFolder()
  const { mutateAsync: deleteFolder, isPending: isDeleting } = useDeleteFolder()

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
      const result = await createFolder(values)
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
      await updateFolder({
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
      const result = await moveFolder({
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
      const result = await copyFolder({
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
      await deleteFolder(selectedFolder.id)
      notificationService.success('Folder deleted successfully')
      setSelectedId(selectedFolder.parentFolderId ?? null)
      closeModal()
    } catch (error) {
      notificationService.error(resolveErrorMessage(error, 'Unable to delete folder'))
    }
  }


  if (isLoading) {
    return (
      <Container maxWidth="lg" sx={{ py: 4 }}>
        <SuspenseLoader />
      </Container>
    )
  }

  if (isError) {
    return (
      <Container maxWidth="lg" sx={{ py: 4 }}>
        <Stack spacing={2}>
          <Alert severity="error">
            Failed to load folder data. Please retry or check the backend connection.
          </Alert>
          <Button variant="contained" onClick={() => refetch()}>
            Retry
          </Button>
        </Stack>
      </Container>
    )
  }

  return (
    <>
      <PageHelmet title="Folder Management" />
      <PageTitleWrapper>
        <PageTitle
          heading="Folder management"
          subHeading="Organize and manage folders with full support for UC-007 through UC-011."
        />
      </PageTitleWrapper>
      <Container maxWidth="lg">
        <Grid container direction="row" justifyContent="center" alignItems="stretch" spacing={4}>
          <Grid item xs={12} md={4}>
            <Stack spacing={2} sx={{ height: '100%' }}>
              <FolderTree
                nodes={tree}
                selectedId={selectedId}
                onSelect={handleSelect}
              />
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
            <Stack spacing={3} sx={{ height: '100%' }}>
              <FolderStatsPanel />
              <FolderDetailsPanel folder={selectedFolder ?? null} allFolders={data?.list ?? []} />
              <FolderDeckListPlaceholder />
            </Stack>
          </Grid>
        </Grid>
      </Container>

      <CreateFolderModal
        open={activeModal === 'create'}
        parent={createParent}
        isSubmitting={isCreating}
        onClose={closeModal}
        onSubmit={handleCreateSubmit}
      />
      <RenameFolderModal
        open={activeModal === 'rename'}
        folder={selectedFolder ?? null}
        isSubmitting={isUpdating}
        onClose={closeModal}
        onSubmit={handleRenameSubmit}
      />
      <MoveFolderModal
        open={activeModal === 'move'}
        folder={selectedFolder ?? null}
        tree={tree}
        isSubmitting={isMoving}
        onClose={closeModal}
        onSubmit={handleMoveSubmit}
      />
      <CopyFolderModal
        open={activeModal === 'copy'}
        folder={selectedFolder ?? null}
        tree={tree}
        isSubmitting={isCopying}
        onClose={closeModal}
        onSubmit={handleCopySubmit}
      />
      <DeleteFolderDialog
        open={activeModal === 'delete'}
        folder={selectedFolder ?? null}
        isSubmitting={isDeleting}
        onClose={closeModal}
        onConfirm={handleDeleteConfirm}
      />
      <Footer />
    </>
  )
}

export default FolderManagementPage

