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
public class PatientRegisteredEvent {

    private UUID patientId;
    private UUID    doctorId;
    private String  firstName;
    private String  lastName;
    private String  email;
    private String  temporaryPassword;
    private String  dateOfBirth;
    private String  gender;
    private Integer age;
}