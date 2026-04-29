package dev.maram.doctor.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocWelcomeEvent {

    private String email;
    private String lastName;
    private String numeroRPPS;
    private String password;
}
