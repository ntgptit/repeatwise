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

const formatLabel = (name: string, depth: number) => {
  if (depth === 0) {
    return name
  }
  return `${'  '.repeat(depth)}• ${name}`
}

const flattenTree = (nodes: FolderTreeNode[], depth = 0): OptionItem[] => {
  const items: OptionItem[] = []
  nodes.forEach((node) => {
    items.push({
      id: node.id,
      label: node.name,
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
      <DialogTitle sx={{ pb: 1 }}>
        <Typography variant="h6" fontWeight={700}>
          Move folder
        </Typography>
        <Typography variant="body2" color="text.secondary">
          Select the destination for <strong>{folder?.name}</strong>.
        </Typography>
      </DialogTitle>
      <Divider />
      <DialogContent>
        <Stack spacing={2}>
          <Box
            sx={{
              p: 2,
              borderRadius: 2,
              border: (theme) => `1px solid ${theme.palette.divider}`,
              backgroundColor: (theme) => theme.palette.action.hover,
            }}
          >
            <Typography variant="subtitle2" color="text.primary" fontWeight={600}>
              Current folder
            </Typography>
            <Typography variant="body1" fontWeight={600}>
              {folder?.name}
            </Typography>
            <Typography variant="caption" color="text.secondary">
              You can move it to any folder below (except its descendants).
            </Typography>
          </Box>
          <Box
            sx={{
              borderRadius: 2,
              border: (theme) => `1px solid ${theme.palette.divider}`,
              backgroundColor: 'background.paper',
              maxHeight: 340,
              overflowY: 'auto',
            }}
          >
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
                    label={
                      <Box display="flex" flexDirection="column">
                        <Typography
                          variant="body2"
                          fontWeight={selected === option.id ? 600 : 500}
                          sx={{ whiteSpace: 'pre', color: disabled ? 'text.disabled' : 'text.primary' }}
                        >
                          {option.id === null ? 'Root folder' : formatLabel(option.label, option.depth)}
                        </Typography>
                        {disabled ? (
                          <Typography variant="caption" color="error.main">
                            Cannot move into a descendant folder
                          </Typography>
                        ) : null}
                      </Box>
                    }
                    sx={{
                      alignItems: 'flex-start',
                      px: 2,
                      py: 1,
                      '& .MuiRadio-root': { mt: 0.5 },
                    }}
                  />
                )
              })}
            </RadioGroup>
          </Box>
        </Stack>
      </DialogContent>
      <DialogActions sx={{ px: 3, pb: 3 }}>
        <LoadingButton onClick={onClose} color="secondary" disabled={isSubmitting} variant="outlined">
          Cancel
        </LoadingButton>
        <LoadingButton loading={isSubmitting} onClick={handleSubmit} variant="contained">
          Move folder
        </LoadingButton>
      </DialogActions>
    </Dialog>
  )
}

export default MoveFolderModal

