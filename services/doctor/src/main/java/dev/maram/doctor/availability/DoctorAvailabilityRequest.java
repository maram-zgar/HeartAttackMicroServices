package dev.maram.doctor.availability;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record DoctorAvailabilityRequest(
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime,
        int slotDurationMinutes
) {}