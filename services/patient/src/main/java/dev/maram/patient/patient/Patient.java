package dev.maram.patient.patient;


//import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Node
public class Patient {
    @Id
    @GeneratedValue(GeneratedValue.UUIDGenerator.class)
    private UUID id;
    private String firstName;
    private String lastName;
    private int age;
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    //@Column(unique = true)
    private String email;
    private String hospital;
}

