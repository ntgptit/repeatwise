import { LoadingButton } from '@mui/lab'
import { Dialog, DialogActions, DialogContent, DialogTitle, Stack, Typography } from '@mui/material'
import type { FolderTreeNode } from '@/api/types/folder.types'

interface DeleteFolderDialogProps {
  open: boolean
  folder: FolderTreeNode | null
  isSubmitting: boolean
  onClose: () => void
  onConfirm: () => Promise<void>
}

export const DeleteFolderDialog = ({
  open,
  folder,
  isSubmitting,
  onClose,
  onConfirm,
}: DeleteFolderDialogProps) => {
  const handleConfirm = async () => {
    await onConfirm()
  }

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle>Delete folder</DialogTitle>
      <DialogContent>
        <Stack spacing={2}>
          <Typography variant="body1">
            Are you sure you want to delete the folder <strong>{folder?.name}</strong>? This action
            applies to all of its subfolders and decks.
          </Typography>
          <Typography variant="body2" color="text.secondary">
            The system performs a soft delete for 30 days. Contact an administrator if you need to
            restore it.
          </Typography>
        </Stack>
      </DialogContent>
      <DialogActions>
        <LoadingButton onClick={onClose} color="secondary" disabled={isSubmitting}>
          Cancel
        </LoadingButton>
        <LoadingButton
          loading={isSubmitting}
          onClick={handleConfirm}
          variant="contained"
          color="error"
        >
          Delete folder
        </LoadingButton>
      </DialogActions>
    </Dialog>
  )
}

export default DeleteFolderDialog

