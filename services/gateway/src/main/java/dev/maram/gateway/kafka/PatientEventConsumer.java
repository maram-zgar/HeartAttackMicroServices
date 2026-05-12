package dev.maram.gateway.kafka;

import dev.maram.gateway.user.User;
import dev.maram.gateway.user.UserRepository;
import dev.maram.gateway.user.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientEventConsumer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @KafkaListener(topics = "patient-events", groupId = "gateway-auth-group")
    public void consumePatientCreated(PatientRegisteredEvent event) {
        log.info("Gateway Consumer: Processing event for {}", event.getEmail());

        if (userRepository.findByEmail(event.getEmail()).isEmpty()) {
            User newUser = User.builder()
                    .email(event.getEmail())
                    .password(passwordEncoder.encode(event.getTemporaryPassword()))
                    .role(Role.PATIENT)
                    .firstName(event.getFirstName())
                    .lastName(event.getLastName())
                    .dateOfBirth(event.getDateOfBirth())
                    .gender(event.getGender())
                    .age(event.getAge())
                    .doctorId(event.getDoctorId())
                    .build();
            userRepository.save(newUser);
            log.info("User created from Kafka event: {}", event.getEmail());
        } else {
            log.warn("User already exists, skipping creation: {}", event.getEmail());
        }
    }
}