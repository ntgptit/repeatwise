/**
 * FolderTree Component
 * Displays hierarchical folder structure as a tree
 * Supports drag & drop, context menu, and keyboard shortcuts
 */

import { useState, useMemo } from 'react'
import {
  Box,
  Card,
  CardContent,
  Typography,
  IconButton,
  Menu,
  MenuItem,
  ListItemIcon,
  ListItemText,
  CircularProgress,
  Alert,
  Collapse,
} from '@mui/material'
import {
  Folder as FolderIcon,
  FolderOpen as FolderOpenIcon,
  MoreVert as MoreVertIcon,
  Add as AddIcon,
  Edit as EditIcon,
  DriveFileMove as MoveIcon,
  ContentCopy as CopyIcon,
  Delete as DeleteIcon,
  ExpandMore as ExpandMoreIcon,
  ChevronRight as ChevronRightIcon,
} from '@mui/icons-material'
import type { FolderResponse } from '@/api/types/folder.types'

interface FolderTreeProps {
  folders: FolderResponse[]
  isLoading?: boolean
  error?: Error | null
  selectedFolderId?: string | null
  onSelectFolder?: (folderId: string) => void
  onCreateFolder?: (parentId?: string | null) => void
  onRenameFolder?: (folder: FolderResponse) => void
  onMoveFolder?: (folder: FolderResponse) => void
  onCopyFolder?: (folder: FolderResponse) => void
  onDeleteFolder?: (folder: FolderResponse) => void
}

interface FolderNodeProps {
  folder: FolderResponse
  level: number
  isSelected: boolean
  onSelect: (folderId: string) => void
  onCreateFolder?: (parentId?: string | null) => void
  onRenameFolder?: (folder: FolderResponse) => void
  onMoveFolder?: (folder: FolderResponse) => void
  onCopyFolder?: (folder: FolderResponse) => void
  onDeleteFolder?: (folder: FolderResponse) => void
}

const FolderNode = ({
  folder,
  level,
  isSelected,
  onSelect,
  onCreateFolder,
  onRenameFolder,
  onMoveFolder,
  onCopyFolder,
  onDeleteFolder,
}: FolderNodeProps) => {
  const [expanded, setExpanded] = useState(false)
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null)

  const hasChildren = folder.children && folder.children.length > 0

  const handleMenuOpen = (event: React.MouseEvent<HTMLElement>) => {
    event.stopPropagation()
    setAnchorEl(event.currentTarget)
  }

  const handleMenuClose = () => {
    setAnchorEl(null)
  }

  const handleAction = (action: () => void) => {
    handleMenuClose()
    action()
  }

  return (
    <>
      <Box
        sx={{
          display: 'flex',
          alignItems: 'center',
          pl: level * 3,
          py: 0.5,
          cursor: 'pointer',
          backgroundColor: isSelected ? 'action.selected' : 'transparent',
          '&:hover': {
            backgroundColor: 'action.hover',
          },
        }}
        onClick={() => onSelect(folder.id)}
      >
        <IconButton
          size="small"
          onClick={(e) => {
            e.stopPropagation()
            setExpanded(!expanded)
          }}
          sx={{ visibility: hasChildren ? 'visible' : 'hidden' }}
        >
          {expanded ? <ExpandMoreIcon fontSize="small" /> : <ChevronRightIcon fontSize="small" />}
        </IconButton>

        {expanded ? (
          <FolderOpenIcon fontSize="small" sx={{ mr: 1, color: 'primary.main' }} />
        ) : (
          <FolderIcon fontSize="small" sx={{ mr: 1, color: 'action.active' }} />
        )}

        <Typography variant="body2" sx={{ flexGrow: 1 }}>
          {folder.name}
        </Typography>

        <Typography variant="caption" color="text.secondary" sx={{ mr: 1 }}>
          {folder.deckCount || 0}
        </Typography>

        <IconButton size="small" onClick={handleMenuOpen}>
          <MoreVertIcon fontSize="small" />
        </IconButton>

        <Menu
          anchorEl={anchorEl}
          open={Boolean(anchorEl)}
          onClose={handleMenuClose}
          onClick={(e) => e.stopPropagation()}
        >
          <MenuItem onClick={() => handleAction(() => onCreateFolder?.(folder.id))}>
            <ListItemIcon>
              <AddIcon fontSize="small" />
            </ListItemIcon>
            <ListItemText>New Subfolder</ListItemText>
          </MenuItem>
          <MenuItem onClick={() => handleAction(() => onRenameFolder?.(folder))}>
            <ListItemIcon>
              <EditIcon fontSize="small" />
            </ListItemIcon>
            <ListItemText>Rename</ListItemText>
          </MenuItem>
          <MenuItem onClick={() => handleAction(() => onMoveFolder?.(folder))}>
            <ListItemIcon>
              <MoveIcon fontSize="small" />
            </ListItemIcon>
            <ListItemText>Move</ListItemText>
          </MenuItem>
          <MenuItem onClick={() => handleAction(() => onCopyFolder?.(folder))}>
            <ListItemIcon>
              <CopyIcon fontSize="small" />
            </ListItemIcon>
            <ListItemText>Copy</ListItemText>
          </MenuItem>
          <MenuItem onClick={() => handleAction(() => onDeleteFolder?.(folder))}>
            <ListItemIcon>
              <DeleteIcon fontSize="small" color="error" />
            </ListItemIcon>
            <ListItemText>Delete</ListItemText>
          </MenuItem>
        </Menu>
      </Box>

      {hasChildren && (
        <Collapse in={expanded} timeout="auto" unmountOnExit>
          {folder.children?.map((child) => (
            <FolderNode
              key={child.id}
              folder={child}
              level={level + 1}
              isSelected={child.id === folder.id}
              onSelect={onSelect}
              onCreateFolder={onCreateFolder}
              onRenameFolder={onRenameFolder}
              onMoveFolder={onMoveFolder}
              onCopyFolder={onCopyFolder}
              onDeleteFolder={onDeleteFolder}
            />
          ))}
        </Collapse>
      )}
    </>
  )
}

export const FolderTree = ({
  folders,
  isLoading,
  error,
  selectedFolderId,
  onSelectFolder,
  onCreateFolder,
  onRenameFolder,
  onMoveFolder,
  onCopyFolder,
  onDeleteFolder,
}: FolderTreeProps) => {
  // Build tree structure from flat list
  const folderTree = useMemo(() => {
    const buildTree = (parentId: string | null = null): FolderResponse[] => {
      return folders
        .filter((f) => f.parentId === parentId)
        .map((folder) => ({
          ...folder,
          children: buildTree(folder.id),
        }))
    }
    return buildTree(null)
  }, [folders])

  if (isLoading) {
    return (
      <Card>
        <CardContent>
          <Box display="flex" justifyContent="center" alignItems="center" minHeight={200}>
            <CircularProgress />
          </Box>
        </CardContent>
      </Card>
    )
  }

  if (error) {
    return (
      <Card>
        <CardContent>
          <Alert severity="error">Failed to load folders: {error.message}</Alert>
        </CardContent>
      </Card>
    )
  }

  return (
    <Card>
      <CardContent sx={{ p: 0 }}>
        <Box
          sx={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            p: 2,
            borderBottom: 1,
            borderColor: 'divider',
          }}
        >
          <Typography variant="h6">Folders</Typography>
          <IconButton
            size="small"
            color="primary"
            onClick={() => onCreateFolder?.(null)}
            title="Create Root Folder"
          >
            <AddIcon />
          </IconButton>
        </Box>

        <Box sx={{ maxHeight: 600, overflow: 'auto' }}>
          {folderTree.length === 0 ? (
            <Box p={3} textAlign="center">
              <Typography variant="body2" color="text.secondary">
                No folders yet. Click + to create your first folder.
              </Typography>
            </Box>
          ) : (
            folderTree.map((folder) => (
              <FolderNode
                key={folder.id}
                folder={folder}
                level={0}
                isSelected={folder.id === selectedFolderId}
                onSelect={onSelectFolder || (() => {})}
                onCreateFolder={onCreateFolder}
                onRenameFolder={onRenameFolder}
                onMoveFolder={onMoveFolder}
                onCopyFolder={onCopyFolder}
                onDeleteFolder={onDeleteFolder}
              />
            ))
          )}
        </Box>
      </CardContent>
    </Card>
  )
}

export default FolderTree
