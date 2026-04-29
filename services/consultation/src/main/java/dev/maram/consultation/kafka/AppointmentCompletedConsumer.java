package dev.maram.consultation.kafka;

import dev.maram.consultation.consultation.ConsultationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppointmentCompletedConsumer {

    private final ConsultationService consultationService;

    @KafkaListener(topics = "appointment.completed", groupId = "consultation-service")
    public void consume(AppointmentCompletedEvent event) {
        log.info("Received appointment.completed for appointmentId={}", event.getAppointmentId());
        consultationService.createFromCompletedAppointment(event);
    }
}