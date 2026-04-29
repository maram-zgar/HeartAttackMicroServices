package dev.maram.gateway.kafka;


import lombok.Builder;
import lombok.Data;

import java.util.UUID;


@Data
@Builder
public class DoctorRegisteredEvent {

    private UUID userId;
    private String firstName;
    private String lastName;
    private String email;
    private String numeroRPPS;
    private String hospital;
    private String password;
}
