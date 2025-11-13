export interface CardDto {
  id: string
  deckId: string
  front: string
  back: string
  createdAt: string
  updatedAt: string
}

export interface CreateCardRequest {
  deckId: string
  front: string
  back: string
}

export interface UpdateCardRequest {
  front?: string
  back?: string
}

export interface DeleteCardResponse {
  cardId: string
  message: string
  deletedAt: string
}

