/**
 * Folder List Page
 * 
 * Displays a list of all folders with filtering and actions
 * 
 * Features:
 * - Folder list with grid layout
 * - Create folder dialog
 * - Search and filter
 * - Actions (edit, delete, navigate)
 * - Empty state
 */

import * as React from 'react'
import { useNavigate } from 'react-router-dom'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import {
  Layout,
  PageContainer,
  Section,
} from '@/components/common/Layout'
import { Header } from '@/components/common/Header'
import { Sidebar } from '@/components/common/Sidebar'
import { FolderTree } from '@/components/folder/FolderTree'
import { FolderCreateDialog } from '@/components/folder/FolderCreateDialog'
import { FolderEditDialog } from '@/components/folder/FolderEditDialog'
import { LoadingSpinner } from '@/components/common/LoadingSpinner'
import { Button } from '@/components/ui/button'
import { Plus, Search } from 'lucide-react'
import { ROUTES } from '@/constants/routes'
import { useAuth } from '@/hooks/domain/useAuth'
import { folderApi, type Folder, type CreateFolderRequest } from '@/api/modules/folder.api'
import { toast } from 'sonner'

export function FolderListPage() {
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  
  const [isCreateDialogOpen, setIsCreateDialogOpen] = React.useState(false)
  const [isEditDialogOpen, setIsEditDialogOpen] = React.useState(false)
  const [editingFolder, setEditingFolder] = React.useState<Folder | null>(null)
  const [searchQuery, setSearchQuery] = React.useState('')

  // Fetch folder tree
  const {
    data: folderTree,
    isLoading,
    error,
  } = useQuery({
    queryKey: ['folders', 'tree'],
    queryFn: () => folderApi.getFolderTree(),
  })

  // Create folder mutation
  const createMutation = useMutation({
    mutationFn: (data: CreateFolderRequest) => folderApi.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['folders'] })
      toast.success('Folder created successfully')
      setIsCreateDialogOpen(false)
    },
    onError: (error) => {
      console.error('Failed to create folder:', error)
      toast.error('Failed to create folder')
    },
  })

  const handleCreate = async (data: { name: string; description?: string; parentId?: string | null }) => {
    await createMutation.mutateAsync({
      name: data.name,
      parentId: data.parentId ?? null,
    })
  }

  // Helper function to find folder in tree
  const findFolderInTree = React.useCallback((folders: typeof folderTree, folderId: string): Folder | null => {
    if (!folders) return null
    for (const folder of folders) {
      if (folder.id === folderId) return folder
      if (folder.children) {
        const found = findFolderInTree(folder.children, folderId)
        if (found) return found
      }
    }
    return null
  }, [])

  const handleFolderClick = (folderId: string) => {
    navigate(`${ROUTES.FOLDERS}/${folderId}`)
  }

  const handleEditSubmit = async (data: { name: string; description?: string }) => {
    if (!editingFolder) return

    try {
      await folderApi.update(editingFolder.id, {
        name: data.name,
      })
      queryClient.invalidateQueries({ queryKey: ['folders'] })
      toast.success('Folder updated successfully')
      setIsEditDialogOpen(false)
      setEditingFolder(null)
    } catch (error) {
      console.error('Failed to update folder:', error)
      toast.error('Failed to update folder')
    }
  }

  // Get user for Header
  const { user } = useAuth()

  return (
    <Layout>
      <Header 
        {...(user && { 
          user: { 
            ...(user.name && { name: user.name }), 
            email: user.email 
          } 
        })}
      />
      <PageContainer sidebar={<Sidebar />}>
        <div className="container mx-auto px-4 py-6 space-y-6">
          {/* Header */}
          <Section>
            <div className="flex items-center justify-between">
              <div>
                <h1 className="text-3xl font-bold">Folders</h1>
                <p className="text-muted-foreground mt-1">
                  Organize your decks with folders
                </p>
              </div>
              <Button onClick={() => setIsCreateDialogOpen(true)}>
                <Plus className="mr-2 h-4 w-4" />
                Create Folder
              </Button>
            </div>
          </Section>

          {/* Search */}
          <Section>
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground" />
              <input
                type="text"
                placeholder="Search folders..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="w-full pl-10 pr-4 py-2 border rounded-md bg-background"
              />
            </div>
          </Section>

          {/* Folder Tree */}
          <Section>
            {(() => {
              if (isLoading) {
                return (
                  <div className="flex items-center justify-center py-12">
                    <LoadingSpinner label="Loading folders..." />
                  </div>
                )
              }
              
              if (error) {
                return (
                  <div className="text-center py-12">
                    <p className="text-destructive">Failed to load folders</p>
                    <Button
                      variant="outline"
                      onClick={() => queryClient.invalidateQueries({ queryKey: ['folders'] })}
                      className="mt-4"
                    >
                      Retry
                    </Button>
                  </div>
                )
              }
              
              return (
                <FolderTree
                  folders={folderTree || []}
                  onClick={handleFolderClick}
                  searchQuery={searchQuery}
                  showSearch={false}
                />
              )
            })()}
          </Section>
        </div>
      </PageContainer>

      {/* Create Dialog */}
      <FolderCreateDialog
        open={isCreateDialogOpen}
        onOpenChange={setIsCreateDialogOpen}
        onSubmit={async (data) => {
          await handleCreate({
            name: data.name,
            ...(data.description && { description: data.description }),
            parentId: data.parentId ?? null,
          })
        }}
        isLoading={createMutation.isPending}
        parentOptions={[]}
      />

      {/* Edit Dialog */}
      {editingFolder && (
        <FolderEditDialog
          open={isEditDialogOpen}
          onOpenChange={(open) => {
            setIsEditDialogOpen(open)
            if (!open) setEditingFolder(null)
          }}
          initialData={{
            name: editingFolder.name,
            description: '',
          }}
          onSubmit={async (data) => {
            await handleEditSubmit({
              name: data.name,
              ...(data.description && { description: data.description }),
            })
          }}
        />
      )}
    </Layout>
  )
}

// Default export for compatibility
export default FolderListPage

