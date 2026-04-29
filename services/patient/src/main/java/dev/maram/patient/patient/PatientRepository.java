package dev.maram.patient.patient;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Optional;
import java.util.UUID;

public interface PatientRepository extends Neo4jRepository<Patient, UUID> {
    Optional<Patient> findByEmail(@NotNull(message = "Email du Patient est obligatoire") @Email(message = "Email unvalid") String email);
}
