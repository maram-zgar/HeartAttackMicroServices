package dev.maram.gateway.auth;


import dev.maram.gateway.user.Role;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class UserProfileResponse {

    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;        // ADMIN | DOCTOR | PATIENT
}
