package dev.maram.patient.patient;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record PatientResponse (
        Long id,
        String firstName,
        String lastName,
        int age,
        String email,
        String hospital
){
}
