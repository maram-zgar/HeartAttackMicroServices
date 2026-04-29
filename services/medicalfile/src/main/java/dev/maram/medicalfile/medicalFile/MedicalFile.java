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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.OUTGOING;

@Node
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalFile {

    @Id
    @GeneratedValue
    private UUID id;
    private UUID patientId;
    private LocalDate creationDate;
    private LocalDate lastUpdateDate;
    private double riskPercentage;
    private RiskLevel riskLevel;

//    @Relationship(type = "POSSEDE", direction = Relationship.Direction.INCOMING)
//    private dev.maram.medicalfile.patient.Patient patient;


    //@Relationship(type = "CONTIENT", direction = OUTGOING)
    //private List<Recommendation> recommendations = new ArrayList<>();
}
