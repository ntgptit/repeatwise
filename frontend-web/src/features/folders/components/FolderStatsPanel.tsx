import { Box, Grid, Paper, Typography } from '@mui/material'

interface StatItem {
  label: string
  value: string
  subLabel?: string
}

const defaultStats: StatItem[] = [
  { label: 'Total decks', value: '—', subLabel: 'Coming soon' },
  { label: 'Total cards', value: '—', subLabel: 'Coming soon' },
  { label: 'Due cards', value: '—', subLabel: 'Coming soon' },
  { label: 'New cards', value: '—', subLabel: 'Coming soon' },
]

export const FolderStatsPanel = ({ stats = defaultStats }: { stats?: StatItem[] }) => {
  return (
    <Box>
      <Typography variant="subtitle1" fontWeight={600} gutterBottom>
        Folder statistics
      </Typography>
      <Grid container spacing={2}>
        {stats.map((item) => (
          <Grid item xs={12} sm={6} md={3} key={item.label}>
            <Paper
              elevation={0}
              sx={{
                borderRadius: 2,
                border: (theme) => `1px solid ${theme.palette.divider}`,
                p: 2,
              }}
            >
              <Typography variant="body2" color="text.secondary">
                {item.label}
              </Typography>
              <Typography variant="h5" fontWeight={700}>
                {item.value}
              </Typography>
              {item.subLabel ? (
                <Typography variant="caption" color="text.secondary">
                  {item.subLabel}
                </Typography>
              ) : null}
            </Paper>
          </Grid>
        ))}
      </Grid>
    </Box>
  )
}

export default FolderStatsPanel

