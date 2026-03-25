package dev.maram.patient.patient;

import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface PatientRepository extends Neo4jRepository<Patient, Long> {
}
