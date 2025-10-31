/**
 * Dashboard Page
 * 
 * Main dashboard with statistics, quick actions, and recent activity
 * 
 * Features:
 * - Statistics cards (Total Cards, Due Cards, Streak Days)
 * - Quick Actions (Start Review, Create Deck, Import Cards)
 * - Recent Activity feed
 * - Box Distribution Chart
 * - Activity Chart (7 days)
 */

import * as React from 'react'
import { useNavigate } from 'react-router-dom'
import { Layout, PageContainer, Section } from '@/components/common/Layout'
import { Header } from '@/components/common/Header'
import { Sidebar } from '@/components/common/Sidebar'
import { StatCard, ActionCard } from '@/components/common/Cards'
import { QuickActions } from '@/components/common/Actions'
import { LoadingSpinner } from '@/components/common/LoadingSpinner'
import { EmptyState } from '@/components/common/EmptyState'
import { DeckList } from '@/components/deck/DeckList'
import { FolderTree } from '@/components/folder/FolderTree'
import { Button } from '@/components/ui/button'
import {
  FileText,
  Clock,
  Flame,
  Play,
  Plus,
  Upload,
  TrendingUp,
  BarChart3,
  BookOpen,
  FolderTree as FolderTreeIcon,
} from 'lucide-react'
import { ROUTES } from '@/constants/routes'
import { useAuth } from '@/hooks/domain/useAuth'

// Mock data types - replace with actual API types
interface DashboardStats {
  totalCards: number
  dueCards: number
  newCards: number
  streakDays: number
  reviewsToday: number
  totalStudyTimeMinutes: number
  reviewsPast7Days: number[]
}

interface RecentActivity {
  id: string
  type: 'review' | 'create_deck' | 'create_card' | 'import'
  message: string
  timestamp: string
}

interface DashboardData {
  stats: DashboardStats
  recentActivity: RecentActivity[]
  recentDecks: Array<{
    id: string
    name: string
    cardCount: number
    dueCards: number
  }>
  folderTree?: Array<{
    id: string
    name: string
    parentId?: string | null
    depth?: number
    children?: any[]
  }>
}

export function DashboardPage() {
  const navigate = useNavigate()
  const { user, logout } = useAuth()
  const [isLoading, setIsLoading] = React.useState(false)

  // Mock data - replace with actual API call
  const [dashboardData, setDashboardData] = React.useState<DashboardData>({
    stats: {
      totalCards: 0,
      dueCards: 0,
      newCards: 0,
      streakDays: 0,
      reviewsToday: 0,
      totalStudyTimeMinutes: 0,
      reviewsPast7Days: [0, 0, 0, 0, 0, 0, 0],
    },
    recentActivity: [],
    recentDecks: [],
    folderTree: [],
  })

  React.useEffect(() => {
    // TODO: Fetch dashboard data from API
    // const fetchDashboardData = async () => {
    //   setIsLoading(true)
    //   try {
    //     const data = await dashboardService.getDashboardData()
    //     setDashboardData(data)
    //   } catch (error) {
    //     console.error('Failed to fetch dashboard data:', error)
    //   } finally {
    //     setIsLoading(false)
    //   }
    // }
    // fetchDashboardData()
  }, [])

  const { stats, recentActivity, recentDecks } = dashboardData

  const quickActions = [
    {
      label: 'Start Review',
      icon: <Play className="h-4 w-4" />,
      onClick: () => navigate(ROUTES.REVIEW),
      variant: 'default' as const,
    },
    {
      label: 'Create Deck',
      icon: <Plus className="h-4 w-4" />,
      onClick: () => navigate(ROUTES.DECKS),
      variant: 'outline' as const,
    },
    {
      label: 'Import Cards',
      icon: <Upload className="h-4 w-4" />,
      onClick: () => {
        // Open import dialog
      },
      variant: 'outline' as const,
    },
    {
      label: 'View Statistics',
      icon: <BarChart3 className="h-4 w-4" />,
      onClick: () => navigate(ROUTES.STATS),
      variant: 'outline' as const,
    },
  ]

  const formatStudyTime = (minutes: number): string => {
    if (minutes < 60) return `${minutes}m`
    const hours = Math.floor(minutes / 60)
    const mins = minutes % 60
    return mins > 0 ? `${hours}h ${mins}m` : `${hours}h`
  }

  if (isLoading) {
    return (
      <Layout>
        <PageContainer>
          <div className="flex items-center justify-center min-h-screen">
            <LoadingSpinner label="Loading dashboard..." />
          </div>
        </PageContainer>
      </Layout>
    )
  }

  return (
    <Layout>
      <Header 
        {...(user && { 
          user: { 
            ...(user.name && { name: user.name }), 
            email: user.email 
          } 
        })}
        onSearch={(query) => console.log('Search:', query)}
        onLogout={() => {
          logout({
            onSuccess: () => {
              navigate(ROUTES.LOGIN, { replace: true })
            },
          })
        }}
      />
      <PageContainer
        sidebar={
          <Sidebar
            folderTree={
              dashboardData.folderTree &&
              dashboardData.folderTree.length > 0 ? (
            <FolderTree
              folders={dashboardData.folderTree}
              onClick={(folderId) =>
                navigate(`${ROUTES.FOLDERS}/${folderId}`)
              }
            />
              ) : undefined
            }
          />
        }
      >
        <div className="container mx-auto px-4 py-6 space-y-6">
          {/* Welcome Section */}
          <Section
            title={`Welcome back${user?.name ? `, ${user.name}` : ''}!`}
            description="Here's your learning overview"
          >
            <></>
          </Section>

          {/* Statistics Cards */} 
          <Section title="Statistics">
            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
              <StatCard
                title="Total Cards"
                value={stats.totalCards}
                icon={<FileText className="h-4 w-4" />}
                description="All your cards"
              />
              <StatCard
                title="Due Cards"
                value={stats.dueCards}
                icon={<Clock className="h-4 w-4" />}
                description="Ready for review"
                className={stats.dueCards > 0 ? 'border-orange-500' : ''}
              />
              <StatCard
                title="Streak Days"
                value={stats.streakDays}
                icon={<Flame className="h-4 w-4" />}
                description="Consecutive days"
                {...(stats.streakDays > 0 && {
                  trend: {
                    value: stats.streakDays,
                    label: 'days',
                    isPositive: true,
                  },
                })}
              />
              <StatCard
                title="Reviews Today"
                value={stats.reviewsToday}
                icon={<TrendingUp className="h-4 w-4" />}
                description="Completed reviews"
              />
            </div>
          </Section>

          {/* Quick Actions */}
          <Section title="Quick Actions" description="Get started quickly">
            <ActionCard
              title="Quick Actions"
              description="Common tasks to get you started"
            >
              <QuickActions actions={quickActions} />
            </ActionCard>
          </Section>

          {/* Recent Activity */}
          <Section title="Recent Activity">
            {recentActivity.length > 0 ? (
              <div className="space-y-2">
                {recentActivity.map((activity) => (
                  <div
                    key={activity.id}
                    className="flex items-center justify-between p-3 border rounded-lg"
                  >
                    <div className="flex items-center gap-3">
                      <div className="h-8 w-8 rounded-full bg-muted flex items-center justify-center">
                        {activity.type === 'review' && (
                          <Play className="h-4 w-4" />
                        )}
                        {activity.type === 'create_deck' && (
                          <BookOpen className="h-4 w-4" />
                        )}
                        {activity.type === 'create_card' && (
                          <FileText className="h-4 w-4" />
                        )}
                        {activity.type === 'import' && (
                          <Upload className="h-4 w-4" />
                        )}
                      </div>
                      <div>
                        <p className="text-sm font-medium">{activity.message}</p>
                        <p className="text-xs text-muted-foreground">
                          {new Date(activity.timestamp).toLocaleDateString()}
                        </p>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <EmptyState
                message="No recent activity"
                description="Start reviewing or creating cards to see your activity here"
                actionLabel="Start Review"
                onAction={() => navigate(ROUTES.REVIEW)}
              />
            )}
          </Section>

          {/* Activity Chart */}
          {stats.reviewsPast7Days.some((count) => count > 0) && (
            <Section title="Activity (Last 7 Days)">
              <div className="space-y-4">
                <div className="grid grid-cols-7 gap-2">
                  {stats.reviewsPast7Days.map((count, index) => {
                    const maxCount = Math.max(...stats.reviewsPast7Days, 1)
                    const percentage = (count / maxCount) * 100
                    const date = new Date()
                    date.setDate(date.getDate() - (6 - index))
                    const dayLabel = date.toLocaleDateString('en-US', {
                      weekday: 'short',
                    })

                    return (
                      <div key={index} className="space-y-2">
                        <div className="text-center text-xs font-medium">
                          {count}
                        </div>
                        <div className="h-32 flex items-end">
                          <div
                            className="w-full bg-primary rounded-t transition-all"
                            style={{ height: `${percentage}%` }}
                            title={`${dayLabel}: ${count} reviews`}
                          />
                        </div>
                        <div className="text-center text-xs text-muted-foreground">
                          {dayLabel}
                        </div>
                      </div>
                    )
                  })}
                </div>
              </div>
            </Section>
          )}

          {/* Recent Decks */}
          {recentDecks.length > 0 && (
            <Section
              title="Recent Decks"
              actions={
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => navigate(ROUTES.DECKS)}
                >
                  View All
                </Button>
              }
            >
              <DeckList
                decks={recentDecks.map((deck) => ({
                  id: deck.id,
                  name: deck.name,
                  cardCount: deck.cardCount,
                  dueCards: deck.dueCards,
                }))}
                onClick={(deckId) => navigate(`${ROUTES.DECKS}/${deckId}`)}
                gridCols={3}
              />
            </Section>
          )}

          {/* Empty State - First Time User */}
          {stats.totalCards === 0 && recentDecks.length === 0 && (
            <Section>
              <EmptyState
                message="Welcome to RepeatWise!"
                description="Get started by creating your first deck and adding some cards"
                icon={<BookOpen className="h-16 w-16" />}
                actionLabel="Create Your First Deck"
                onAction={() => navigate(ROUTES.DECKS)}
              />
            </Section>
          )}
        </div>
      </PageContainer>
    </Layout>
  )
}

