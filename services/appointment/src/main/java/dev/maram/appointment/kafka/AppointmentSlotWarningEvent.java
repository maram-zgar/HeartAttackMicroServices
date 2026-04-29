package dev.maram.appointment.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentSlotWarningEvent {

    private UUID doctorId;
    private UUID patientId;
    private LocalDate requestedDate;
    private String reason; // "PENDING_CONFLICT"
}