package dev.maram.gateway.auth;

import dev.maram.gateway.config.JwtService;
import dev.maram.gateway.kafka.PatientRegisteredEvent;
import dev.maram.gateway.session.Session;
import dev.maram.gateway.session.SessionRepository;
import dev.maram.gateway.session.TokenHasher;
import dev.maram.gateway.token.Token;
import dev.maram.gateway.token.TokenRepository;
import dev.maram.gateway.token.TokenType;
import dev.maram.gateway.user.Role;
import dev.maram.gateway.user.User;
import dev.maram.gateway.user.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ReactiveAuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;



    // PATIENT SELF-REGISTRATION

    public Mono<RegistrationResponse> registerPatient(RegisterRequest request) {
        return Mono.fromCallable(() -> {
            var user = User.builder()
                    .firstName(request.getFirstname())
                    .lastName(request.getLastname())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(Role.PATIENT)
                    .build();

            var savedUser = repository.save(user);
            var accessToken = jwtService.generateToken(savedUser);
            saveUserToken(savedUser, accessToken);

            kafkaTemplate.send("patient.registered", PatientRegisteredEvent.builder()
                    .email(savedUser.getEmail())
                    .firstName(savedUser.getFirstName())
                    .lastName(savedUser.getLastName())
                    .build());

            log.info("Published patient.registered event for {}", savedUser.getEmail());

            return RegistrationResponse.builder()
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .email(user.getEmail())
                    .message("User registered!")
                    .build();

        }).subscribeOn(Schedulers.boundedElastic());
    }


    // DOCTOR CREATION BY ADMIN

    public Mono<RegistrationResponse> createDoctor(CreateDoctorRequest request) {
        return Mono.fromCallable(() -> {

            boolean emailTaken = userRepository.findByEmail(request.getEmail()).isPresent();
            if (emailTaken) {
                throw new IllegalArgumentException("A user with this email already exists: " + request.getEmail());
            }

            var user = User.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getInitialPassword()))
                    .role(Role.DOCTOR)
                    .build();

            var savedUser = userRepository.save(user);

            log.info("Doctor account created by admin — userId={}, email={}", savedUser.getId(), savedUser.getEmail());

            return RegistrationResponse.builder()
                    .userId(savedUser.getId())
                    .firstName(savedUser.getFirstName())
                    .lastName(savedUser.getLastName())
                    .email(savedUser.getEmail())
                    .message("Doctor account created. They can now log in with the provided credentials.")
                    .build();

        }).subscribeOn(Schedulers.boundedElastic());
    }


    // LOGIN — authenticates credentials, issues tokens, opens a session

    public Mono<AuthenticationResponse> authenticate(AuthenticationRequest request) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        ).flatMap(auth ->
                Mono.fromCallable(() -> {
                    var user = repository.findByEmail(request.getEmail()).orElseThrow();

                    // Revoke any existing sessions for this user before opening a new one
                    sessionRepository.revokedSessionForUser(user.getId());

                    var accessToken = jwtService.generateToken(user);
                    var refreshToken = jwtService.generateRefreshToken(user);

                    String refreshTokenHash = TokenHasher.hash(refreshToken);
                    log.info("------NEW AUTHENTICATION");
                    log.info("username {}", user.getUsername());
                    log.info("Generated REFRESH TOKEN: {}", refreshToken);
                    log.info("Generated REFRESH TOKEN HASH: {}", refreshTokenHash);

                    saveUserToken(user, accessToken);
                    openSession(user, refreshToken);

                    log.info("User authenticated and session created for {}", user.getEmail());

                    return AuthenticationResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshToken)
                            .build();

                }).subscribeOn(Schedulers.boundedElastic())
        );
    }


    // /me — returns the profile of the currently authenticated user

    public Mono<UserProfileResponse> getCurrentUserProfile(Authentication authentication) {
        return Mono.fromCallable(() -> {
            String email = authentication.getName();
            var user = userRepository.findByEmail(email).orElseThrow();

            return UserProfileResponse.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .role(user.getRole())
                    .build();

        }).subscribeOn(Schedulers.boundedElastic());
    }


    // REFRESH TOKEN — validates session, issues new access token

    public Mono<AuthenticationResponse> refreshToken(ServerHttpRequest request) {
        return Mono.fromCallable(() -> {
            final String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            log.info("------ REFRESH TOKEN");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.error("Refresh failed: Missing or invalid Authorization header");
                throw new IllegalArgumentException("Missing or invalid Authorization header");
            }

            String refreshToken = authHeader.substring(7).trim();
            log.info("Incoming RefreshToken (from header): {}", refreshToken.substring(0, 20) + "...");
            String userEmail = jwtService.extractUsername(refreshToken);
            log.info("Extracted Username from RefreshToken: {}", userEmail);

            if (userEmail == null) {
                throw new IllegalArgumentException("Invalid refresh token");
            }

            var user = repository.findByEmail(userEmail).orElseThrow();

            if (!jwtService.isTokenValid(refreshToken, user)) {
                log.error("Refresh failed: Token is not valid for user {}", userEmail);
                throw new IllegalArgumentException("Refresh token is not valid");
            }

            String tokenHash = TokenHasher.hash(refreshToken);
            log.info("Computed HASH of incoming token: {}", tokenHash);
            var session = sessionRepository.findByRefreshTokenHash(tokenHash)
                    .orElseThrow(() -> new IllegalArgumentException("No active session found for this refresh token"));

            if (session.isRevoked() || session.getExpiresAt().isBefore(Instant.now())) {
                throw new IllegalArgumentException("Session has been revoked or has expired");
            }

            // hash update

            var newAccessToken = jwtService.generateToken(user);
            var newRefreshToken = jwtService.generateRefreshToken(user);

            session.setRefreshTokenHash(TokenHasher.hash(newRefreshToken));
            session.setExpiresAt(jwtService.extractExpiration(newRefreshToken).toInstant());

            sessionRepository.save(session);

            log.info("Refresh successful. Session hash updated for user: {}", userEmail);

            log.info("Access token refreshed for user={}", userEmail);

            return AuthenticationResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .build();

        }).subscribeOn(Schedulers.boundedElastic());
    }


    // PRIVATE HELPERS

    private void openSession(User user, String refreshToken) {
        Instant expiresAt = jwtService.extractExpiration(refreshToken).toInstant();

        var session = Session.builder()
                .refreshTokenHash(TokenHasher.hash(refreshToken))
                .user(user)
                .expiresAt(expiresAt)
                .createdAt(Instant.now())
                .revoked(false)
                .build();

        sessionRepository.save(session);
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
}