/**
 * Stats Page
 *
 * Displays user statistics and analytics
 *
 * Features:
 * - User statistics (total cards, streak, study time)
 * - Activity chart (past 7 days)
 * - Box distribution chart
 * - Review history
 */

import * as React from 'react'
import { useQuery } from '@tanstack/react-query'
import { Layout, PageContainer, Section } from '@/components/common/Layout'
import { Header } from '@/components/common/Header'
import { Sidebar } from '@/components/common/Sidebar'
import { Breadcrumb } from '@/components/common/Breadcrumb'
import { LoadingSpinner } from '@/components/common/LoadingSpinner'
import { EmptyState } from '@/components/common/EmptyState'
import { StatCard } from '@/components/common/Cards'
import { Button } from '@/components/ui/button'
import {
  FileText,
  Clock,
  Flame,
  TrendingUp,
  BarChart3,
  Calendar,
} from 'lucide-react'
import { ROUTES } from '@/constants/routes'
import { useAuth } from '@/hooks/domain/useAuth'

// Mock data types - replace with actual API types
interface UserStats {
  totalCardsLearned: number
  streakDays: number
  totalStudyTimeMinutes: number
  reviewsToday: number
  reviewsPast7Days: number[]
}

interface BoxDistribution {
  box: number
  count: number
}

// TODO: Replace with actual API when available
const mockStats: UserStats = {
  totalCardsLearned: 0,
  streakDays: 0,
  totalStudyTimeMinutes: 0,
  reviewsToday: 0,
  reviewsPast7Days: [0, 0, 0, 0, 0, 0, 0],
}

export function StatsPage() {
  const { user } = useAuth()

  // TODO: Fetch stats from API
  // const {
  //   data: stats,
  //   isLoading,
  //   error,
  // } = useQuery({
  //   queryKey: ['stats', 'user'],
  //   queryFn: () => statsApi.getUserStats(),
  // })

  const stats = mockStats
  const isLoading = false
  const error = null

  // TODO: Fetch box distribution from API
  // const {
  //   data: boxDistribution,
  //   isLoading: isLoadingBoxDistribution,
  // } = useQuery({
  //   queryKey: ['stats', 'box-distribution'],
  //   queryFn: () => statsApi.getBoxDistribution('ALL', null),
  // })

  const formatStudyTime = (minutes: number): string => {
    if (minutes < 60) {
      return `${minutes}m`
    }
    const hours = Math.floor(minutes / 60)
    const mins = minutes % 60
    return mins > 0 ? `${hours}h ${mins}m` : `${hours}h`
  }

  const getDayLabels = (): string[] => {
    const days = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']
    const today = new Date().getDay()
    return Array.from({ length: 7 }, (_, i) => {
      const dayIndex = (today - 6 + i + 7) % 7
      return days[dayIndex]
    })
  }

  if (isLoading) {
    return (
      <Layout>
        <Header {...(user && { user: { name: user.name, email: user.email } })} />
        <PageContainer sidebar={<Sidebar />}>
          <div className="flex items-center justify-center min-h-screen">
            <LoadingSpinner label="Loading statistics..." />
          </div>
        </PageContainer>
      </Layout>
    )
  }

  if (error) {
    return (
      <Layout>
        <Header {...(user && { user: { name: user.name, email: user.email } })} />
        <PageContainer sidebar={<Sidebar />}>
          <div className="flex items-center justify-center min-h-screen">
            <EmptyState
              message="Failed to load statistics"
              description="Unable to load your statistics. Please try again later."
            />
          </div>
        </PageContainer>
      </Layout>
    )
  }

  const breadcrumbItems = [{ label: 'Statistics' }]

  return (
    <Layout>
      <Header {...(user && { user: { name: user.name, email: user.email } })} />
      <PageContainer sidebar={<Sidebar />}>
        <div className="container mx-auto px-4 py-6 space-y-6">
          {/* Breadcrumb */}
          <Breadcrumb items={breadcrumbItems} />

          {/* Header */}
          <Section>
            <div className="flex items-center justify-between">
              <div>
                <h1 className="text-3xl font-bold">Statistics</h1>
                <p className="text-muted-foreground mt-1">
                  Track your learning progress and performance
                </p>
              </div>
            </div>
          </Section>

          {/* Key Statistics */}
          <Section title="Overview">
            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
              <StatCard
                title="Total Cards Learned"
                value={stats.totalCardsLearned}
                icon={<FileText className="h-4 w-4" />}
                description="Cards you've studied"
              />
              <StatCard
                title="Streak Days"
                value={stats.streakDays}
                icon={<Flame className="h-4 w-4" />}
                description="Consecutive study days"
                {...(stats.streakDays > 0 && {
                  trend: {
                    value: stats.streakDays,
                    label: 'days',
                    isPositive: true,
                  },
                })}
              />
              <StatCard
                title="Study Time"
                value={formatStudyTime(stats.totalStudyTimeMinutes)}
                icon={<Clock className="h-4 w-4" />}
                description="Total time spent studying"
              />
              <StatCard
                title="Reviews Today"
                value={stats.reviewsToday}
                icon={<TrendingUp className="h-4 w-4" />}
                description="Cards reviewed today"
              />
            </div>
          </Section>

          {/* Activity Chart */}
          <Section title="Activity (Past 7 Days)">
            <div className="space-y-4">
              <div className="flex items-center justify-between">
                <p className="text-sm text-muted-foreground">
                  Daily review count over the past week
                </p>
              </div>
              <div className="border rounded-lg p-6">
                <div className="flex items-end justify-between gap-2 h-48">
                  {stats.reviewsPast7Days.map((count, index) => {
                    const max = Math.max(...stats.reviewsPast7Days, 1)
                    const height = (count / max) * 100
                    const dayLabels = getDayLabels()
                    return (
                      <div
                        key={index}
                        className="flex flex-col items-center gap-2 flex-1"
                      >
                        <div className="flex flex-col items-center gap-1 w-full">
                          <div
                            className="w-full bg-primary rounded-t transition-all"
                            style={{ height: `${height}%`, minHeight: count > 0 ? '4px' : '0' }}
                            aria-label={`${dayLabels[index]}: ${count} reviews`}
                          />
                        </div>
                        <span className="text-xs text-muted-foreground">
                          {dayLabels[index]}
                        </span>
                        <span className="text-xs font-medium">{count}</span>
                      </div>
                    )
                  })}
                </div>
              </div>
            </div>
          </Section>

          {/* Box Distribution */}
          <Section title="Box Distribution">
            <div className="space-y-4">
              <p className="text-sm text-muted-foreground">
                Distribution of cards across SRS boxes
              </p>
              <div className="border rounded-lg p-6">
                <div className="flex items-center justify-center h-48">
                  <EmptyState
                    message="Box distribution coming soon"
                    description="Visualization of your card distribution across SRS boxes will be available here."
                    icon={<BarChart3 className="h-12 w-12" />}
                  />
                </div>
              </div>
            </div>
          </Section>

          {/* Study Calendar */}
          <Section title="Study Calendar">
            <div className="space-y-4">
              <p className="text-sm text-muted-foreground">
                Your study activity calendar
              </p>
              <div className="border rounded-lg p-6">
                <div className="flex items-center justify-center h-48">
                  <EmptyState
                    message="Study calendar coming soon"
                    description="A calendar view of your study activity will be available here."
                    icon={<Calendar className="h-12 w-12" />}
                  />
                </div>
              </div>
            </div>
          </Section>
        </div>
      </PageContainer>
    </Layout>
  )
}

// Default export for compatibility
export default StatsPage

