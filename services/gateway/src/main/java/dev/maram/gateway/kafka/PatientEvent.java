package dev.maram.gateway.kafka;

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
    private UUID    doctorId;
    private String  firstName;
    private String  lastName;
    private String  email;
    private String  temporaryPassword;
    private String  dateOfBirth;
    private String  gender;
    private Integer age;
    private String eventType; // CREATED, UPDATED, DELETED
}
