package dev.maram.medicalfile.medicalFile;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Optional;

public interface MedicalFileRepository extends Neo4jRepository<MedicalFile, Long> {
    Optional<MedicalFile> findByPatientId(Long patientId);
}
