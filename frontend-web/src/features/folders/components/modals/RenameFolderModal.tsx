import { useEffect } from 'react'
import { zodResolver } from '@hookform/resolvers/zod'
import { LoadingButton } from '@mui/lab'
import {
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Stack,
  TextField,
  Typography,
} from '@mui/material'
import { useForm } from 'react-hook-form'
import { z } from 'zod'
import type { FolderTreeNode, UpdateFolderRequest } from '@/api/types/folder.types'

const schema = z.object({
  name: z.string().trim().min(1, 'Folder name is required').max(100, 'Maximum 100 characters'),
  description: z
    .string()
    .trim()
    .max(500, 'Maximum 500 characters')
    .optional()
    .or(z.literal('')),
})

type FormValues = z.infer<typeof schema>

interface RenameFolderModalProps {
  open: boolean
  folder: FolderTreeNode | null
  isSubmitting: boolean
  onClose: () => void
  onSubmit: (payload: UpdateFolderRequest) => Promise<void>
}

const normalizeDescription = (value: string | undefined): string | null => {
  const trimmed = value?.trim() ?? ''
  return trimmed.length > 0 ? trimmed : null
}

export const RenameFolderModal = ({
  open,
  folder,
  isSubmitting,
  onClose,
  onSubmit,
}: RenameFolderModalProps) => {
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<FormValues>({
    resolver: zodResolver(schema),
  })

  useEffect(() => {
    if (open) {
      reset({
        name: folder?.name ?? '',
        description: folder?.description ?? '',
      })
    }
  }, [open, folder, reset])

  const handleFormSubmit = async (values: FormValues) => {
    await onSubmit({
      name: values.name.trim(),
      description: normalizeDescription(values.description),
    })
  }

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle>Update folder</DialogTitle>
      <form onSubmit={handleSubmit(handleFormSubmit)} noValidate>
        <DialogContent>
          <Stack spacing={2}>
            <Typography variant="body2" color="text.secondary">
              You are editing the folder <strong>{folder?.name}</strong>
            </Typography>
            <TextField
              label="Folder name *"
              {...register('name')}
              error={Boolean(errors.name)}
              helperText={errors.name?.message}
              inputProps={{ maxLength: 100 }}
            />
            <TextField
              label="Description"
              multiline
              minRows={3}
              {...register('description')}
              error={Boolean(errors.description)}
              helperText={errors.description?.message || 'Optional – maximum 500 characters'}
              inputProps={{ maxLength: 500 }}
            />
          </Stack>
        </DialogContent>
        <DialogActions>
          <LoadingButton onClick={onClose} color="secondary" disabled={isSubmitting}>
            Cancel
          </LoadingButton>
          <LoadingButton loading={isSubmitting} type="submit" variant="contained">
            Save changes
          </LoadingButton>
        </DialogActions>
      </form>
    </Dialog>
  )
}

export default RenameFolderModal

