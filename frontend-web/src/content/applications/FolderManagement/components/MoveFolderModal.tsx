/**
 * MoveFolderModal Component
 * UC-009: Move Folder
 * Modal for moving a folder to a different parent location
 */

import { useState, useEffect, useMemo } from 'react'
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
  RadioGroup,
  FormControlLabel,
  Radio,
  List,
  ListItem,
  ListItemButton,
  ListItemText,
} from '@mui/material'
import { Folder as FolderIcon } from '@mui/icons-material'
import { useMoveFolder, useFolders } from '@/hooks/useFolders'
import { notificationService } from '@/common/services/notification.service'
import type { FolderResponse } from '@/api/types/folder.types'

interface MoveFolderModalProps {
  open: boolean
  onClose: () => void
  folder: FolderResponse | null
}

export const MoveFolderModal = ({ open, onClose, folder }: MoveFolderModalProps) => {
  const [selectedDestinationId, setSelectedDestinationId] = useState<string | null>(null)

  const moveFolderMutation = useMoveFolder()
  const { data: allFolders = [], isLoading: isLoadingFolders } = useFolders()

  // Reset selection when modal opens
  useEffect(() => {
    if (open) {
      setSelectedDestinationId(null)
    }
  }, [open])

  // Filter out invalid destinations (self and descendants)
  const validDestinations = useMemo(() => {
    if (!folder) return []

    const isDescendant = (folderId: string, ancestorPath: string): boolean => {
      const targetFolder = allFolders.find((f) => f.id === folderId)
      return targetFolder?.path.startsWith(ancestorPath) || false
    }

    return allFolders.filter((f) => {
      // Exclude the folder itself
      if (f.id === folder.id) return false
      // Exclude descendants of the folder
      if (isDescendant(f.id, folder.path)) return false
      return true
    })
  }, [folder, allFolders])

  // Build tree structure for display
  const buildTree = (parentId: string | null = null, level = 0): React.ReactNode[] => {
    return validDestinations
      .filter((f) => f.parentId === parentId)
      .map((f) => (
        <Box key={f.id}>
          <ListItem disablePadding sx={{ pl: level * 3 }}>
            <ListItemButton
              selected={selectedDestinationId === f.id}
              onClick={() => setSelectedDestinationId(f.id)}
            >
              <FolderIcon sx={{ mr: 1, color: 'action.active' }} fontSize="small" />
              <ListItemText primary={f.name} />
            </ListItemButton>
          </ListItem>
          {buildTree(f.id, level + 1)}
        </Box>
      ))
  }

  const handleSubmit = async () => {
    if (!folder) return

    // Check if moving to same parent (no-op)
    if (selectedDestinationId === folder.parentId) {
      notificationService.info('Folder is already in this location')
      onClose()
      return
    }

    try {
      await moveFolderMutation.mutateAsync({
        folderId: folder.id,
        request: {
          targetParentFolderId: selectedDestinationId,
        },
      })

      notificationService.success('Folder moved successfully')
      onClose()
    } catch (error: any) {
      const errorMessage = error?.response?.data?.message || error?.message || 'Failed to move folder'
      notificationService.error(errorMessage)
    }
  }

  const isSubmitDisabled = moveFolderMutation.isPending

  if (!folder) return null

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>Move Folder</DialogTitle>
      <DialogContent>
        <Box sx={{ pt: 1 }}>
          {/* Current Location */}
          <Box sx={{ mb: 2 }}>
            <Typography variant="body2" color="text.secondary">
              Moving: <strong>{folder.name}</strong>
            </Typography>
          </Box>

          {/* Destination Picker */}
          <Typography variant="subtitle2" gutterBottom>
            Select destination:
          </Typography>

          {isLoadingFolders ? (
            <Box display="flex" justifyContent="center" p={3}>
              <CircularProgress />
            </Box>
          ) : (
            <Box
              sx={{
                border: 1,
                borderColor: 'divider',
                borderRadius: 1,
                maxHeight: 400,
                overflow: 'auto',
              }}
            >
              <List dense>
                {/* Root option */}
                <ListItem disablePadding>
                  <ListItemButton
                    selected={selectedDestinationId === null}
                    onClick={() => setSelectedDestinationId(null)}
                  >
                    <FolderIcon sx={{ mr: 1, color: 'primary.main' }} fontSize="small" />
                    <ListItemText primary="Root" />
                  </ListItemButton>
                </ListItem>

                {/* Folder tree */}
                {buildTree()}
              </List>

              {validDestinations.length === 0 && (
                <Box p={2} textAlign="center">
                  <Typography variant="body2" color="text.secondary">
                    No available destinations
                  </Typography>
                </Box>
              )}
            </Box>
          )}

          {/* Preview */}
          {selectedDestinationId !== undefined && (
            <Box sx={{ mt: 2, p: 1.5, bgcolor: 'action.hover', borderRadius: 1 }}>
              <Typography variant="body2">
                New location:{' '}
                <strong>
                  {selectedDestinationId === null
                    ? 'Root'
                    : validDestinations.find((f) => f.id === selectedDestinationId)?.name}
                  {' > '}
                  {folder.name}
                </strong>
              </Typography>
            </Box>
          )}

          {/* Error Alert */}
          {moveFolderMutation.isError && (
            <Alert severity="error" sx={{ mt: 2 }}>
              {moveFolderMutation.error?.message || 'Failed to move folder'}
            </Alert>
          )}
        </Box>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} disabled={moveFolderMutation.isPending}>
          Cancel
        </Button>
        <Button
          variant="contained"
          onClick={handleSubmit}
          disabled={isSubmitDisabled}
          startIcon={moveFolderMutation.isPending && <CircularProgress size={16} />}
        >
          Move Here
        </Button>
      </DialogActions>
    </Dialog>
  )
}

export default MoveFolderModal
