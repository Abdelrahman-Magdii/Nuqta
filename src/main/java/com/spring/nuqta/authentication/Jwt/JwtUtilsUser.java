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

    // Build the JWT token
    private String buildToken(Map<String, Object> extraClaims, UserEntity user,
                              long expiration) {
        return Jwts.builder().setClaims(extraClaims).setSubject(user.getUsername())
                .claim("scope", user.getScope().toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256).compact();
    }

    // Create a refresh token for a User
    public String createRefreshToken(UserEntity user) {
        return buildToken(new HashMap<>(), user, getRefreshExpiration());
    }

    // Generate token with additional claims
    public String generateToken(Map<String, Object> extraClaims, UserEntity user) {
        return buildToken(extraClaims, user, getJwtExpiration());
    }

    // Generate JWT token for a User
    public String generateToken(UserEntity user) {
        return generateToken(new HashMap<>(), user);
    }

    // from JwtUtils
    @Override
    public String getSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

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
