package dev.maram.medicalfile.kafka;

import dev.maram.medicalfile.medicalFile.MedicalFileService;
import dev.maram.medicalfile.medicalFile.PatientCreatedDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientEventConsumer {

    private final MedicalFileService service;
    private final KafkaTemplate<String, WelcomeEvent> kafkaTemplate;

    @KafkaListener(topics = "patient-events", groupId = "medical-file-group-v2")
    public void consume(PatientEvent event) {
        log.info("Received patient event: {}", event);
        if ("CREATED".equals(event.getEventType())) {
            service.createMedicalFileForPatient(
                    event.getPatientId(),
                    event.getFirstName(),
                    event.getLastName(),
                    event.getEmail()
            );
        }
    }
}
