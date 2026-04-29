package dev.maram.appointment.appointment;

import dev.maram.appointment.availability.CachedDoctorAvailabilityRepository;
import dev.maram.appointment.kafka.AppointmentCompletedEvent;
import dev.maram.appointment.kafka.AppointmentEvent;
import dev.maram.appointment.kafka.AppointmentEventProducer;
import dev.maram.appointment.kafka.AppointmentSlotWarningEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository repository;
    private final AppointmentEventProducer producer;
    private final AppointmentMapper mapper;
    private final CachedDoctorAvailabilityRepository cachedAvailabilityRepository;


    public AppointmentResponse createAppointment(AppointmentRequest request) {

        // 1. Check doctor has availability on that day of week
        var availability = cachedAvailabilityRepository
                .findByDoctorIdAndDayOfWeek(request.doctorId(), request.dateTime().getDayOfWeek())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Doctor is not available on " + request.dateTime().getDayOfWeek()
                ));

        // 2. Check for a CONFIRMED appointment on the same date — hard block
        boolean confirmedConflict = repository
                .existsByDoctorIdAndDateTimeAndStatus(
                        request.doctorId(),
                        request.dateTime(),
                        AppointmentStatus.CONFIRMED
                );
        if (confirmedConflict) {
            throw new IllegalStateException(
                    "This slot is already confirmed for another patient."
            );
        }

        // 3. Check for a PENDING appointment on the same date — soft warning via Kafka
        boolean pendingConflict = repository
                .existsByDoctorIdAndDateTimeAndStatus(
                        request.doctorId(),
                        request.dateTime(),
                        AppointmentStatus.PENDING
                );
        if (pendingConflict) {
            //log.warn("Pending conflict for doctorId={} on {}", request.doctorId(), request.dateTime());
            producer.sendSlotWarningEvent(AppointmentSlotWarningEvent.builder()
                    .doctorId(request.doctorId())
                    .patientId(request.patientId())
                    .requestedDate(request.dateTime())
                    .reason("PENDING_CONFLICT")
                    .build());
            // We still allow booking — warning is informational
        }

        // 4. Save

        var appointment = Appointment.builder()
                .patientId(request.patientId())
                .doctorId(request.doctorId())
                .dateTime(request.dateTime())
                .hospital(request.hospital())
                .status(AppointmentStatus.PENDING)
                .build();

        var saved = repository.save(appointment);

        producer.sendAppointmentEvent(AppointmentEvent.builder()
                .appointmentId(saved.getId())
                .patientId(request.patientId())
                .patientEmail(request.patientEmail())
                .patientFirstName(request.patientFirstName())
                .appointmentDate(saved.getDateTime())
                .hospital(saved.getHospital())
                .status("PENDING")
                .build());

        return mapper.toResponse(saved);
    }

    public AppointmentResponse rescheduleAppointment(UUID id, AppointmentRequest request) {
        var appointment = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setDateTime(request.dateTime());
        appointment.setStatus(request.status());

        var saved = repository.save(appointment);

        producer.sendAppointmentEvent(AppointmentEvent.builder()
                .appointmentId(saved.getId())
                .patientId(saved.getPatientId())
                .patientEmail(request.patientEmail())
                .patientFirstName(request.patientFirstName())
                .appointmentDate(saved.getDateTime())
                .hospital(saved.getHospital())
                .status("RESCHEDULED")
                .build());

        return mapper.toResponse(saved);
    }

    public AppointmentResponse confirmAppointment(UUID id, AppointmentRequest request) {
        var appointment = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setStatus(AppointmentStatus.CONFIRMED);
        var saved = repository.save(appointment);

        producer.sendAppointmentEvent(AppointmentEvent.builder()
                .appointmentId(saved.getId())
                .patientId(saved.getPatientId())
                .patientEmail(request.patientEmail())
                .patientFirstName(request.patientFirstName())
                .appointmentDate(saved.getDateTime())
                .hospital(saved.getHospital())
                .status("CONFIRMED")
                .build());

        return mapper.toResponse(saved);
    }

    public AppointmentResponse cancelAppointment(UUID id, AppointmentRequest request) {
        var appointment = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setStatus(AppointmentStatus.CANCELLED);

        var saved = repository.save(appointment);

        producer.sendAppointmentEvent(AppointmentEvent.builder()
                .appointmentId(saved.getId())
                .patientId(saved.getPatientId())
                .patientEmail(request.patientEmail())
                .patientFirstName(request.patientFirstName())
                .appointmentDate(saved.getDateTime())
                .hospital(saved.getHospital())
                .status("CANCELLED")
                .build());

        return mapper.toResponse(saved);
    }

    // Doctor marks appointment as completed → triggers consultation creation
    public AppointmentResponse completeAppointment(UUID id, AppointmentRequest request) {
        var appointment = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setStatus(AppointmentStatus.COMPLETED);
        var saved = repository.save(appointment);

        // Publish to appointment.completed → Consultation Service picks this up
        producer.sendAppointmentCompletedEvent(AppointmentCompletedEvent.builder()
                .appointmentId(saved.getId())
                .patientId(saved.getPatientId())
                .doctorId(saved.getDoctorId())
                .patientEmail(request.patientEmail())
                .patientFirstName(request.patientFirstName())
                .build());

        return mapper.toResponse(saved);
    }

    public AvailableSlotsResponse getAvailableSlots(UUID doctorId, LocalDate date) {
        // Check cached availability for that day of week
        var availability = cachedAvailabilityRepository
                .findByDoctorIdAndDayOfWeek(doctorId, date.getDayOfWeek())
                .orElse(null);

        if (availability == null) {
            return new AvailableSlotsResponse(doctorId, date, List.of());
        }

        // Find already booked (non-cancelled) dates
        Set<LocalDate> booked = repository.findByDoctorIdAndDate(doctorId, date)
                .stream()
                .map(Appointment::getDateTime)
                .collect(Collectors.toSet());

        // Only filter out CONFIRMED slots — PENDING ones still show with a warning on UI later
        Set<LocalDate> confirmed = repository.findByDoctorIdAndDateAndStatus(doctorId, date, AppointmentStatus.CONFIRMED)
                .stream()
                .map(Appointment::getDateTime)
                .collect(Collectors.toSet());

        return new AvailableSlotsResponse(doctorId, date, confirmed.isEmpty()
                ? List.of(date)   // slot is free
                : List.of()       // slot fully confirmed, hide it
        );
    }

    public List<AppointmentResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public AppointmentResponse findById(UUID id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
    }

    public void deleteAppointment(UUID id) {
        repository.deleteById(id);
    }
}