package dev.maram.gateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

//import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//public class SecurityConfiguration {
//
//    //private static final String[] WHITE_LIST_URL = {"/api/v1/auth/**"};
//    private final JwtAuthenticationFilter jwtAuthFilter;
//    private final AuthenticationProvider authenticationProvider;
//
//    // White listing : some endpoints do not require any auth/token (ex: create an account)
//    // The session state should not be stores (STATELESS) -> ensures that each requests gets authenticated
//    // The auth provider
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(req ->
//                        req.requestMatchers("/api/v1/auth/**") // Authorize any request within this list but request auth for all others
//                                .permitAll()
//                                .anyRequest()
//                                .authenticated()
//                )
//                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
//                .authenticationProvider(authenticationProvider)
//                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
//        ;
//
//        return http.build();
//    }
//}


@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final ReactiveAuthenticationManager reactiveAuthenticationManager;


    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("http://localhost:4200"));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))
                .csrf(csrf -> csrf.disable())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        // Public auth endpoints
                        .pathMatchers("/api/v1/auth/authenticate",
                                "/api/v1/auth/register",
                                "/api/v1/auth/refresh-token").permitAll()
                        .pathMatchers("/api/v1/auth/me").authenticated()
                        .pathMatchers(HttpMethod.PATCH, "/api/v1/auth/change-password").authenticated()

                        // Admin-only: managing doctors (CRUD)
                        .pathMatchers("/admin/**").hasRole("ADMIN")

                        // Doctor-only
                        .pathMatchers(HttpMethod.POST, "/api/v1/doctors/consultation/complete").hasRole("DOCTOR")
                        .pathMatchers(HttpMethod.GET, "/api/v1/patients/**").hasRole("DOCTOR")
                        .pathMatchers(HttpMethod.POST, "/api/v1/patients").hasRole("DOCTOR")
                        .pathMatchers(HttpMethod.GET, "/api/v1/appointments").hasRole("DOCTOR")
                        .pathMatchers("/api/v1/medicalfiles/**").hasRole("DOCTOR")


                        .pathMatchers("/api/v1/appointments/**").hasAnyRole("DOCTOR", "PATIENT")

                        .anyExchange().authenticated()
                )
                .authenticationManager(reactiveAuthenticationManager)
                .addFilterBefore(jwtAuthFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .logout(Customizer.withDefaults())
                .build();
    }
}