package dev.maram.notification.kafka;

import dev.maram.notification.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final EmailService emailService;

    @KafkaListener(
            topics = "appointment.events",
            groupId = "notification-service",
            containerFactory = "appointmentKafkaListenerContainerFactory"

    )
    public void onAppointmentEvent(AppointmentEvent event) {
        switch (event.getStatus()) {
            case "PENDING"    -> emailService.sendAppointmentBooked(event);
            case "ACCEPTED" -> emailService.sendAppointmentAccepted(event);
            case "CANCELLED" -> emailService.sendAppointmentCancelled(event);
            case "RESCHEDULED" -> emailService.sendAppointmentRescheduled(event);
            default -> log.warn("Unknown appointment status: {}", event.getStatus());
        }
    }

    @KafkaListener(
            topics = "patient.welcome",
            groupId = "notification-service",
            containerFactory = "welcomeKafkaListenerContainerFactory"
    )
    public void onPatientWelcome(WelcomeEvent event) {
        log.info("Received patient.welcome for {}", event.getEmail());
        emailService.sendWelcome(event);
    }

    @KafkaListener(
            topics = "doctor.welcome",
            groupId = "notification-service",
            containerFactory = "docWelcomeKafkaListenerContainerFactory"
    )
    public void onDoctorWelcome(DocWelcomeEvent event) {
        log.info("Received doctor.welcome for {}", event.getEmail());
        emailService.sendDocWelcome(event);
    }
}