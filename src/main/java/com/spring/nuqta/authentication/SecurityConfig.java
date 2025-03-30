package com.spring.nuqta.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.nuqta.authentication.Jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import java.util.Map;

import static com.spring.nuqta.enums.Scope.ORGANIZATION;
import static com.spring.nuqta.enums.Scope.USER;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    public static final String[] PUBLIC_APIS = {"/swagger-ui/**", "/api/auth/**",
            "/api-docs/**", "/", "/verify", "/verification-success.html", "/verification-failed.html"}; // WebSocket is public, authentication handled in handshake

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final MessageSource ms;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Enable CORS
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for WebSockets
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(PUBLIC_APIS).permitAll()
                        .requestMatchers("/api/user/**").hasAuthority(String.valueOf(USER))
                        .requestMatchers("/api/org/**").hasAuthority(String.valueOf(ORGANIZATION))
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(exceptions -> exceptions
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");

                            String message = ms.getMessage("security.permission", null, LocaleContextHolder.getLocale());

                            Map<String, String> errorResponse = Map.of(
                                    "message", message,
                                    "timestamp", java.time.LocalDateTime.now().toString(),
                                    "details", request.getRequestURI()
                            );

                            response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
                        })
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
