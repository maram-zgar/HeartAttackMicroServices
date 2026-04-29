package dev.maram.doctor.doctor;

import java.util.UUID;

public record DoctorResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String numeroRPPS,
        String hospital
) {
}
