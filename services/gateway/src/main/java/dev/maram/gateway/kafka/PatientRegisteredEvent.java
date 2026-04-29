package dev.maram.gateway.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientRegisteredEvent {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
}