import {
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Stack,
  Typography,
} from '@mui/material'
import { LoadingButton } from '@mui/lab'

interface CardDeleteDialogProps {
  open: boolean
  cardFrontPreview: string | null
  isSubmitting: boolean
  onClose: () => void
  onConfirm: () => Promise<void>
}

export const CardDeleteDialog = ({
  open,
  cardFrontPreview,
  isSubmitting,
  onClose,
  onConfirm,
}: CardDeleteDialogProps) => {
  const handleConfirm = async () => {
    await onConfirm()
  }

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>Delete card?</DialogTitle>
      <DialogContent>
        <Stack spacing={2}>
          <Typography variant="body1">
            This card will be moved to trash and excluded from upcoming reviews. You can restore it
            within the 30-day grace period from the trash view.
          </Typography>
          {cardFrontPreview ? (
            <Stack spacing={0.5}>
              <Typography variant="body2" color="text.secondary">
                Card preview
              </Typography>
              <Typography
                variant="body2"
                sx={{
                  borderRadius: 1,
                  border: theme => `1px solid ${theme.palette.divider}`,
                  p: 1.5,
                  backgroundColor: theme => theme.palette.action.hover,
                }}
              >
                {cardFrontPreview}
              </Typography>
            </Stack>
          ) : null}
        </Stack>
      </DialogContent>
      <DialogActions>
        <LoadingButton onClick={onClose} color="secondary" disabled={isSubmitting}>
          Cancel
        </LoadingButton>
        <LoadingButton
          loading={isSubmitting}
          onClick={handleConfirm}
          color="error"
          variant="contained"
        >
          Delete card
        </LoadingButton>
      </DialogActions>
    </Dialog>
  )
}

export default CardDeleteDialog

