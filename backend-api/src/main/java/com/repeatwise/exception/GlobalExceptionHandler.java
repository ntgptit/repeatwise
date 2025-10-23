package com.repeatwise.exception;

import com.repeatwise.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.repeatwise.log.LogEvent;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Global Exception Handler
 *
 * Requirements:
 * - Coding Convention: Centralized exception handling with MessageSource
 * - API Spec: Standard error response format
 *
 * Handles all exceptions and returns consistent ErrorResponse
 *
 * @author RepeatWise Team
 */
@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    /**
     * Handle ResourceNotFoundException (404)
     *
     * @param ex ResourceNotFoundException
     * @param request HttpServletRequest
     * @return ResponseEntity with ErrorResponse
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            final ResourceNotFoundException ex,
            final HttpServletRequest request) {

        log.warn("event={} Resource not found: errorCode={}, message={}, path={}",
            LogEvent.EX_RESOURCE_NOT_FOUND, ex.getErrorCode(), ex.getMessage(), request.getRequestURI());

        final ErrorResponse error = ErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("NOT_FOUND")
            .errorCode(ex.getErrorCode())
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handle ValidationException (400)
     *
     * @param ex ValidationException
     * @param request HttpServletRequest
     * @return ResponseEntity with ErrorResponse
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            final ValidationException ex,
            final HttpServletRequest request) {

        log.warn("event={} Validation error: errorCode={}, message={}, path={}",
            LogEvent.EX_VALIDATION, ex.getErrorCode(), ex.getMessage(), request.getRequestURI());

        final ErrorResponse error = ErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("VALIDATION_ERROR")
            .errorCode(ex.getErrorCode())
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Handle DuplicateResourceException (409)
     *
     * @param ex DuplicateResourceException
     * @param request HttpServletRequest
     * @return ResponseEntity with ErrorResponse
     */
    @ExceptionHandler({DuplicateResourceException.class,
                       DuplicateUsernameException.class,
                       DuplicateEmailException.class})
    public ResponseEntity<ErrorResponse> handleDuplicateResource(
            final BusinessException ex,
            final HttpServletRequest request) {

        log.warn("event={} Duplicate resource: errorCode={}, message={}, path={}",
            LogEvent.EX_DUPLICATE_RESOURCE, ex.getErrorCode(), ex.getMessage(), request.getRequestURI());

        final ErrorResponse error = ErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.CONFLICT.value())
            .error("DUPLICATE_RESOURCE")
            .errorCode(ex.getErrorCode())
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Handle InvalidCredentialsException (401)
     *
     * @param ex InvalidCredentialsException
     * @param request HttpServletRequest
     * @return ResponseEntity with ErrorResponse
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(
            final InvalidCredentialsException ex,
            final HttpServletRequest request) {

        log.warn("event={} Invalid credentials: errorCode={}, message={}, path={}",
            LogEvent.EX_INVALID_CREDENTIALS, ex.getErrorCode(), ex.getMessage(), request.getRequestURI());

        final ErrorResponse error = ErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.UNAUTHORIZED.value())
            .error("INVALID_CREDENTIALS")
            .errorCode(ex.getErrorCode())
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Handle InvalidTokenException (401)
     *
     * @param ex InvalidTokenException
     * @param request HttpServletRequest
     * @return ResponseEntity with ErrorResponse
     */
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidToken(
            final InvalidTokenException ex,
            final HttpServletRequest request) {

        log.warn("event={} Invalid token: errorCode={}, message={}, path={}",
            LogEvent.EX_INVALID_TOKEN, ex.getErrorCode(), ex.getMessage(), request.getRequestURI());

        final ErrorResponse error = ErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.UNAUTHORIZED.value())
            .error("INVALID_TOKEN")
            .errorCode(ex.getErrorCode())
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Handle ForbiddenException (403)
     *
     * @param ex ForbiddenException
     * @param request HttpServletRequest
     * @return ResponseEntity with ErrorResponse
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(
            final ForbiddenException ex,
            final HttpServletRequest request) {

        log.warn("event={} Forbidden: errorCode={}, message={}, path={}",
            LogEvent.EX_FORBIDDEN, ex.getErrorCode(), ex.getMessage(), request.getRequestURI());

        final ErrorResponse error = ErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.FORBIDDEN.value())
            .error("FORBIDDEN")
            .errorCode(ex.getErrorCode())
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    /**
     * Handle MaxDepthExceededException (400)
     *
     * @param ex MaxDepthExceededException
     * @param request HttpServletRequest
     * @return ResponseEntity with ErrorResponse
     */
    @ExceptionHandler(MaxDepthExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxDepthExceeded(
            final MaxDepthExceededException ex,
            final HttpServletRequest request) {

        log.warn("event={} Max depth exceeded: errorCode={}, message={}, path={}",
            LogEvent.FOLDER_MAX_DEPTH_EXCEEDED, ex.getErrorCode(), ex.getMessage(), request.getRequestURI());

        final ErrorResponse error = ErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("MAX_DEPTH_EXCEEDED")
            .errorCode(ex.getErrorCode())
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Handle CircularReferenceException (400)
     *
     * @param ex CircularReferenceException
     * @param request HttpServletRequest
     * @return ResponseEntity with ErrorResponse
     */
    @ExceptionHandler(CircularReferenceException.class)
    public ResponseEntity<ErrorResponse> handleCircularReference(
            final CircularReferenceException ex,
            final HttpServletRequest request) {

        log.warn("event={} Circular reference: errorCode={}, message={}, path={}",
            LogEvent.EX_VALIDATION, ex.getErrorCode(), ex.getMessage(), request.getRequestURI());

        final ErrorResponse error = ErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("CIRCULAR_REFERENCE")
            .errorCode(ex.getErrorCode())
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Handle FolderTooLargeException (400)
     *
     * @param ex FolderTooLargeException
     * @param request HttpServletRequest
     * @return ResponseEntity with ErrorResponse
     */
    @ExceptionHandler(FolderTooLargeException.class)
    public ResponseEntity<ErrorResponse> handleFolderTooLarge(
            final FolderTooLargeException ex,
            final HttpServletRequest request) {

        log.warn("event={} Folder too large: errorCode={}, message={}, path={}",
            LogEvent.EX_VALIDATION, ex.getErrorCode(), ex.getMessage(), request.getRequestURI());

        final ErrorResponse error = ErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("FOLDER_TOO_LARGE")
            .errorCode(ex.getErrorCode())
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Handle MethodArgumentNotValidException (400) - Bean Validation
     *
     * @param ex MethodArgumentNotValidException
     * @param request HttpServletRequest
     * @return ResponseEntity with ErrorResponse
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            final MethodArgumentNotValidException ex,
            final HttpServletRequest request) {

        log.warn("event={} Bean validation error: path={}", LogEvent.EX_VALIDATION, request.getRequestURI());

        final List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(this::formatFieldError)
            .collect(Collectors.toList());

        final ErrorResponse error = ErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("VALIDATION_ERROR")
            .errorCode("VALIDATION_001")
            .message("Validation failed")
            .path(request.getRequestURI())
            .details(errors)
            .build();

        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Handle IllegalArgumentException (400)
     *
     * @param ex IllegalArgumentException
     * @param request HttpServletRequest
     * @return ResponseEntity with ErrorResponse
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            final IllegalArgumentException ex,
            final HttpServletRequest request) {

        log.warn("event={} Illegal argument: message={}, path={}",
            LogEvent.EX_ILLEGAL_ARGUMENT, ex.getMessage(), request.getRequestURI());

        final ErrorResponse error = ErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("INVALID_ARGUMENT")
            .errorCode("VALIDATION_002")
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Handle all other exceptions (500)
     *
     * @param ex Exception
     * @param request HttpServletRequest
     * @return ResponseEntity with ErrorResponse
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(
            final Exception ex,
            final HttpServletRequest request) {

        log.error("event={} Unexpected error: path={}", LogEvent.EX_INTERNAL_SERVER, request.getRequestURI(), ex);

        final String message = getMessage("error.internal.server");

        final ErrorResponse error = ErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error("INTERNAL_SERVER_ERROR")
            .errorCode("INTERNAL_001")
            .message(message)
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    private String formatFieldError(final FieldError fieldError) {
        return String.format("%s: %s", fieldError.getField(), fieldError.getDefaultMessage());
    }

    private String getMessage(final String code, final Object... args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}
