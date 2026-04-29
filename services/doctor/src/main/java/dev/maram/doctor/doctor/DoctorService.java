package dev.maram.doctor.doctor;

import dev.maram.doctor.exception.DoctorNotFoundException;
import dev.maram.doctor.kafka.AppointmentCompletedEvent;
import dev.maram.doctor.kafka.DoctorEvent;
import dev.maram.doctor.kafka.DoctorEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorService {

    private final DoctorRepository repository;
    private final DoctorMapper mapper;
    private final DoctorEventProducer producer;

    public UUID createDoctor(DoctorRequest request) {
        var doctor = Doctor.builder()
                .id(request.id() != null ? request.id() : UUID.randomUUID())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .numeroRPPS(request.numeroRPPS())
                .hospital(request.hospital())
                .build();
        return repository.save(doctor).getId();
    }

    public void updateDoctor(DoctorRequest request) {
        var doctor = repository.findById(request.id())
                .orElseThrow(() -> new DoctorNotFoundException(
                        String.format("Cannot update doctor:: No doctor found with the id:: %s", request.id())
                ));
        // to avoid overriding an existing value with a null value
        mergerDoctor(doctor, request);
        repository.save(doctor);
    }

    public void markConsultationCompleted(UUID appointmentId,
                                          UUID patientId,
                                          String patientEmail,
                                          String patientFirstName,
                                          UUID doctorId,
                                          String doctorEmail) {
        producer.sendConsultationCompleted(
                AppointmentCompletedEvent.builder()
                        .appointmentId(appointmentId)
                        .patientId(patientId)
                        .patientEmail(patientEmail)
                        .patientFirstName(patientFirstName)
                        .doctorId(doctorId)
                        .doctorEmail(doctorEmail)
                        .completedAt(LocalDate.now())
                        .build()
        );

        log.info("Published appointment.completed for appointmentId={}", appointmentId);
    }

    private void mergerDoctor(Doctor doctor, DoctorRequest request) {
        if (StringUtils.isNotBlank(request.firstName())) {
            doctor.setFirstName(request.firstName());
        }
        if (StringUtils.isNotBlank(request.lastName())) {
            doctor.setLastName(request.lastName());
        }
        if (StringUtils.isNotBlank(request.email())) {
            doctor.setEmail(request.email());
        }
        if(StringUtils.isNotBlank(request.numeroRPPS())) {
            doctor.setNumeroRPPS(request.numeroRPPS());
        }
        if (StringUtils.isNotBlank(request.hospital())) {
            doctor.setHospital(request.hospital());
        }
    }

    public List<DoctorResponse> findAllDoctors() {
        return repository.findAll()
                .stream()
                .map(mapper::fromDoctor)
                .collect(Collectors.toList());
    }

    public Boolean existsById(UUID doctorId) {
        return repository.findById(doctorId)
                .isPresent();
    }

    public DoctorResponse findById(UUID doctorId) {
        return repository.findById(doctorId)
                .map(mapper::fromDoctor)
                .orElseThrow(() -> new DoctorNotFoundException(String.format("No patient found with the id:: %s", doctorId)));
    }

    public void deleteDoctor(UUID doctorId) {
        repository.deleteById(doctorId);
    }

}
