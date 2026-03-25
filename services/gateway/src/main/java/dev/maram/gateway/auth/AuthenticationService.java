package dev.maram.gateway.auth;


import dev.maram.gateway.config.JwtService;
import dev.maram.gateway.token.Token;
import dev.maram.gateway.token.TokenRepository;
import dev.maram.gateway.token.TokenType;
import dev.maram.gateway.user.Role;
import dev.maram.gateway.user.User;
import dev.maram.gateway.user.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ReactiveAuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;

    public Mono<RegistrationResponse> register(RegisterRequest request) {
        return Mono.fromCallable(() -> {
            var user = User.builder()
                    .firstName(request.getFirstname())
                    .lastName(request.getLastname())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(Role.USER)
                    .build();
            repository.save(user);
            return RegistrationResponse.builder()
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .email(user.getEmail())
                    .message("User registered!")
                    .build();
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<AuthenticationResponse> authenticate(AuthenticationRequest request) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        ).flatMap(auth ->
                Mono.fromCallable(() -> {
                    var user = repository.findByEmail(request.getEmail())
                            .orElseThrow();
                    var jwtToken = jwtService.generateToken(user);
                    var refreshToken = jwtService.generateRefreshToken(user);
                    saveUserToken(user, jwtToken);
                    return AuthenticationResponse.builder()
                            .accessToken(jwtToken)
                            .refreshToken(refreshToken)
                            .build();
                }).subscribeOn(Schedulers.boundedElastic())
        );
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    public Mono<AuthenticationResponse> refreshToken(ServerHttpRequest request) {
        return Mono.fromCallable(() -> {
            final String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new IllegalArgumentException("Missing or invalid Authorization header");
            }

            String refreshToken = authHeader.substring(7);
            String userEmail = jwtService.extractUsername(refreshToken);

            if (userEmail == null) {
                throw new IllegalArgumentException("Invalid refresh token");
            }

            var user = repository.findByEmail(userEmail).orElseThrow();

            if (!jwtService.isTokenValid(refreshToken, user)) {
                throw new IllegalArgumentException("Refresh token is not valid");
            }

            var accessToken = jwtService.generateToken(user);
            return AuthenticationResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

        }).subscribeOn(Schedulers.boundedElastic());
    }
}


//import dev.maram.gateway.config.JwtService;
//import dev.maram.gateway.user.Role;
//import dev.maram.gateway.user.User;
//import dev.maram.gateway.user.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.authentication.ReactiveAuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import reactor.core.publisher.Mono;
//
//@Service
//@RequiredArgsConstructor
//public class AuthenticationService {
//
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final JwtService jwtService;
//    // Switch to the Reactive version
//    private final ReactiveAuthenticationManager authenticationManager;
//
//    public Mono<AuthenticationResponse> register(RegisterRequest request) {
//        var user = User.builder()
//                .firstName(request.getFirstname())
//                .lastName(request.getLastname())
//                .email(request.getEmail())
//                .password(passwordEncoder.encode(request.getPassword()))
//                .role(Role.USER)
//                .build();
//
//        // repository.save(user) now returns Mono<User>
//        return userRepository.save(user) // This returns Mono<User>
//                .map(savedUser -> {
//                    var jwtToken = jwtService.generateToken((UserDetails) savedUser);
//                    return AuthenticationResponse.builder()
//                            .token(jwtToken)
//                            .build();
//                });
//    }
//
//    public Mono<AuthenticationResponse> authenticate(AuthenticationRequest request) {
//        return authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        request.getEmail(),
//                        request.getPassword()
//                )
//        ).flatMap(auth ->
//                // After successful auth, find the user and generate token
//                userRepository.findByEmail(request.getEmail())
//                        .switchIfEmpty(Mono.error(new RuntimeException("User not found")))
//                        .map(user -> {
//                            var jwtToken = jwtService.generateToken(user);
//                            return AuthenticationResponse.builder()
//                                    .token(jwtToken)
//                                    .build();
//                        })
//        );
//    }
//}