import type { FolderDto, FolderTreeNode } from '@/api/types/folder.types'

const sortTreeInPlace = (nodes: FolderTreeNode[]): void => {
  nodes.sort((a, b) => {
    const orderDiff = (a.sortOrder ?? 0) - (b.sortOrder ?? 0)
    if (orderDiff !== 0) {
      return orderDiff
    }

    return a.name.localeCompare(b.name)
  })

  for (const node of nodes) {
    if (node.children.length > 0) {
      sortTreeInPlace(node.children)
    }
  }
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
        continue
      }
    }

    roots.push(node)
  }

  sortTreeInPlace(roots)

  return roots
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

