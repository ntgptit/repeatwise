import EditIcon from '@mui/icons-material/EditOutlined'
import DeleteIcon from '@mui/icons-material/DeleteOutline'
import { Chip, IconButton, Paper, Stack, Typography, Tooltip } from '@mui/material'
import { formatDistanceToNowStrict } from 'date-fns'
import { useMemo } from 'react'
import type { CardDto } from '@/api/types/card.types'

interface CardListItemProps {
  card: CardDto
  index: number
  onEdit: (card: CardDto) => void
  onDelete: (card: CardDto) => void
}

const formatRelativeTime = (value: string) => {
  try {
    return formatDistanceToNowStrict(new Date(value), { addSuffix: true })
  } catch {
    return 'Unknown'
  }
}

export const CardListItem = ({ card, index, onEdit, onDelete }: CardListItemProps) => {
  const updatedLabel = useMemo(() => formatRelativeTime(card.updatedAt), [card.updatedAt])

  return (
    <Paper
      variant="outlined"
      sx={{
        borderRadius: 2,
        p: 2,
        display: 'flex',
        flexDirection: 'column',
        gap: 1.5,
      }}
    >
      <Stack direction="row" alignItems="center" justifyContent="space-between">
        <Stack direction="row" spacing={1} alignItems="baseline">
          <Typography variant="subtitle2" color="text.secondary">
            #{index + 1}
          </Typography>
          <Chip label={updatedLabel} size="small" color="primary" variant="outlined" />
        </Stack>
        <Stack direction="row" spacing={1}>
          <Tooltip title="Edit card">
            <IconButton size="small" onClick={() => onEdit(card)}>
              <EditIcon fontSize="small" />
            </IconButton>
          </Tooltip>
          <Tooltip title="Delete card">
            <IconButton size="small" color="error" onClick={() => onDelete(card)}>
              <DeleteIcon fontSize="small" />
            </IconButton>
          </Tooltip>
        </Stack>
      </Stack>
      <Stack spacing={1}>
        <Typography variant="body2" color="text.secondary">
          Front
        </Typography>
        <Typography
          variant="body1"
          sx={{
            borderRadius: 1,
            border: theme => `1px solid ${theme.palette.divider}`,
            p: 1.5,
            backgroundColor: theme => theme.palette.background.default,
            whiteSpace: 'pre-wrap',
          }}
        >
          {card.front}
        </Typography>
      </Stack>
      <Stack spacing={1}>
        <Typography variant="body2" color="text.secondary">
          Back
        </Typography>
        <Typography
          variant="body1"
          sx={{
            borderRadius: 1,
            border: theme => `1px solid ${theme.palette.divider}`,
            p: 1.5,
            backgroundColor: theme => theme.palette.background.default,
            whiteSpace: 'pre-wrap',
          }}
        >
          {card.back}
        </Typography>
      </Stack>
    </Paper>
  )
}

export default CardListItem

