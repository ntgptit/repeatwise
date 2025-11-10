import { useEffect, useMemo, useState } from 'react'
import { zodResolver } from '@hookform/resolvers/zod'
import { LoadingButton } from '@mui/lab'
import {
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControlLabel,
  Radio,
  RadioGroup,
  Stack,
  TextField,
  Typography,
} from '@mui/material'
import { useForm } from 'react-hook-form'
import { z } from 'zod'
import type { CopyFolderRequest, FolderTreeNode } from '@/api/types/folder.types'

interface OptionItem {
  id: string | null
  label: string
  depth: number
}

const schema = z.object({
  newName: z
    .string()
    .trim()
    .max(100, 'Maximum 100 characters')
    .optional()
    .or(z.literal('')),
})

type FormValues = z.infer<typeof schema>

const collectInvalidIds = (folder: FolderTreeNode | null): Set<string> => {
  const ids = new Set<string>()
  if (!folder) {
    return ids
  }

  const walk = (node: FolderTreeNode) => {
    ids.add(node.id)
    node.children.forEach(walk)
  }

  walk(folder)
  return ids
}

const flattenTree = (nodes: FolderTreeNode[], depth = 0): OptionItem[] => {
  const items: OptionItem[] = []
  nodes.forEach((node) => {
    items.push({
      id: node.id,
      label: `${'— '.repeat(depth)}${node.name}`,
      depth,
    })
    items.push(...flattenTree(node.children, depth + 1))
  })
  return items
}

interface CopyFolderModalProps {
  open: boolean
  folder: FolderTreeNode | null
  tree: FolderTreeNode[]
  isSubmitting: boolean
  onClose: () => void
  onSubmit: (payload: CopyFolderRequest) => Promise<void>
}

export const CopyFolderModal = ({
  open,
  folder,
  tree,
  isSubmitting,
  onClose,
  onSubmit,
}: CopyFolderModalProps) => {
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<FormValues>({
    resolver: zodResolver(schema),
  })

  const [destination, setDestination] = useState<string | null>(null)
  const invalidIds = useMemo(() => collectInvalidIds(folder), [folder])
  const options = useMemo<OptionItem[]>(() => {
    return [{ id: null, label: 'Root folder', depth: 0 }, ...flattenTree(tree)]
  }, [tree])

  useEffect(() => {
    if (open) {
      reset({
        newName: '',
      })
      setDestination(folder?.parentFolderId ?? null)
    }
  }, [open, folder, reset])

  const onRadioChange = (value: string) => {
    if (value === 'null') {
      setDestination(null)
      return
    }
    setDestination(value)
  }

  const handleFormSubmit = async (values: FormValues) => {
    const newName = values.newName?.trim()

    await onSubmit({
      destinationFolderId: destination,
      newName: newName ? newName : undefined,
      renamePolicy: 'APPEND_COPY_SUFFIX',
    })
  }

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle>Copy folder</DialogTitle>
      <form onSubmit={handleSubmit(handleFormSubmit)} noValidate>
        <DialogContent>
          <Stack spacing={2}>
            <Typography variant="body2" color="text.secondary">
              Select where to place the copy of <strong>{folder?.name}</strong>. Provide a new name or
              leave it blank to let the system append a suffix automatically.
            </Typography>
            <TextField
              label="New name (optional)"
              {...register('newName')}
              error={Boolean(errors.newName)}
              helperText={errors.newName?.message || 'Leave blank to keep the original name'}
              inputProps={{ maxLength: 100 }}
            />
            <RadioGroup
              value={destination === null ? 'null' : destination ?? ''}
              onChange={(event) => onRadioChange(event.target.value)}
            >
              {options.map((option) => {
                const disabled = option.id ? invalidIds.has(option.id) : false
                return (
                  <FormControlLabel
                    key={option.id ?? 'root'}
                    value={option.id === null ? 'null' : option.id}
                    control={<Radio size="small" />}
                    disabled={disabled}
                    sx={{ pl: option.depth * 2 }}
                    label={disabled ? `${option.label} (not allowed)` : option.label}
                  />
                )
              })}
            </RadioGroup>
          </Stack>
        </DialogContent>
        <DialogActions>
          <LoadingButton onClick={onClose} color="secondary" disabled={isSubmitting}>
            Cancel
          </LoadingButton>
          <LoadingButton loading={isSubmitting} type="submit" variant="contained">
            Copy
          </LoadingButton>
        </DialogActions>
      </form>
    </Dialog>
  )
}

export default CopyFolderModal

