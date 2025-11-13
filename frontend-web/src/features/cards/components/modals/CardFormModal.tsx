import { useEffect, useMemo, type KeyboardEvent } from 'react'
import { z } from 'zod'
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

const schema = z.object({
  front: z
    .string()
    .trim()
    .min(1, 'Front is required')
    .max(5000, 'Maximum 5000 characters'),
  back: z
    .string()
    .trim()
    .min(1, 'Back is required')
    .max(5000, 'Maximum 5000 characters'),
})

type FormValues = z.infer<typeof schema>

const normalize = (value: string) => value.trim()

export type CardFormMode = 'create' | 'edit'

interface CardFormModalProps {
  open: boolean
  mode: CardFormMode
  initialValues?: { front: string; back: string } | null
  isSubmitting: boolean
  onClose: () => void
  onSubmit: (values: { front: string; back: string }) => Promise<void>
}

export const CardFormModal = ({
  open,
  mode,
  initialValues,
  isSubmitting,
  onClose,
  onSubmit,
}: CardFormModalProps) => {
  const {
    register,
    handleSubmit,
    reset,
    watch,
    formState: { errors },
  } = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: {
      front: initialValues?.front ?? '',
      back: initialValues?.back ?? '',
    },
  })

  useEffect(() => {
    if (open) {
      reset({
        front: initialValues?.front ?? '',
        back: initialValues?.back ?? '',
      })
    }
  }, [open, initialValues, reset])

  const frontValue = watch('front')
  const backValue = watch('back')

  const title = useMemo(() => (mode === 'create' ? 'Create new card' : 'Edit card'), [mode])
  const submitLabel = mode === 'create' ? 'Create card' : 'Save changes'

  const handleFormSubmit = async (values: FormValues) => {
    await onSubmit({
      front: normalize(values.front),
      back: normalize(values.back),
    })
  }

  const handleTextareaKeyDown = (event: KeyboardEvent<HTMLTextAreaElement>) => {
    if (event.ctrlKey && event.key === 'Enter') {
      event.preventDefault()
      void handleSubmit(handleFormSubmit)()
    }
  }

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="md">
      <DialogTitle>{title}</DialogTitle>
      <form onSubmit={handleSubmit(handleFormSubmit)} noValidate>
        <DialogContent>
          <Stack spacing={3}>
            <Stack spacing={1}>
              <Typography variant="body2" color="text.secondary">
                Prompt (Front) *
              </Typography>
              <TextField
                {...register('front')}
                multiline
                minRows={4}
                maxRows={12}
                placeholder="Enter the question or prompt shown during review"
                error={Boolean(errors.front)}
                helperText={
                  errors.front?.message ??
                  `${normalize(frontValue ?? '').length}/5000 characters`
                }
                inputProps={{ maxLength: 5000 }}
                onKeyDown={handleTextareaKeyDown}
              />
            </Stack>
            <Stack spacing={1}>
              <Typography variant="body2" color="text.secondary">
                Answer (Back) *
              </Typography>
              <TextField
                {...register('back')}
                multiline
                minRows={6}
                maxRows={16}
                placeholder="Enter the answer or explanation displayed after revealing the card"
                error={Boolean(errors.back)}
                helperText={
                  errors.back?.message ?? `${normalize(backValue ?? '').length}/5000 characters`
                }
                inputProps={{ maxLength: 5000 }}
                onKeyDown={handleTextareaKeyDown}
              />
            </Stack>
          </Stack>
        </DialogContent>
        <DialogActions>
          <LoadingButton onClick={onClose} color="secondary" disabled={isSubmitting}>
            Cancel
          </LoadingButton>
          <LoadingButton loading={isSubmitting} type="submit" variant="contained">
            {submitLabel}
          </LoadingButton>
        </DialogActions>
      </form>
    </Dialog>
  )
}

export default CardFormModal

