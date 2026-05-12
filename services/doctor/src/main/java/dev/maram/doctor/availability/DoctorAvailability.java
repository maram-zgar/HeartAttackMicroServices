package dev.maram.doctor.availability;


import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

@Node("doctor_availability")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DoctorAvailability {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID doctorId;

    private DayOfWeek dayOfWeek;      // MONDAY, TUESDAY, ...

    private LocalTime startTime;

    private LocalTime endTime;

    private int slotDurationMinutes;   // e.g. 30
}
