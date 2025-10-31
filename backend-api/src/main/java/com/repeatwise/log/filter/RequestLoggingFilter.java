package com.repeatwise.log.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.stream.Collectors;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import com.repeatwise.log.LogEvent;
import com.repeatwise.log.context.LogContext;
import com.repeatwise.log.util.LogSanitizer;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Filter for logging HTTP requests and responses.
 * Automatically sets up MDC context for each request.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class RequestLoggingFilter implements Filter {

    private static final int MAX_PAYLOAD_LENGTH = 1000;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (!(request instanceof final HttpServletRequest httpRequest) || !(response instanceof final HttpServletResponse httpResponse)) {
            chain.doFilter(request, response);
            return;
        }

        // Wrap request and response to allow reading body multiple times
        final var wrappedRequest = new ContentCachingRequestWrapper(httpRequest);
        final var wrappedResponse = new ContentCachingResponseWrapper(httpResponse);

        try {
            // Setup MDC context
            setupMDC(wrappedRequest);

            // Log request
            logRequest(wrappedRequest);

            final var startTime = System.currentTimeMillis();

            // Process request
            chain.doFilter(wrappedRequest, wrappedResponse);

            final var duration = System.currentTimeMillis() - startTime;

            // Log response
            logResponse(wrappedRequest, wrappedResponse, duration);

            // Copy response body to actual response
            wrappedResponse.copyBodyToResponse();

        } finally {
            // Clear MDC to prevent memory leaks
            LogContext.clear();
        }
    }

    /**
     * Setup MDC context for the request.
     */
    private void setupMDC(HttpServletRequest request) {
        LogContext.generateRequestId();

        // Set client IP
        final var clientIp = getClientIp(request);
        LogContext.setClientIp(clientIp);

        // Set user agent
        final var userAgent = request.getHeader("User-Agent");
        if (userAgent != null) {
            LogContext.setUserAgent(userAgent);
        }

        // Set correlation ID if present
        final var correlationId = request.getHeader("X-Correlation-ID");
        if (correlationId != null) {
            LogContext.setCorrelationId(correlationId);
        }

        // Set operation
        final var operation = request.getMethod() + " " + request.getRequestURI();
        LogContext.setOperation(operation);
    }

    /**
     * Log incoming request.
     */
    private void logRequest(ContentCachingRequestWrapper request) {
        final var msg = new StringBuilder();
        msg.append("[HTTP_REQUEST] ")
                .append(request.getMethod()).append(" ")
                .append(request.getRequestURI());

        final var queryString = request.getQueryString();
        if (queryString != null) {
            msg.append("?").append(queryString);
        }

        msg.append(" | client=").append(LogContext.getClientIp());

        // Log headers (exclude sensitive ones)
        final var headers = Collections.list(request.getHeaderNames())
                .stream()
                .filter(this::isLoggableHeader)
                .map(name -> name + "=" + request.getHeader(name))
                .collect(Collectors.joining(", "));

        if (!headers.isEmpty()) {
            msg.append(" | headers={").append(headers).append("}");
        }

        // Log request body for POST/PUT/PATCH (sanitize sensitive data)
        if (isRequestBodyLoggable(request)) {
            final var body = getRequestBody(request);
            if ((body != null) && !body.isEmpty()) {
                final var sanitizedBody = LogSanitizer.sanitize(body);
                msg.append(" | body=").append(truncate(sanitizedBody));
            }
        }

        log.info("event={} {}", LogEvent.START, msg.toString());
    }

    /**
     * Log outgoing response.
     */
    private void logResponse(ContentCachingRequestWrapper request,
            ContentCachingResponseWrapper response,
            long duration) {
        final var msg = new StringBuilder();
        msg.append("[HTTP_RESPONSE] ")
                .append(request.getMethod()).append(" ")
                .append(request.getRequestURI())
                .append(" | status=").append(response.getStatus())
                .append(" | duration=").append(duration).append("ms");

        // Log response body for errors or if explicitly enabled (sanitize sensitive data)
        if (response.getStatus() >= 400) {
            final var body = getResponseBody(response);
            if ((body != null) && !body.isEmpty()) {
                final var sanitizedBody = LogSanitizer.sanitize(body);
                msg.append(" | body=").append(truncate(sanitizedBody));
            }
        }

        // Determine log level based on status
        if (response.getStatus() >= 500) {
            log.error("event={} {}", LogEvent.EX_INTERNAL_SERVER, msg.toString());
        } else if (response.getStatus() >= 400) {
            log.warn("event={} {}", LogEvent.EX_VALIDATION, msg.toString());
        } else {
            log.info("event={} {}", LogEvent.SUCCESS, msg.toString());
        }
    }

    /**
     * Get client IP from request, considering proxy headers.
     */
    private String getClientIp(HttpServletRequest request) {
        var ip = request.getHeader("X-Forwarded-For");
        if ((ip == null) || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if ((ip == null) || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // X-Forwarded-For can contain multiple IPs, take the first one
        if ((ip != null) && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * Check if header should be logged (exclude sensitive headers).
     */
    private boolean isLoggableHeader(String headerName) {
        final var lowerName = headerName.toLowerCase();
        return !lowerName.contains("authorization") &&
                !lowerName.contains("cookie") &&
                !lowerName.contains("token") &&
                !lowerName.contains("password");
    }

    /**
     * Check if request body should be logged.
     */
    private boolean isRequestBodyLoggable(HttpServletRequest request) {
        final var method = request.getMethod();
        return "POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method);
    }

    /**
     * Get request body as string.
     */
    private String getRequestBody(ContentCachingRequestWrapper request) {
        final var content = request.getContentAsByteArray();
        if (content.length > 0) {
            return new String(content, StandardCharsets.UTF_8);
        }
        return null;
    }

    /**
     * Get response body as string.
     */
    private String getResponseBody(ContentCachingResponseWrapper response) {
        final var content = response.getContentAsByteArray();
        if (content.length > 0) {
            return new String(content, StandardCharsets.UTF_8);
        }
        return null;
    }

    /**
     * Truncate long strings for logging.
     */
    private String truncate(String str) {
        if (str.length() <= MAX_PAYLOAD_LENGTH) {
            return str;
        }
        return str.substring(0, MAX_PAYLOAD_LENGTH) + "... (truncated)";
    }
}
