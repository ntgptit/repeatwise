/**
 * CopyFolderModal Component
 * UC-010: Copy Folder
 * Modal for copying a folder and its subtree to a new location
 */

import { useState, useEffect, useMemo } from 'react'
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Button,
  Box,
  Typography,
  Alert,
  CircularProgress,
  List,
  ListItem,
  ListItemButton,
  ListItemText,
} from '@mui/material'
import { Folder as FolderIcon, Info as InfoIcon } from '@mui/icons-material'
import { useCopyFolder, useFolders } from '@/hooks/useFolders'
import { notificationService } from '@/common/services/notification.service'
import type { FolderResponse } from '@/api/types/folder.types'

interface CopyFolderModalProps {
  open: boolean
  onClose: () => void
  folder: FolderResponse | null
}

export const CopyFolderModal = ({ open, onClose, folder }: CopyFolderModalProps) => {
  const [newName, setNewName] = useState('')
  const [selectedDestinationId, setSelectedDestinationId] = useState<string | null>(null)
  const [nameError, setNameError] = useState('')

  const copyFolderMutation = useCopyFolder()
  const { data: allFolders = [], isLoading: isLoadingFolders } = useFolders()

  // Initialize form when modal opens
  useEffect(() => {
    if (open && folder) {
      setNewName(`${folder.name} (copy)`)
      setSelectedDestinationId(null)
      setNameError('')
    }
  }, [open, folder])

  const validateName = (value: string): boolean => {
    const trimmed = value.trim()
    if (trimmed.length === 0) {
      setNameError('Folder name is required')
      return false
    }
    if (trimmed.length > 100) {
      setNameError('Folder name must be 100 characters or less')
      return false
    }
    setNameError('')
    return true
  }

  const handleNameChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const value = event.target.value
    setNewName(value)
    validateName(value)
  }

  // Build tree structure for destination picker
  const buildTree = (parentId: string | null = null, level = 0): React.ReactNode[] => {
    return allFolders
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

    // Validate name
    if (!validateName(newName)) {
      return
    }

    try {
      await copyFolderMutation.mutateAsync({
        folderId: folder.id,
        request: {
          destinationFolderId: selectedDestinationId,
          newName: newName.trim(),
        },
      })

      notificationService.success('Folder copied successfully')
      onClose()
    } catch (error: any) {
      const errorMessage = error?.response?.data?.message || error?.message || 'Failed to copy folder'
      notificationService.error(errorMessage)
    }
  }

  const isSubmitDisabled =
    copyFolderMutation.isPending || newName.trim().length === 0 || nameError.length > 0

  if (!folder) return null

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>Copy Folder</DialogTitle>
      <DialogContent>
        <Box sx={{ pt: 1 }}>
          {/* Source Folder Info */}
          <Box sx={{ mb: 2 }}>
            <Typography variant="body2" color="text.secondary">
              Source: <strong>{folder.name}</strong>
            </Typography>
          </Box>

          {/* New Name Field */}
          <TextField
            label="New name"
            value={newName}
            onChange={handleNameChange}
            error={!!nameError}
            helperText={nameError || `${newName.length}/100`}
            required
            fullWidth
            margin="normal"
          />

          {/* Destination Picker */}
          <Typography variant="subtitle2" gutterBottom sx={{ mt: 2 }}>
            Destination:
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
                maxHeight: 300,
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
            </Box>
          )}

          {/* Info Alert */}
          <Alert severity="info" icon={<InfoIcon />} sx={{ mt: 2 }}>
            This will copy the folder and all its contents (subfolders and decks).
          </Alert>

          {/* Error Alert */}
          {copyFolderMutation.isError && (
            <Alert severity="error" sx={{ mt: 2 }}>
              {copyFolderMutation.error?.message || 'Failed to copy folder'}
            </Alert>
          )}
        </Box>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} disabled={copyFolderMutation.isPending}>
          Cancel
        </Button>
        <Button
          variant="contained"
          onClick={handleSubmit}
          disabled={isSubmitDisabled}
          startIcon={copyFolderMutation.isPending && <CircularProgress size={16} />}
        >
          Copy Here
        </Button>
      </DialogActions>
    </Dialog>
  )
}

export default CopyFolderModal
