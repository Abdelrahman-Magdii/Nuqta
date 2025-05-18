package com.spring.nuqta.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.nuqta.authentication.Jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Map;

import static com.spring.nuqta.enums.Scope.ORGANIZATION;
import static com.spring.nuqta.enums.Scope.USER;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    public static final String[] PUBLIC_APIS = {"/swagger-ui/**", "/api/auth/**",
            "/api-docs/**", "/", "/verify", "/verification-success.html", "/verification-failed.html"};

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final MessageSource ms;

    @Value("${cors.allowed-origins}")
    private String[] allowedOrigins;

    @Value("${token.base.url}")
    private String baseUrl;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .requiresChannel(channel -> channel
                        .requestMatchers(r ->
                                "http".equals(r.getHeader("X-Forwarded-Proto"))
                        ).requiresSecure()
                )
                .headers(headers -> headers
                        .xssProtection(HeadersConfigurer.XXssConfig::disable)
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                        .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'"))
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Enable CORS
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(PUBLIC_APIS).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/user").hasAnyAuthority(String.valueOf(USER), String.valueOf(ORGANIZATION))
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

        // Add base URL first
        if (baseUrl != null && !baseUrl.isEmpty()) {
            configuration.addAllowedOrigin(baseUrl);
        }

        // Add all allowed origins
        if (allowedOrigins != null) {
            for (String origin : allowedOrigins) {
                configuration.addAllowedOrigin(origin.trim());
            }
        }


        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "X-Total-Count"));
        configuration.setMaxAge(3600L);
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


}
