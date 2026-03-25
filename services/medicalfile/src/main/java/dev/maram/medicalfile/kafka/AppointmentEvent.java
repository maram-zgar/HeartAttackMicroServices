package dev.maram.medicalfile.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentEvent {

    private Long appointmentId;
    private Long patientId;
    private String patientEmail;
    private String patientFirstName;
    private LocalDateTime dateTime;
    private String hospital;
    private String status;
}