package com.repeatwise.exception.deck;

import org.springframework.http.HttpStatus;

import com.repeatwise.constant.ApiErrorCode;
import com.repeatwise.exception.base.BaseException;

/**
 * Exception thrown when deck name already exists in folder
 */

public class DeckNameAlreadyExistsException extends BaseException {

    private static final long serialVersionUID = 1L;

    public DeckNameAlreadyExistsException(String deckName) {
        super("error.deck.name.exists", ApiErrorCode.DECK_NAME_ALREADY_EXISTS, deckName);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
