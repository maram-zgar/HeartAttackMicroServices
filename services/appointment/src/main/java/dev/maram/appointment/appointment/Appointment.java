package dev.maram.appointment.appointment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Node
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {
    @Id
    @GeneratedValue
    private UUID id;
    private UUID patientId;
    private UUID doctorId;
    private LocalDateTime dateTime;
    private  AppointmentType appointmentType;
    private AppointmentStatus status;
}
