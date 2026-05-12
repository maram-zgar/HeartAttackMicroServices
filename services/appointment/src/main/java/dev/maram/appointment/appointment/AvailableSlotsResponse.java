package dev.maram.appointment.appointment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AvailableSlotsResponse(
        UUID doctorId,
        LocalDate date,
        List<LocalDateTime> slots
) {}