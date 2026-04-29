package dev.maram.patient.kafka;

import dev.maram.patient.patient.PatientRequest;
import dev.maram.patient.patient.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PatientEventConsumer {

    private final PatientService patientService;

    @KafkaListener(topics = "patient.registered", groupId = "patient-service")
    public void onPatientRegistered(PatientRegisteredEvent event) {
        log.info("Received patient.registered for {}", event.getEmail());

        patientService.createPatient(new PatientRequest(
                null,
                event.getFirstName(),
                event.getLastName(),
                0,
                event.getEmail(),
                null
        ));
    }
}