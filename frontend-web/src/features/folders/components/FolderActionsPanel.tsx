import AddIcon from '@mui/icons-material/CreateNewFolderOutlined'
import ContentCopyIcon from '@mui/icons-material/FileCopyOutlined'
import DriveFileMoveIcon from '@mui/icons-material/DriveFileMoveOutlined'
import EditIcon from '@mui/icons-material/EditOutlined'
import DeleteIcon from '@mui/icons-material/DeleteOutline'
import { Box, Button, Divider, Stack, Typography } from '@mui/material'
import type { FolderTreeNode } from '@/api/types/folder.types'

interface FolderActionsPanelProps {
  selected: FolderTreeNode | null
  onCreateRoot: () => void
  onCreateChild: () => void
  onRename: () => void
  onMove: () => void
  onCopy: () => void
  onDelete: () => void
}

export const FolderActionsPanel = ({
  selected,
  onCreateRoot,
  onCreateChild,
  onRename,
  onMove,
  onCopy,
  onDelete,
}: FolderActionsPanelProps) => {
  const hasSelection = Boolean(selected)

  return (
    <Box
      sx={{
        borderRadius: 2,
        border: (theme) => `1px solid ${theme.palette.divider}`,
        p: 2,
        backgroundColor: 'background.paper',
      }}
    >
      <Stack spacing={2}>
        <Stack direction="row" justifyContent="space-between" alignItems="center">
          <Typography variant="subtitle1" fontWeight={600}>
            Folder actions
          </Typography>
          <Button startIcon={<AddIcon />} variant="contained" size="small" onClick={onCreateRoot}>
            Root folder
          </Button>
        </Stack>
        <Typography variant="body2" color="text.secondary">
          {selected ? `Selected: ${selected.name}` : 'Select a folder to enable more actions.'}
        </Typography>
        <Divider />
        <Stack spacing={1.5}>
          <Button
            startIcon={<AddIcon />}
            variant="outlined"
            size="small"
            onClick={onCreateChild}
            disabled={!hasSelection}
          >
            Create subfolder
          </Button>
          <Button
            startIcon={<EditIcon />}
            variant="outlined"
            size="small"
            onClick={onRename}
            disabled={!hasSelection}
          >
            Rename / update description
          </Button>
          <Button
            startIcon={<DriveFileMoveIcon />}
            variant="outlined"
            size="small"
            onClick={onMove}
            disabled={!hasSelection}
          >
            Move folder
          </Button>
          <Button
            startIcon={<ContentCopyIcon />}
            variant="outlined"
            size="small"
            onClick={onCopy}
            disabled={!hasSelection}
          >
            Copy folder
          </Button>
          <Button
            startIcon={<DeleteIcon />}
            variant="outlined"
            size="small"
            color="error"
            onClick={onDelete}
            disabled={!hasSelection}
          >
            Delete folder
          </Button>
        </Stack>
      </Stack>
    </Box>
  )
}

export default FolderActionsPanel

