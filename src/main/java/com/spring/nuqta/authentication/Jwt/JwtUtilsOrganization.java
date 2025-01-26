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

    // Build the JWT token
    private String buildToken(Map<String, Object> extraClaims, OrgEntity organization,
                              long expiration) {
        return Jwts.builder().setClaims(extraClaims)
                .setSubject(organization.getEmail())
                .claim("scope", organization.getScope().toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256).compact();
    }

    // Generate token with additional claims
    public String generateToken(Map<String, Object> extraClaims,
                                OrgEntity organization) {
        return buildToken(extraClaims, organization, getJwtExpiration());
    }

    // Create a refresh token for an Organization
    public String createRefreshToken(OrgEntity organization) {
        return buildToken(new HashMap<>(), organization, getRefreshExpiration());
    }

    // Generate JWT token for an Organization
    public String generateToken(OrgEntity organization) {
        return generateToken(new HashMap<>(), organization);
    }

    // Override method to get the subject (organization username) from the token
    @Override
    public String getSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Override method to get the scope (organization scope) from the token
    @Override
    public String getScope(String token) {
        return extractClaim(token, claims -> claims.get("scope", String.class));
    }

    // Helper method to get JWT expiration from the parent class
    public long getJwtExpiration() {
        return super.getJwtExpiration();
    }

    // Helper method to get refresh expiration from the parent class
    public long getRefreshExpiration() {
        return super.getRefreshExpiration();
    }
}
