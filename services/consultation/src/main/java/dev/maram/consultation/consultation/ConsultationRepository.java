package dev.maram.consultation.consultation;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.UUID;

public interface ConsultationRepository extends Neo4jRepository<Consultation, UUID> {
}
