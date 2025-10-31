/**
 * Route Constants
 * 
 * Centralized route definitions for the application
 */

export const ROUTES = {
  // Public routes
  HOME: '/',
  LOGIN: '/login',
  REGISTER: '/register',
  
  // Protected routes
  DASHBOARD: '/dashboard',
  DECKS: '/decks',
  DECK_DETAIL: (id: string) => `/decks/${id}`,
  FOLDERS: '/folders',
  FOLDER_DETAIL: (id: string) => `/folders/${id}`,
  REVIEW: '/review',
  REVIEW_SESSION: (sessionId: string) => `/review/${sessionId}`,
  STATS: '/stats',
  SETTINGS: '/settings',
  PROFILE: '/profile',
} as const

export type RouteKey = keyof typeof ROUTES

