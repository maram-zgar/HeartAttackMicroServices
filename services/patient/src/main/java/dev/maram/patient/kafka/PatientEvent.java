package dev.maram.patient.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientEvent {
    private UUID patientId;
    private String firstName;
    private String lastName;
    private String email;
    private String eventType; // CREATED, UPDATED, DELETED
}
