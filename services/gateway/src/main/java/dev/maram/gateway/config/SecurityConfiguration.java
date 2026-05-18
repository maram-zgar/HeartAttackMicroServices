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
                        // 1. Preflight
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 2. Public auth
                        .pathMatchers(
                                "/api/v1/auth/authenticate",
                                "/api/v1/auth/register",
                                "/api/v1/auth/refresh-token"
                        ).permitAll()

                        // 3. Authenticated auth
                        .pathMatchers("/api/v1/auth/me").authenticated()
                        .pathMatchers(HttpMethod.PATCH, "/api/v1/auth/change-password").authenticated()

                        // 4. Admin
                        .pathMatchers("/admin/**").hasRole("ADMIN")

                        // 5. Doctor availability — GET open to both, mutations doctor-only
                        .pathMatchers(HttpMethod.GET, "/api/v1/doctors/*/availability")
                        .hasAnyRole("DOCTOR", "PATIENT")
                        .pathMatchers(HttpMethod.POST,   "/api/v1/doctors/*/availability").hasRole("DOCTOR")
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/doctors/*/availability").hasRole("DOCTOR")
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/doctors/*/availability/**").hasRole("DOCTOR")

                        // 6. Doctor read — patients need this to resolve their doctor's name
                        .pathMatchers(HttpMethod.GET, "/api/v1/doctors/*")
                        .hasAnyRole("DOCTOR", "PATIENT")

                        // 7. All other doctor endpoints — doctor only
                        .pathMatchers("/api/v1/doctors/**").hasRole("DOCTOR")

                        // 8. Available slots — must be BEFORE the broad /appointments/** rule
                        .pathMatchers(HttpMethod.GET, "/api/v1/appointments/available-slots")
                        .hasAnyRole("DOCTOR", "PATIENT")

                        // 9. Appointments — both roles
                        .pathMatchers(HttpMethod.GET,  "/api/v1/appointments")
                        .hasAnyRole("DOCTOR", "PATIENT")
                        .pathMatchers(HttpMethod.POST, "/api/v1/appointments")
                        .hasAnyRole("DOCTOR", "PATIENT")
                        .pathMatchers("/api/v1/appointments/**")
                        .hasAnyRole("DOCTOR", "PATIENT")

                        // 10. Medical file — patients can GET their own, doctors get everything
                        .pathMatchers(HttpMethod.GET, "/api/v1/medicalfiles/patient/*")
                        .hasAnyRole("DOCTOR", "PATIENT")
                        .pathMatchers("/api/v1/medicalfiles/**").hasRole("DOCTOR")

                        // 11. Patients — doctor only
                        .pathMatchers("/api/v1/patients", "/api/v1/patients/**").hasRole("DOCTOR")

                        // 12. Catch-all
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