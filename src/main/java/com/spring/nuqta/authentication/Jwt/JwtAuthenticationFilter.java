package com.spring.nuqta.authentication.Jwt;

import com.spring.nuqta.authentication.Dto.AuthOrgDto;
import com.spring.nuqta.authentication.Dto.AuthUserDto;
import com.spring.nuqta.authentication.SecurityConfig;
import com.spring.nuqta.authentication.Services.AuthService;
import com.spring.nuqta.enums.Scope;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final HandlerExceptionResolver handlerExceptionResolver;

    private final AuthService authService;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return checkPath(path, SecurityConfig.PUBLIC_APIS);
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Extract the Authorization header from the request
        final String authHeader = request.getHeader("Authorization");
        logger.info("Authorization Header: " + authHeader);

        // Skip JWT validation for Basic Authentication
        if (authHeader != null && authHeader.startsWith("Basic ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Check if the Authorization header is missing or does not start with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extract the JWT token from the Authorization header
            final String jwt = authHeader.substring(7);
            logger.info("Extracted JWT: " + jwt);

            // If the JWT is empty, return an unauthorized response
            if (jwt.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // Authenticate the user or organization using the JWT token
            Optional<Object> auth = authService.authByToken(jwt);

            // If authentication fails, return an unauthorized response
            if (auth.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // Create an authentication token based on the authenticated user or organization
            UsernamePasswordAuthenticationToken authenticationToken = null;
            if (auth.get() instanceof AuthUserDto) {
                AuthUserDto userDto = (AuthUserDto) auth.get();
                authenticationToken = createAuthenticationToken(userDto, request);
            } else if (auth.get() instanceof AuthOrgDto) {
                AuthOrgDto orgDto = (AuthOrgDto) auth.get();
                authenticationToken = createAuthenticationToken(orgDto, request);
            }

            // Set the authentication token in the security context if valid
            if (authenticationToken != null) {
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

            // Continue processing the request
            filterChain.doFilter(request, response);

        } catch (Exception exception) {
            // Handle any exceptions that occur during the filter process
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }

    /**
     * Creates an authentication token for the authenticated user or organization.
     *
     * @param dto     The authenticated user or organization DTO.
     * @param request The HTTP request.
     * @return A UsernamePasswordAuthenticationToken containing the authentication details.
     */
    private UsernamePasswordAuthenticationToken createAuthenticationToken(Object dto,
                                                                          HttpServletRequest request) {
        // Determine the scope (role) of the authenticated entity
        Scope scope = (dto instanceof AuthUserDto) ? ((AuthUserDto) dto).getScope()
                : ((AuthOrgDto) dto).getScope();

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(scope.name()));

        // Create an authentication token with the scope and DTO as the principal
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(scope, dto, authorities);

        // Set additional details from the request (e.g., IP address, session ID)
        authenticationToken.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request));
        return authenticationToken;
    }


    private boolean checkPath(String path, String[] publicPaths) {
        for (String publicPath : publicPaths) {
            if (pathMatcher.match(publicPath, path)) {
                return true;
            }
        }
        return false;
    }
}