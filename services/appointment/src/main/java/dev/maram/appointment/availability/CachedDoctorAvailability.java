package dev.maram.appointment.availability;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

// Local cache of doctor availability — kept in sync via Kafka
@Node
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CachedDoctorAvailability {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID doctorId;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private int slotDurationMinutes;
}