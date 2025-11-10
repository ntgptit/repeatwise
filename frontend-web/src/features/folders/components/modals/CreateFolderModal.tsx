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
import type { CreateFolderRequest, FolderTreeNode } from '@/api/types/folder.types'

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

interface CreateFolderModalProps {
  open: boolean
  parent: FolderTreeNode | null
  isSubmitting: boolean
  onClose: () => void
  onSubmit: (payload: CreateFolderRequest) => Promise<void>
}

const normalizeDescription = (value: string | undefined): string | undefined => {
  const trimmed = value?.trim() ?? ''
  return trimmed.length > 0 ? trimmed : undefined
}

export const CreateFolderModal = ({ open, parent, isSubmitting, onClose, onSubmit }: CreateFolderModalProps) => {
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: {
      name: '',
      description: '',
    },
  })

  useEffect(() => {
    if (open) {
      reset({
        name: '',
        description: '',
      })
    }
  }, [open, reset])

  const handleFormSubmit = async (values: FormValues) => {
    await onSubmit({
      name: values.name.trim(),
      description: normalizeDescription(values.description),
      parentFolderId: parent?.id ?? null,
    })
  }

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle>Create new folder</DialogTitle>
      <form onSubmit={handleSubmit(handleFormSubmit)} noValidate>
        <DialogContent>
          <Stack spacing={2}>
            <Stack spacing={0.5}>
              <Typography variant="body2" color="text.secondary">
                Parent folder
              </Typography>
              <Typography variant="body1" fontWeight={600}>
                {parent ? parent.name : 'Root'}
              </Typography>
            </Stack>
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
            Create folder
          </LoadingButton>
        </DialogActions>
      </form>
    </Dialog>
  )
}

export default CreateFolderModal

