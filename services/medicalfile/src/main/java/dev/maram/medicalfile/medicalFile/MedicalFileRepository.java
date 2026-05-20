package dev.maram.medicalfile.medicalFile;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.Optional;
import java.util.UUID;

public interface MedicalFileRepository extends Neo4jRepository<MedicalFile, UUID> {
    @Query("""
        MATCH (m:MedicalFile {patientId: $patientId})
        OPTIONAL MATCH (m)-[:CONTIENT]->(c:Consultation)
        RETURN m, collect(c) AS consultations
        """)
    Optional<MedicalFile> findByPatientId(UUID patientId);
}
