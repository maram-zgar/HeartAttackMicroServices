package dev.maram.consultation.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConsultationEventProducer {

    private final KafkaTemplate<String, ConsultationCreatedEvent> kafkaTemplate;

    public void send(ConsultationCreatedEvent event) {
        kafkaTemplate.send("consultation.created", event);
        log.info("Published consultation.created for patientId={}", event.getPatientId());
    }
}