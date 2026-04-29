package dev.maram.gateway.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DoctorEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendDoctorRegisteredEvent(DoctorRegisteredEvent event) {
        log.info("Publishing doctor.registered for {}", event.getEmail());
        kafkaTemplate.send("doctor.registered", event.getEmail(), event);
    }
}