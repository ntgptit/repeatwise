import { api } from '@/services/api'
import { API_ENDPOINTS } from '@/constants/api'

/**
 * Example usage of the API client
 * 
 * This file demonstrates how to use the API client in your services
 */

// Example: Auth Service
export const authService = {
  async login(email: string, password: string) {
    const response = await api.post<{ accessToken: string; refreshToken: string }>(
      API_ENDPOINTS.AUTH.LOGIN,
      { email, password },
      { skipAuth: true }, // Skip auth token for login
    )
    return response.data
  },

  async register(userData: {
    email: string
    password: string
    name: string
  }) {
    const response = await api.post<{ id: string }>(
      API_ENDPOINTS.AUTH.REGISTER,
      userData,
      { skipAuth: true }, // Skip auth token for register
    )
    return response.data
  },

  async getProfile() {
    const response = await api.get<{ id: string; email: string; name: string }>(
      API_ENDPOINTS.AUTH.PROFILE,
    )
    return response.data
  },

  async logout() {
    await api.post(API_ENDPOINTS.AUTH.LOGOUT)
  },
}

// Example: Card Service
export const cardService = {
  async getCards(deckId?: string) {
    const url = deckId
      ? API_ENDPOINTS.CARDS.BY_DECK(deckId)
      : API_ENDPOINTS.CARDS.BASE
    const response = await api.get<Array<{ id: string; front: string; back: string }>>(
      url,
    )
    return response.data
  },

  async getCardById(id: string) {
    const response = await api.get<{ id: string; front: string; back: string }>(
      API_ENDPOINTS.CARDS.BY_ID(id),
    )
    return response.data
  },

  async createCard(cardData: { front: string; back: string; deckId: string }) {
    const response = await api.post<{ id: string }>(
      API_ENDPOINTS.CARDS.BASE,
      cardData,
    )
    return response.data
  },

  async updateCard(id: string, cardData: { front?: string; back?: string }) {
    const response = await api.put<{ id: string }>(
      API_ENDPOINTS.CARDS.BY_ID(id),
      cardData,
    )
    return response.data
  },

  async deleteCard(id: string) {
    await api.delete(API_ENDPOINTS.CARDS.BY_ID(id))
  },
}

// Example: Deck Service
export const deckService = {
  async getDecks(folderId?: string) {
    const url = folderId
      ? API_ENDPOINTS.DECKS.BY_FOLDER(folderId)
      : API_ENDPOINTS.DECKS.BASE
    const response = await api.get<Array<{ id: string; name: string }>>(url)
    return response.data
  },

  async getDeckById(id: string) {
    const response = await api.get<{ id: string; name: string; cardCount: number }>(
      API_ENDPOINTS.DECKS.BY_ID(id),
    )
    return response.data
  },

  async createDeck(deckData: { name: string; folderId?: string }) {
    const response = await api.post<{ id: string }>(
      API_ENDPOINTS.DECKS.BASE,
      deckData,
    )
    return response.data
  },
}

// Example: Using with error handling
export async function exampleWithErrorHandling() {
  try {
    const cards = await cardService.getCards()
    console.log('Cards:', cards)
  } catch (error) {
    // Error is already handled by interceptor and shown via toast
    // But you can add additional handling here if needed
    console.error('Failed to fetch cards:', error)
    throw error
  }
}

// Example: Using with custom config
export async function exampleWithCustomConfig() {
  // Skip error handler for this specific request
  const response = await api.get('/some-endpoint', {
    skipErrorHandler: true,
  })

  // Custom timeout
  const response2 = await api.get('/slow-endpoint', {
    timeout: 60000, // 60 seconds
  })

  return response
}
