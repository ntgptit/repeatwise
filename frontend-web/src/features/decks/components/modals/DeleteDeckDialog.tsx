import { LoadingButton } from '@mui/lab'
import {
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Stack,
  Typography,
} from '@mui/material'
import type { DeckDto } from '@/api/types/deck.types'

interface DeleteDeckDialogProps {
  open: boolean
  deck: DeckDto | null
  isSubmitting: boolean
  onClose: () => void
  onConfirm: () => Promise<void>
}

export const DeleteDeckDialog = ({ open, deck, isSubmitting, onClose, onConfirm }: DeleteDeckDialogProps) => {
  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="xs">
      <DialogTitle>Delete deck</DialogTitle>
      <DialogContent>
        <Stack spacing={2}>
          <Typography color="text.primary">
            Are you sure you want to delete <strong>{deck?.name ?? 'this deck'}</strong>? This will
            move the deck and its cards to trash for 30 days.
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Card count: {deck?.cardCount ?? 0}. You can restore the deck from trash within the grace
            period or it will be permanently deleted afterwards.
          </Typography>
        </Stack>
      </DialogContent>
      <DialogActions>
        <LoadingButton onClick={onClose} color="secondary" disabled={isSubmitting}>
          Cancel
        </LoadingButton>
        <LoadingButton
          loading={isSubmitting}
          onClick={onConfirm}
          color="error"
          variant="contained"
        >
          Delete deck
        </LoadingButton>
      </DialogActions>
    </Dialog>
  )
}

export default DeleteDeckDialog

