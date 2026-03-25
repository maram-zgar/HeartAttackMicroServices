package dev.maram.appointment.appointment;

import com.netflix.spectator.api.Measurement;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AppointmentResponse(
        Long id,
        Long patientId,
        Long doctorId,
        LocalDateTime dateTime,
        String hospital,
        AppointmentStatus status
) {
}
