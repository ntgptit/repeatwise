import AppRoutes from 'src/router'
import { CssBaseline } from '@mui/material'
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider'
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns'
import ThemeProvider from './theme/ThemeProvider'
import NotificationCenter from '@/common/components/NotificationCenter'

function App() {
  return (
    <ThemeProvider>
      <LocalizationProvider dateAdapter={AdapterDateFns}>
        <CssBaseline />
        <NotificationCenter />
        <AppRoutes />
      </LocalizationProvider>
    </ThemeProvider>
  )
}
export default App
