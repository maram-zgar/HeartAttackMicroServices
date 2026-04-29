package dev.maram.doctor.doctor;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService service;

    @PutMapping
    public ResponseEntity<Void> updateDoctor(
            @RequestBody @Valid DoctorRequest request
    ) {
        service.updateDoctor(request);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/{doctor-id}")
    public ResponseEntity<Void> delete(
            @PathVariable("doctor-id") UUID doctorId
    ) {
        service.deleteDoctor(doctorId);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/complete-consultation")
    public ResponseEntity<Void> completeConsultation(@RequestBody ConsultationCompletedRequest request) {
        service.markConsultationCompleted(
                request.appointmentId(),
                request.patientId(),
                request.patientEmail(),
                request.patientFirstName(),
                request.doctorId(),
                request.doctorEmail()
        );
        return ResponseEntity.ok().build();
    }

}
