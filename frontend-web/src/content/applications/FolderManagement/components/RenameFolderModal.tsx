/**
 * RenameFolderModal Component
 * UC-008: Rename Folder
 * Modal for renaming a folder (updating name and/or description)
 */

import { useState, useEffect } from 'react'
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Button,
  Box,
  Alert,
  CircularProgress,
} from '@mui/material'
import { useUpdateFolder } from '@/hooks/useFolders'
import { notificationService } from '@/common/services/notification.service'
import type { FolderResponse } from '@/api/types/folder.types'

interface RenameFolderModalProps {
  open: boolean
  onClose: () => void
  folder: FolderResponse | null
}

export const RenameFolderModal = ({ open, onClose, folder }: RenameFolderModalProps) => {
  const [name, setName] = useState('')
  const [description, setDescription] = useState('')
  const [nameError, setNameError] = useState('')
  const [descriptionError, setDescriptionError] = useState('')

  const updateFolderMutation = useUpdateFolder()

  // Initialize form with folder data when modal opens
  useEffect(() => {
    if (open && folder) {
      setName(folder.name)
      setDescription(folder.description || '')
      setNameError('')
      setDescriptionError('')
    }
  }, [open, folder])

  const validateName = (value: string): boolean => {
    const trimmed = value.trim()
    if (trimmed.length === 0) {
      setNameError('Folder name cannot be empty')
      return false
    }
    if (trimmed.length > 100) {
      setNameError('Folder name must be 100 characters or less')
      return false
    }
    setNameError('')
    return true
  }

  const validateDescription = (value: string): boolean => {
    if (value.length > 500) {
      setDescriptionError('Description must be 500 characters or less')
      return false
    }
    setDescriptionError('')
    return true
  }

  const handleNameChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const value = event.target.value
    setName(value)
    validateName(value)
  }

  const handleDescriptionChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const value = event.target.value
    setDescription(value)
    validateDescription(value)
  }

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault()

    if (!folder) return

    // Validate all fields
    const isNameValid = validateName(name)
    const isDescriptionValid = validateDescription(description)

    if (!isNameValid || !isDescriptionValid) {
      return
    }

    // Check if anything changed
    const nameChanged = name.trim() !== folder.name
    const descriptionChanged = description.trim() !== (folder.description || '')

    if (!nameChanged && !descriptionChanged) {
      notificationService.info('No changes made')
      onClose()
      return
    }

    try {
      await updateFolderMutation.mutateAsync({
        folderId: folder.id,
        request: {
          name: nameChanged ? name.trim() : undefined,
          description: descriptionChanged ? description.trim() || null : undefined,
        },
      })

      notificationService.success('Folder renamed successfully')
      onClose()
    } catch (error: any) {
      const errorMessage = error?.response?.data?.message || error?.message || 'Failed to rename folder'
      notificationService.error(errorMessage)
    }
  }

  const handleKeyDown = (event: React.KeyboardEvent) => {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault()
      handleSubmit(event as any)
    } else if (event.key === 'Escape') {
      onClose()
    }
  }

  const isSubmitDisabled =
    updateFolderMutation.isPending ||
    name.trim().length === 0 ||
    nameError.length > 0 ||
    descriptionError.length > 0

  if (!folder) return null

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <form onSubmit={handleSubmit}>
        <DialogTitle>Rename Folder</DialogTitle>
        <DialogContent>
          <Box sx={{ pt: 1 }}>
            {/* Folder Name Field */}
            <TextField
              label="Folder Name"
              value={name}
              onChange={handleNameChange}
              onKeyDown={handleKeyDown}
              error={!!nameError}
              helperText={nameError || `${name.length}/100`}
              required
              fullWidth
              autoFocus
              margin="normal"
            />

            {/* Description Field */}
            <TextField
              label="Description (Optional)"
              value={description}
              onChange={handleDescriptionChange}
              onKeyDown={handleKeyDown}
              error={!!descriptionError}
              helperText={descriptionError || `${description.length}/500`}
              fullWidth
              multiline
              rows={3}
              margin="normal"
            />

            {/* Error Alert */}
            {updateFolderMutation.isError && (
              <Alert severity="error" sx={{ mt: 2 }}>
                {updateFolderMutation.error?.message || 'Failed to rename folder'}
              </Alert>
            )}
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={onClose} disabled={updateFolderMutation.isPending}>
            Cancel
          </Button>
          <Button
            type="submit"
            variant="contained"
            disabled={isSubmitDisabled}
            startIcon={updateFolderMutation.isPending && <CircularProgress size={16} />}
          >
            Save Changes
          </Button>
        </DialogActions>
      </form>
    </Dialog>
  )
}

export default RenameFolderModal
