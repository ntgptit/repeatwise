import { Box, Chip, Divider, Stack, Typography } from '@mui/material'
import type { FolderTreeNode } from '@/api/types/folder.types'

interface FolderDetailsPanelProps {
  folder: FolderTreeNode | null
}

const formatDateTime = (value: string | null) => {
  if (!value) {
    return '—'
  }

  return new Date(value).toLocaleString('vi-VN')
}

export const FolderDetailsPanel = ({ folder }: FolderDetailsPanelProps) => {
  if (!folder) {
    return (
      <Box
        sx={{
          borderRadius: 2,
          border: (theme) => `1px solid ${theme.palette.divider}`,
          p: 3,
          backgroundColor: 'background.paper',
          minHeight: 220,
        }}
      >
        <Typography variant="h6" gutterBottom>
          Folder details
        </Typography>
        <Typography variant="body2" color="text.secondary">
          Select a folder from the tree to view its information.
        </Typography>
      </Box>
    )
  }

  return (
    <Box
      sx={{
        borderRadius: 2,
        border: (theme) => `1px solid ${theme.palette.divider}`,
        p: 3,
        backgroundColor: 'background.paper',
      }}
    >
      <Stack spacing={2}>
        <Stack direction="row" spacing={1} alignItems="center">
          <Typography variant="h6">{folder.name}</Typography>
          <Chip size="small" label={`Depth: ${folder.depth}`} />
        </Stack>
        <Typography variant="body2" color="text.secondary">
          {folder.description ? folder.description : 'No description provided yet.'}
        </Typography>
        <Divider />
        <Stack spacing={1}>
          <Typography variant="subtitle2" color="text.secondary">
            System information
          </Typography>
          <Typography variant="body2">
            <strong>Path:</strong> {folder.path}
          </Typography>
          <Typography variant="body2">
            <strong>Parent folder:</strong> {folder.parentFolderId ?? 'Root'}
          </Typography>
          <Typography variant="body2">
            <strong>Created at:</strong> {formatDateTime(folder.createdAt)}
          </Typography>
          <Typography variant="body2">
            <strong>Last updated:</strong> {formatDateTime(folder.updatedAt)}
          </Typography>
        </Stack>
      </Stack>
    </Box>
  )
}

export default FolderDetailsPanel

