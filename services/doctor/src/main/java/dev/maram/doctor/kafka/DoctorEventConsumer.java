package dev.maram.doctor.kafka;

import dev.maram.doctor.doctor.DoctorRequest;
import dev.maram.doctor.doctor.DoctorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DoctorEventConsumer {

    private final DoctorService doctorService;
    private final DoctorEventProducer doctorEventProducer;

    @KafkaListener(topics = "doctor.registered", groupId = "doctor-service")
    public void onDoctorRegistered(DoctorRegisteredEvent event) {
        log.info("Received doctor.registered for {}", event.getEmail());

        doctorService.createDoctor(new DoctorRequest(
                event.getUserId(),
                event.getFirstName(),
                event.getLastName(),
                event.getEmail(),
                event.getNumeroRPPS(),
                event.getHospital()

        ));

        doctorEventProducer.publishDocWelcome(
                DocWelcomeEvent.builder()
                        .email(event.getEmail())
                        .lastName(event.getLastName())
                        .numeroRPPS(event.getNumeroRPPS())
                        .password(event.getPassword())
                        .build()
        );
    }
}
