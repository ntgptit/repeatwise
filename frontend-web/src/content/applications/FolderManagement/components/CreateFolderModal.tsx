/**
 * CreateFolderModal Component
 * UC-007: Create Folder
 * Modal for creating a new folder with name and description
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
  Typography,
  Alert,
  CircularProgress,
} from '@mui/material'
import { useCreateFolder } from '@/hooks/useFolders'
import { notificationService } from '@/common/services/notification.service'

interface CreateFolderModalProps {
  open: boolean
  onClose: () => void
  parentId?: string | null
  parentName?: string
}

export const CreateFolderModal = ({
  open,
  onClose,
  parentId = null,
  parentName = 'Root',
}: CreateFolderModalProps) => {
  const [name, setName] = useState('')
  const [description, setDescription] = useState('')
  const [nameError, setNameError] = useState('')
  const [descriptionError, setDescriptionError] = useState('')

  const createFolderMutation = useCreateFolder()

  // Reset form when modal opens/closes
  useEffect(() => {
    if (open) {
      setName('')
      setDescription('')
      setNameError('')
      setDescriptionError('')
    }
  }, [open])

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

    // Validate all fields
    const isNameValid = validateName(name)
    const isDescriptionValid = validateDescription(description)

    if (!isNameValid || !isDescriptionValid) {
      return
    }

    try {
      await createFolderMutation.mutateAsync({
        name: name.trim(),
        description: description.trim() || null,
        parentId,
      })

      notificationService.success('Folder created successfully')
      onClose()
    } catch (error: any) {
      const errorMessage = error?.response?.data?.message || error?.message || 'Failed to create folder'
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
    createFolderMutation.isPending ||
    name.trim().length === 0 ||
    nameError.length > 0 ||
    descriptionError.length > 0

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <form onSubmit={handleSubmit}>
        <DialogTitle>Create New Folder</DialogTitle>
        <DialogContent>
          <Box sx={{ pt: 1 }}>
            {/* Parent Folder Info */}
            <Box sx={{ mb: 2 }}>
              <Typography variant="body2" color="text.secondary">
                Parent Folder: <strong>{parentName}</strong>
              </Typography>
            </Box>

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
              placeholder="e.g., IELTS Preparation"
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
              placeholder="e.g., Materials for IELTS exam preparation"
            />

            {/* Error Alert */}
            {createFolderMutation.isError && (
              <Alert severity="error" sx={{ mt: 2 }}>
                {createFolderMutation.error?.message || 'Failed to create folder'}
              </Alert>
            )}
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={onClose} disabled={createFolderMutation.isPending}>
            Cancel
          </Button>
          <Button
            type="submit"
            variant="contained"
            disabled={isSubmitDisabled}
            startIcon={createFolderMutation.isPending && <CircularProgress size={16} />}
          >
            Create Folder
          </Button>
        </DialogActions>
      </form>
    </Dialog>
  )
}

export default CreateFolderModal
