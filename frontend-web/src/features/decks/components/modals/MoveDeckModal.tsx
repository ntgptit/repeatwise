import { useEffect, useMemo, useState } from 'react'
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
  Typography,
} from '@mui/material'
import type { MoveDeckRequest } from '@/api/types/deck.types'
import type { FolderTreeNode } from '@/api/types/folder.types'

interface MoveDeckModalProps {
  open: boolean
  deckName: string | null
  currentFolderName: string | null
  currentFolderId: string | null
  tree: FolderTreeNode[]
  isSubmitting: boolean
  onClose: () => void
  onSubmit: (payload: MoveDeckRequest) => Promise<void>
}

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

export const MoveDeckModal = ({
  open,
  deckName,
  currentFolderName,
  currentFolderId,
  tree,
  isSubmitting,
  onClose,
  onSubmit,
}: MoveDeckModalProps) => {
  const options = useMemo<OptionItem[]>(() => {
    return [{ id: null, label: 'Root level', depth: 0 }, ...flattenTree(tree)]
  }, [tree])

  const [selected, setSelected] = useState<string | null>(currentFolderId ?? null)

  useEffect(() => {
    if (open) {
      setSelected(currentFolderId ?? null)
    }
  }, [open, currentFolderId])

  const canSubmit = selected !== currentFolderId

  const handleSubmit = async () => {
    if (!canSubmit) {
      return
    }
    await onSubmit({
      targetFolderId: selected,
    })
  }

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle>Move deck</DialogTitle>
      <DialogContent>
        <Stack spacing={3}>
          <Box>
            <Typography variant="subtitle2" color="text.secondary">
              Deck
            </Typography>
            <Typography variant="h6" fontWeight={700}>
              {deckName ?? '—'}
            </Typography>
          </Box>
          <Box>
            <Typography variant="subtitle2" color="text.secondary">
              Current location
            </Typography>
            <Typography variant="body1">
              {currentFolderName ?? 'Root level'}
            </Typography>
          </Box>
          <Divider />
          <Stack spacing={1}>
            <Typography variant="subtitle2" fontWeight={600}>
              Choose destination
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
                  label={
                    <Typography
                      variant="body2"
                      color={
                        option.id === currentFolderId ? 'text.primary' : 'text.secondary'
                      }
                    >
                      {formatLabel(option)}
                    </Typography>
                  }
                />
              ))}
            </RadioGroup>
            {!canSubmit ? (
              <Typography variant="caption" color="text.secondary">
                Select a different location to enable the move action.
              </Typography>
            ) : null}
          </Stack>
        </Stack>
      </DialogContent>
      <DialogActions>
        <LoadingButton onClick={onClose} color="secondary" disabled={isSubmitting}>
          Cancel
        </LoadingButton>
        <LoadingButton
          loading={isSubmitting}
          onClick={handleSubmit}
          variant="contained"
          disabled={!canSubmit}
        >
          Move deck
        </LoadingButton>
      </DialogActions>
    </Dialog>
  )
}

export default MoveDeckModal

