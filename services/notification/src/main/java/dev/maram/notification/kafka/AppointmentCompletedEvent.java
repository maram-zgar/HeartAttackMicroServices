package dev.maram.notification.kafka;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentCompletedEvent {
    private String appointmentId;
    private String patientId;
    private String patientEmail;
    private String patientFirstName;
    private String doctorId;
    private String doctorEmail;
    private LocalDate completedAt;
}
