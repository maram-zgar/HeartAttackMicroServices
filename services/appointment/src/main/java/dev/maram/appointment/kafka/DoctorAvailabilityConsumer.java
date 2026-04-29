package dev.maram.appointment.kafka;

import dev.maram.appointment.availability.CachedDoctorAvailability;
import dev.maram.appointment.availability.CachedDoctorAvailabilityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DoctorAvailabilityConsumer {

    private final CachedDoctorAvailabilityRepository cachedAvailabilityRepository;

    @KafkaListener(topics = "doctor.availability", groupId = "appointment-service")
    @Transactional
    public void consume(DoctorAvailabilityEvent event) {
        log.info("Received doctor.availability event: action={} doctorId={} day={}",
                event.getAction(), event.getDoctorId(), event.getDayOfWeek());

        // Always remove old entry first
        cachedAvailabilityRepository.deleteByDoctorIdAndDayOfWeek(
                event.getDoctorId(), event.getDayOfWeek()
        );

        if ("SET".equals(event.getAction())) {
            var cached = CachedDoctorAvailability.builder()
                    .doctorId(event.getDoctorId())
                    .dayOfWeek(event.getDayOfWeek())
                    .startTime(event.getStartTime())
                    .endTime(event.getEndTime())
                    .slotDurationMinutes(event.getSlotDurationMinutes())
                    .build();
            cachedAvailabilityRepository.save(cached);
            log.info("Cached availability for doctorId={} on {}", event.getDoctorId(), event.getDayOfWeek());
        }
        // If DELETED, the deleteBy above already handled it
    }
}