package dev.maram.appointment.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppointmentEventProducer {

    private final KafkaTemplate<String, AppointmentEvent> appointmentKafkaTemplate;
    private final KafkaTemplate<String, AppointmentCompletedEvent> completedKafkaTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendAppointmentEvent(AppointmentEvent event) {
        appointmentKafkaTemplate.send("appointment.events", event);
        log.info("Sending appointment event: {}", event);
    }

    public void sendAppointmentCompletedEvent(AppointmentCompletedEvent event) {
        completedKafkaTemplate.send("appointment.completed", event);
        log.info("Sending appointment.completed for appointmentId={}", event.getAppointmentId());
    }

    public void sendSlotWarningEvent(AppointmentSlotWarningEvent event) {
        kafkaTemplate.send("appointment.slot.warning", event);
        log.warn("Sent appointment.slot.warning: doctorId={} date={} reason={}",
                event.getDoctorId(), event.getRequestedDate(), event.getReason());
    }
}