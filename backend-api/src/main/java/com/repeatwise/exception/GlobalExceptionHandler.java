package com.repeatwise.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.repeatwise.dto.response.ErrorResponse;
import com.repeatwise.log.LogEvent;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
     * @param ex      ResourceNotFoundException
     * @param request HttpServletRequest
     * @return ResponseEntity with ErrorResponse
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            final ResourceNotFoundException ex,
            final HttpServletRequest request) {

        log.warn("event={} Resource not found: errorCode={}, message={}, path={}",
                LogEvent.EX_RESOURCE_NOT_FOUND, ex.getErrorCode(), ex.getMessage(), request.getRequestURI());

        final var error = ErrorResponse.builder()
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
     * @param ex      ValidationException
     * @param request HttpServletRequest
     * @return ResponseEntity with ErrorResponse
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            final ValidationException ex,
            final HttpServletRequest request) {

        log.warn("event={} Validation error: errorCode={}, message={}, path={}",
                LogEvent.EX_VALIDATION, ex.getErrorCode(), ex.getMessage(), request.getRequestURI());

        final var error = ErrorResponse.builder()
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
     * Note: DuplicateEmailException and DuplicateUsernameException return 400 for UC-001 compliance
     *
     * @param ex      DuplicateResourceException
     * @param request HttpServletRequest
     * @return ResponseEntity with ErrorResponse
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResource(
            final BusinessException ex,
            final HttpServletRequest request) {

        log.warn("event={} Duplicate resource: errorCode={}, message={}, path={}",
                LogEvent.EX_DUPLICATE_RESOURCE, ex.getErrorCode(), ex.getMessage(), request.getRequestURI());

        final var error = ErrorResponse.builder()
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
     * Handle DuplicateEmailException (400)
     * UC-001: Email already exists returns 400 Bad Request
     *
     * @param ex      DuplicateEmailException
     * @param request HttpServletRequest
     * @return ResponseEntity with ErrorResponse
     */
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmail(
            final DuplicateEmailException ex,
            final HttpServletRequest request) {

        log.warn("event={} Duplicate email: errorCode={}, message={}, path={}",
                LogEvent.EX_DUPLICATE_RESOURCE, ex.getErrorCode(), ex.getMessage(), request.getRequestURI());

        final var error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Email already exists")
                .errorCode(ex.getErrorCode())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Handle DuplicateUsernameException (400)
     * UC-001: Username already exists returns 400 Bad Request
     *
     * @param ex      DuplicateUsernameException
     * @param request HttpServletRequest
     * @return ResponseEntity with ErrorResponse
     */
    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateUsername(
            final DuplicateUsernameException ex,
            final HttpServletRequest request) {

        log.warn("event={} Duplicate username: errorCode={}, message={}, path={}",
                LogEvent.EX_DUPLICATE_RESOURCE, ex.getErrorCode(), ex.getMessage(), request.getRequestURI());

        final var error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Username already exists")
                .errorCode(ex.getErrorCode())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Handle InvalidCredentialsException (400 for password change, 401 for login)
     * UC-002: Invalid credentials returns generic error message for login
     * UC-006: Invalid password returns 400 Bad Request for password change
     *
     * @param ex      InvalidCredentialsException
     * @param request HttpServletRequest
     * @return ResponseEntity with ErrorResponse
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(
            final InvalidCredentialsException ex,
            final HttpServletRequest request) {

        log.warn("event={} Invalid credentials: errorCode={}, message={}, path={}",
                LogEvent.EX_INVALID_CREDENTIALS, ex.getErrorCode(), ex.getMessage(), request.getRequestURI());

        // UC-006: Password change errors return 400 with "Invalid password"
        final var isPasswordChange = (request.getRequestURI() != null)
                && request.getRequestURI().contains("/change-password");

        final var status = isPasswordChange ? HttpStatus.BAD_REQUEST : HttpStatus.UNAUTHORIZED;
        final var errorType = isPasswordChange ? "Invalid password" : "Invalid credentials";

        final var error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(errorType)
                .errorCode(ex.getErrorCode())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(error);
    }

    /**
     * Handle InvalidTokenException (401)
     * UC-003: Different error codes for different token errors
     *
     * @param ex      InvalidTokenException
     * @param request HttpServletRequest
     * @return ResponseEntity with ErrorResponse
     */
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidToken(
            final InvalidTokenException ex,
            final HttpServletRequest request) {

        log.warn("event={} Invalid token: errorCode={}, message={}, path={}",
                LogEvent.EX_INVALID_TOKEN, ex.getErrorCode(), ex.getMessage(), request.getRequestURI());

        // UC-003: Map error codes to specific error types
        var errorType = "INVALID_TOKEN";
        if ("AUTH_007".equals(ex.getErrorCode())) {
            errorType = "REFRESH_TOKEN_MISSING";
        } else if ("AUTH_009".equals(ex.getErrorCode())) {
            errorType = "REFRESH_TOKEN_EXPIRED";
        } else if ("AUTH_008".equals(ex.getErrorCode())) {
            errorType = "REFRESH_TOKEN_REVOKED";
        }

        final var error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(errorType)
                .errorCode(ex.getErrorCode())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Handle TokenReuseException (401)
     * UC-003: Token reuse detected - security issue
     *
     * @param ex      TokenReuseException
     * @param request HttpServletRequest
     * @return ResponseEntity with ErrorResponse
     */
    @ExceptionHandler(TokenReuseException.class)
    public ResponseEntity<ErrorResponse> handleTokenReuse(
            final TokenReuseException ex,
            final HttpServletRequest request) {

        log.error("event={} Token reuse detected: errorCode={}, message={}, path={}",
                LogEvent.EX_INVALID_TOKEN, ex.getErrorCode(), ex.getMessage(), request.getRequestURI());

        final var error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("TOKEN_REUSE_DETECTED")
                .errorCode(ex.getErrorCode())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Handle ForbiddenException (403)
     *
     * @param ex      ForbiddenException
     * @param request HttpServletRequest
     * @return ResponseEntity with ErrorResponse
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(
            final ForbiddenException ex,
            final HttpServletRequest request) {

        log.warn("event={} Forbidden: errorCode={}, message={}, path={}",
                LogEvent.EX_FORBIDDEN, ex.getErrorCode(), ex.getMessage(), request.getRequestURI());

        final var error = ErrorResponse.builder()
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
     * @param ex      MaxDepthExceededException
     * @param request HttpServletRequest
     * @return ResponseEntity with ErrorResponse
     */
    @ExceptionHandler(MaxDepthExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxDepthExceeded(
            final MaxDepthExceededException ex,
            final HttpServletRequest request) {

        log.warn("event={} Max depth exceeded: errorCode={}, message={}, path={}",
                LogEvent.FOLDER_MAX_DEPTH_EXCEEDED, ex.getErrorCode(), ex.getMessage(), request.getRequestURI());

        final var error = ErrorResponse.builder()
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
     * @param ex      CircularReferenceException
     * @param request HttpServletRequest
     * @return ResponseEntity with ErrorResponse
     */
    @ExceptionHandler(CircularReferenceException.class)
    public ResponseEntity<ErrorResponse> handleCircularReference(
            final CircularReferenceException ex,
            final HttpServletRequest request) {

        log.warn("event={} Circular reference: errorCode={}, message={}, path={}",
                LogEvent.EX_VALIDATION, ex.getErrorCode(), ex.getMessage(), request.getRequestURI());

        final var error = ErrorResponse.builder()
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
     * @param ex      FolderTooLargeException
     * @param request HttpServletRequest
     * @return ResponseEntity with ErrorResponse
     */
    @ExceptionHandler(FolderTooLargeException.class)
    public ResponseEntity<ErrorResponse> handleFolderTooLarge(
            final FolderTooLargeException ex,
            final HttpServletRequest request) {

        log.warn("event={} Folder too large: errorCode={}, message={}, path={}",
                LogEvent.EX_VALIDATION, ex.getErrorCode(), ex.getMessage(), request.getRequestURI());

        final var error = ErrorResponse.builder()
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
     * UC-001: Validation error format with details array
     *
     * @param ex      MethodArgumentNotValidException
     * @param request HttpServletRequest
     * @return ResponseEntity with ErrorResponse
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            final MethodArgumentNotValidException ex,
            final HttpServletRequest request) {

        log.warn("event={} Bean validation error: path={}", LogEvent.EX_VALIDATION, request.getRequestURI());

        // UC-001: Format validation errors as array of objects with field and message
        final var errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldErrorAsMap)
                .toList();

        final var error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation failed")
                .errorCode("VALIDATION_001")
                .message("Validation failed")
                .path(request.getRequestURI())
                .details(errors)
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Handle IllegalArgumentException (400)
     * UC-006: Password change errors return "Invalid password" error type
     *
     * @param ex      IllegalArgumentException
     * @param request HttpServletRequest
     * @return ResponseEntity with ErrorResponse
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            final IllegalArgumentException ex,
            final HttpServletRequest request) {

        log.warn("event={} Illegal argument: message={}, path={}",
                LogEvent.EX_ILLEGAL_ARGUMENT, ex.getMessage(), request.getRequestURI());

        // UC-006: Password change validation errors return "Invalid password"
        final var isPasswordChange = (request.getRequestURI() != null)
                && request.getRequestURI().contains("/change-password");
        final var isPasswordError = (ex.getMessage() != null)
                && (ex.getMessage().contains("password") || ex.getMessage().contains("Password"));

        final var errorType = (isPasswordChange && isPasswordError)
                ? "Invalid password"
                : "INVALID_ARGUMENT";

        final var error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(errorType)
                .errorCode("VALIDATION_002")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Handle all other exceptions (500)
     *
     * @param ex      Exception
     * @param request HttpServletRequest
     * @return ResponseEntity with ErrorResponse
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(
            final Exception ex,
            final HttpServletRequest request) {

        log.error("event={} Unexpected error: path={}", LogEvent.EX_INTERNAL_SERVER, request.getRequestURI(), ex);

        final var message = getMessage("error.internal.server");

        final var error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("INTERNAL_SERVER_ERROR")
                .errorCode("INTERNAL_001")
                .message(message)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Format field error as Map for UC-001 compliance
     * Returns Map with "field" and "message" keys
     */
    private Map<String, String> formatFieldErrorAsMap(final FieldError fieldError) {
        final Map<String, String> errorMap = new HashMap<>();
        errorMap.put("field", fieldError.getField());
        errorMap.put("message", fieldError.getDefaultMessage());
        return errorMap;
    }

    private String getMessage(final String code, final Object... args) {
        return this.messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}
