package dev.maram.consultation.consultation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.time.LocalDate;
import java.util.UUID;

@Node
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Consultation {

    @Id
    @GeneratedValue
    private UUID id;
    private UUID appointmentId;
    private UUID patientId;
    private UUID doctorId;
    private LocalDate dateDeConsultation;
}
