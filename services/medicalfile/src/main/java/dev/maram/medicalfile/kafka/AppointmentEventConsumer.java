package dev.maram.medicalfile.kafka;

import dev.maram.medicalfile.medicalFile.MedicalFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentEventConsumer {

    private final MedicalFileService service;

    @KafkaListener(topics = "appointment-events", groupId = "medical-file-appointment-group")
    public void consume(AppointmentEvent event) {
        log.info("Received appointment event: {}", event);
        service.linkAppointmentToPatient(event);
    }
}