package dev.maram.doctor.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentEventConsumer {

    @KafkaListener(
            topics = {"appointment.created", "appointment.rescheduled", "appointment.cancelled"},
            groupId = "doctor-service-group"
    )
    public void handleAppointmentEvent(AppointmentEvent event) {
        log.info("Doctor-service received [{}] for doctor: {}",
                event.getStatus(), event.getDoctorEmail());
        // plug into your DoctorService notification logic here
    }
}
