import { TreeView } from '@mui/x-tree-view/TreeView'
import { TreeItem } from '@mui/x-tree-view/TreeItem'
import FolderIcon from '@mui/icons-material/FolderOutlined'
import FolderOpenIcon from '@mui/icons-material/FolderOpenOutlined'
import { Box, Stack, Typography } from '@mui/material'
import type { FolderTreeNode } from '@/api/types/folder.types'

interface FolderTreeProps {
  nodes: FolderTreeNode[]
  selectedId: string | null
  onSelect: (folderId: string | null) => void
}

const renderLabel = (node: FolderTreeNode) => (
  <Stack direction="row" spacing={1} alignItems="center">
    <Typography variant="body2" fontWeight={500}>
      {node.name}
    </Typography>
    <Typography variant="caption" color="text.secondary">
      {node.children.length} subfolders
    </Typography>
  </Stack>
)

const renderNode = (node: FolderTreeNode): React.ReactNode => {
  return (
    <TreeItem
      key={node.id}
      nodeId={node.id}
      label={renderLabel(node)}
      collapseIcon={<FolderOpenIcon fontSize="small" />}
      expandIcon={<FolderIcon fontSize="small" />}
    >
      {node.children.map(renderNode)}
    </TreeItem>
  )
}

export const FolderTree = ({ nodes, selectedId, onSelect }: FolderTreeProps) => {
  return (
    <Box
      sx={{
        borderRadius: 2,
        border: (theme) => `1px solid ${theme.palette.divider}`,
        p: 2,
        backgroundColor: 'background.paper',
        height: '100%',
      }}
    >
      <Typography variant="subtitle1" gutterBottom fontWeight={600}>
        Folder tree
      </Typography>
      <TreeView
        multiSelect={false}
        selected={selectedId ?? null}
        onNodeSelect={(_, nodeIds) => {
          const nextId = Array.isArray(nodeIds) ? nodeIds[0] : nodeIds
          onSelect(nextId ?? null)
        }}
        defaultCollapseIcon={<FolderOpenIcon fontSize="small" />}
        defaultExpandIcon={<FolderIcon fontSize="small" />}
        sx={{ '& .MuiTreeItem-group': { ml: 2 }, flexGrow: 1 }}
      >
        {nodes.length > 0 ? nodes.map(renderNode) : null}
      </TreeView>
      {nodes.length === 0 ? (
        <Typography variant="body2" color="text.secondary">
          No folders yet. Create your first folder to get started.
        </Typography>
      ) : null}
    </Box>
  )
}

export default FolderTree

