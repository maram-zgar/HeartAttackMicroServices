package dev.maram.medicalfile.kafka;

import dev.maram.medicalfile.medicalFile.MedicalFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConsultationCreatedConsumer {

    private final MedicalFileService medicalFileService;

    @KafkaListener(topics = "consultation.created", groupId = "medical-file-group")
    public void consumeConsultationCreated(ConsultationCreatedEvent event) {
        log.info("Received ConsultationCreatedEvent for consultationId={}", event.getConsultationId());
        medicalFileService.linkConsultationToMedicalFile(event);
    }
}