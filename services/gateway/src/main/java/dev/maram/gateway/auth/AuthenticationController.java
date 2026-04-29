package dev.maram.gateway.auth;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<Mono<RegistrationResponse>> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(service.registerPatient(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<Mono<AuthenticationResponse>> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Mono<AuthenticationResponse>> refreshToken(ServerWebExchange exchange) {
        return ResponseEntity.ok(service.refreshToken(exchange.getRequest()));
    }

    // returns the authenticated users s profile
    @GetMapping("/me")
    public ResponseEntity<Mono<UserProfileResponse>> me(Authentication authentication) {
        return ResponseEntity.ok(service.getCurrentUserProfile(authentication));
    }
}
