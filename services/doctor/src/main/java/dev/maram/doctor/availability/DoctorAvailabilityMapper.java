package dev.maram.doctor.availability;

import org.springframework.stereotype.Component;

@Component
public class DoctorAvailabilityMapper {

    public DoctorAvailabilityResponse toResponse(DoctorAvailability a) {
        return new DoctorAvailabilityResponse(
                a.getId(),
                a.getDoctorId(),
                a.getDayOfWeek(),
                a.getStartTime(),
                a.getEndTime(),
                a.getSlotDurationMinutes()
        );
    }
}