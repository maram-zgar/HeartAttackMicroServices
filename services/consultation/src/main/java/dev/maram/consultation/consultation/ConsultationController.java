package dev.maram.consultation.consultation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/consultations")
@RequiredArgsConstructor
public class ConsultationController {

    private final ConsultationService service;

    @GetMapping("/{id}")
    public ResponseEntity<Consultation> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    // Doctor submits their medical notes
    @PutMapping("/{id}")
    public ResponseEntity<Consultation> updateDetails(
            @PathVariable UUID id,
            @RequestBody ConsultationUpdateRequest request) {
        return ResponseEntity.ok(service.updateConsultation(id, request));
    }
}