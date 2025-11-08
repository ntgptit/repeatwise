import { I18nextProvider } from 'react-i18next'
import { Suspense, useEffect, useState, type JSX, type ReactNode } from 'react'

import { initI18n, i18n } from '@/lib/i18n'

type I18nProviderProps = {
  children: ReactNode
  fallback?: JSX.Element | null
}

export const I18nProvider = ({
  children,
  fallback = null,
}: I18nProviderProps): JSX.Element | null => {
  const [ready, setReady] = useState(() => i18n.isInitialized)

  useEffect(() => {
    if (i18n.isInitialized) {
      return
    }

    initI18n()
      .then(() => setReady(true))
      .catch(error => {
        console.error('Failed to initialize i18n', error)
      })
  }, [])

  if (!ready) {
    return fallback ?? null
  }

  return (
    <I18nextProvider i18n={i18n}>
      <Suspense fallback={fallback}>{children}</Suspense>
    </I18nextProvider>
  )
}

