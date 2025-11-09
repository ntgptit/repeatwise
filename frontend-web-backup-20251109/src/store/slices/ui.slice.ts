import { localStorageService } from '@/common/services'
import { STORAGE_KEYS } from '@/config/app.config'
import { i18nConfig, themeConfig } from '@/config/services.config'
import type { StateCreator } from 'zustand'

export type SupportedLanguage = (typeof i18nConfig.supportedLanguages)[number]
export type SupportedTheme = (typeof themeConfig.supportedThemes)[number]

export type UiSlice = {
  language: SupportedLanguage
  theme: SupportedTheme
  sidebarCollapsed: boolean
  setLanguage: (language: SupportedLanguage) => void
  setThemePreference: (theme: SupportedTheme) => void
  toggleSidebar: () => void
  setSidebarCollapsed: (collapsed: boolean) => void
}

const isBrowser = typeof window !== 'undefined'

const resolveStoredLanguage = (): SupportedLanguage => {
  if (!isBrowser) {
    return i18nConfig.defaultLanguage as SupportedLanguage
  }

  const stored = localStorageService.get<SupportedLanguage>(STORAGE_KEYS.LANGUAGE)
  if (stored && i18nConfig.supportedLanguages.includes(stored)) {
    return stored
  }
  return i18nConfig.defaultLanguage as SupportedLanguage
}

const resolveStoredTheme = (): SupportedTheme => {
  if (!isBrowser) {
    return themeConfig.defaultTheme
  }

  const stored = localStorageService.get<SupportedTheme>(STORAGE_KEYS.THEME)
  if (stored && themeConfig.supportedThemes.includes(stored)) {
    return stored
  }
  return themeConfig.defaultTheme
}

const resolveSidebarState = (): boolean => {
  if (!isBrowser) {
    return false
  }

  const stored = localStorageService.get<boolean>(STORAGE_KEYS.SIDEBAR_COLLAPSED)
  return stored ?? false
}

export const createUiSlice: StateCreator<UiSlice> = set => ({
  language: resolveStoredLanguage(),
  theme: resolveStoredTheme(),
  sidebarCollapsed: resolveSidebarState(),
  setLanguage: language => {
    if (isBrowser) {
      localStorageService.set(STORAGE_KEYS.LANGUAGE, language)
    }
    set({ language })
  },
  setThemePreference: theme => {
    if (isBrowser) {
      localStorageService.set(STORAGE_KEYS.THEME, theme)
    }
    set({ theme })
  },
  toggleSidebar: () => {
    set(state => {
      const nextValue = !state.sidebarCollapsed
      if (isBrowser) {
        localStorageService.set(STORAGE_KEYS.SIDEBAR_COLLAPSED, nextValue)
      }
      return { sidebarCollapsed: nextValue }
    })
  },
  setSidebarCollapsed: collapsed => {
    if (isBrowser) {
      localStorageService.set(STORAGE_KEYS.SIDEBAR_COLLAPSED, collapsed)
    }
    set({ sidebarCollapsed: collapsed })
  },
})

