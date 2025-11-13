import { useMemo } from 'react'
import RefreshIcon from '@mui/icons-material/Refresh'
import AddIcon from '@mui/icons-material/Add'
import LocationOnIcon from '@mui/icons-material/LocationOnOutlined'
import {
  Alert,
  Box,
  Button,
  Divider,
  Grid,
  Skeleton,
  Stack,
  ToggleButton,
  ToggleButtonGroup,
  Typography,
} from '@mui/material'
import type { DeckDto } from '@/api/types/deck.types'
import { DeckCard } from '@/features/decks/components/DeckCard'

export type DeckScope = 'selected' | 'root'

interface DeckListPanelProps {
  decks: DeckDto[]
  isLoading: boolean
  isFetching: boolean
  error: string | null
  scope: DeckScope
  hasFolderSelection: boolean
  currentFolderName: string | null
  onScopeChange: (scope: DeckScope) => void
  onCreateDeck: () => void
  onRetry: () => void
  onEditDeck: (deck: DeckDto) => void
  onMoveDeck: (deck: DeckDto) => void
  onCopyDeck: (deck: DeckDto) => void
  onDeleteDeck: (deck: DeckDto) => void
  onManageCards: (deck: DeckDto) => void
}

const skeletonItems = [1, 2, 3, 4]

export const DeckListPanel = ({
  decks,
  isLoading,
  isFetching,
  error,
  scope,
  hasFolderSelection,
  currentFolderName,
  onScopeChange,
  onCreateDeck,
  onRetry,
  onEditDeck,
  onMoveDeck,
  onCopyDeck,
  onDeleteDeck,
  onManageCards,
}: DeckListPanelProps) => {
  const headline = useMemo(() => {
    if (scope === 'selected' && currentFolderName) {
      return `Decks in “${currentFolderName}”`
    }
    return 'Root level decks'
  }, [scope, currentFolderName])

  const subtitle = useMemo(() => {
    if (scope === 'selected' && currentFolderName) {
      return 'Manage learning decks under the currently selected folder.'
    }
    return 'Manage decks that sit at the root level.'
  }, [scope, currentFolderName])

  const handleScopeChange = (_: unknown, nextScope: DeckScope | null) => {
    if (!nextScope) {
      return
    }
    if (nextScope === 'selected' && !hasFolderSelection) {
      return
    }
    onScopeChange(nextScope)
  }

  const showEmptyState = !isLoading && !error && decks.length === 0

  return (
    <Box
      sx={{
        borderRadius: 2,
        border: theme => `1px solid ${theme.palette.divider}`,
        backgroundColor: 'background.paper',
        p: 3,
        minHeight: 420,
      }}
    >
      <Stack spacing={3}>
        <Stack direction="row" justifyContent="space-between" alignItems="flex-start" spacing={2}>
          <Stack spacing={1}>
            <Typography variant="h6" fontWeight={700}>
              {headline}
            </Typography>
            <Stack direction="row" spacing={1} alignItems="center">
              <LocationOnIcon fontSize="small" color="primary" />
              <Typography variant="body2" color="text.secondary">
                {subtitle}
              </Typography>
            </Stack>
          </Stack>
          <Stack direction="row" spacing={1}>
            <ToggleButtonGroup
              size="small"
              color="primary"
              exclusive
              value={scope}
              onChange={handleScopeChange}
            >
              <ToggleButton value="selected" disabled={!hasFolderSelection}>
                Folder
              </ToggleButton>
              <ToggleButton value="root">Root</ToggleButton>
            </ToggleButtonGroup>
            <Button
              variant="contained"
              startIcon={<AddIcon />}
              onClick={onCreateDeck}
              disableElevation
            >
              New deck
            </Button>
          </Stack>
        </Stack>

        <Divider />

        {error ? (
          <Alert
            severity="error"
            action={
              <Button color="inherit" size="small" onClick={onRetry} startIcon={<RefreshIcon />}>
                Retry
              </Button>
            }
          >
            {error}
          </Alert>
        ) : null}

        {isLoading ? (
          <Grid container spacing={2}>
            {skeletonItems.map(item => (
              <Grid item xs={12} sm={6} key={item}>
                <Skeleton variant="rectangular" height={220} sx={{ borderRadius: 2 }} />
              </Grid>
            ))}
          </Grid>
        ) : null}

        {!isLoading && !error
          ? showEmptyState
            ? (
                <Stack
                  spacing={2}
                  alignItems="center"
                  justifyContent="center"
                  sx={{ py: 8, borderRadius: 2, border: theme => `1px dashed ${theme.palette.divider}` }}
                >
                  <Typography variant="h6" fontWeight={600}>
                    No decks yet
                  </Typography>
                  <Typography variant="body2" color="text.secondary" textAlign="center" maxWidth={420}>
                    Create your first deck to start organizing flashcards. Decks help you group cards by
                    topic, exam or difficulty level.
                  </Typography>
                  <Button variant="contained" startIcon={<AddIcon />} onClick={onCreateDeck}>
                    Create deck
                  </Button>
                </Stack>
              )
            : (
                <Grid container spacing={2}>
                  {decks.map(deck => (
                    <Grid item xs={12} sm={6} key={deck.id}>
                      <DeckCard
                        deck={deck}
                        onEdit={onEditDeck}
                        onMove={onMoveDeck}
                        onCopy={onCopyDeck}
                        onDelete={onDeleteDeck}
                        onManageCards={onManageCards}
                      />
                    </Grid>
                  ))}
                </Grid>
              )
          : null}

        {isFetching && !isLoading && !error ? (
          <Typography variant="caption" color="text.secondary">
            Refreshing deck list…
          </Typography>
        ) : null}
      </Stack>
    </Box>
  )
}

export default DeckListPanel

