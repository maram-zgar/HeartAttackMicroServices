package dev.maram.appointment.kafka;

import dev.maram.appointment.appointment.AppointmentRepository;
import dev.maram.appointment.appointment.AppointmentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentCompletedConsumer {

    private final AppointmentRepository repository;

    @KafkaListener(topics = "appointment.completed", groupId = "appointment-service-group")
    public void onConsultationCompleted(AppointmentCompletedEvent event) {
        repository.findById(event.getAppointmentId()).ifPresent(appointment -> {
            appointment.setStatus(AppointmentStatus.COMPLETED);
            repository.save(appointment);
            log.info("Marked appointment {} as COMPLETED", event.getAppointmentId());
        });
    }
}
