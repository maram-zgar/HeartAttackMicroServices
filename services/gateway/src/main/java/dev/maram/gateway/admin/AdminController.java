package dev.maram.gateway.admin;

import dev.maram.gateway.auth.*;
import dev.maram.gateway.user.Role;
import dev.maram.gateway.user.User;
import dev.maram.gateway.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")   // entire controller is admin-only
public class AdminController {

    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final AdminService adminService;

    @PostMapping("/register-doctor")
    public Mono<ResponseEntity<RegistrationResponse>> createDoctor(@RequestBody CreateDoctorRequest request) {
        return authenticationService.createDoctor(request)
                .doOnSuccess(response -> adminService.registerDoctor(request, response.getUserId()))
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    // GET ALL DOCTORS
    @GetMapping("/doctors")
    public Mono<ResponseEntity<List<UserProfileResponse>>> getAllDoctors() {
        return Mono.fromCallable(() -> {
            List<UserProfileResponse> doctors = userRepository.findAllByRole(Role.DOCTOR)
                    .stream()
                    .map(this::toProfileResponse)
                    .toList();
            return ResponseEntity.ok(doctors);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    // FIND BY ID
    @GetMapping("/doctors/{id}")
    public Mono<ResponseEntity<UserProfileResponse>> getDoctorById(@PathVariable UUID id) {
        return Mono.fromCallable(() -> {
            var user = userRepository.findByIdAndRole(id, Role.DOCTOR)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found"));
            return ResponseEntity.ok(toProfileResponse(user));
        }).subscribeOn(Schedulers.boundedElastic());
    }

    // DELETE DOCTOR
    @DeleteMapping("/doctors/{id}")
    public Mono<ResponseEntity<Void>> deleteDoctor(@PathVariable UUID id) {
        return Mono.fromCallable(() -> {
            var user = userRepository.findByIdAndRole(id, Role.DOCTOR)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found"));
            userRepository.delete(user);
            return ResponseEntity.noContent().<Void>build();
        }).subscribeOn(Schedulers.boundedElastic());
    }

    // HELPER
    private UserProfileResponse toProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }



}
