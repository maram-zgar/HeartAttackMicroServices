package dev.maram.patient.patient;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PatientRequest(
        UUID id,
        @NotNull(message = "Prénom du Patient (first name) est obligatoire")
        String firstName,
        @NotNull(message = "Nom du Patient (last name) est obligatoire")
        String lastName,
        @NotNull(message = "Age du Patient est obligatoire")
        int age,
        @NotNull(message = "Email du Patient est obligatoire")
        @Email(message = "Email unvalid")
        String email,
        @NotNull(message = "Hopital est obligatoire")
        String hospital
) {
}


