package dev.maram.gateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final ReactiveAuthenticationManager reactiveAuthenticationManager;

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .cors(Customizer.withDefaults())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        // 1. Global Preflight Options (Must be first)
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 2. Public Auth Endpoints
                        .pathMatchers("/api/v1/auth/authenticate",
                                "/api/v1/auth/register",
                                "/api/v1/auth/refresh-token").permitAll()

                        // 3. Authenticated Generic Auth Endpoints
                        .pathMatchers("/api/v1/auth/me").authenticated()
                        .pathMatchers(HttpMethod.PATCH, "/api/v1/auth/change-password").authenticated()

                        // 4. Admin Management
                        .pathMatchers("/admin/**").hasRole("ADMIN")

                        // 5. Shared Space: Doctor Availability (Specific rules BEFORE broad rules)
                        // Allow both Patients and Doctors to read availability
                        .pathMatchers(HttpMethod.GET, "/api/v1/doctors/*/availability").hasAnyRole("DOCTOR", "PATIENT")
                        // Restrict modifications (POST, DELETE, sub-paths) to DOCTORS only
                        .pathMatchers("/api/v1/doctors/*/availability", "/api/v1/doctors/*/availability/**").hasRole("DOCTOR")

                        // 6. Shared Space: Appointments
                        .pathMatchers(HttpMethod.GET, "/api/v1/appointments").hasRole("DOCTOR")
                        .pathMatchers("/api/v1/appointments/**").hasAnyRole("DOCTOR", "PATIENT")

                        // 7. Strict Doctor-Only Space
                        // Protects GET, POST, PUT, and DELETE for all patient tracks safely
                        .pathMatchers("/api/v1/patients", "/api/v1/patients/**").hasRole("DOCTOR")
                        .pathMatchers("/api/v1/medicalfiles/**").hasRole("DOCTOR")
                        .pathMatchers("/api/v1/doctors/consultation/complete").hasRole("DOCTOR")
                        .pathMatchers("/api/v1/doctors/profile/**").hasRole("DOCTOR")

                        // 8. Global Catch-All
                        .anyExchange().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((exchange, e) -> {
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                            var body = "{\"error\":\"Unauthorized\"}".getBytes(StandardCharsets.UTF_8);
                            var buffer = exchange.getResponse().bufferFactory().wrap(body);
                            return exchange.getResponse().writeWith(Mono.just(buffer));
                        })
                        .accessDeniedHandler((exchange, denied) -> {
                            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                            var body = "{\"error\":\"Forbidden\",\"message\":\"You do not have access to this resource.\"}".getBytes(StandardCharsets.UTF_8);
                            var buffer = exchange.getResponse().bufferFactory().wrap(body);
                            return exchange.getResponse().writeWith(Mono.just(buffer));
                        })
                )
                .authenticationManager(reactiveAuthenticationManager)
                .addFilterBefore(jwtAuthFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .logout(Customizer.withDefaults())
                .build();
    }
}