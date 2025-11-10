import orderBy from 'lodash-es/orderBy'
import type { FolderDto, FolderTreeNode } from '@/api/types/folder.types'

const sortNodes = (nodes: FolderTreeNode[]): FolderTreeNode[] => {
  const sorted = orderBy(nodes, ['name'], ['asc'])

  return sorted.map((node) => ({
    ...node,
    children: sortNodes(node.children),
  }))
}

export const buildFolderTree = (folders: FolderDto[]): FolderTreeNode[] => {
  const nodeMap = new Map<string, FolderTreeNode>()
  const roots: FolderTreeNode[] = []

  for (const folder of folders) {
    nodeMap.set(folder.id, { ...folder, children: [] })
  }

  for (const node of nodeMap.values()) {
    if (node.parentFolderId) {
      const parent = nodeMap.get(node.parentFolderId)
      if (parent) {
        parent.children.push(node)
      } else {
        roots.push(node)
      }
    } else {
      roots.push(node)
    }
  }

  return sortNodes(roots)
}

export const findFolderNode = (
  nodes: FolderTreeNode[],
  folderId: string | null
): FolderTreeNode | null => {
  if (!folderId) {
    return null
  }

  for (const node of nodes) {
    if (node.id === folderId) {
      return node
    }

    const child = findFolderNode(node.children, folderId)
    if (child) {
      return child
    }
  }

  return null
}

