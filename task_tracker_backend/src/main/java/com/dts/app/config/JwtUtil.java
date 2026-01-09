package com.dts.app.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Utility class - manages the lifecycle of JSON Web Tokens
 * Responsible for token generation, parsing, and validation
 */

@Component
public class JwtUtil {
    @Value("${jwt.secret}") 
    private String jwtSecret;
    @Value("${jwt.expiration}")
    private int jwtExpirationMs;
    private SecretKey key;

    /**
     * Post-construction initialisation to prepare HMAC SHA key.
     */
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates a signed JWT for a given username
     * * @param username - subject of the token
     * @return - compact URL-safe JWT string
     */
    public String generateToken(String username) {
        return Jwts.builder().setSubject(username).setIssuedAt(new Date())
        .setExpiration(new Date(new Date().getTime() + jwtExpirationMs))
        .signWith(key, SignatureAlgorithm.HS256 )
        .compact();
    }

    /**
     * Extracts the username from a valid JWT
     * * @param token - JWT to parse
     * @return - username contained in the token
     * @throws io.jsonwebtoken.JwtException if the token is invalid/ tampered with
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
            .parseClaimsJws(token).getBody().getSubject();
    }
    
    /**
     * Validates the JWT against its signature and expiration date
     * * @param token - JWT string to validate
     * @return true if the token is valid,otherwise false 
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException e) {
            System.out.println("Invalid JWT signature : " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println("invalid JWT token : " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.out.println("Expired JWT token : " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.out.println("JWT token unsupported : " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("JWT claims string is empty : " + e.getMessage());
        }
        return false;
    }
}