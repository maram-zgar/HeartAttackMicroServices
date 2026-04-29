package dev.maram.gateway.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateDoctorRequest {

    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String numeroRPPS;
    @NotBlank
    private String hospital;
    @NotBlank
    private String initialPassword;

}
