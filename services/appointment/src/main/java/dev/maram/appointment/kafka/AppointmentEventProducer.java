package dev.maram.appointment.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentEventProducer {

    private final KafkaTemplate<String, AppointmentEvent> kafkaTemplate;
    private static final String TOPIC = "appointment-events";

    public void sendAppointmentEvent(AppointmentEvent event) {
        log.info("Sending appointment event: {}", event);
        kafkaTemplate.send(TOPIC, event);
    }
}
