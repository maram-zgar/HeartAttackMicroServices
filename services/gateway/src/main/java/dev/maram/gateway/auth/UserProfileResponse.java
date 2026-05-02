package dev.maram.gateway.auth;

import dev.maram.gateway.user.Role;
import lombok.*;

import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UserProfileResponse {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;

    // Doctor-specific (null for patients)
    private String numeroRPPS;
    private String phoneNumber;
    private String avatarUrl;
    private String hospital;
    private Boolean isActive;

    // Patient-specific (null for doctors) — extend later
    private String dateOfBirth;
    private String gender;
}