/**
 * DeleteFolderDialog Component
 * UC-011: Delete Folder
 * Confirmation dialog for deleting a folder (soft delete)
 */

import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Box,
  Typography,
  Alert,
  CircularProgress,
  List,
  ListItem,
  ListItemText,
} from '@mui/material'
import { Warning as WarningIcon } from '@mui/icons-material'
import { useDeleteFolder } from '@/hooks/useFolders'
import { notificationService } from '@/common/services/notification.service'
import type { FolderResponse } from '@/api/types/folder.types'

interface DeleteFolderDialogProps {
  open: boolean
  onClose: () => void
  folder: FolderResponse | null
}

export const DeleteFolderDialog = ({ open, onClose, folder }: DeleteFolderDialogProps) => {
  const deleteFolderMutation = useDeleteFolder()

  const handleDelete = async () => {
    if (!folder) return

    try {
      const response = await deleteFolderMutation.mutateAsync(folder.id)

      notificationService.success(
        `Folder deleted successfully. ${response.deletedFolders} folders and ${response.deletedDecks} decks removed.`
      )
      onClose()
    } catch (error: any) {
      const errorMessage = error?.response?.data?.message || error?.message || 'Failed to delete folder'
      notificationService.error(errorMessage)
    }
  }

  if (!folder) return null

  // Calculate approximate item counts (you may want to get this from API)
  const estimatedSubfolders = folder.children?.length || 0
  const estimatedDecks = folder.deckCount || 0

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
        <WarningIcon color="warning" />
        Delete Folder?
      </DialogTitle>
      <DialogContent>
        <Box sx={{ pt: 1 }}>
          <Typography variant="body1" gutterBottom>
            Are you sure you want to delete:
          </Typography>

          <Box
            sx={{
              p: 2,
              my: 2,
              bgcolor: 'error.lighter',
              borderRadius: 1,
              border: 1,
              borderColor: 'error.light',
            }}
          >
            <Typography variant="h6" color="error.main">
              {folder.name}
            </Typography>
            {folder.description && (
              <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5 }}>
                {folder.description}
              </Typography>
            )}
          </Box>

          {/* Item counts */}
          {(estimatedSubfolders > 0 || estimatedDecks > 0) && (
            <Box sx={{ mb: 2 }}>
              <Typography variant="subtitle2" gutterBottom>
                This folder contains:
              </Typography>
              <List dense disablePadding>
                {estimatedSubfolders > 0 && (
                  <ListItem disablePadding>
                    <ListItemText
                      primary={`• ${estimatedSubfolders} subfolder${estimatedSubfolders > 1 ? 's' : ''}`}
                    />
                  </ListItem>
                )}
                {estimatedDecks > 0 && (
                  <ListItem disablePadding>
                    <ListItemText primary={`• ${estimatedDecks} deck${estimatedDecks > 1 ? 's' : ''}`} />
                  </ListItem>
                )}
              </List>
            </Box>
          )}

          {/* Recovery info */}
          <Alert severity="info" sx={{ mt: 2 }}>
            You can restore this folder within 30 days from the Trash.
          </Alert>

          {/* Error Alert */}
          {deleteFolderMutation.isError && (
            <Alert severity="error" sx={{ mt: 2 }}>
              {deleteFolderMutation.error?.message || 'Failed to delete folder'}
            </Alert>
          )}
        </Box>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} disabled={deleteFolderMutation.isPending}>
          Cancel
        </Button>
        <Button
          variant="contained"
          color="error"
          onClick={handleDelete}
          disabled={deleteFolderMutation.isPending}
          startIcon={deleteFolderMutation.isPending && <CircularProgress size={16} />}
        >
          Delete
        </Button>
      </DialogActions>
    </Dialog>
  )
}

export default DeleteFolderDialog
