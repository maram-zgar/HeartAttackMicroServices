package dev.maram.patient.patient;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PatientResponse (
        UUID id,
        String firstName,
        String lastName,
        int age,
        String email,
        String hospital
){
}
