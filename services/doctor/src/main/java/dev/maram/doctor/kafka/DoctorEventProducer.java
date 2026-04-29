package dev.maram.doctor.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "doctor.events";
    private static final String COMPLETED_TOPIC = "appointment.completed";

    public void publishDocWelcome(DocWelcomeEvent event) {
        log.info("Publishing doctor.welcome for {}", event.getEmail());
        kafkaTemplate.send("doctor.welcome", event.getEmail(), event);
    }

    public void sendConsultationCompleted(AppointmentCompletedEvent event) {
        log.info("Sending appointment.completed event for appointmentId={}", event.getAppointmentId());
        kafkaTemplate.send(COMPLETED_TOPIC, event);
    }
}
