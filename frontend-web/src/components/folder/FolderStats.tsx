/**
 * Folder Stats Component
 * 
 * Displays folder statistics (cards, decks, due cards, etc.)
 * 
 * Features:
 * - Total cards count
 * - Due cards count
 * - New cards count
 * - Mature cards count
 * - Visual indicators
 */

import * as React from 'react'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Progress } from '@/components/ui/progress'
import {
  FileText,
  Clock,
  Sparkles,
  CheckCircle2,
  TrendingUp,
} from 'lucide-react'
import { cn } from '@/lib/utils'

export interface FolderStatsData {
  totalCards?: number
  dueCards?: number
  newCards?: number
  matureCards?: number
  childrenCount?: number
  deckCount?: number
}

export interface FolderStatsProps {
  /** Statistics data */
  stats: FolderStatsData
  /** Show detailed breakdown */
  showDetails?: boolean
  /** Show progress bars */
  showProgress?: boolean
  /** Additional className */
  className?: string
}

export const FolderStats = React.memo<FolderStatsProps>(
  ({ stats, showDetails = true, showProgress = false, className }) => {
    const {
      totalCards = 0,
      dueCards = 0,
      newCards = 0,
      matureCards = 0,
      childrenCount = 0,
      deckCount = 0,
    } = stats

    const duePercentage =
      totalCards > 0 ? Math.round((dueCards / totalCards) * 100) : 0
    const newPercentage =
      totalCards > 0 ? Math.round((newCards / totalCards) * 100) : 0
    const maturePercentage =
      totalCards > 0 ? Math.round((matureCards / totalCards) * 100) : 0

    return (
      <Card className={cn('', className)}>
        <CardHeader>
          <CardTitle className="text-base">Statistics</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          {/* Overview Stats */}
          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-1">
              <div className="flex items-center gap-2 text-sm text-muted-foreground">
                <FileText className="h-4 w-4" />
                <span>Total Cards</span>
              </div>
              <p className="text-2xl font-bold">{totalCards}</p>
            </div>
            <div className="space-y-1">
              <div className="flex items-center gap-2 text-sm text-muted-foreground">
                <Clock className="h-4 w-4" />
                <span>Due Cards</span>
              </div>
              <p className="text-2xl font-bold text-orange-500">{dueCards}</p>
            </div>
          </div>

          {/* Progress Bars */}
          {showProgress && totalCards > 0 && (
            <div className="space-y-3">
              {dueCards > 0 && (
                <div className="space-y-1">
                  <div className="flex items-center justify-between text-xs">
                    <span className="text-muted-foreground">Due</span>
                    <span className="font-medium">{duePercentage}%</span>
                  </div>
                  <Progress value={duePercentage} className="h-2" />
                </div>
              )}
              {newCards > 0 && (
                <div className="space-y-1">
                  <div className="flex items-center justify-between text-xs">
                    <span className="text-muted-foreground">New</span>
                    <span className="font-medium">{newPercentage}%</span>
                  </div>
                  <Progress value={newPercentage} className="h-2" />
                </div>
              )}
              {matureCards > 0 && (
                <div className="space-y-1">
                  <div className="flex items-center justify-between text-xs">
                    <span className="text-muted-foreground">Mature</span>
                    <span className="font-medium">{maturePercentage}%</span>
                  </div>
                  <Progress value={maturePercentage} className="h-2" />
                </div>
              )}
            </div>
          )}

          {/* Detailed Breakdown */}
          {showDetails && (
            <div className="space-y-2 pt-2 border-t">
              <div className="flex items-center justify-between text-sm">
                <div className="flex items-center gap-2">
                  <Sparkles className="h-4 w-4 text-blue-500" />
                  <span className="text-muted-foreground">New Cards</span>
                </div>
                <Badge variant="outline">{newCards}</Badge>
              </div>
              <div className="flex items-center justify-between text-sm">
                <div className="flex items-center gap-2">
                  <CheckCircle2 className="h-4 w-4 text-green-500" />
                  <span className="text-muted-foreground">Mature Cards</span>
                </div>
                <Badge variant="outline">{matureCards}</Badge>
              </div>
              {(childrenCount > 0 || deckCount > 0) && (
                <div className="flex items-center justify-between text-sm pt-2 border-t">
                  <div className="flex items-center gap-2">
                    <TrendingUp className="h-4 w-4 text-muted-foreground" />
                    <span className="text-muted-foreground">Structure</span>
                  </div>
                  <div className="flex gap-2">
                    {childrenCount > 0 && (
                      <Badge variant="secondary" className="text-xs">
                        {childrenCount} folders
                      </Badge>
                    )}
                    {deckCount > 0 && (
                      <Badge variant="secondary" className="text-xs">
                        {deckCount} decks
                      </Badge>
                    )}
                  </div>
                </div>
              )}
            </div>
          )}
        </CardContent>
      </Card>
    )
  },
)

FolderStats.displayName = 'FolderStats'

