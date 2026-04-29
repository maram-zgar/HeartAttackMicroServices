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
public class AppointmentEvent {

    private UUID appointmentId;
    private UUID patientId;
    private String patientEmail;
    private String patientFirstName;

    private UUID doctorId;
    private String doctorEmail;
    private String lastName;

    private LocalDate appointmentDate;
    private String hospital;
    private String status;

}
