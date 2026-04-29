package dev.maram.gateway.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationResponse {

    private UUID userId;
    private String firstName;
    private String lastName;
    private String email;
    private String message;
}
