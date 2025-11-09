import i18next, { type i18n as I18nInstance } from 'i18next'
import { initReactI18next } from 'react-i18next'

import enTranslation from '@/locales/en/translation.json'
import viTranslation from '@/locales/vi/translation.json'
import { STORAGE_KEYS } from '@/config/app.config'
import { i18nConfig } from '@/config/services.config'
import { localStorageService } from '@/common/services'

type SupportedLanguage = (typeof i18nConfig.supportedLanguages)[number]

const resources = {
  en: {
    common: enTranslation.common,
    auth: enTranslation.auth,
    folder: enTranslation.folder,
    deck: enTranslation.deck,
    review: enTranslation.review,
  },
  vi: {
    common: viTranslation.common,
    auth: viTranslation.auth,
    folder: viTranslation.folder,
    deck: viTranslation.deck,
    review: viTranslation.review,
  },
} satisfies Record<SupportedLanguage, Record<string, Record<string, string>>>

const isBrowser = typeof window !== 'undefined'

const getStoredLanguage = (): SupportedLanguage | undefined => {
  if (!isBrowser) {
    return undefined
  }

  const stored = localStorageService.get<string>(STORAGE_KEYS.LANGUAGE)
  if (!stored) {
    return undefined
  }

  return i18nConfig.supportedLanguages.find(language => language === stored) as
    | SupportedLanguage
    | undefined
}

const detectBrowserLanguage = (): SupportedLanguage | undefined => {
  if (typeof navigator === 'undefined') {
    return undefined
  }

  const browserLanguage = navigator.language.split('-')[0]
  return i18nConfig.supportedLanguages.find(language => language === browserLanguage) as
    | SupportedLanguage
    | undefined
}

const resolveInitialLanguage = (): SupportedLanguage => {
  const storedLanguage = getStoredLanguage()
  if (storedLanguage) {
    return storedLanguage
  }

  if (i18nConfig.autoDetect) {
    const browserLanguage = detectBrowserLanguage()
    if (browserLanguage) {
      return browserLanguage
    }
  }

  return i18nConfig.defaultLanguage as SupportedLanguage
}

export const initI18n = (): Promise<I18nInstance> => {
  if (i18next.isInitialized) {
    return Promise.resolve(i18next)
  }

  const initialLanguage = resolveInitialLanguage()

  return i18next.use(initReactI18next).init({
    resources,
    lng: initialLanguage,
    fallbackLng: i18nConfig.fallbackLanguage,
    supportedLngs: i18nConfig.supportedLanguages,
    ns: ['common', 'auth', 'folder', 'deck', 'review'],
    defaultNS: 'common',
    interpolation: {
      escapeValue: false,
    },
    returnEmptyString: false,
  })
}

export const changeLanguage = async (language: SupportedLanguage): Promise<void> => {
  if (!i18nConfig.supportedLanguages.includes(language)) {
    throw new Error(`Unsupported language: ${language}`)
  }

  await i18next.changeLanguage(language)
  if (isBrowser) {
    localStorageService.set(STORAGE_KEYS.LANGUAGE, language)
  }
}

export const i18n = i18next

export default i18n

