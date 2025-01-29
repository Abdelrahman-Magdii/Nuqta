package com.spring.nuqta.authentication.Jwt;

import com.spring.nuqta.usermanagement.Entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtUtilsUser extends JwtUtils {

    /**
     * Builds a JWT token with the provided claims, user details, and expiration time.
     *
     * @param extraClaims Additional claims to include in the token.
     * @param user        The user entity for which the token is being generated.
     * @param expiration  The expiration time for the token (in milliseconds).
     * @return The generated JWT token.
     */
    private String buildToken(Map<String, Object> extraClaims, UserEntity user,
                              long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims) // Set additional claims
                .setSubject(user.getUsername()) // Set the subject (username)
                .claim("scope", user.getScope().toString()) // Add the user's scope as a claim
                .setIssuedAt(new Date(System.currentTimeMillis())) // Set the token issuance time
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // Set the token expiration time
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Sign the token with the signing key
                .compact(); // Compact the token into a string
    }

    /**
     * Generates a JWT token with additional claims for the user.
     *
     * @param extraClaims Additional claims to include in the token.
     * @param user        The user entity for which the token is being generated.
     * @return The generated JWT token.
     */
    public String generateToken(Map<String, Object> extraClaims, UserEntity user) {
        return buildToken(extraClaims, user, getJwtExpiration());
    }

    /**
     * Generates a JWT token for the user without additional claims.
     *
     * @param user The user entity for which the token is being generated.
     * @return The generated JWT token.
     */
    public String generateToken(UserEntity user) {
        return generateToken(new HashMap<>(), user);
    }

    /**
     * Extracts the subject (username) from the JWT token.
     *
     * @param token The JWT token.
     * @return The subject (username) of the token.
     */
    @Override
    public String getSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the scope (user scope) from the JWT token.
     *
     * @param token The JWT token.
     * @return The scope of the user.
     */
    @Override
    public String getScope(String token) {
        return extractClaim(token, claims -> claims.get("scope", String.class));
    }

    /**
     * Helper method to get the JWT expiration time from the parent class.
     *
     * @return The JWT expiration time (in milliseconds).
     */
    public long getJwtExpiration() {
        return super.getJwtExpiration();
    }

}