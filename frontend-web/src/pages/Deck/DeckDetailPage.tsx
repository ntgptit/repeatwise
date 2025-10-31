/**
 * Deck Detail Page
 *
 * Displays detailed information about a deck and its cards
 *
 * Features:
 * - Deck information (name, description, stats)
 * - Card list with pagination
 * - Actions (edit, delete, review, move)
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
import { CardList } from '@/components/card/CardList'
import { DeckEditDialog } from '@/components/deck/DeckEditDialog'
import { Button } from '@/components/ui/button'
import {
  Edit2,
  Trash2,
  Play,
  ArrowLeft,
  FileText,
  Clock,
  Sparkles,
  MoreVertical,
} from 'lucide-react'
import { ROUTES } from '@/constants/routes'
import { useAuth } from '@/hooks/domain/useAuth'
import { deckApi } from '@/api/modules/deck.api'
import { toast } from 'sonner'

export function DeckDetailPage() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const { user } = useAuth()

  const [isEditDialogOpen, setIsEditDialogOpen] = React.useState(false)

  // Fetch deck details
  const {
    data: deck,
    isLoading: isLoadingDeck,
    error: deckError,
  } = useQuery({
    queryKey: ['deck', id],
    queryFn: () => deckApi.getById(id!),
    enabled: Boolean(id),
  })

  // TODO: Fetch cards in deck when card API is ready
  // const {
  //   data: cardsData,
  //   isLoading: isLoadingCards,
  // } = useQuery({
  //   queryKey: ['deck', id, 'cards'],
  //   queryFn: () => cardApi.getByDeck(id!),
  //   enabled: !!id,
  // })

  const handleDelete = async () => {
    if (!deck || !id) {
      return
    }

    if (!confirm(`Are you sure you want to delete "${deck.name}"? This action cannot be undone.`)) {
      return
    }

    try {
      await deckApi.delete(id)
      toast.success('Deck deleted successfully')
      navigate(ROUTES.DECKS)
    } catch (error) {
      toast.error('Failed to delete deck')
    }
  }

  const handleReview = () => {
    if (!id) {
      return
    }
    navigate(`${ROUTES.REVIEW}?deckId=${id}`)
  }

  if (isLoadingDeck) {
    return (
      <Layout>
        <PageContainer>
          <div className="flex items-center justify-center min-h-screen">
            <LoadingSpinner label="Loading deck..." />
          </div>
        </PageContainer>
      </Layout>
    )
  }

  if (deckError || !deck) {
    return (
      <Layout>
        <PageContainer>
          <div className="flex items-center justify-center min-h-screen">
            <EmptyState
              message="Deck not found"
              description="The deck you're looking for doesn't exist or has been deleted."
              actionLabel="Go to Decks"
              onAction={() => navigate(ROUTES.DECKS)}
            />
          </div>
        </PageContainer>
      </Layout>
    )
  }

  // TODO: Replace with actual cards data when card API is ready
  const cards: any[] = []
  const isLoading = isLoadingDeck

  const breadcrumbItems = [{ label: 'Decks', href: ROUTES.DECKS }, { label: deck.name }]

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
                  <Button variant="ghost" size="icon" onClick={() => navigate(ROUTES.DECKS)}>
                    <ArrowLeft className="h-4 w-4" />
                  </Button>
                  <h1 className="text-3xl font-bold">{deck.name}</h1>
                </div>
                {deck.description ? (
                  <p className="text-muted-foreground">{deck.description}</p>
                ) : null}
                <div className="flex items-center gap-4 text-sm text-muted-foreground">
                  {deck.createdAt ? (
                    <span>Created: {new Date(deck.createdAt).toLocaleDateString()}</span>
                  ) : null}
                  {deck.updatedAt ? (
                    <span>Updated: {new Date(deck.updatedAt).toLocaleDateString()}</span>
                  ) : null}
                </div>
              </div>
              <div className="flex items-center gap-2">
                <Button onClick={handleReview} disabled={deck.dueCards === 0}>
                  <Play className="mr-2 h-4 w-4" />
                  Start Review
                </Button>
                <ActionMenu actions={actions} />
              </div>
            </div>
          </Section>

          {/* Statistics */}
          <Section title="Statistics">
            <div className="grid gap-4 md:grid-cols-3">
              <StatCard
                title="Total Cards"
                value={deck.cardCount || 0}
                icon={<FileText className="h-4 w-4" />}
                description="Cards in this deck"
              />
              <StatCard
                title="Due Cards"
                value={deck.dueCards || 0}
                icon={<Clock className="h-4 w-4" />}
                description="Ready for review"
                className={deck.dueCards && deck.dueCards > 0 ? 'border-orange-500' : ''}
              />
              <StatCard
                title="New Cards"
                value={deck.newCards || 0}
                icon={<Sparkles className="h-4 w-4" />}
                description="Never studied"
              />
            </div>
          </Section>

          {/* Cards List */}
          <Section
            title="Cards"
            actions={
              <Button
                variant="outline"
                size="sm"
                onClick={() => {
                  // Navigate to add card page or open dialog
                  console.log('Add card')
                }}
              >
                Add Card
              </Button>
            }
          >
            {(() => {
              if (isLoading) {
                return (
                  <div className="flex items-center justify-center py-12">
                    <LoadingSpinner label="Loading cards..." />
                  </div>
                )
              }
              
              if (cards.length === 0) {
                return (
                  <EmptyState
                    message="No cards yet"
                    description="Start adding cards to this deck to begin learning."
                    actionLabel="Add First Card"
                    onAction={() => {
                      // Navigate to add card page or open dialog
                      console.log('Add first card')
                    }}
                  />
                )
              }
              
              return (
                <CardList
                  cards={cards.map(card => ({
                    id: card.id,
                    front: card.front,
                    back: card.back,
                    deckId: card.deckId,
                    createdAt: card.createdAt,
                    updatedAt: card.updatedAt,
                  }))}
                  onPreview={cardId => {
                    // Navigate to card detail or open edit dialog
                    console.log('Card clicked:', cardId)
                  }}
                />
              )
            })()}
          </Section>
        </div>
      </PageContainer>

      {/* Edit Dialog */}
      {deck ? (
        <DeckEditDialog
          open={isEditDialogOpen}
          onOpenChange={setIsEditDialogOpen}
          initialData={{
            name: deck.name,
            description: deck.description || '',
          }}
          onSubmit={async data => {
            try {
              await deckApi.update(deck.id, {
                name: data.name,
                description: data.description || null,
              })
              toast.success('Deck updated successfully')
              setIsEditDialogOpen(false)
            } catch (error) {
              console.error('Failed to update deck:', error)
              toast.error('Failed to update deck')
            }
          }}
        />
      ) : null}
    </Layout>
  )
}

// Default export for compatibility
export default DeckDetailPage
