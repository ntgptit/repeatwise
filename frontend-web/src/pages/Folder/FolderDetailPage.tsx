/**
 * Folder Detail Page
 *
 * Displays detailed information about a folder and its contents
 *
 * Features:
 * - Folder information (name, stats)
 * - Subfolders list
 * - Decks list
 * - Actions (edit, delete, move)
 * - Breadcrumb navigation
 */

import * as React from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { Layout, PageContainer, Section } from '@/components/common/Layout'
import { Header } from '@/components/common/Header'
import { Sidebar } from '@/components/common/Sidebar'
import { Breadcrumb } from '@/components/common/Breadcrumb'
import { LoadingSpinner } from '@/components/common/LoadingSpinner'
import { EmptyState } from '@/components/common/EmptyState'
import { StatCard } from '@/components/common/Cards'
import { ActionMenu } from '@/components/common/Actions'
import { FolderTree } from '@/components/folder/FolderTree'
import { DeckList } from '@/components/deck/DeckList'
import { FolderEditDialog } from '@/components/folder/FolderEditDialog'
import { Button } from '@/components/ui/button'
import {
  Edit2,
  Trash2,
  ArrowLeft,
  Folder,
  FileText,
  Clock,
} from 'lucide-react'
import { ROUTES } from '@/constants/routes'
import { useAuth } from '@/hooks/domain/useAuth'
import { folderApi } from '@/api/modules/folder.api'
import { deckApi } from '@/api/modules/deck.api'
import { toast } from 'sonner'

export function FolderDetailPage() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const { user } = useAuth()

  const [isEditDialogOpen, setIsEditDialogOpen] = React.useState(false)

  // Fetch folder details
  const {
    data: folder,
    isLoading: isLoadingFolder,
    error: folderError,
  } = useQuery({
    queryKey: ['folder', id],
    queryFn: () => folderApi.getById(id!),
    enabled: !!id,
  })

  // Fetch subfolders
  const {
    data: subfolders,
    isLoading: isLoadingSubfolders,
  } = useQuery({
    queryKey: ['folders', id, 'children'],
    queryFn: () => folderApi.getFolders(id!),
    enabled: !!id,
  })

  // Fetch decks in folder
  const {
    data: decks,
    isLoading: isLoadingDecks,
  } = useQuery({
    queryKey: ['decks', 'folder', id],
    queryFn: () => deckApi.getByFolder(id!),
    enabled: !!id,
  })

  const handleDelete = async () => {
    if (!folder || !id) {
      return
    }

    if (!confirm(`Are you sure you want to delete "${folder.name}"? This action cannot be undone.`)) {
      return
    }

    try {
      await folderApi.delete(id)
      toast.success('Folder deleted successfully')
      navigate(ROUTES.FOLDERS)
    } catch (error) {
      console.error('Failed to delete folder:', error)
      toast.error('Failed to delete folder')
    }
  }

  if (isLoadingFolder) {
    return (
      <Layout>
        <PageContainer>
          <div className="flex items-center justify-center min-h-screen">
            <LoadingSpinner label="Loading folder..." />
          </div>
        </PageContainer>
      </Layout>
    )
  }

  if (folderError || !folder) {
    return (
      <Layout>
        <PageContainer>
          <div className="flex items-center justify-center min-h-screen">
            <EmptyState
              message="Folder not found"
              description="The folder you're looking for doesn't exist or has been deleted."
              actionLabel="Go to Folders"
              onAction={() => navigate(ROUTES.FOLDERS)}
            />
          </div>
        </PageContainer>
      </Layout>
    )
  }

  const isLoading = isLoadingSubfolders || isLoadingDecks

  const breadcrumbItems = [
    { label: 'Folders', href: ROUTES.FOLDERS },
    { label: folder.name },
  ]

  const actions = [
    {
      label: 'Edit',
      icon: <Edit2 className="h-4 w-4" />,
      onClick: () => setIsEditDialogOpen(true),
    },
    {
      label: 'Delete',
      icon: <Trash2 className="h-4 w-4" />,
      onClick: handleDelete,
      variant: 'destructive' as const,
      separator: true,
    },
  ]

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
          {/* Breadcrumb */}
          <Breadcrumb items={breadcrumbItems} />

          {/* Header */}
          <Section>
            <div className="flex items-start justify-between">
              <div className="space-y-2">
                <div className="flex items-center gap-4">
                  <Button variant="ghost" size="icon" onClick={() => navigate(ROUTES.FOLDERS)}>
                    <ArrowLeft className="h-4 w-4" />
                  </Button>
                  <Folder className="h-6 w-6 text-muted-foreground" />
                  <h1 className="text-3xl font-bold">{folder.name}</h1>
                </div>
                <div className="flex items-center gap-4 text-sm text-muted-foreground">
                  {folder.createdAt ? (
                    <span>Created: {new Date(folder.createdAt).toLocaleDateString()}</span>
                  ) : null}
                  {folder.updatedAt ? (
                    <span>Updated: {new Date(folder.updatedAt).toLocaleDateString()}</span>
                  ) : null}
                </div>
              </div>
              <div className="flex items-center gap-2">
                <ActionMenu actions={actions} />
              </div>
            </div>
          </Section>

          {/* Statistics */}
          <Section title="Statistics">
            <div className="grid gap-4 md:grid-cols-3">
              <StatCard
                title="Total Cards"
                value={folder.cardCount || 0}
                icon={<FileText className="h-4 w-4" />}
                description="Cards in this folder"
              />
              <StatCard
                title="Decks"
                value={folder.deckCount || 0}
                icon={<Folder className="h-4 w-4" />}
                description="Decks in this folder"
              />
              <StatCard
                title="Due Cards"
                value={0}
                icon={<Clock className="h-4 w-4" />}
                description="Ready for review"
              />
            </div>
          </Section>

          {/* Subfolders */}
          {(() => {
            if (isLoading) {
              return (
                <Section title="Subfolders">
                  <div className="flex items-center justify-center py-12">
                    <LoadingSpinner label="Loading subfolders..." />
                  </div>
                </Section>
              )
            }
            
            if (subfolders && subfolders.length > 0) {
              return (
                <Section title="Subfolders">
                  <FolderTree
                    folders={subfolders.map(f => ({
                      id: f.id,
                      name: f.name,
                      parentId: f.parentId,
                      children: [],
                    }))}
                    onClick={(folderId) => navigate(`${ROUTES.FOLDERS}/${folderId}`)}
                    showSearch={false}
                  />
                </Section>
              )
            }
            
            return null
          })()}

          {/* Decks */}
          <Section
            title="Decks"
            actions={
              <Button
                variant="outline"
                size="sm"
                onClick={() => {
                  // Navigate to create deck page or open dialog
                  navigate(`${ROUTES.DECKS}?folderId=${id}`)
                }}
              >
                Create Deck
              </Button>
            }
          >
            {(() => {
              if (isLoading) {
                return (
                  <div className="flex items-center justify-center py-12">
                    <LoadingSpinner label="Loading decks..." />
                  </div>
                )
              }
              
              if (decks && decks.length > 0) {
                return (
                  <DeckList
                    decks={decks.map(deck => ({
                      id: deck.id,
                      name: deck.name,
                      ...(deck.description !== undefined && deck.description !== null && { description: deck.description }),
                      ...(deck.folderId !== undefined && deck.folderId !== null && { folderId: deck.folderId }),
                      ...(deck.cardCount !== undefined && { cardCount: deck.cardCount }),
                      ...(deck.dueCards !== undefined && { dueCards: deck.dueCards }),
                      ...(deck.newCards !== undefined && { newCards: deck.newCards }),
                      ...(deck.createdAt !== undefined && { createdAt: deck.createdAt }),
                      ...(deck.updatedAt !== undefined && { updatedAt: deck.updatedAt }),
                    }))}
                    onClick={(deckId) => navigate(`${ROUTES.DECKS}/${deckId}`)}
                    gridCols={3}
                    emptyMessage="No decks in this folder"
                    emptyDescription="Create your first deck in this folder"
                  />
                )
              }
              
              return (
                <EmptyState
                  message="No decks yet"
                  description="Start creating decks in this folder to organize your flashcards."
                  actionLabel="Create First Deck"
                  onAction={() => {
                    navigate(`${ROUTES.DECKS}?folderId=${id}`)
                  }}
                />
              )
            })()}
          </Section>
        </div>
      </PageContainer>

      {/* Edit Dialog */}
      {folder ? (
        <FolderEditDialog
          open={isEditDialogOpen}
          onOpenChange={setIsEditDialogOpen}
          initialData={{
            name: folder.name,
            description: '',
          }}
          onSubmit={async (data) => {
            try {
              await folderApi.update(folder.id, {
                name: data.name,
              })
              toast.success('Folder updated successfully')
              setIsEditDialogOpen(false)
            } catch (error) {
              console.error('Failed to update folder:', error)
              toast.error('Failed to update folder')
            }
          }}
        />
      ) : null}
    </Layout>
  )
}

// Default export for compatibility
export default FolderDetailPage

