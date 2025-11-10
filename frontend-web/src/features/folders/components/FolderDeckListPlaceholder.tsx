import { Box, Divider, Stack, Typography } from '@mui/material'

export const FolderDeckListPlaceholder = () => {
  return (
    <Box
      sx={{
        borderRadius: 2,
        border: (theme) => `1px solid ${theme.palette.divider}`,
        p: 3,
        backgroundColor: 'background.paper',
      }}
    >
      <Stack spacing={2}>
        <Typography variant="h6">Deck list</Typography>
        <Divider />
        <Typography variant="body2" color="text.secondary">
          This section will display decks within the selected folder once the backend endpoints are
          available. For now, a placeholder message is shown.
        </Typography>
      </Stack>
    </Box>
  )
}

export default FolderDeckListPlaceholder

