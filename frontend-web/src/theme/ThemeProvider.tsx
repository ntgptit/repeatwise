import {
  createContext,
  type ReactElement,
  type ReactNode,
  useCallback,
  useMemo,
  useState,
} from 'react'
import { ThemeProvider } from '@mui/material'
import { StylesProvider } from '@mui/styles'
import { themeCreator } from './base'

type ThemeContextValue = (themeName: string) => void

const ThemeContext = createContext<ThemeContextValue | null>(null)

type ThemeProviderWrapperProps = {
  children: ReactNode
}

export default function ThemeProviderWrapper({
  children,
}: ThemeProviderWrapperProps): ReactElement {
  const curThemeName = localStorage.getItem('appTheme') || 'PureLightTheme'
  const [themeName, setThemeNameState] = useState(curThemeName)
  const theme = useMemo(() => themeCreator(themeName), [themeName])

  const setThemeName = useCallback<ThemeContextValue>(nextThemeName => {
    localStorage.setItem('appTheme', nextThemeName)
    setThemeNameState(nextThemeName)
  }, [])

  return (
    <StylesProvider injectFirst>
      <ThemeContext.Provider value={setThemeName}>
        <ThemeProvider theme={theme}>{children}</ThemeProvider>
      </ThemeContext.Provider>
    </StylesProvider>
  )
}
