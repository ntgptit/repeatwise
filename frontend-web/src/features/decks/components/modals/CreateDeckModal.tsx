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
import type { CreateDeckRequest } from '@/api/types/deck.types'

const schema = z.object({
  name: z.string().trim().min(1, 'Deck name is required').max(100, 'Maximum 100 characters'),
  description: z
    .string()
    .trim()
    .max(500, 'Maximum 500 characters')
    .optional()
    .or(z.literal('')),
})

type FormValues = z.infer<typeof schema>

interface CreateDeckModalProps {
  open: boolean
  locationLabel: string
  folderId: string | null
  isSubmitting: boolean
  onClose: () => void
  onSubmit: (payload: CreateDeckRequest) => Promise<void>
}

const normalizeOptional = (value: string | undefined) => {
  const trimmed = value?.trim() ?? ''
  return trimmed.length > 0 ? trimmed : undefined
}

export const CreateDeckModal = ({
  open,
  locationLabel,
  folderId,
  isSubmitting,
  onClose,
  onSubmit,
}: CreateDeckModalProps) => {
  const {
    register,
    handleSubmit,
    reset,
    watch,
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

  const nameValue = watch('name')
  const descriptionValue = watch('description')

  const handleFormSubmit = async (values: FormValues) => {
    await onSubmit({
      name: values.name.trim(),
      description: normalizeOptional(values.description),
      folderId: folderId ?? null,
    })
  }

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle>Create new deck</DialogTitle>
      <form onSubmit={handleSubmit(handleFormSubmit)} noValidate>
        <DialogContent>
          <Stack spacing={2}>
            <Stack spacing={0.5}>
              <Typography variant="body2" color="text.secondary">
                Location
              </Typography>
              <Typography variant="body1" fontWeight={600}>
                {locationLabel}
              </Typography>
            </Stack>
            <TextField
              label="Deck name *"
              {...register('name')}
              error={Boolean(errors.name)}
              helperText={
                errors.name?.message ?? `${nameValue?.trim().length ?? 0}/100 characters`
              }
              inputProps={{ maxLength: 100 }}
            />
            <TextField
              label="Description"
              multiline
              minRows={3}
              {...register('description')}
              error={Boolean(errors.description)}
              helperText={
                errors.description?.message ??
                `${descriptionValue?.trim().length ?? 0}/500 characters`
              }
              inputProps={{ maxLength: 500 }}
            />
          </Stack>
        </DialogContent>
        <DialogActions>
          <LoadingButton onClick={onClose} color="secondary" disabled={isSubmitting}>
            Cancel
          </LoadingButton>
          <LoadingButton loading={isSubmitting} type="submit" variant="contained">
            Create deck
          </LoadingButton>
        </DialogActions>
      </form>
    </Dialog>
  )
}

export default CreateDeckModal

