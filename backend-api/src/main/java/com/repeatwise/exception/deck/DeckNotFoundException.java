package com.repeatwise.exception.deck;

import org.springframework.http.HttpStatus;

import com.repeatwise.constant.ApiErrorCode;
import com.repeatwise.exception.base.BaseException;

/**
 * Exception thrown when deck is not found
 */

public class DeckNotFoundException extends BaseException {

    private static final long serialVersionUID = 1L;

    public DeckNotFoundException(Object deckId) {
        super("error.deck.not.found", ApiErrorCode.DECK_NOT_FOUND, deckId);
    }

    public DeckNotFoundException(String message) {
        super(message, ApiErrorCode.DECK_NOT_FOUND);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
