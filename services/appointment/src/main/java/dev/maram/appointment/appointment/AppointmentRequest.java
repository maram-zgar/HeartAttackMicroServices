package dev.maram.appointment.appointment;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AppointmentRequest(
        Long patientId,
        String patientEmail,
        String patientFirstName,
        Long doctorId,
        @NotNull LocalDateTime dateTime,
        @NotNull String hospital,
        AppointmentStatus status
) {
}
