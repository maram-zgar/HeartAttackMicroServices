package dev.maram.doctor.availability;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

public record DoctorAvailabilityResponse(
        UUID id,
        UUID doctorId,
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime,
        int slotDurationMinutes
) {}