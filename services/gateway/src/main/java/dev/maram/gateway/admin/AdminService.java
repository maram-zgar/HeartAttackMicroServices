package dev.maram.gateway.admin;

import dev.maram.gateway.auth.CreateDoctorRequest;
import dev.maram.gateway.kafka.DoctorEventProducer;
import dev.maram.gateway.kafka.DoctorRegisteredEvent;
import dev.maram.gateway.user.Role;
import dev.maram.gateway.user.User;
import dev.maram.gateway.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final DoctorEventProducer eventProducer;

    public void registerDoctor(CreateDoctorRequest request, UUID userId) {
        eventProducer.sendDoctorRegisteredEvent(DoctorRegisteredEvent.builder()
                .userId(userId)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .numeroRPPS(request.getNumeroRPPS())
                .hospital(request.getHospital())
                .password(request.getInitialPassword())
                .build());

        log.info("Doctor registered event published for {}", request.getEmail());
    }
}