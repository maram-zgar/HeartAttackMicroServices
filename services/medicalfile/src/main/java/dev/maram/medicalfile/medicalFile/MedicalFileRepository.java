package dev.maram.medicalfile.medicalFile;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Optional;
import java.util.UUID;

public interface MedicalFileRepository extends Neo4jRepository<MedicalFile, UUID> {
    Optional<MedicalFile> findByPatientId(UUID patientId);
}
