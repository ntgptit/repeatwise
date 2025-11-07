package com.repeatwise.exception.srs;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;

import com.repeatwise.constant.ApiErrorCode;
import com.repeatwise.exception.base.BaseException;

/**
 * Exception thrown when attempting to review a card that is not due
 */

public class CardNotDueForReviewException extends BaseException {

    private static final long serialVersionUID = 1L;

    public CardNotDueForReviewException(Object cardId, LocalDate dueDate) {
        super("error.review.card.not.due", ApiErrorCode.CARD_NOT_DUE_FOR_REVIEW, cardId, dueDate);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
