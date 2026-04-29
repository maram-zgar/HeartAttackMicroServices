package dev.maram.medicalfile.patient;

import dev.maram.medicalfile.medicalFile.MedicalFile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.UUID;

@Node
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Patient {

    @Id
    @GeneratedValue(GeneratedValue.UUIDGenerator.class)
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;

    @Relationship(type = "POSSEDE", direction = Relationship.Direction.OUTGOING)
    private MedicalFile medicalFile;
}