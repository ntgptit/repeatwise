/**
 * Deck List Page
 * 
 * Displays a list of all decks with filtering and actions
 * 
 * Features:
 * - Deck list with grid layout
 * - Create deck dialog
 * - Search and filter
 * - Actions (edit, delete, review)
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
import { DeckList } from '@/components/deck/DeckList'
import { DeckCreateDialog } from '@/components/deck/DeckCreateDialog'
import { DeckEditDialog } from '@/components/deck/DeckEditDialog'
import { LoadingSpinner } from '@/components/common/LoadingSpinner'
import { Button } from '@/components/ui/button'
import { Plus, Search } from 'lucide-react'
import { ROUTES } from '@/constants/routes'
import { useAuth } from '@/hooks/domain/useAuth'
import { deckApi, type Deck, type CreateDeckRequest } from '@/api/modules/deck.api'
import { toast } from 'sonner'

export function DeckListPage() {
  const navigate = useNavigate()
  const { user } = useAuth()
  const queryClient = useQueryClient()
  
  const [isCreateDialogOpen, setIsCreateDialogOpen] = React.useState(false)
  const [isEditDialogOpen, setIsEditDialogOpen] = React.useState(false)
  const [editingDeck, setEditingDeck] = React.useState<Deck | null>(null)
  const [searchQuery, setSearchQuery] = React.useState('')

  // Fetch all decks
  const {
    data: decks,
    isLoading,
    error,
  } = useQuery({
    queryKey: ['decks'],
    queryFn: () => deckApi.getAll(),
  })

  // Create deck mutation
  const createMutation = useMutation({
    mutationFn: (data: CreateDeckRequest) => deckApi.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['decks'] })
      toast.success('Deck created successfully')
      setIsCreateDialogOpen(false)
    },
    onError: () => {
      toast.error('Failed to create deck')
    },
  })

  // Delete deck mutation
  const deleteMutation = useMutation({
    mutationFn: (id: string) => deckApi.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['decks'] })
      toast.success('Deck deleted successfully')
    },
    onError: () => {
      toast.error('Failed to delete deck')
    },
  })

  // Filter decks by search query
  const filteredDecks = React.useMemo(() => {
    if (!decks) return []
    if (!searchQuery.trim()) return decks

    const query = searchQuery.toLowerCase()
    return decks.filter(
      (deck) =>
        deck.name.toLowerCase().includes(query) ||
        deck.description?.toLowerCase().includes(query),
    )
  }, [decks, searchQuery])

  const handleCreate = async (data: { name: string; description?: string; folderId?: string | null }) => {
    await createMutation.mutateAsync(data)
  }

  const handleEdit = (deckId: string) => {
    const deck = decks?.find((d) => d.id === deckId)
    if (deck) {
      setEditingDeck(deck)
      setIsEditDialogOpen(true)
    }
  }

  const handleDelete = async (deckId: string) => {
    const deck = decks?.find((d) => d.id === deckId)
    if (!deck) return

    if (!confirm(`Are you sure you want to delete "${deck.name}"? This action cannot be undone.`)) {
      return
    }

    await deleteMutation.mutateAsync(deckId)
  }

  const handleReview = (deckId: string) => {
    navigate(`${ROUTES.REVIEW}?deckId=${deckId}`)
  }

  const handleDeckClick = (deckId: string) => {
    navigate(`${ROUTES.DECKS}/${deckId}`)
  }

  const handleEditSubmit = async (data: { name: string; description?: string }) => {
    if (!editingDeck) return

    try {
      await deckApi.update(editingDeck.id, data)
      queryClient.invalidateQueries({ queryKey: ['decks'] })
      toast.success('Deck updated successfully')
      setIsEditDialogOpen(false)
      setEditingDeck(null)
    } catch (error) {
      toast.error('Failed to update deck')
    }
  }

  return (
    <Layout>
      <Header user={user} />
      <PageContainer sidebar={<Sidebar />}>
        <div className="container mx-auto px-4 py-6 space-y-6">
          {/* Header */}
          <Section>
            <div className="flex items-center justify-between">
              <div>
                <h1 className="text-3xl font-bold">Decks</h1>
                <p className="text-muted-foreground mt-1">
                  Manage your flashcard decks
                </p>
              </div>
              <Button onClick={() => setIsCreateDialogOpen(true)}>
                <Plus className="mr-2 h-4 w-4" />
                Create Deck
              </Button>
            </div>
          </Section>

          {/* Search */}
          <Section>
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground" />
              <input
                type="text"
                placeholder="Search decks..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="w-full pl-10 pr-4 py-2 border rounded-md bg-background"
              />
            </div>
          </Section>

          {/* Deck List */}
          <Section>
            {isLoading ? (
              <div className="flex items-center justify-center py-12">
                <LoadingSpinner label="Loading decks..." />
              </div>
            ) : error ? (
              <div className="text-center py-12">
                <p className="text-destructive">Failed to load decks</p>
                <Button
                  variant="outline"
                  onClick={() => queryClient.invalidateQueries({ queryKey: ['decks'] })}
                  className="mt-4"
                >
                  Retry
                </Button>
              </div>
            ) : (
              <DeckList
                decks={filteredDecks.map((deck) => ({
                  id: deck.id,
                  name: deck.name,
                  description: deck.description,
                  folderId: deck.folderId,
                  cardCount: deck.cardCount,
                  dueCards: deck.dueCards,
                  newCards: deck.newCards,
                  createdAt: deck.createdAt,
                  updatedAt: deck.updatedAt,
                }))}
                onEdit={handleEdit}
                onDelete={handleDelete}
                onReview={handleReview}
                onClick={handleDeckClick}
                gridCols={3}
                emptyMessage="No decks found"
                emptyDescription={
                  searchQuery
                    ? `No decks match "${searchQuery}"`
                    : 'Create your first deck to get started'
                }
              />
            )}
          </Section>
        </div>
      </PageContainer>

      {/* Create Dialog */}
      <DeckCreateDialog
        open={isCreateDialogOpen}
        onOpenChange={setIsCreateDialogOpen}
        onSubmit={handleCreate}
        isLoading={createMutation.isPending}
      />

      {/* Edit Dialog */}
      {editingDeck && (
        <DeckEditDialog
          open={isEditDialogOpen}
          onOpenChange={(open) => {
            setIsEditDialogOpen(open)
            if (!open) setEditingDeck(null)
          }}
          initialData={{
            name: editingDeck.name,
            description: editingDeck.description || '',
          }}
          onSubmit={handleEditSubmit}
        />
      )}
    </Layout>
  )
}

// Default export for compatibility
export default DeckListPage

