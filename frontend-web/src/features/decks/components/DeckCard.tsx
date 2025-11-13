import { format } from 'date-fns'
import MoreVertIcon from '@mui/icons-material/MoreVert'
import CalendarMonthIcon from '@mui/icons-material/CalendarMonthOutlined'
import LibraryBooksIcon from '@mui/icons-material/LibraryBooksOutlined'
import { Box, Button, Card, CardActions, CardContent, IconButton, Menu, MenuItem, Stack, Typography } from '@mui/material'
import { useMemo, useState, type MouseEvent } from 'react'
import type { DeckDto } from '@/api/types/deck.types'

export interface DeckCardProps {
  deck: DeckDto
  onEdit: (deck: DeckDto) => void
  onMove: (deck: DeckDto) => void
  onCopy: (deck: DeckDto) => void
  onDelete: (deck: DeckDto) => void
  onManageCards: (deck: DeckDto) => void
}

const formatDate = (value: string) => {
  if (!value) {
    return '—'
  }
  try {
    return format(new Date(value), 'dd MMM yyyy')
  } catch {
    return '—'
  }
}

export const DeckCard = ({
  deck,
  onEdit,
  onMove,
  onCopy,
  onDelete,
  onManageCards,
}: DeckCardProps) => {
  const [anchorEl, setAnchorEl] = useState<HTMLElement | null>(null)
  const open = Boolean(anchorEl)

  const createdDate = useMemo(() => formatDate(deck.createdAt), [deck.createdAt])

  const handleMenuOpen = (event: MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget)
  }

  const handleMenuClose = () => {
    setAnchorEl(null)
  }

  const handleAction = (action: (deck: DeckDto) => void) => {
    action(deck)
    handleMenuClose()
  }

  return (
    <Card
      elevation={0}
      sx={{
        borderRadius: 3,
        border: theme => `1px solid ${theme.palette.divider}`,
        height: '100%',
        display: 'flex',
        flexDirection: 'column',
      }}
    >
      <CardContent sx={{ flexGrow: 1 }}>
        <Stack direction="row" alignItems="flex-start" justifyContent="space-between" spacing={1.5}>
          <Box sx={{ flexGrow: 1, minWidth: 0 }}>
            <Typography variant="h6" component="h3" fontWeight={700} noWrap title={deck.name}>
              {deck.name}
            </Typography>
            <Typography variant="body2" color="text.secondary" noWrap>
              {deck.folderId ? 'In selected folder' : 'Root level deck'}
            </Typography>
          </Box>
          <IconButton size="small" onClick={handleMenuOpen} aria-label="Deck actions">
            <MoreVertIcon fontSize="small" />
          </IconButton>
        </Stack>

        <Typography
          variant="body2"
          color={deck.description ? 'text.primary' : 'text.secondary'}
          sx={{ mt: 2, minHeight: 48 }}
        >
          {deck.description || 'No description yet. Add one to highlight the purpose of this deck.'}
        </Typography>

        <Stack direction="row" spacing={2} sx={{ mt: 3 }} alignItems="center">
          <Stack direction="row" spacing={1} alignItems="center">
            <LibraryBooksIcon fontSize="small" color="primary" />
            <Typography variant="body2" fontWeight={600}>
              {deck.cardCount} thẻ
            </Typography>
          </Stack>
          <Stack direction="row" spacing={1} alignItems="center">
            <CalendarMonthIcon fontSize="small" color="action" />
              <Typography variant="body2" color="text.secondary">
              Created {createdDate}
            </Typography>
          </Stack>
        </Stack>
      </CardContent>
      <CardActions sx={{ px: 3, pb: 3, pt: 0, gap: 1 }}>
        <Button variant="outlined" size="small" onClick={() => onManageCards(deck)}>
          Manage cards
        </Button>
        <Button variant="contained" size="small" disabled>
          Bắt đầu ôn tập
        </Button>
      </CardActions>

      <Menu anchorEl={anchorEl} open={open} onClose={handleMenuClose}>
        <MenuItem onClick={() => handleAction(onEdit)}>Edit</MenuItem>
        <MenuItem onClick={() => handleAction(onMove)}>Move</MenuItem>
        <MenuItem onClick={() => handleAction(onCopy)}>Copy</MenuItem>
        <MenuItem onClick={() => handleAction(onDelete)} sx={{ color: 'error.main' }}>
          Xóa
        </MenuItem>
      </Menu>
    </Card>
  )
}

export default DeckCard

