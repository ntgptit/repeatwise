import { useEffect, type JSX } from 'react'

import { Button } from '@/components/ui/button'
import { changeLanguage } from '@/lib/i18n'
import { useUiStore } from '@/store'
import { useTheme } from 'next-themes'
import { useTranslation } from 'react-i18next'

export const HomeRoute = (): JSX.Element => {
  const { t, i18n } = useTranslation('common')
  const { theme, setTheme } = useTheme()
  const { language, setLanguage, themePreference, setThemePreference } = useUiStore(state => ({
    language: state.language,
    setLanguage: state.setLanguage,
    themePreference: state.theme,
    setThemePreference: state.setThemePreference,
  }))

  useEffect(() => {
    if (i18n.language !== language) {
      changeLanguage(language).catch(error => {
        console.error('Failed to change language', error)
      })
    }
  }, [i18n, language])

  useEffect(() => {
    if (!themePreference || theme === themePreference) {
      return
    }

    setTheme(themePreference)
  }, [setTheme, theme, themePreference])

  const toggleTheme = (): void => {
    const nextTheme = theme === 'dark' ? 'light' : 'dark'
    setTheme(nextTheme)
    setThemePreference(nextTheme)
  }

  const toggleLanguage = (): void => {
    const nextLanguage = language === 'en' ? 'vi' : 'en'
    setLanguage(nextLanguage)
  }

  return (
    <main className="flex min-h-screen flex-col items-center justify-center bg-background px-6 py-16 text-foreground">
      <div className="flex max-w-2xl flex-col items-center gap-6 text-center">
        <span className="rounded-full bg-primary/10 px-4 py-1 text-sm font-medium text-primary">
          RepeatWise Design System
        </span>
        <h1 className="text-4xl font-bold tracking-tight sm:text-5xl">{t('welcome')}</h1>
        <p className="text-muted-foreground">
          React Query, Zustand, theming, and i18n have been wired together. Start building product
          features with confidence that the core experience is consistent across light and dark
          themes as well as Vietnamese and English locales.
        </p>
        <div className="flex flex-wrap items-center justify-center gap-4">
          <Button onClick={toggleTheme} variant="secondary">
            {t('theme')}
          </Button>
          <Button onClick={toggleLanguage}>{t('language')}</Button>
        </div>
      </div>
    </main>
  )
}

export default HomeRoute

