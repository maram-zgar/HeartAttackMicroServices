package dev.maram.patient.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientRegisteredEvent {

    private String email;
    private String firstName;
    private String lastName;
}