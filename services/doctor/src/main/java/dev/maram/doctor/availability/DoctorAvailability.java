package dev.maram.doctor.availability;


//import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

@Node
//@Table(name = "doctor_availability",
//        uniqueConstraints = @UniqueConstraint(columnNames = {"doctor_id", "day_of_week"}))
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DoctorAvailability {

    @Id
    @GeneratedValue
    private UUID id;
    //@Column(name = "doctor_id", nullable = false)
    private UUID doctorId;

    //@Enumerated(EnumType.STRING)
    //@Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;      // MONDAY, TUESDAY, ...

    //@Column(nullable = false)
    private LocalTime startTime;
    //@Column(nullable = false)// e.g. 09:00
    private LocalTime endTime;
    //@Column(nullable = false)// e.g. 17:00
    private int slotDurationMinutes;   // e.g. 30
}
