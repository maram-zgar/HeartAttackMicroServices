package dev.maram.doctor.availability;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/doctors/{doctorId}/availability")
@RequiredArgsConstructor
public class DoctorAvailabilityController {

    private final DoctorAvailabilityService availabilityService;

    @PostMapping
    public ResponseEntity<DoctorAvailabilityResponse> setAvailability(
            @PathVariable UUID doctorId,
            @RequestBody DoctorAvailabilityRequest request) {
        return ResponseEntity.ok(availabilityService.setAvailability(doctorId, request));
    }

    @GetMapping
    public ResponseEntity<List<DoctorAvailabilityResponse>> getAvailability(
            @PathVariable UUID doctorId) {
        return ResponseEntity.ok(availabilityService.getAvailability(doctorId));
    }

    @DeleteMapping("/day")
    public ResponseEntity<Void> deleteAvailability(
            @PathVariable UUID doctorId,
            @RequestParam DayOfWeek day) {
        availabilityService.deleteAvailability(doctorId, day);
        return ResponseEntity.noContent().build();
    }
}