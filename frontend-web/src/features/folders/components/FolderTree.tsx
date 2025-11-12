import { useEffect, useMemo, useState, type ReactNode, type SyntheticEvent } from 'react'
import { TreeView } from '@mui/x-tree-view/TreeView'
import { TreeItem } from '@mui/x-tree-view/TreeItem'
import IndeterminateCheckBoxOutlinedIcon from '@mui/icons-material/IndeterminateCheckBoxOutlined'
import AddBoxOutlinedIcon from '@mui/icons-material/AddBoxOutlined'
import { Box, Button, Stack, Tooltip, Typography } from '@mui/material'
import type { FolderTreeNode } from '@/api/types/folder.types'

interface FolderTreeProps {
  nodes: FolderTreeNode[]
  selectedId: string | null
  onSelect: (folderId: string | null) => void
}

const collectAllNodeIds = (nodes: FolderTreeNode[]): string[] => {
  const ids: string[] = []

  const walk = (items: FolderTreeNode[]) => {
    for (const item of items) {
      ids.push(item.id)
      if (item.children.length > 0) {
        walk(item.children)
      }
    }
  }

  walk(nodes)

  return ids
}

const renderNode = (node: FolderTreeNode): ReactNode => {
  return (
    <TreeItem
      key={node.id}
      nodeId={node.id}
      label={
        <Tooltip title={node.name} placement="right" enterDelay={400}>
          <Box sx={{ display: 'flex', alignItems: 'center', maxWidth: '100%' }}>
            <Typography
              variant="body2"
              fontWeight={500}
              noWrap
              sx={{ overflow: 'hidden', textOverflow: 'ellipsis', maxWidth: '100%' }}
            >
              {node.name}
            </Typography>
          </Box>
        </Tooltip>
      }
      collapseIcon={<IndeterminateCheckBoxOutlinedIcon fontSize="small" />}
      expandIcon={<AddBoxOutlinedIcon fontSize="small" />}
      endIcon={<Box component="span" sx={{ display: 'inline-flex', width: 18, height: 18 }} />}
    >
      {node.children.map(renderNode)}
    </TreeItem>
  )
}

export const FolderTree = ({ nodes, selectedId, onSelect }: FolderTreeProps) => {
  const topLevelIds = useMemo(() => nodes.map(node => node.id), [nodes])
  const allNodeIds = useMemo(() => collectAllNodeIds(nodes), [nodes])

  const [expanded, setExpanded] = useState<string[]>(() => topLevelIds)

  useEffect(() => {
    setExpanded(previous => {
      if (previous.length === 0) {
        return topLevelIds
      }

      const validIds = new Set(allNodeIds)
      const next = previous.filter(id => validIds.has(id))
      return next.length === previous.length ? previous : next
    })
  }, [allNodeIds, topLevelIds])

  const handleNodeToggle = (_: SyntheticEvent, nodeIds: string[]) => {
    setExpanded(nodeIds)
  }

  const handleExpandAll = () => {
    setExpanded(allNodeIds)
  }

  const handleCollapseAll = () => {
    setExpanded([])
  }

  return (
    <Box
      sx={{
        borderRadius: 2,
        border: theme => `1px solid ${theme.palette.divider}`,
        p: 2,
        backgroundColor: 'background.paper',
        height: '100%',
        display: 'flex',
        flexDirection: 'column',
      }}
    >
      <Typography variant="subtitle1" gutterBottom fontWeight={600}>
        Folder tree
      </Typography>
      <Stack direction="row" spacing={1} sx={{ mb: 1 }}>
        <Button
          size="small"
          variant="outlined"
          onClick={handleExpandAll}
          disabled={allNodeIds.length === 0}
        >
          Expand all
        </Button>
        <Button
          size="small"
          variant="text"
          onClick={handleCollapseAll}
          disabled={expanded.length === 0}
        >
          Collapse all
        </Button>
      </Stack>
      <Box sx={{ flexGrow: 1, overflowY: 'auto', maxHeight: 338, minHeight: 338, pr: 1 }}>
        <TreeView
          multiSelect={false}
          selected={selectedId ?? null}
          expanded={expanded}
          onNodeToggle={handleNodeToggle}
          onNodeSelect={(_, nodeIds) => {
            const nextId = Array.isArray(nodeIds) ? nodeIds[0] : nodeIds
            onSelect(nextId ?? null)
          }}
          defaultCollapseIcon={<IndeterminateCheckBoxOutlinedIcon fontSize="small" />}
          defaultExpandIcon={<AddBoxOutlinedIcon fontSize="small" />}
          sx={{
            '& .MuiTreeItem-group': { ml: 2 },
            '& .MuiTreeItem-label': {
              display: 'flex',
              alignItems: 'center',
              maxWidth: '100%',
              pr: 1,
            },
            flexGrow: 1,
          }}
        >
          {nodes.length > 0 ? nodes.map(renderNode) : null}
        </TreeView>
      </Box>
      {nodes.length === 0 ? (
        <Typography variant="body2" color="text.secondary">
          No folders yet. Create your first folder to get started.
        </Typography>
      ) : null}
    </Box>
  )
}

export default FolderTree
