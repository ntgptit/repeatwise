import { useEffect, useMemo, useState } from 'react'
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
  Typography,
} from '@mui/material'
import type { FolderTreeNode, MoveFolderRequest } from '@/api/types/folder.types'

interface MoveFolderModalProps {
  open: boolean
  folder: FolderTreeNode | null
  tree: FolderTreeNode[]
  isSubmitting: boolean
  onClose: () => void
  onSubmit: (payload: MoveFolderRequest) => Promise<void>
}

interface OptionItem {
  id: string | null
  label: string
  depth: number
}

const collectDescendantIds = (node: FolderTreeNode | null): Set<string> => {
  const ids = new Set<string>()
  if (!node) {
    return ids
  }

  const walk = (current: FolderTreeNode) => {
    ids.add(current.id)
    current.children.forEach(walk)
  }

  walk(node)
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

export const MoveFolderModal = ({
  open,
  folder,
  tree,
  isSubmitting,
  onClose,
  onSubmit,
}: MoveFolderModalProps) => {
  const invalidIds = useMemo(() => collectDescendantIds(folder), [folder])
  const options = useMemo<OptionItem[]>(() => {
    return [{ id: null, label: 'Root folder', depth: 0 }, ...flattenTree(tree)]
  }, [tree])

  const [selected, setSelected] = useState<string | null>(() => folder?.parentFolderId ?? null)

  useEffect(() => {
    if (open) {
      setSelected(folder?.parentFolderId ?? null)
    }
  }, [open, folder])

  const handleSubmit = async () => {
    await onSubmit({
      targetParentFolderId: selected,
    })
  }

  const handleChange = (value: string) => {
    if (value === 'null') {
      setSelected(null)
      return
    }
    setSelected(value)
  }

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle>Move folder</DialogTitle>
      <DialogContent>
        <Stack spacing={2}>
          <Typography variant="body2" color="text.secondary">
            Choose the destination folder for <strong>{folder?.name}</strong>. You cannot move a folder
            into itself or one of its descendants.
          </Typography>
          <RadioGroup
            value={selected === null ? 'null' : selected ?? ''}
            onChange={(event) => handleChange(event.target.value)}
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
        <LoadingButton loading={isSubmitting} onClick={handleSubmit} variant="contained">
          Move
        </LoadingButton>
      </DialogActions>
    </Dialog>
  )
}

export default MoveFolderModal

