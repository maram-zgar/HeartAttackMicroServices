package dev.maram.medicalfile.medicalFile;

import dev.maram.medicalfile.patient.Patient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.OUTGOING;

@Node
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalFile {

    @Id
    @GeneratedValue
    private Long id;

    private Long patientId;
    private LocalDate creationDate;
    private LocalDate lastUpdateDate;
    private float riskPercentage;
    private RiskLevel riskLevel;

    @Relationship(type = "POSSEDE", direction = OUTGOING)
    private Patient patient;

    //@Relationship(type = "CONTIENT", direction = OUTGOING)
    //private List<Recommendation> recommendations = new ArrayList<>();
}
