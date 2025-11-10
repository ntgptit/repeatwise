package com.repeatwise.service.impl;

import java.util.Date;
import java.util.HashMap;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.repeatwise.config.properties.JwtProperties;
import com.repeatwise.entity.User;
import com.repeatwise.exception.RepeatWiseError;
import com.repeatwise.exception.RepeatWiseException;
import com.repeatwise.service.JwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of JwtService using jjwt library.
 * Handles JWT token generation and validation with HS256 algorithm.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final JwtProperties jwtProperties;

    /**
     * Generate secret key from configured secret string.
     * Uses HS256 algorithm (HMAC with SHA-256).
     *
     * @return SecretKey for signing JWT tokens
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(this.jwtProperties.getSecret().getBytes());
    }

    /**
     * Generate access token (JWT) for authenticated user.
     * Token includes userId, email, username in payload.
     * Expiry: 15 minutes (900 seconds).
     */
    @Override
    public String generateAccessToken(User user) {
        final var claims = new HashMap<String, Object>();
        claims.put("userId", user.getId().toString());
        claims.put("email", user.getEmail());
        claims.put("username", user.getUsername());

        final var now = new Date();
        final var expiryDate = new Date(now.getTime() +
                (this.jwtProperties.getAccessTokenExpirationMinutes() * 60 * 1000));

        final var token = Jwts.builder()
                .claims(claims)
                .subject(user.getId().toString())
                .issuer(this.jwtProperties.getIssuer())
                .audience().add(this.jwtProperties.getAudience()).and()
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();

        log.debug("Generated access token for user: {}", user.getId());
        return token;
    }

    /**
     * Extract all claims from JWT token.
     *
     * @param token JWT token
     * @return Claims object
     * @throws RepeatWiseException with error INVALID_TOKEN or TOKEN_EXPIRED
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .requireIssuer(this.jwtProperties.getIssuer())
                    .requireAudience(this.jwtProperties.getAudience())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (final ExpiredJwtException e) {
            log.warn("JWT token expired: {}", e.getMessage());
            throw new RepeatWiseException(RepeatWiseError.TOKEN_EXPIRED);
        } catch (MalformedJwtException | SignatureException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            throw new RepeatWiseException(RepeatWiseError.INVALID_TOKEN);
        } catch (final Exception e) {
            log.error("Error parsing JWT token: {}", e.getMessage());
            throw new RepeatWiseException(RepeatWiseError.INVALID_TOKEN);
        }
    }

    /**
     * Extract user ID from JWT token.
     */
    @Override
    public String extractUserId(String token) {
        final var claims = extractAllClaims(token);
        return claims.get("userId", String.class);
    }

    /**
     * Extract email from JWT token.
     */
    @Override
    public String extractEmail(String token) {
        final var claims = extractAllClaims(token);
        return claims.get("email", String.class);
    }

    /**
     * Validate JWT token.
     * Checks signature, expiration, issuer, and audience.
     */
    @Override
    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (final RepeatWiseException e) {
            return false;
        }
    }

    /**
     * Check if JWT token is expired.
     */
    @Override
    public boolean isTokenExpired(String token) {
        try {
            final var claims = extractAllClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (final RepeatWiseException e) {
            return e.getError() == RepeatWiseError.TOKEN_EXPIRED;
        }
    }
}
