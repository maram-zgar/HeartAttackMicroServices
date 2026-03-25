package dev.maram.appointment.appointment;

import dev.maram.appointment.kafka.AppointmentEvent;
import dev.maram.appointment.kafka.AppointmentEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository repository;
    private final AppointmentEventProducer producer;
    private final AppointmentMapper mapper;

    // CREATE
    public AppointmentResponse createAppointment(AppointmentRequest request) {
        var appointment = Appointment.builder()
                .patientId(request.patientId())
                .doctorId(request.doctorId())
                .dateTime(request.dateTime())
                .hospital(request.hospital())
                .status(AppointmentStatus.PENDING)
                .build();

        var saved = repository.save(appointment);

        // Send Kafka event
        producer.sendAppointmentEvent(AppointmentEvent.builder()
                .appointmentId(saved.getId())
                .patientId(request.patientId())
                .patientEmail(request.patientEmail())
                .patientFirstName(request.patientFirstName())
                .dateTime(saved.getDateTime())
                .hospital(saved.getHospital())
                .status("PENDING")
                .build());

        return mapper.toResponse(saved);
    }

    // UPDATE
    public AppointmentResponse updateAppointment(Long id, AppointmentRequest request) {
        var appointment = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setDateTime(request.dateTime());
        appointment.setStatus(request.status());

        var saved = repository.save(appointment);

        // Send Kafka event
        producer.sendAppointmentEvent(AppointmentEvent.builder()
                .appointmentId(saved.getId())
                .patientId(saved.getPatientId())
                .patientEmail(request.patientEmail())
                .patientFirstName(request.patientFirstName())
                .dateTime(saved.getDateTime())
                .hospital(saved.getHospital())
                .status("UPDATED")
                .build());

        return mapper.toResponse(saved);
    }

    // CANCEL
    public AppointmentResponse cancelAppointment(Long id, AppointmentRequest request) {
        var appointment = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setStatus(AppointmentStatus.CANCELLED);

        var saved = repository.save(appointment);

        // Send Kafka event
        producer.sendAppointmentEvent(AppointmentEvent.builder()
                .appointmentId(saved.getId())
                .patientId(saved.getPatientId())
                .patientEmail(request.patientEmail())
                .patientFirstName(request.patientFirstName())
                .dateTime(saved.getDateTime())
                .hospital(saved.getHospital())
                .status("CANCELLED")
                .build());

        return mapper.toResponse(saved);
    }

    public List<AppointmentResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public AppointmentResponse findById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
    }

    public void deleteAppointment(Long id) {
        repository.deleteById(id);
    }


}
