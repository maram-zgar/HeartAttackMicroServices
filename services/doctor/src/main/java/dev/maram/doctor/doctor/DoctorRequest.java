package dev.maram.doctor.doctor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DoctorRequest(
        UUID id,
        @NotNull(message = "Prénom du Docteur (first name) est obligatoire")
        String firstName,
        @NotNull(message = "Nom du Docteur (last name) est obligatoire")
        String lastName,
        @NotNull(message = "Email du Docteur est obligatoire")
        @Email(message = "Email unvalid")
        String email,
        @NotNull(message = "Numéro RPPS est obligatoire")
        String numeroRPPS,
        @NotNull(message = "Hopital est obligatoire")
        String hospital
) {
}
