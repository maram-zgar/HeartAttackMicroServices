package dev.maram.appointment.kafka;


import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

// Mirror of Doctor Service DTO — kept separate to avoid coupling
public record DoctorAvailabilityResponse(
        UUID id,
        UUID doctorId,
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime,
        int slotDurationMinutes
) {}
