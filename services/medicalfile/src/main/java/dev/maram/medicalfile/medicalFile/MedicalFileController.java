package dev.maram.medicalfile.medicalFile;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/medicalfiles")
@RequiredArgsConstructor
public class MedicalFileController {

    private final MedicalFileService service;

    @GetMapping
    public ResponseEntity<List<MedicalFileResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<MedicalFileResponse> findByPatientId(
            @PathVariable UUID patientId
    ) {
        return ResponseEntity.ok(service.findByPatientId(patientId));
    }
}
