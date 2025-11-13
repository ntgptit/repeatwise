import { useCallback, useEffect, useMemo, useState } from 'react'
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
import type {
  CopyDeckRequest,
  CreateDeckRequest,
  DeckDto,
  MoveDeckRequest,
  UpdateDeckRequest,
} from '@/api/types/deck.types'
import { FolderTree } from '@/features/folders/components/FolderTree'
import { FolderActionsPanel } from '@/features/folders/components/FolderActionsPanel'
import { FolderDetailsPanel } from '@/features/folders/components/FolderDetailsPanel'
import { FolderStatsPanel } from '@/features/folders/components/FolderStatsPanel'
import { DeckListPanel, type DeckScope } from '@/features/decks/components/DeckListPanel'
import {
  CopyDeckModal,
  CreateDeckModal,
  DeleteDeckDialog,
  EditDeckModal,
  MoveDeckModal,
} from '@/features/decks/components/modals'
import { CardManagerDialog } from '@/features/cards/components'
import {
  CopyFolderModal,
  CreateFolderModal,
  DeleteFolderDialog,
  MoveFolderModal,
  RenameFolderModal,
} from '@/features/folders/components/modals'
import { useFolderTree } from '@/features/folders/hooks/useFolderQueries'
import { useFolderStats } from '@/features/folders/hooks/useFolderStats'
import {
  useCopyFolder,
  useCreateFolder,
  useDeleteFolder,
  useMoveFolder,
  useUpdateFolder,
} from '@/features/folders/hooks/useFolderMutations'
import { findFolderNode } from '@/features/folders/utils/tree'
import { useDecks } from '@/features/decks/hooks/useDeckQueries'
import {
  useCopyDeck,
  useCreateDeck,
  useDeleteDeck,
  useMoveDeck,
  useUpdateDeck,
} from '@/features/decks/hooks/useDeckMutations'

type ModalType = 'create' | 'rename' | 'move' | 'copy' | 'delete' | null
type DeckModalType = 'create' | 'edit' | 'move' | 'copy' | 'delete' | null

interface FolderManagementPageProps {
  initialDeckScope?: DeckScope
}

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

export const FolderManagementPage = ({ initialDeckScope }: FolderManagementPageProps) => {
  const { data, isLoading, isError, refetch } = useFolderTree()
  const tree = data?.tree ?? EMPTY_TREE
  const [selectedId, setSelectedId] = useInitialSelection(tree, null)
  const selectedFolder = useMemo(
    () => findFolderNode(tree, selectedId ?? null),
    [tree, selectedId]
  )
  const selectedFolderId = selectedFolder?.id ?? null

  const {
    data: folderStats,
    isLoading: isStatsLoading,
    isFetching: isStatsFetching,
    isError: isStatsError,
    error: statsError,
    refetch: refetchFolderStats,
    refresh: refreshFolderStats,
  } = useFolderStats(selectedFolderId)

  const folderStatsErrorMessage = isStatsError
    ? resolveErrorMessage(statsError, 'Failed to load folder statistics. Please try again.')
    : null
  const isStatsRefreshing = isStatsFetching && !isStatsLoading
  const refreshStatsSafely = useCallback(async () => {
    try {
      await refreshFolderStats()
    } catch {
      // no-op: keep UI responsive even if stats refresh fails
    }
  }, [refreshFolderStats])
  const [activeModal, setActiveModal] = useState<ModalType>(null)
  const [createParent, setCreateParent] = useState<FolderTreeNode | null>(null)

  const { mutateAsync: createFolder, isPending: isCreating } = useCreateFolder()
  const { mutateAsync: updateFolder, isPending: isUpdating } = useUpdateFolder()
  const { mutateAsync: moveFolder, isPending: isMoving } = useMoveFolder()
  const { mutateAsync: copyFolder, isPending: isCopying } = useCopyFolder()
  const { mutateAsync: deleteFolder, isPending: isDeleting } = useDeleteFolder()

  const [deckModal, setDeckModal] = useState<DeckModalType>(null)
  const [activeDeck, setActiveDeck] = useState<DeckDto | null>(null)
  const [cardManagerDeckId, setCardManagerDeckId] = useState<string | null>(null)
  const [deckScope, setDeckScope] = useState<DeckScope>(() => {
    if (initialDeckScope) {
      return initialDeckScope
    }
    return selectedId ? 'selected' : 'root'
  })

  useEffect(() => {
    if (!selectedId && deckScope === 'selected') {
      setDeckScope('root')
    }
  }, [selectedId, deckScope])

  const closeModal = () => {
    setActiveModal(null)
    setCreateParent(null)
  }

  const closeDeckModal = () => {
    setDeckModal(null)
    setActiveDeck(null)
  }

  const folderIdForDecks = deckScope === 'selected' ? selectedId ?? null : null
  const {
    data: deckData,
    isLoading: isDecksLoading,
    isFetching: isDecksFetching,
    isError: isDecksError,
    error: decksError,
    refetch: refetchDecks,
  } = useDecks(folderIdForDecks)
  const decks = useMemo(() => deckData ?? [], [deckData])
  const cardManagerDeck = useMemo(
    () => (cardManagerDeckId ? decks.find(deck => deck.id === cardManagerDeckId) ?? null : null),
    [decks, cardManagerDeckId]
  )
  const deckErrorMessage = isDecksError
    ? resolveErrorMessage(decksError, 'Failed to load deck data. Please try again.')
    : null

  const { mutateAsync: createDeck, isPending: isCreatingDeck } = useCreateDeck()
  const { mutateAsync: updateDeck, isPending: isUpdatingDeck } = useUpdateDeck()
  const { mutateAsync: moveDeck, isPending: isMovingDeck } = useMoveDeck()
  const { mutateAsync: copyDeck, isPending: isCopyingDeck } = useCopyDeck()
  const { mutateAsync: deleteDeck, isPending: isDeletingDeck } = useDeleteDeck()

  const activeDeckFolder = useMemo(
    () => (activeDeck?.folderId ? findFolderNode(tree, activeDeck.folderId) : null),
    [tree, activeDeck]
  )

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
      await refreshStatsSafely()
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
      await refreshStatsSafely()
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

  const handleOpenCreateDeck = () => {
    setActiveDeck(null)
    setDeckModal('create')
  }

  const handleOpenEditDeck = (deck: DeckDto) => {
    setActiveDeck(deck)
    setDeckModal('edit')
  }

  const handleOpenMoveDeck = (deck: DeckDto) => {
    setActiveDeck(deck)
    setDeckModal('move')
  }

  const handleOpenCopyDeck = (deck: DeckDto) => {
    setActiveDeck(deck)
    setDeckModal('copy')
  }

  const handleOpenDeleteDeck = (deck: DeckDto) => {
    setActiveDeck(deck)
    setDeckModal('delete')
  }

  const handleOpenCardManager = (deck: DeckDto) => {
    setCardManagerDeckId(deck.id)
  }

  const handleCloseCardManager = () => {
    setCardManagerDeckId(null)
  }

  const handleCreateDeckSubmit = async (payload: CreateDeckRequest) => {
    try {
      await createDeck(payload)
      notificationService.success('Deck created successfully')
      await refreshStatsSafely()
      closeDeckModal()
    } catch (error) {
      notificationService.error(resolveErrorMessage(error, 'Unable to create deck'))
    }
  }

  const handleUpdateDeckSubmit = async (payload: UpdateDeckRequest) => {
    if (!activeDeck) {
      return
    }

    try {
      await updateDeck({
        deckId: activeDeck.id,
        payload,
      })
      notificationService.success('Deck updated successfully')
      await refreshStatsSafely()
      closeDeckModal()
    } catch (error) {
      notificationService.error(resolveErrorMessage(error, 'Unable to update deck'))
    }
  }

  const handleMoveDeckSubmit = async (payload: MoveDeckRequest) => {
    if (!activeDeck) {
      return
    }

    try {
      await moveDeck({
        deckId: activeDeck.id,
        payload,
      })
      notificationService.success('Deck moved successfully')
      await refreshStatsSafely()
      closeDeckModal()
    } catch (error) {
      notificationService.error(resolveErrorMessage(error, 'Unable to move deck'))
    }
  }

  const handleCopyDeckSubmit = async (payload: CopyDeckRequest) => {
    if (!activeDeck) {
      return
    }

    try {
      const result = await copyDeck({
        deckId: activeDeck.id,
        payload,
      })
      notificationService.success(result?.message ?? 'Deck copied successfully')
      await refreshStatsSafely()
      closeDeckModal()
    } catch (error) {
      notificationService.error(resolveErrorMessage(error, 'Unable to copy deck'))
    }
  }

  const handleDeleteDeckConfirm = async () => {
    if (!activeDeck) {
      return
    }

    try {
      const result = await deleteDeck(activeDeck.id)
      notificationService.success(result?.message ?? 'Deck deleted successfully')
      await refreshStatsSafely()
      closeDeckModal()
    } catch (error) {
      notificationService.error(resolveErrorMessage(error, 'Unable to delete deck'))
    }
  }

  const handleCardsChanged = async (deckId: string) => {
    await Promise.allSettled([refreshStatsSafely(), refetchDecks()])
    if (cardManagerDeckId === deckId && !isDecksLoading) {
      const latestDeck = decks.find(deck => deck.id === deckId)
      if (!latestDeck) {
        setCardManagerDeckId(null)
      }
    }
  }

  const createDeckLocationLabel =
    deckScope === 'selected' && selectedFolder ? selectedFolder.name : 'Root level'
  const createDeckFolderId =
    deckScope === 'selected' && selectedFolder ? selectedFolder.id : null
  const activeDeckLocationLabel = activeDeckFolder?.name ?? 'Root level'


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
              <FolderStatsPanel
                folder={selectedFolder ?? null}
                stats={folderStats ?? null}
                isLoading={isStatsLoading}
                isRefreshing={isStatsRefreshing}
                error={folderStatsErrorMessage}
                onRefresh={refreshStatsSafely}
                onRetry={() => {
                  void refetchFolderStats()
                }}
              />
              <FolderDetailsPanel folder={selectedFolder ?? null} allFolders={data?.list ?? []} />
              <DeckListPanel
                decks={decks}
                isLoading={isDecksLoading}
                isFetching={isDecksFetching}
                error={deckErrorMessage}
                scope={deckScope}
                hasFolderSelection={Boolean(selectedFolder)}
                currentFolderName={selectedFolder?.name ?? null}
                onScopeChange={setDeckScope}
                onCreateDeck={handleOpenCreateDeck}
                onRetry={() => {
                  void refetchDecks()
                }}
                onEditDeck={handleOpenEditDeck}
                onMoveDeck={handleOpenMoveDeck}
                onCopyDeck={handleOpenCopyDeck}
                onDeleteDeck={handleOpenDeleteDeck}
                onManageCards={handleOpenCardManager}
              />
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
      <CreateDeckModal
        open={deckModal === 'create'}
        locationLabel={createDeckLocationLabel}
        folderId={createDeckFolderId}
        isSubmitting={isCreatingDeck}
        onClose={closeDeckModal}
        onSubmit={handleCreateDeckSubmit}
      />
      <EditDeckModal
        open={deckModal === 'edit'}
        deck={activeDeck}
        locationLabel={activeDeckLocationLabel}
        isSubmitting={isUpdatingDeck}
        onClose={closeDeckModal}
        onSubmit={handleUpdateDeckSubmit}
      />
      <MoveDeckModal
        open={deckModal === 'move'}
        deckName={activeDeck?.name ?? null}
        currentFolderName={activeDeckLocationLabel}
        currentFolderId={activeDeck?.folderId ?? null}
        tree={tree}
        isSubmitting={isMovingDeck}
        onClose={closeDeckModal}
        onSubmit={handleMoveDeckSubmit}
      />
      <CopyDeckModal
        open={deckModal === 'copy'}
        deck={activeDeck}
        tree={tree}
        isSubmitting={isCopyingDeck}
        onClose={closeDeckModal}
        onSubmit={handleCopyDeckSubmit}
      />
      <DeleteDeckDialog
        open={deckModal === 'delete'}
        deck={activeDeck}
        isSubmitting={isDeletingDeck}
        onClose={closeDeckModal}
        onConfirm={handleDeleteDeckConfirm}
      />
      <CardManagerDialog
        open={Boolean(cardManagerDeckId)}
        deck={cardManagerDeck}
        onClose={handleCloseCardManager}
        onCardsChanged={handleCardsChanged}
      />
      <Footer />
    </>
  )
}

export default FolderManagementPage

