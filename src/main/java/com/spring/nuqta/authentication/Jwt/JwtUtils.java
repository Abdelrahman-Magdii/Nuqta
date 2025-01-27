package com.spring.nuqta.authentication.Jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
@Getter
public abstract class JwtUtils {

    // Secret key used for signing and verifying JWT tokens
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    // Expiration time for regular JWT tokens (in milliseconds)
    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    // Expiration time for refresh tokens (in milliseconds)
    @Value("${security.jwt.refresh-expiration-time}")
    private long refreshExpiration;

    /**
     * Extracts the username (subject) from the JWT token.
     *
     * @param token The JWT token.
     * @return The subject (username) of the token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts a specific claim from the JWT token using a claims resolver function.
     *
     * @param token          The JWT token.
     * @param claimsResolver A function to resolve the desired claim from the token's claims.
     * @return The resolved claim.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts the expiration date of the JWT token.
     *
     * @param token The JWT token.
     * @return The expiration date of the token.
     */
    public Date getExpireAt(String token) {
        return extractExpiration(token);
    }

    /**
     * Extracts the expiration date from the JWT token.
     *
     * @param token The JWT token.
     * @return The expiration date of the token.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts all claims from the JWT token.
     *
     * @param token The JWT token.
     * @return All claims contained in the token.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey()) // Set the signing key for verification
                .build()
                .parseClaimsJws(token) // Parse the token
                .getBody(); // Extract the claims
    }

    /**
     * Generates the signing key from the base64-encoded secret key.
     *
     * @return The signing key used for JWT verification.
     */
    Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey); // Decode the base64 secret key
        return Keys.hmacShaKeyFor(keyBytes); // Create a HMAC SHA key
    }

    /**
     * Abstract method to extract the subject (username) from the token.
     * Must be implemented by subclasses.
     *
     * @param token The JWT token.
     * @return The subject of the token.
     */
    public abstract String getSubject(String token);

    /**
     * Abstract method to extract the scope (role) from the token.
     * Must be implemented by subclasses.
     *
     * @param token The JWT token.
     * @return The scope of the token.
     */
    public abstract String getScope(String token);
}