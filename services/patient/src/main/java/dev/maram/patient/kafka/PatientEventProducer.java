package dev.maram.patient.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientEventProducer {

    private final KafkaTemplate<String, PatientEvent> kafkaTemplate;
    private static final String TOPIC = "patient-events";

    public void sendPatientEvent(PatientEvent event) {
        log.info("Sending patient event: {}", event);
        kafkaTemplate.send(TOPIC, event);
    }
}
