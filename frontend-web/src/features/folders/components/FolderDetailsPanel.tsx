import { Box, Chip, Divider, Stack, Typography } from '@mui/material'
import { format } from 'date-fns'
import type { FolderDto, FolderTreeNode } from '@/api/types/folder.types'

interface FolderDetailsPanelProps {
  folder: FolderTreeNode | null
  allFolders: FolderDto[]
}

const formatDateTime = (value: string | null) => {
  if (!value) {
    return '—'
  }

  const date = new Date(value)
  if (Number.isNaN(date.getTime())) {
    return '—'
  }

  return format(date, 'yyyy-MM-dd HH:mm:ss')
}

const buildPathDisplay = (node: FolderTreeNode, allFolders: FolderDto[]): string => {
  if (!node) {
    return 'Root'
  }

  const map = new Map(allFolders.map((item) => [item.id, item]))
  const segments: string[] = []

  let current: FolderDto | FolderTreeNode | undefined = node
  while (current) {
    segments.unshift(current.name)

    if (!current.parentFolderId) {
      break
    }

    const parent = map.get(current.parentFolderId)
    if (!parent) {
      break
    }

    current = parent
  }

  return ['Root', ...segments].join(' / ')
}

const resolveParentName = (node: FolderTreeNode, allFolders: FolderDto[]): string => {
  if (!node.parentFolderId) {
    return 'Root'
  }

  const parent = allFolders.find((item) => item.id === node.parentFolderId)
  return parent?.name ?? 'Root'
}

export const FolderDetailsPanel = ({ folder, allFolders }: FolderDetailsPanelProps) => {
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

  const pathDisplay = buildPathDisplay(folder, allFolders)
  const parentName = resolveParentName(folder, allFolders)

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
            <strong>Path:</strong> {pathDisplay}
          </Typography>
          <Typography variant="body2">
            <strong>Parent folder:</strong> {parentName}
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

