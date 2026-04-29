package dev.maram.appointment.appointment;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record AppointmentRequest(
        UUID patientId,
        String patientEmail,
        String patientFirstName,
        UUID doctorId,
        @NotNull LocalDate dateTime,
        @NotNull String hospital,
        AppointmentStatus status
) {
}
