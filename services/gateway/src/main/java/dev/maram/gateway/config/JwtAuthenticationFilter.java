package dev.maram.gateway.config;


import dev.maram.gateway.token.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
//import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.List;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtService jwtService;
    private final ReactiveUserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;

    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/v1/auth/authenticate",
            "/api/v1/auth/register",
            "/api/v1/auth/refresh-token"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        // Skip JWT validation entirely for public endpoints
        if (PUBLIC_PATHS.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        String jwt = authHeader.substring(7);
        String userEmail = jwtService.extractUsername(jwt);

        if (userEmail != null) {
            return userDetailsService.findByUsername(userEmail)
                    .flatMap(userDetails -> {
                        if (!jwtService.isTokenValid(jwt, userDetails)) {
                            return chain.filter(exchange);
                        }

                        // DB check: is this exact token still valid and not revoked?
                        return Mono.fromCallable(() ->
                                        tokenRepository.findByToken(jwt)
                                                .map(t -> !t.isExpired() && !t.isRevoked())
                                                .orElse(false)
                                )
                                .subscribeOn(Schedulers.boundedElastic())
                                .flatMap(isDbValid -> {
                                    if (isDbValid) {
                                        var authToken = new UsernamePasswordAuthenticationToken(
                                                userDetails, null, userDetails.getAuthorities()
                                        );
                                        return chain.filter(exchange)
                                                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authToken));
                                    }
                                    return chain.filter(exchange);
                                });
                    });
        }
        return chain.filter(exchange);
    }
}