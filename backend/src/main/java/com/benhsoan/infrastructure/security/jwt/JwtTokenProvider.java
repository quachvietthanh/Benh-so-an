package com.benhsoan.infrastructure.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long expirationMs;
    private final Set<String> tokenBlacklist = new HashSet<>();

    public JwtTokenProvider(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms:1800000}") long expirationMs) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String generateToken(String username, List<String> roles) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    public String generateToken(String username) {
        return generateToken(username, List.of());
    }

    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        Claims claims = parseClaims(token);
        List<String> roles = claims.get("roles", List.class);
        return (roles != null) ? roles : List.of();
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            if (isTokenBlacklisted(token)) {
                return false;
            }
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.warn("JWT expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT empty: {}", e.getMessage());
        } catch (JwtException e) {
            log.error("JWT error: {}", e.getMessage());
        }
        return false;
    }

    public void invalidateToken(String token) {
        try {
            Claims claims = parseClaims(token);
            String jti = claims.getId();
            String tokenKey = (jti != null) ? jti : claims.getSubject() + ":" + claims.getExpiration().getTime();
            tokenBlacklist.add(tokenKey);
        } catch (Exception e) {
            log.warn("Could not invalidate token: {}", e.getMessage());
        }
    }

    public boolean isTokenBlacklisted(String token) {
        try {
            Claims claims = parseClaims(token);
            String jti = claims.getId();
            String tokenKey = (jti != null) ? jti : claims.getSubject() + ":" + claims.getExpiration().getTime();
            return tokenBlacklist.contains(tokenKey);
        } catch (Exception e) {
            return false;
        }
    }

    public Date getExpirationDateFromToken(String token) {
        try {
            return parseClaims(token).getExpiration();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isTokenExpiringSoon(String token) {
        Date expiration = getExpirationDateFromToken(token);
        if (expiration == null) {
            return true;
        }
        long remainingMs = expiration.getTime() - System.currentTimeMillis();
        return remainingMs > 0 && remainingMs < 300_000;
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
