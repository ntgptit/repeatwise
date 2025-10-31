/**
 * Review Session Page
 *
 * Displays a review session for flashcards
 *
 * Features:
 * - Card review with flip animation
 * - Progress tracking
 * - Rating buttons (AGAIN, HARD, GOOD, EASY)
 * - Keyboard shortcuts
 * - Session completion
 */

import * as React from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { Layout, PageContainer, Section } from '@/components/common/Layout'
import { Header } from '@/components/common/Header'
import { Sidebar } from '@/components/common/Sidebar'
import { Breadcrumb } from '@/components/common/Breadcrumb'
import { LoadingSpinner } from '@/components/common/LoadingSpinner'
import { EmptyState } from '@/components/common/EmptyState'
import { ReviewCard } from '@/components/review/ReviewCard'
import { ReviewProgress } from '@/components/review/ReviewProgress'
import { RatingButtons, type CardRating } from '@/components/review/RatingButtons'
import { Button } from '@/components/ui/button'
import { X, CheckCircle2 } from 'lucide-react'
import { ROUTES } from '@/constants/routes'
import { useAuth } from '@/hooks/domain/useAuth'
import { toast } from 'sonner'

// Mock card interface - replace with actual API when available
interface ReviewCard {
  id: string
  front: string
  back: string
  deckId: string
}

export function ReviewSessionPage() {
  const navigate = useNavigate()
  const [searchParams] = useSearchParams()
  const { user } = useAuth()
  
  const deckId = searchParams.get('deckId')
  const [currentIndex, setCurrentIndex] = React.useState(0)
  const [isRevealed, setIsRevealed] = React.useState(false)
  const [reviewedCards, setReviewedCards] = React.useState<Set<string>>(new Set())

  // TODO: Replace with actual card API when available
  // Mock cards for now
  const mockCards: ReviewCard[] = React.useMemo(() => {
    if (!deckId) return []
    // Return empty array for now - will be replaced with actual API call
    return []
  }, [deckId])

  // TODO: Fetch cards from API
  // const {
  //   data: cards,
  //   isLoading,
  //   error,
  // } = useQuery({
  //   queryKey: ['cards', 'review', deckId],
  //   queryFn: () => cardApi.getReviewCards(deckId!),
  //   enabled: !!deckId,
  // })

  const cards = mockCards
  const isLoading = false
  const error = null

  const currentCard = cards[currentIndex]
  const isComplete = currentIndex >= cards.length

  const handleReveal = React.useCallback(() => {
    setIsRevealed(true)
  }, [])

  const handleRate = React.useCallback(
    async (rating: CardRating) => {
      if (!currentCard) return

      // TODO: Submit rating to API
      // await cardApi.submitRating(currentCard.id, rating)

      // Mark card as reviewed
      setReviewedCards((prev) => new Set(prev).add(currentCard.id))

      // Move to next card
      if (currentIndex < cards.length - 1) {
        setCurrentIndex((prev) => prev + 1)
        setIsRevealed(false)
      } else {
        // Session complete
        setIsRevealed(false)
      }
    },
    [currentCard, currentIndex, cards.length],
  )

  const handleEndSession = React.useCallback(() => {
    if (reviewedCards.size === 0) {
      navigate(deckId ? `${ROUTES.DECKS}/${deckId}` : ROUTES.DECKS)
      return
    }

    toast.success(`Reviewed ${reviewedCards.size} card${reviewedCards.size !== 1 ? 's' : ''}`)
    navigate(deckId ? `${ROUTES.DECKS}/${deckId}` : ROUTES.DECKS)
  }, [reviewedCards.size, navigate, deckId])

  React.useEffect(() => {
    if (isComplete && reviewedCards.size > 0) {
      toast.success(`Session complete! Reviewed ${reviewedCards.size} card${reviewedCards.size !== 1 ? 's' : ''}`)
    }
  }, [isComplete, reviewedCards.size])

  if (!deckId) {
    return (
      <Layout>
        <Header {...(user && { user: { name: user.name, email: user.email } })} />
        <PageContainer sidebar={<Sidebar />}>
          <div className="flex items-center justify-center min-h-screen">
            <EmptyState
              message="No deck selected"
              description="Please select a deck to start reviewing."
              actionLabel="Go to Decks"
              onAction={() => navigate(ROUTES.DECKS)}
            />
          </div>
        </PageContainer>
      </Layout>
    )
  }

  if (isLoading) {
    return (
      <Layout>
        <Header {...(user && { user: { name: user.name, email: user.email } })} />
        <PageContainer sidebar={<Sidebar />}>
          <div className="flex items-center justify-center min-h-screen">
            <LoadingSpinner label="Loading review session..." />
          </div>
        </PageContainer>
      </Layout>
    )
  }

  if (error || cards.length === 0) {
    return (
      <Layout>
        <Header {...(user && { user: { name: user.name, email: user.email } })} />
        <PageContainer sidebar={<Sidebar />}>
          <div className="flex items-center justify-center min-h-screen">
            <EmptyState
              message="No cards to review"
              description="There are no cards available for review in this deck."
              actionLabel="Go to Decks"
              onAction={() => navigate(ROUTES.DECKS)}
            />
          </div>
        </PageContainer>
      </Layout>
    )
  }

  const breadcrumbItems = [
    { label: 'Decks', href: ROUTES.DECKS },
    { label: 'Review Session' },
  ]

  return (
    <Layout>
      <Header {...(user && { user: { name: user.name, email: user.email } })} />
      <PageContainer sidebar={<Sidebar />}>
        <div className="container mx-auto px-4 py-6 space-y-6 max-w-4xl">
          {/* Breadcrumb */}
          <Breadcrumb items={breadcrumbItems} />

          {/* Header */}
          <Section>
            <div className="flex items-center justify-between">
              <h1 className="text-2xl font-bold">Review Session</h1>
              <Button variant="ghost" size="icon" onClick={handleEndSession}>
                <X className="h-4 w-4" />
              </Button>
            </div>
          </Section>

          {/* Progress */}
          <Section>
            <ReviewProgress
              current={Math.min(currentIndex + 1, cards.length)}
              total={cards.length}
            />
          </Section>

          {/* Review Card */}
          {!isComplete ? (
            <Section>
              <ReviewCard
                front={currentCard.front}
                back={currentCard.back}
                isRevealed={isRevealed}
                onReveal={handleReveal}
              />
            </Section>
          ) : (
            <Section>
              <div className="flex flex-col items-center justify-center py-12 space-y-4">
                <CheckCircle2 className="h-16 w-16 text-green-500" />
                <h2 className="text-2xl font-bold">Session Complete!</h2>
                <p className="text-muted-foreground">
                  You've reviewed all {cards.length} card{cards.length !== 1 ? 's' : ''}
                </p>
                <Button onClick={handleEndSession}>Return to Decks</Button>
              </div>
            </Section>
          )}

          {/* Rating Buttons */}
          {!isComplete && isRevealed && (
            <Section>
              <RatingButtons onRate={handleRate} disabled={false} />
            </Section>
          )}
        </div>
      </PageContainer>
    </Layout>
  )
}

// Default export for compatibility
export default ReviewSessionPage

