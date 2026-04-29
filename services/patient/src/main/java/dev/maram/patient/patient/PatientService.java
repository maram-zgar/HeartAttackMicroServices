package dev.maram.patient.patient;


import dev.maram.patient.exception.PatientNotFoundException;
import dev.maram.patient.kafka.PatientEvent;
import dev.maram.patient.kafka.PatientEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientService {

    private final PatientRepository repository;
    private final PatientMapper mapper;
    private final PatientEventProducer producer;

    public UUID createPatient(PatientRequest request) {

        // If patient already exists by email, update instead of creating
        var existing = repository.findByEmail(request.email());
        if (existing.isPresent()) {
            var patient = existing.get();
            mergerPatient(patient, request);
            repository.save(patient);
            log.info("Updated existing patient: {}", patient.getEmail());
            return patient.getId();
        }

        var patient = mapper.toPatient(request);
        var saved = repository.save(patient);

        log.info("SAVED PATIENT: {}", saved);

        // Publish event so MedicalFile service creates a file
        producer.sendPatientEvent(PatientEvent.builder()
                .patientId(saved.getId())
                .firstName(saved.getFirstName())
                .lastName(saved.getLastName())
                .email(saved.getEmail())
                .eventType("CREATED")
                .build());

        return saved.getId();
    }

    public void updatePatient(PatientRequest request) {
        var patient = repository.findById(request.id())
                .orElseThrow(() -> new PatientNotFoundException(
                        String.format("Cannot update patient:: No patient found with the id:: %s", request.id())
                ));
        // to avoid overriding an existing value with a null value
        mergerPatient(patient, request);
        repository.save(patient);
    }

    private void mergerPatient(Patient patient, PatientRequest request) {
        if (StringUtils.isNotBlank(request.firstName())) {
            patient.setFirstName(request.firstName());
        }
        if (StringUtils.isNotBlank(request.lastName())) {
            patient.setLastName(request.lastName());
        }
        if (request.age() != 0) {
            patient.setAge(request.age());
        }
        if (StringUtils.isNotBlank(request.email())) {
            patient.setEmail(request.email());
        }
        if (StringUtils.isNotBlank(request.hospital())) {
            patient.setHospital(request.hospital());
        }
    }

    public List<PatientResponse> findAllPatients() {
        return repository.findAll()
                .stream()
                .map(mapper::fromPatient)
                .collect(Collectors.toList());
    }

    public Boolean existsById(UUID patientId) {
        return repository.findById(patientId)
                .isPresent();
    }

    public PatientResponse findById(UUID patientId) {
        return repository.findById(patientId)
                .map(mapper::fromPatient)
                .orElseThrow(() -> new PatientNotFoundException(String.format("No patient found with the id:: %s", patientId)));
    }

    public void deletePatient(UUID patientId) {
        repository.deleteById(patientId);
    }
}
