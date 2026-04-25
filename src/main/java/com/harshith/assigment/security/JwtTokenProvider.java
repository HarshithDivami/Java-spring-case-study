package com.harshith.assigment.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

/**
 * Creates, signs, and validates JWT tokens used for stateless authentication.
 * Expects the secret to be a Base64-encoded string with at least 256 bits of entropy.
 */
@Component
@Slf4j
public class JwtTokenProvider {

    private final SecretKey key;
    private final long expirationMs;

    public JwtTokenProvider(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms}") long expirationMs) {
        this.key = buildKey(secret);
        this.expirationMs = expirationMs;
    }

    /**
     * Attempts to decode the secret as Base64; falls back to raw UTF-8 bytes when
     * the value is not Base64-encoded (e.g. a plain environment variable).
     * Logs a warning on fallback so misconfiguration is visible in startup logs.
     */
    private static SecretKey buildKey(String secret) {
        try {
            return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        } catch (Exception e) {
            log.warn("JWT secret is not Base64-encoded; using raw UTF-8 bytes. "
                    + "Provide a Base64-encoded secret with >= 256 bits for production.");
            return Keys.hmacShaKeyFor(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        }
    }

    /** Generates a signed JWT for the authenticated principal, valid for {@code expirationMs} milliseconds. */
    public String generateToken(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return Jwts.builder()
                .subject(principal.getUsername())
                .claim("userId", principal.getId().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }

    /** Extracts the username (subject) from a verified token. */
    public String getUsernameFromToken(String token) {
        return Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Returns {@code true} if the token has a valid signature and is not expired.
     * Any {@link JwtException} is treated as invalid rather than propagated,
     * so callers receive a simple boolean rather than having to catch JWT internals.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            log.debug("Invalid JWT token: {}", ex.getMessage());
            return false;
        }
    }
}
