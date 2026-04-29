package dev.maram.doctor.doctor;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Node
public class Doctor {

    @Id
    @GeneratedValue
    private UUID id;
    private String firstName;
    private String lastName;
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    //@Column(unique = true)
    private String email;
    private String numeroRPPS;
    private String hospital;
    private String password;
}
