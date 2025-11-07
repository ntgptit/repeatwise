package com.repeatwise.exception.card;

import org.springframework.http.HttpStatus;

import com.repeatwise.constant.ApiErrorCode;
import com.repeatwise.exception.base.BaseException;

/**
 * Exception thrown when card is not found
 */

public class CardNotFoundException extends BaseException {

    private static final long serialVersionUID = 1L;

    public CardNotFoundException(Object cardId) {
        super("error.card.not.found", ApiErrorCode.CARD_NOT_FOUND, cardId);
    }

    public CardNotFoundException(String message) {
        super(message, ApiErrorCode.CARD_NOT_FOUND);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
