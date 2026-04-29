package dev.maram.doctor.availability;

import dev.maram.doctor.doctor.DoctorRepository;
import dev.maram.doctor.exception.DoctorNotFoundException;
import dev.maram.doctor.kafka.DoctorAvailabilityEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorAvailabilityService {

    private final DoctorAvailabilityRepository availabilityRepository;
    private final DoctorRepository doctorRepository;
    private final KafkaTemplate<String, DoctorAvailabilityEvent> kafkaTemplate;

    @Transactional
    public DoctorAvailabilityResponse setAvailability(UUID doctorId, DoctorAvailabilityRequest request) {
        doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException(
                        String.format("No doctor found with id:: %s", doctorId)
                ));

        // Upsert: delete existing for that day then re-save
        availabilityRepository.deleteByDoctorIdAndDayOfWeek(doctorId, request.dayOfWeek());

        var availability = DoctorAvailability.builder()
                .doctorId(doctorId)
                .dayOfWeek(request.dayOfWeek())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .slotDurationMinutes(request.slotDurationMinutes())
                .build();

        var saved = availabilityRepository.save(availability);

        // Publish so Appointment Service can cache it
        kafkaTemplate.send("doctor.availability", DoctorAvailabilityEvent.builder()
                .doctorId(doctorId)
                .dayOfWeek(request.dayOfWeek())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .slotDurationMinutes(request.slotDurationMinutes())
                .action("SET")
                .build());

        log.info("Availability set and published for doctorId={} on {}", doctorId, request.dayOfWeek());
        return toResponse(saved);
    }

    public List<DoctorAvailabilityResponse> getAvailability(UUID doctorId) {
        return availabilityRepository.findByDoctorId(doctorId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteAvailability(UUID doctorId, DayOfWeek day) {
        availabilityRepository.deleteByDoctorIdAndDayOfWeek(doctorId, day);

        kafkaTemplate.send("doctor.availability", DoctorAvailabilityEvent.builder()
                .doctorId(doctorId)
                .dayOfWeek(day)
                .action("DELETED")
                .build());

        log.info("Availability deleted and published for doctorId={} on {}", doctorId, day);
    }

    private DoctorAvailabilityResponse toResponse(DoctorAvailability a) {
        return new DoctorAvailabilityResponse(
                a.getId(),
                a.getDoctorId(),
                a.getDayOfWeek(),
                a.getStartTime(),
                a.getEndTime(),
                a.getSlotDurationMinutes()
        );
    }
}