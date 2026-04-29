package dev.maram.patient.patient;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService service;

    @PostMapping
    public ResponseEntity<UUID> createPatient(
            @RequestBody @Valid PatientRequest request
    ) {
        return  ResponseEntity.ok(service.createPatient(request));
    }

    @PutMapping
    public ResponseEntity<Void> updatePatient(
            @RequestBody @Valid PatientRequest request
    ) {
        service.updatePatient(request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    public ResponseEntity<List<PatientResponse>> findAll() {
        return ResponseEntity.ok(service.findAllPatients());
    }

    @GetMapping("/exists/{patient-id}")
    public ResponseEntity<Boolean> existsById(
            @PathVariable("patient-id") UUID patientId
    ) {
        return ResponseEntity.ok(service.existsById(patientId));
    }

    @GetMapping("/{patient-id}")
    public ResponseEntity<PatientResponse> findById(
            @PathVariable("patient-id") UUID patientId
    ) {
        return ResponseEntity.ok(service.findById(patientId));
    }

    @DeleteMapping("/{patient-id}")
    public ResponseEntity<Void> delete(
            @PathVariable("patient-id") UUID patientId
    ) {
        service.deletePatient(patientId);
        return ResponseEntity.accepted().build();
    }
}
