package com.repeatwise.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import com.repeatwise.log.LogEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

/**
 * JWT Token Provider
 *
 * Requirements:
 * - UC-002: User Login - Generate JWT access token
 * - Security: HS256 algorithm, 15-minute expiry
 * - API Spec: POST /api/auth/login returns accessToken
 *
 * Token Claims:
 * - sub: User ID (UUID)
 * - email: User email
 * - iat: Issued at timestamp
 * - exp: Expiration timestamp
 * - iss: Issuer (repeatwise-api)
 * - aud: Audience (repeatwise-app)
 *
 * @author RepeatWise Team
 */
@Component
@Slf4j
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpirationMinutes;
    private final String issuer;
    private final String audience;

    public JwtTokenProvider(
            @Value("${jwt.secret}") final String secret,
            @Value("${jwt.access-token-expiration-minutes}") final long accessTokenExpirationMinutes,
            @Value("${jwt.issuer}") final String issuer,
            @Value("${jwt.audience}") final String audience) {

        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationMinutes = accessTokenExpirationMinutes;
        this.issuer = issuer;
        this.audience = audience;

        log.info("event={} JwtTokenProvider initialized: issuer={}, audience={}, expirationMinutes={}",
            LogEvent.START, issuer, audience, accessTokenExpirationMinutes);
    }

    /**
     * Generate JWT access token for user
     *
     * Business Logic:
     * 1. Create JWT with user ID as subject
     * 2. Add email claim
     * 3. Set issued at timestamp (now)
     * 4. Set expiration (now + 15 minutes)
     * 5. Set issuer and audience
     * 6. Sign with HS256 algorithm
     *
     * @param userId User UUID
     * @param email User email
     * @return JWT access token string
     */
    public String generateAccessToken(final UUID userId, final String email) {
        final Instant now = Instant.now();
        final Instant expiration = now.plus(accessTokenExpirationMinutes, ChronoUnit.MINUTES);

        final String token = Jwts.builder()
            .setSubject(userId.toString())
            .claim("email", email)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(expiration))
            .setIssuer(issuer)
            .setAudience(audience)
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact();

        log.debug("event={} Generated access token: userId={}, email={}, expiresAt={}",
            LogEvent.AUTH_LOGIN_SUCCESS, userId, email, expiration);

        return token;
    }

    /**
     * Validate JWT token
     *
     * Business Logic:
     * 1. Parse token with secret key
     * 2. Validate signature
     * 3. Check expiration
     * 4. Return true if valid
     *
     * @param token JWT token string
     * @return true if valid, false otherwise
     */
    public boolean validateToken(final String token) {
        try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);

            log.debug("event={} Token validation successful", LogEvent.START);
            return true;

        } catch (SignatureException ex) {
            log.error("event={} Invalid JWT signature: {}", LogEvent.EX_INVALID_TOKEN, ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.error("event={} Invalid JWT token: {}", LogEvent.EX_INVALID_TOKEN, ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("event={} Expired JWT token: {}", LogEvent.EX_INVALID_TOKEN, ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("event={} Unsupported JWT token: {}", LogEvent.EX_INVALID_TOKEN, ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("event={} JWT claims string is empty: {}", LogEvent.EX_INVALID_TOKEN, ex.getMessage());
        }

        return false;
    }

    /**
     * Extract user ID from JWT token
     *
     * @param token JWT token string
     * @return User UUID
     */
    public UUID getUserIdFromToken(final String token) {
        final Claims claims = Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();

        final String userIdString = claims.getSubject();
        return UUID.fromString(userIdString);
    }

    /**
     * Extract email from JWT token
     *
     * @param token JWT token string
     * @return User email
     */
    public String getEmailFromToken(final String token) {
        final Claims claims = Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();

        return claims.get("email", String.class);
    }

    /**
     * Get access token expiration time in seconds
     *
     * @return Expiration time in seconds (900 for 15 minutes)
     */
    public long getAccessTokenExpirationSeconds() {
        return accessTokenExpirationMinutes * 60;
    }
}
