package dev.maram.doctor.doctor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Optional;
import java.util.UUID;

public interface DoctorRepository extends Neo4jRepository<Doctor, UUID> {
    Optional<Doctor> findByEmail(@NotNull(message = "Email du Patient est obligatoire") @Email(message = "Email unvalid") String email);
}
