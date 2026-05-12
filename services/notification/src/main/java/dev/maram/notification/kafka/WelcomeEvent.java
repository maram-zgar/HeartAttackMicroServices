package dev.maram.notification.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WelcomeEvent {

    private String email;
    private String firstName;
    private String lastName;
    private UUID medicalFileId;
    private String temporaryPassword;
}