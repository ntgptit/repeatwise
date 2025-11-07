package com.repeatwise.exception.srs;

import org.springframework.http.HttpStatus;

import com.repeatwise.constant.ApiErrorCode;
import com.repeatwise.exception.base.BaseException;

/**
 * Exception thrown when daily review limit is exceeded
 */

public class DailyReviewLimitExceededException extends BaseException {

    private static final long serialVersionUID = 1L;

    public DailyReviewLimitExceededException(int limit) {
        super("error.review.daily.limit.reached", ApiErrorCode.DAILY_REVIEW_LIMIT_EXCEEDED, limit);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
