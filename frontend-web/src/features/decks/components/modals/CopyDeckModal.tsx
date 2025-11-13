import { useEffect, useMemo, useState } from 'react'
import { Controller, useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { LoadingButton } from '@mui/lab'
import {
  Box,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Divider,
  FormControlLabel,
  Radio,
  RadioGroup,
  Stack,
  Switch,
  TextField,
  Typography,
} from '@mui/material'
import { z } from 'zod'
import type { CopyDeckRequest, DeckDto } from '@/api/types/deck.types'
import type { FolderTreeNode } from '@/api/types/folder.types'

const schema = z.object({
  newName: z
    .string()
    .trim()
    .max(100, 'Maximum 100 characters')
    .optional()
    .or(z.literal('')),
  appendCopySuffix: z.boolean(),
})

type FormValues = z.infer<typeof schema>

interface OptionItem {
  id: string | null
  label: string
  depth: number
}

const flattenTree = (nodes: FolderTreeNode[], depth = 0): OptionItem[] => {
  const items: OptionItem[] = []
  nodes.forEach(node => {
    items.push({
      id: node.id,
      label: node.name,
      depth,
    })
    if (node.children.length > 0) {
      items.push(...flattenTree(node.children, depth + 1))
    }
  })
  return items
}

const formatLabel = (option: OptionItem) => {
  if (option.id === null) {
    return 'Root level'
  }
  return `${'— '.repeat(option.depth)}${option.label}`
}

interface CopyDeckModalProps {
  open: boolean
  deck: DeckDto | null
  tree: FolderTreeNode[]
  isSubmitting: boolean
  onClose: () => void
  onSubmit: (payload: CopyDeckRequest) => Promise<void>
}

const normalizeOptional = (value: string | undefined) => {
  const trimmed = value?.trim() ?? ''
  return trimmed.length > 0 ? trimmed : undefined
}

export const CopyDeckModal = ({ open, deck, tree, isSubmitting, onClose, onSubmit }: CopyDeckModalProps) => {
  const options = useMemo<OptionItem[]>(() => {
    return [{ id: null, label: 'Root level', depth: 0 }, ...flattenTree(tree)]
  }, [tree])

  const [selected, setSelected] = useState<string | null>(deck?.folderId ?? null)

  const {
    control,
    register,
    handleSubmit,
    reset,
    watch,
    formState: { errors },
  } = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: {
      newName: '',
      appendCopySuffix: true,
    },
  })

  useEffect(() => {
    if (open) {
      setSelected(deck?.folderId ?? null)
      reset({
        newName: '',
        appendCopySuffix: true,
      })
    }
  }, [open, deck, reset])

  const newNameValue = watch('newName')

  const handleFormSubmit = async (values: FormValues) => {
    await onSubmit({
      destinationFolderId: selected,
      newName: normalizeOptional(values.newName),
      appendCopySuffix: values.appendCopySuffix,
    })
  }

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle>Copy deck</DialogTitle>
      <form onSubmit={handleSubmit(handleFormSubmit)} noValidate>
        <DialogContent>
          <Stack spacing={3}>
            <Box>
              <Typography variant="subtitle2" color="text.secondary">
                Source deck
              </Typography>
              <Typography variant="h6" fontWeight={700}>
                {deck?.name ?? '—'}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                {deck?.cardCount ?? 0} cards will be duplicated into the new deck.
              </Typography>
            </Box>

            <Divider />

            <Stack spacing={2}>
              <Typography variant="subtitle2" fontWeight={600}>
                Destination
              </Typography>
              <RadioGroup
                value={selected === null ? 'null' : selected ?? ''}
                onChange={event => {
                  const { value } = event.target
                  setSelected(value === 'null' ? null : value)
                }}
              >
                {options.map(option => (
                  <FormControlLabel
                    key={option.id ?? 'root'}
                    value={option.id === null ? 'null' : option.id}
                    control={<Radio size="small" />}
                    label={<Typography variant="body2">{formatLabel(option)}</Typography>}
                  />
                ))}
              </RadioGroup>
            </Stack>

            <Stack spacing={1.5}>
              <TextField
                label="New deck name (optional)"
                {...register('newName')}
                error={Boolean(errors.newName)}
                helperText={
                  errors.newName?.message ?? `${newNameValue?.trim().length ?? 0}/100 characters`
                }
                inputProps={{ maxLength: 100 }}
              />
              <Controller
                control={control}
                name="appendCopySuffix"
                render={({ field }) => (
                  <FormControlLabel
                    control={<Switch color="primary" checked={field.value} onChange={event => field.onChange(event.target.checked)} />}
                    label="Automatically append “(copy)” when the name already exists"
                  />
                )}
              />
            </Stack>
          </Stack>
        </DialogContent>
        <DialogActions>
          <LoadingButton onClick={onClose} color="secondary" disabled={isSubmitting}>
            Cancel
          </LoadingButton>
          <LoadingButton loading={isSubmitting} type="submit" variant="contained">
            Copy deck
          </LoadingButton>
        </DialogActions>
      </form>
    </Dialog>
  )
}

export default CopyDeckModal

