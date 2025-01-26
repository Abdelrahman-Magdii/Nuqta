package com.spring.nuqta.authentication;

import com.spring.nuqta.authentication.Jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // List of public API endpoints that don't require authentication
    public static final String[] PUBLIC_APIS = {"/swagger-ui/**", "/api/auth/**",
            "/api-docs/**", "/api/user/signin"};
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable() // Disable CSRF for APIs (adjust for forms)
                .httpBasic() // Enable basic HTTP authentication for testing
                .and()
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(HttpMethod.GET, PUBLIC_APIS)
                        .permitAll() // Allow access to public endpoints

                        .requestMatchers(HttpMethod.POST, PUBLIC_APIS)
                        .permitAll()

                        .anyRequest().authenticated() // All other requests require authentication
                ).sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Stateless session management (JWT)
                .and().authenticationProvider(authenticationProvider) // Custom authentication provider
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class); // Add JWT filter

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Set allowed origins (adjust for production)
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));

        // Allow specific methods, avoid "*"
        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Allow specific headers, avoid "*"
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply CORS config to all paths

        return source;
    }

}
