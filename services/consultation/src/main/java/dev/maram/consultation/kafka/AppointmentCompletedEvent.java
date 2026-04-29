package dev.maram.consultation.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentCompletedEvent {

    private UUID appointmentId;
    private UUID patientId;
    private UUID doctorId;
    private String patientEmail;
    private String patientFirstName;
}