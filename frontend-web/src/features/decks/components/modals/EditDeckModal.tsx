import { useEffect, useMemo } from 'react'
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
import type { DeckDto, UpdateDeckRequest } from '@/api/types/deck.types'

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

interface EditDeckModalProps {
  open: boolean
  deck: DeckDto | null
  locationLabel: string
  isSubmitting: boolean
  onClose: () => void
  onSubmit: (payload: UpdateDeckRequest) => Promise<void>
}

const normalizeOptional = (value: string | undefined) => {
  const trimmed = value?.trim() ?? ''
  return trimmed.length > 0 ? trimmed : undefined
}

export const EditDeckModal = ({
  open,
  deck,
  locationLabel,
  isSubmitting,
  onClose,
  onSubmit,
}: EditDeckModalProps) => {
  const {
    register,
    handleSubmit,
    reset,
    watch,
    formState: { errors, isDirty },
  } = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: {
      name: deck?.name ?? '',
      description: deck?.description ?? '',
    },
  })

  useEffect(() => {
    if (open) {
      reset({
        name: deck?.name ?? '',
        description: deck?.description ?? '',
      })
    }
  }, [open, deck, reset])

  const nameValue = watch('name')
  const descriptionValue = watch('description')

  const nameLength = useMemo(() => nameValue?.trim().length ?? 0, [nameValue])
  const descriptionLength = useMemo(
    () => descriptionValue?.trim().length ?? 0,
    [descriptionValue]
  )

  const handleFormSubmit = async (values: FormValues) => {
    await onSubmit({
      name: values.name.trim(),
      description: normalizeOptional(values.description),
    })
  }

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle>Edit deck</DialogTitle>
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
              helperText={errors.name?.message ?? `${nameLength}/100 characters`}
              inputProps={{ maxLength: 100 }}
            />
            <TextField
              label="Description"
              multiline
              minRows={3}
              {...register('description')}
              error={Boolean(errors.description)}
              helperText={errors.description?.message ?? `${descriptionLength}/500 characters`}
              inputProps={{ maxLength: 500 }}
            />
          </Stack>
        </DialogContent>
        <DialogActions>
          <LoadingButton onClick={onClose} color="secondary" disabled={isSubmitting}>
            Cancel
          </LoadingButton>
          <LoadingButton loading={isSubmitting} type="submit" variant="contained" disabled={!isDirty}>
            Save changes
          </LoadingButton>
        </DialogActions>
      </form>
    </Dialog>
  )
}

export default EditDeckModal

