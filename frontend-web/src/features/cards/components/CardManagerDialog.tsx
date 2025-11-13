import { useMemo, useState } from 'react'
import SearchIcon from '@mui/icons-material/Search'
import CloseIcon from '@mui/icons-material/Close'
import {
  Alert,
  Box,
  Button,
  Dialog,
  DialogContent,
  DialogTitle,
  IconButton,
  InputAdornment,
  Skeleton,
  Stack,
  TextField,
  Typography,
} from '@mui/material'
import type { DeckDto } from '@/api/types/deck.types'
import type { CardDto } from '@/api/types/card.types'
import { useCardsByDeck } from '@/features/cards/hooks/useCardQueries'
import {
  useCreateCard,
  useDeleteCard,
  useUpdateCard,
} from '@/features/cards/hooks/useCardMutations'
import { notificationService } from '@/common/services/notification.service'
import { CardListItem } from '@/features/cards/components/CardListItem'
import {
  CardDeleteDialog,
  CardFormModal,
  type CardFormMode,
} from '@/features/cards/components/modals'

type CardModalType = CardFormMode | 'delete' | null

const skeletonPlaceholders = ['card-skeleton-1', 'card-skeleton-2', 'card-skeleton-3'] as const

interface CardManagerDialogProps {
  open: boolean
  deck: DeckDto | null
  onClose: () => void
  onCardsChanged: (deckId: string) => void
}

const filterCards = (cards: CardDto[], keyword: string) => {
  const normalized = keyword.trim().toLowerCase()
  if (!normalized) {
    return cards
  }
  return cards.filter(card => {
    return (
      card.front.toLowerCase().includes(normalized) ||
      card.back.toLowerCase().includes(normalized)
    )
  })
}

export const CardManagerDialog = ({ open, deck, onClose, onCardsChanged }: CardManagerDialogProps) => {
  const deckId = deck?.id ?? null
  const [searchTerm, setSearchTerm] = useState('')
  const [activeCard, setActiveCard] = useState<CardDto | null>(null)
  const [cardModal, setCardModal] = useState<CardModalType>(null)

  const { data: cards = [], isLoading, isError, error, refetch, isFetching } = useCardsByDeck(deckId)
  const filteredCards = useMemo(() => filterCards(cards, searchTerm), [cards, searchTerm])

  const { mutateAsync: createCard, isPending: isCreating } = useCreateCard()
  const { mutateAsync: updateCard, isPending: isUpdating } = useUpdateCard()
  const { mutateAsync: deleteCard, isPending: isDeleting } = useDeleteCard()

  const handleClose = () => {
    setSearchTerm('')
    setActiveCard(null)
    setCardModal(null)
    onClose()
  }

  const handleOpenCreate = () => {
    setActiveCard(null)
    setCardModal('create')
  }

  const handleEditCard = (card: CardDto) => {
    setActiveCard(card)
    setCardModal('edit')
  }

  const handleDeleteCard = (card: CardDto) => {
    setActiveCard(card)
    setCardModal('delete')
  }

  const resolveErrorMessage = () => {
    if (error instanceof Error && error.message) {
      return error.message
    }
    return 'Failed to load cards. Please try again.'
  }

  const handleCreateSubmit = async (values: { front: string; back: string }) => {
    if (!deckId) {
      return
    }
    try {
      await createCard({
        deckId,
        front: values.front,
        back: values.back,
      })
      notificationService.success('Card created successfully')
      setCardModal(null)
      await refetch()
      onCardsChanged(deckId)
    } catch (err) {
      notificationService.error(
        err instanceof Error ? err.message : 'Unable to create card. Please try again.'
      )
    }
  }

  const handleUpdateSubmit = async (values: { front: string; back: string }) => {
    if (!deckId || !activeCard) {
      return
    }
    try {
      await updateCard({
        cardId: activeCard.id,
        deckId,
        payload: {
          front: values.front,
          back: values.back,
        },
      })
      notificationService.success('Card updated successfully')
      setCardModal(null)
      await refetch()
      onCardsChanged(deckId)
    } catch (err) {
      notificationService.error(
        err instanceof Error ? err.message : 'Unable to update card. Please try again.'
      )
    }
  }

  const handleDeleteConfirm = async () => {
    if (!deckId || !activeCard) {
      return
    }
    try {
      await deleteCard({
        cardId: activeCard.id,
        deckId,
      })
      notificationService.success('Card deleted successfully')
      setCardModal(null)
      await refetch()
      onCardsChanged(deckId)
    } catch (err) {
      notificationService.error(
        err instanceof Error ? err.message : 'Unable to delete card. Please try again.'
      )
    }
  }

  const deckName = deck?.name ?? 'Selected deck'

  return (
    <>
      <Dialog open={open} onClose={handleClose} fullWidth maxWidth="md">
        <DialogTitle sx={{ pr: 6 }}>
          Manage cards
          <IconButton
            onClick={handleClose}
            sx={{ position: 'absolute', right: 12, top: 12 }}
            aria-label="Close card manager"
          >
            <CloseIcon />
          </IconButton>
        </DialogTitle>
        <DialogContent dividers>
          <Stack spacing={3}>
            <Box
              sx={{
                borderRadius: 2,
                border: theme => `1px solid ${theme.palette.divider}`,
                p: 2,
                backgroundColor: theme => theme.palette.action.hover,
              }}
            >
              <Typography variant="subtitle2" color="text.secondary">
                Deck
              </Typography>
              <Typography variant="h6" fontWeight={700}>
                {deckName}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                {cards.length} cards in this deck. Use search to find specific prompts instantly.
              </Typography>
            </Box>

            <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} alignItems="stretch">
              <TextField
                fullWidth
                value={searchTerm}
                onChange={event => setSearchTerm(event.target.value)}
                placeholder="Search cards by front or back content"
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <SearchIcon fontSize="small" />
                    </InputAdornment>
                  ),
                }}
              />
              <Button variant="contained" onClick={handleOpenCreate} disableElevation>
                Create card
              </Button>
            </Stack>

            {isError ? (
              <Alert
                severity="error"
                action={
                  <Button size="small" color="inherit" onClick={() => refetch()}>
                    Retry
                  </Button>
                }
              >
                {resolveErrorMessage()}
              </Alert>
            ) : null}

            {isLoading ? (
              <Stack spacing={2}>
                {skeletonPlaceholders.map(key => (
                  <Skeleton key={key} variant="rectangular" height={160} sx={{ borderRadius: 2 }} />
                ))}
              </Stack>
            ) : null}

            {!isLoading && !isError && filteredCards.length === 0 ? (
              <Stack
                spacing={2}
                alignItems="center"
                justifyContent="center"
                sx={{
                  borderRadius: 2,
                  border: theme => `1px dashed ${theme.palette.divider}`,
                  py: 6,
                }}
              >
                <Typography variant="h6" fontWeight={600}>
                  {searchTerm ? 'No cards match your search' : 'No cards yet'}
                </Typography>
                <Typography variant="body2" color="text.secondary" maxWidth={420} textAlign="center">
                  {searchTerm
                    ? 'Try adjusting your keywords or clear the search input.'
                    : 'Create your first card to start building spaced repetition content for this deck.'}
                </Typography>
                {searchTerm ? (
                  <Button variant="text" onClick={() => setSearchTerm('')}>
                    Clear search
                  </Button>
                ) : (
                  <Button variant="contained" onClick={handleOpenCreate}>
                    Create card
                  </Button>
                )}
              </Stack>
            ) : null}

            {!isLoading && !isError ? (
              <Stack spacing={2}>
                {filteredCards.map((card, index) => (
                  <CardListItem
                    key={card.id}
                    card={card}
                    index={index}
                    onEdit={handleEditCard}
                    onDelete={handleDeleteCard}
                  />
                ))}
              </Stack>
            ) : null}

            {isFetching && !isLoading ? (
              <Typography variant="caption" color="text.secondary">
                Refreshing card list…
              </Typography>
            ) : null}
          </Stack>
        </DialogContent>
      </Dialog>

      <CardFormModal
        open={cardModal === 'create'}
        mode="create"
        initialValues={null}
        isSubmitting={isCreating}
        onClose={() => setCardModal(null)}
        onSubmit={handleCreateSubmit}
      />
      <CardFormModal
        open={cardModal === 'edit'}
        mode="edit"
        initialValues={
          activeCard
            ? {
                front: activeCard.front,
                back: activeCard.back,
              }
            : null
        }
        isSubmitting={isUpdating}
        onClose={() => setCardModal(null)}
        onSubmit={handleUpdateSubmit}
      />
      <CardDeleteDialog
        open={cardModal === 'delete'}
        cardFrontPreview={activeCard?.front ?? null}
        isSubmitting={isDeleting}
        onClose={() => setCardModal(null)}
        onConfirm={handleDeleteConfirm}
      />
    </>
  )
}

export default CardManagerDialog

