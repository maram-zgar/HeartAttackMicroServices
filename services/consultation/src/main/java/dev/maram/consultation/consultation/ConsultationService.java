package dev.maram.consultation.consultation;

import dev.maram.consultation.kafka.AppointmentCompletedEvent;
import dev.maram.consultation.kafka.ConsultationCreatedEvent;
import dev.maram.consultation.kafka.ConsultationEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsultationService {

    private final ConsultationRepository repository;
    private final Neo4jClient neo4jClient;
    private final ConsultationEventProducer producer;

    @Transactional
    public void createFromCompletedAppointment(AppointmentCompletedEvent event) {

        // 1. Create Consultation node
        Consultation consultation = Consultation.builder()
                .appointmentId(event.getAppointmentId())
                .patientId(event.getPatientId())
                .doctorId(event.getDoctorId())
                .dateDeConsultation(LocalDate.now())
                .build();
        Consultation saved = repository.save(consultation);

        // 2. Create all nodes and relationships in a single query
        neo4jClient.query("""
            MERGE (d:Doctor {id: $doctorId})
            MERGE (p:Patient {id: $patientId})
            MERGE (a:Appointment {id: $appointmentId})
            WITH d, p, a
            MATCH (c:Consultation {appointmentId: $appointmentId})
            MERGE (d)-[:CONSULTER]->(p)
            MERGE (d)-[:EFFECTUE]->(c)
            MERGE (c)-[:CONCERNE]->(p)
            MERGE (c)-[:ISSUE_DE]->(a)
            """)
                .bind(event.getDoctorId().toString()).to("doctorId")
                .bind(event.getPatientId().toString()).to("patientId")
                .bind(event.getAppointmentId().toString()).to("appointmentId")
                .run();
        log.info("Consultation {} created: Doctor {} consulted Patient {}",
                saved.getId(), event.getDoctorId(), event.getPatientId());

        // 3. Publish consultation.created
        producer.send(ConsultationCreatedEvent.builder()
                .consultationId(saved.getId())
                .patientId(event.getPatientId())
                .doctorId(event.getDoctorId())
                .patientEmail(event.getPatientEmail())
                .patientFirstName(event.getPatientFirstName())
                .build());
    }
}