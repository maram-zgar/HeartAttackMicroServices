package dev.maram.medicalfile.medicalFile;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.time.LocalDate;
import java.util.UUID;

@Node
@Data
public class Consultation {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID appointmentId;
    private UUID patientId;
    private UUID doctorId;
    private LocalDate dateDeConsultation;
    private String notes;
    private String diagnosis;
}