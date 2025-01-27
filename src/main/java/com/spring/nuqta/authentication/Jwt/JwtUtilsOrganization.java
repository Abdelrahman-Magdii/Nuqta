package com.spring.nuqta.authentication.Jwt;

import com.spring.nuqta.organization.Entity.OrgEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtUtilsOrganization extends JwtUtils {

    /**
     * Builds a JWT token with the provided claims, organization details, and expiration time.
     *
     * @param extraClaims  Additional claims to include in the token.
     * @param organization The organization entity for which the token is being generated.
     * @param expiration   The expiration time for the token (in milliseconds).
     * @return The generated JWT token.
     */
    private String buildToken(Map<String, Object> extraClaims, OrgEntity organization,
                              long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims) // Set additional claims
                .setSubject(organization.getEmail()) // Set the subject (organization email)
                .claim("scope", organization.getScope().toString()) // Add the organization's scope as a claim
                .setIssuedAt(new Date(System.currentTimeMillis())) // Set the token issuance time
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // Set the token expiration time
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Sign the token with the signing key
                .compact(); // Compact the token into a string
    }

    /**
     * Generates a JWT token with additional claims for the organization.
     *
     * @param extraClaims  Additional claims to include in the token.
     * @param organization The organization entity for which the token is being generated.
     * @return The generated JWT token.
     */
    public String generateToken(Map<String, Object> extraClaims,
                                OrgEntity organization) {
        return buildToken(extraClaims, organization, getJwtExpiration());
    }

    /**
     * Creates a refresh token for the organization.
     *
     * @param organization The organization entity for which the refresh token is being generated.
     * @return The generated refresh token.
     */
    public String createRefreshToken(OrgEntity organization) {
        return buildToken(new HashMap<>(), organization, getRefreshExpiration());
    }

    /**
     * Generates a JWT token for the organization without additional claims.
     *
     * @param organization The organization entity for which the token is being generated.
     * @return The generated JWT token.
     */
    public String generateToken(OrgEntity organization) {
        return generateToken(new HashMap<>(), organization);
    }

    /**
     * Extracts the subject (organization email) from the JWT token.
     *
     * @param token The JWT token.
     * @return The subject (organization email) of the token.
     */
    @Override
    public String getSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the scope (organization scope) from the JWT token.
     *
     * @param token The JWT token.
     * @return The scope of the organization.
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

    /**
     * Helper method to get the refresh token expiration time from the parent class.
     *
     * @return The refresh token expiration time (in milliseconds).
     */
    public long getRefreshExpiration() {
        return super.getRefreshExpiration();
    }
}