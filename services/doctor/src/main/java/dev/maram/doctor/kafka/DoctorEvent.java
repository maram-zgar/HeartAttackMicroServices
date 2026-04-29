package dev.maram.doctor.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorEvent {

    private UUID doctorId;
    private String firstName;
    private String lastName;
    private String email;
    private String numeroRPPS;
    private String eventType;

}
