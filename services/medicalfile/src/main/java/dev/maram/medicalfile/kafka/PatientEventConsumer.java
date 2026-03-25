package dev.maram.medicalfile.kafka;

import dev.maram.medicalfile.medicalFile.MedicalFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class PatientEventConsumer {

    private final MedicalFileService service;

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
