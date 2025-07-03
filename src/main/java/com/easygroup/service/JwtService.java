package com.easygroup.service;

import com.easygroup.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for JWT token generation and validation.
 */
@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${jwt.secret:}")
    private String secretKey;

    @Value("${jwt.expiration:86400000}") // 24 hours in milliseconds
    private long jwtExpiration;

    /**
     * Constructor for JwtService.
     * If no secret key is provided, generate a random one.
     */
    public JwtService() {
        // The @Value annotation is processed after constructor execution
        // We'll check and potentially generate the key in the getSigningKey method
    }

    /**
     * Generate a random secret key for JWT signing.
     * 
     * @return a random secret key of 64 bytes (512 bits) encoded as Base64
     */
    private String generateRandomSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] keyBytes = new byte[64];
        random.nextBytes(keyBytes);
        return Base64.getEncoder().encodeToString(keyBytes);
    }

    /**
     * Extract username (email) from token.
     *
     * @param token the JWT token
     * @return the email stored in the subject
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract expiration date from token.
     *
     * @param token the JWT token
     * @return the expiration date
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract a specific claim from token.
     *
     * @param token the JWT token
     * @param claimsResolver the function to extract the claim
     * @return the claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract all claims from token.
     *
     * @param token the JWT token
     * @return all claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Check if token is expired.
     *
     * @param token the JWT token
     * @return true if token is expired
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Generate a token from a User entity.
     *
     * @param user the user entity
     * @return the generated JWT token
     */
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId().toString());
        claims.put("role", user.getRole().toString());

        return createToken(claims, user.getEmail());
    }

    /**
     * Generate a token from a User entity.
     *
     * @param user the user entity
     * @return the generated JWT token
     */
    public String generateValidationToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId().toString());

        return createToken(claims, user.getEmail());
    }

    /**
     * Create a token with custom claims and subject (email).
     *
     * @param claims  the claims map
     * @param subject the subject (user email)
     * @return the JWT token
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validate token against expected email and expiration.
     *
     * @param token the JWT token
     * @param expectedEmail the expected email (subject)
     * @return true if valid and not expired
     */
    public boolean validateToken(String token, String expectedEmail) {
        String extractedEmail = extractUsername(token);
        return extractedEmail.equals(expectedEmail) && !isTokenExpired(token);
    }

    /**
     * Generate the HMAC signing key from the configured secret.
     * If no secret key is provided, generate a random one.
     *
     * @return the signing key
     */
    private Key getSigningKey() {
        // Check if secret key is empty and generate a random one if needed
        if (secretKey == null || secretKey.isEmpty()) {
            logger.info("No JWT secret key provided. Generating a random one...");
            secretKey = generateRandomSecretKey();
            logger.info("Random JWT secret key generated successfully.");
        }

        byte[] keyBytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}