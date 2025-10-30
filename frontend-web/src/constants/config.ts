/**
 * Application Configuration
 */
export const APP_CONFIG = {
  APP_NAME: 'RepeatWise',
  APP_VERSION: '1.0.0',
  DEFAULT_LOCALE: 'vi',
  SUPPORTED_LOCALES: ['vi', 'en'],
  STORAGE_KEYS: {
    ACCESS_TOKEN: 'repeatwise_access_token',
    REFRESH_TOKEN: 'repeatwise_refresh_token',
    USER: 'repeatwise_user',
    THEME: 'repeatwise_theme',
    LANGUAGE: 'repeatwise_language',
  },
} as const
