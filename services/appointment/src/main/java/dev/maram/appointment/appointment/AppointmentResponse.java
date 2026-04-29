package dev.maram.appointment.appointment;

import com.netflix.spectator.api.Measurement;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record AppointmentResponse(
        UUID id,
        UUID patientId,
        UUID doctorId,
        LocalDate dateTime,
        String hospital,
        AppointmentStatus status
) {
}
