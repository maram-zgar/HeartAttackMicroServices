package dev.maram.patient.patient;


import dev.maram.patient.exception.PatientNotFoundException;
import dev.maram.patient.kafka.PatientEvent;
import dev.maram.patient.kafka.PatientEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Base64;
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


    private String generatePassword() {
        SecureRandom sr = new SecureRandom();
        byte[] bytes = new byte[9];
        sr.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    @Transactional
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

        Patient patient = mapper.toPatient(request);
        var saved = repository.save(patient);
        log.info("SAVED PATIENT: {}", saved);

        String temporaryPassword = generatePassword();

        PatientEvent event = PatientEvent.builder()
                .patientId(saved.getId())
                .doctorId(request.doctorId())
                .firstName(saved.getFirstName())
                .lastName(saved.getLastName())
                .email(saved.getEmail())
                .eventType("CREATED")
                .temporaryPassword(temporaryPassword)
                .dateOfBirth(request.dateOfBirth())
                .gender(request.gender())
                .age(saved.getAge())
                .build();

        // Publish event so MedicalFile service creates a file
        producer.sendPatientEvent(event);
        log.info("Published patient.created for patientId={}", saved.getId());

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
