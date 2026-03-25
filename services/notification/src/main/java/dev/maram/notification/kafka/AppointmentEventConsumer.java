package dev.maram.notification.kafka;

import dev.maram.notification.kafka.AppointmentEvent;
import dev.maram.notification.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentEventConsumer {

    private final EmailService emailService;

    @KafkaListener(topics = "appointment-events", groupId = "notification-group-v3")
    public void consume(AppointmentEvent event) {
        log.info("Received appointment event: {}", event);
        switch (event.getStatus()) {
            case "PENDING", "CREATED"     -> emailService.sendAppointmentBooked(event);
            case "CANCELLED"  -> emailService.sendAppointmentCancelled(event);
            case "UPDATED"    -> emailService.sendAppointmentUpdated(event);
            default -> log.warn("Unknown event status: {}", event.getStatus());
        }
    }
}
