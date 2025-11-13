import { formatDistanceToNow } from 'date-fns'
import RefreshIcon from '@mui/icons-material/Refresh'
import CachedIcon from '@mui/icons-material/Cached'
import EmojiEventsIcon from '@mui/icons-material/EmojiEvents'
import LibraryBooksIcon from '@mui/icons-material/LibraryBooks'
import InsertChartOutlinedIcon from '@mui/icons-material/InsertChartOutlined'
import WarningAmberIcon from '@mui/icons-material/WarningAmber'
import {
  Alert,
  Box,
  Chip,
  Grid,
  LinearProgress,
  Paper,
  Skeleton,
  Stack,
  Tooltip,
  Typography,
  useTheme,
  Button,
} from '@mui/material'
import { LoadingButton } from '@mui/lab'
import type { FolderTreeNode, FolderStatsDto } from '@/api/types/folder.types'

interface FolderStatsPanelProps {
  folder: FolderTreeNode | null
  stats: FolderStatsDto | null
  isLoading: boolean
  isRefreshing: boolean
  error: string | null
  onRefresh: () => Promise<unknown> | void
  onRetry: () => void
}

const numberFormatter = new Intl.NumberFormat()

const renderLoadingState = () => {
  const skeletonKeys = ['folders', 'decks', 'cards', 'progress'] as const

  return (
    <Paper
      elevation={0}
      sx={{
        borderRadius: 2,
        border: theme => `1px solid ${theme.palette.divider}`,
        p: 3,
      }}
    >
      <Stack spacing={3}>
        <Skeleton variant="text" width={160} height={32} />
        <Grid container spacing={2}>
          {skeletonKeys.map(item => (
            <Grid item xs={12} sm={6} md={3} key={item}>
              <Skeleton variant="rounded" height={90} />
            </Grid>
          ))}
        </Grid>
        <Skeleton variant="rounded" height={160} />
      </Stack>
    </Paper>
  )
}

export const FolderStatsPanel = ({
  folder,
  stats,
  isLoading,
  isRefreshing,
  error,
  onRefresh,
  onRetry,
}: FolderStatsPanelProps) => {
  const theme = useTheme()

  if (!folder) {
    return (
      <Paper
        elevation={0}
        sx={{
          borderRadius: 2,
          border: t => `1px dashed ${t.palette.divider}`,
          p: 3,
          textAlign: 'center',
        }}
      >
        <Typography variant="subtitle1" fontWeight={600} gutterBottom>
          Folder statistics
        </Typography>
        <Typography variant="body2" color="text.secondary">
          Select a folder from the tree to view its statistics.
        </Typography>
      </Paper>
    )
  }

  if (isLoading) {
    return renderLoadingState()
  }

  if (error) {
    return (
      <Alert
        severity="error"
        action={
          <Button color="inherit" size="small" onClick={onRetry}>
            Retry
          </Button>
        }
        sx={{
          borderRadius: 2,
          alignItems: 'center',
        }}
      >
        {error}
      </Alert>
    )
  }

  if (!stats) {
    return null
  }

  const totalCards = stats.totalCards ?? 0
  const progressSegments = [
    {
      label: 'New',
      value: stats.newCards,
      color: theme.palette.info.main,
    },
    {
      label: 'Learning',
      value: stats.learningCards,
      color: theme.palette.warning.main,
    },
    {
      label: 'Review',
      value: stats.reviewCards,
      color: theme.palette.primary.main,
    },
    {
      label: 'Mastered',
      value: stats.masteredCards,
      color: theme.palette.success.main,
    },
  ]

  const lastUpdatedLabel = stats.lastUpdatedAt
    ? formatDistanceToNow(new Date(stats.lastUpdatedAt), { addSuffix: true })
    : 'Not available'

  const dueSeverityColor =
    stats.dueCards > 0 ? theme.palette.warning.main : theme.palette.success.main

  const completionRateLabel =
    stats.completionRate !== undefined ? `${stats.completionRate.toFixed(1)}%` : '0%'

  const formattedMetrics = {
    folders: numberFormatter.format(stats.totalFolders ?? 0),
    decks: numberFormatter.format(stats.totalDecks ?? 0),
    cards: numberFormatter.format(totalCards),
    due: numberFormatter.format(stats.dueCards ?? 0),
  }

  return (
    <Paper
      elevation={0}
      sx={{
        borderRadius: 2,
        border: t => `1px solid ${t.palette.divider}`,
        p: 3,
      }}
    >
      <Stack spacing={3}>
        <Stack direction={{ xs: 'column', sm: 'row' }} justifyContent="space-between" alignItems="flex-start">
          <Stack spacing={0.5}>
            <Typography variant="h6" fontWeight={700}>
              {folder.name}
            </Typography>
            <Stack direction="row" spacing={1} alignItems="center">
              <InsertChartOutlinedIcon fontSize="small" color="action" />
              <Typography variant="body2" color="text.secondary">
                Comprehensive statistics for this folder and its descendants.
              </Typography>
              {stats.cached ? (
                <Tooltip title="Serving cached statistics (updated within the last 5 minutes)">
                  <Chip
                    size="small"
                    color="default"
                    icon={<CachedIcon fontSize="small" />}
                    label="Cached"
                    sx={{ fontWeight: 500 }}
                  />
                </Tooltip>
              ) : null}
            </Stack>
          </Stack>
          <LoadingButton
            variant="outlined"
            startIcon={<RefreshIcon />}
            loading={isRefreshing}
            onClick={() => void onRefresh()}
            color="primary"
            sx={{ mt: { xs: 2, sm: 0 } }}
          >
            Refresh
          </LoadingButton>
        </Stack>

        <Grid container spacing={2}>
          <Grid item xs={12} sm={6} md={3}>
            <Paper
              elevation={0}
              sx={{
                borderRadius: 2,
                border: t => `1px solid ${t.palette.divider}`,
                p: 2,
                height: '100%',
              }}
            >
              <Typography variant="body2" color="text.secondary" gutterBottom>
                Subfolders
              </Typography>
              <Stack direction="row" alignItems="center" spacing={1}>
                <InsertChartOutlinedIcon color="primary" />
                <Typography variant="h4" fontWeight={700}>
                  {formattedMetrics.folders}
                </Typography>
              </Stack>
              <Typography variant="caption" color="text.secondary">
                Total descendant folders
              </Typography>
            </Paper>
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <Paper
              elevation={0}
              sx={{
                borderRadius: 2,
                border: t => `1px solid ${t.palette.divider}`,
                p: 2,
                height: '100%',
              }}
            >
              <Typography variant="body2" color="text.secondary" gutterBottom>
                Decks
              </Typography>
              <Stack direction="row" alignItems="center" spacing={1}>
                <LibraryBooksIcon color="secondary" />
                <Typography variant="h4" fontWeight={700}>
                  {formattedMetrics.decks}
                </Typography>
              </Stack>
              <Typography variant="caption" color="text.secondary">
                Across all subfolders
              </Typography>
            </Paper>
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <Paper
              elevation={0}
              sx={{
                borderRadius: 2,
                border: t => `1px solid ${t.palette.divider}`,
                p: 2,
                height: '100%',
              }}
            >
              <Typography variant="body2" color="text.secondary" gutterBottom>
                Cards
              </Typography>
              <Stack direction="row" alignItems="center" spacing={1}>
                <EmojiEventsIcon color="info" />
                <Typography variant="h4" fontWeight={700}>
                  {formattedMetrics.cards}
                </Typography>
              </Stack>
              <Typography variant="caption" color="text.secondary">
                Active cards in this subtree
              </Typography>
            </Paper>
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <Paper
              elevation={0}
              sx={{
                borderRadius: 2,
                border: t => `1px solid ${t.palette.warning.light}`,
                p: 2,
                height: '100%',
                backgroundColor: theme.palette.mode === 'dark' ? 'rgba(255,193,7,0.12)' : 'rgba(255,193,7,0.08)',
              }}
            >
              <Typography variant="body2" color="text.secondary" gutterBottom>
                Due today
              </Typography>
              <Stack direction="row" alignItems="center" spacing={1}>
                <WarningAmberIcon sx={{ color: dueSeverityColor }} />
                <Typography variant="h4" fontWeight={700} sx={{ color: dueSeverityColor }}>
                  {formattedMetrics.due}
                </Typography>
              </Stack>
              <Typography variant="caption" color="text.secondary">
                Cards requiring review today
              </Typography>
            </Paper>
          </Grid>
        </Grid>

        <Paper
          elevation={0}
          sx={{
            borderRadius: 2,
            border: t => `1px solid ${t.palette.divider}`,
            p: 3,
          }}
        >
          <Stack direction={{ xs: 'column', md: 'row' }} justifyContent="space-between" spacing={2}>
            <Stack spacing={1} flex={1}>
              <Typography variant="subtitle2" color="text.secondary">
                Learning progress
              </Typography>
              {progressSegments.map(segment => {
                const percentage = totalCards > 0 ? Math.round((segment.value / totalCards) * 1000) / 10 : 0
                return (
                  <Box key={segment.label} sx={{ mb: 1.5 }}>
                    <Stack direction="row" justifyContent="space-between" alignItems="baseline">
                      <Typography variant="body2" fontWeight={600}>
                        {segment.label}
                      </Typography>
                      <Typography variant="caption" color="text.secondary">
                        {numberFormatter.format(segment.value)} ({percentage.toFixed(1)}%)
                      </Typography>
                    </Stack>
                    <LinearProgress
                      variant="determinate"
                      value={Math.min(percentage, 100)}
                      sx={{
                        mt: 0.5,
                        height: 8,
                        borderRadius: 4,
                        backgroundColor: theme.palette.mode === 'dark' ? 'rgba(255,255,255,0.08)' : 'rgba(0,0,0,0.06)',
                        '& .MuiLinearProgress-bar': {
                          borderRadius: 4,
                          backgroundColor: segment.color,
                        },
                      }}
                    />
                  </Box>
                )
              })}
            </Stack>
            <Paper
              elevation={0}
              sx={{
                borderRadius: 2,
                border: t => `1px solid ${t.palette.divider}`,
                p: 2.5,
                minWidth: 200,
                background:
                  theme.palette.mode === 'dark'
                    ? 'linear-gradient(135deg, rgba(144,202,249,0.15), rgba(25,118,210,0.08))'
                    : 'linear-gradient(135deg, rgba(25,118,210,0.12), rgba(25,118,210,0.05))',
              }}
            >
              <Typography variant="body2" color="text.secondary">
                Completion rate
              </Typography>
              <Typography variant="h4" fontWeight={700}>
                {completionRateLabel}
              </Typography>
              <Typography variant="caption" color="text.secondary">
                Based on mastered cards
              </Typography>
            </Paper>
          </Stack>
        </Paper>

        <Stack direction="row" spacing={1} alignItems="center">
          <Typography variant="caption" color="text.secondary">
            Last updated: {lastUpdatedLabel}
          </Typography>
        </Stack>
      </Stack>
    </Paper>
  )
}

export default FolderStatsPanel
