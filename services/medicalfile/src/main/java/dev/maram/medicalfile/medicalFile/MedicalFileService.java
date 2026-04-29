package dev.maram.medicalfile.medicalFile;

import dev.maram.medicalfile.kafka.AppointmentEvent;
import dev.maram.medicalfile.kafka.WelcomeEvent;
import dev.maram.medicalfile.kafka.WelcomeEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicalFileService {

    private final MedicalFileRepository repository;
    private final MedicalFileMapper mapper;
    private final Neo4jClient neo4jClient;
    private final WelcomeEventProducer welcomeEventProducer;

    @Transactional
    public void createMedicalFileForPatient(UUID patientId, String firstName, String lastName, String email) {
        neo4jClient.query("""
                MERGE (p:Patient {id: $patientId})
                SET p.firstName = $firstName,
                    p.lastName  = $lastName,
                    p.email     = $email
                """)
                .bind(patientId.toString()).to("patientId")
                .bind(firstName).to("firstName")
                .bind(lastName).to("lastName")
                .bind(email).to("email")
                .run();

        MedicalFile file = MedicalFile.builder()
                .patientId(patientId)
                .creationDate(LocalDate.now())
                .lastUpdateDate(LocalDate.now())
                .riskPercentage(0.0f)
                .riskLevel(RiskLevel.FAIBLE)
                .build();

        MedicalFile savedFile = repository.save(file);

        // Create POSSEDE relationship: Patient -[:POSSEDE]-> MedicalFile
        neo4jClient.query("""
                MATCH (p:Patient {id: $patientId})
                MATCH (m:MedicalFile {id: $medicalFileId})
                MERGE (p)-[:POSSEDE]->(m)
                """)
                .bind(patientId.toString()).to("patientId")
                .bind(savedFile.getId().toString()).to("medicalFileId")
                .run();

        log.info("MedicalFile created and linked to Patient {}", patientId);

        // Send Welcome mail
        welcomeEventProducer.sendWelcomeEvent(
                WelcomeEvent.builder()
                        .email(email)
                        .firstName(firstName)
                        .medicalFileId(savedFile.getId())
                        .build()
        );
    }

    public MedicalFileResponse findByPatientId(UUID patientId) {
        return repository.findByPatientId(patientId)
                .map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Medical file not found for patient: " + patientId));
    }

    public List<MedicalFileResponse> findAll() {
        List<MedicalFile> files = repository.findAll();
        return files.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void linkAppointmentToPatient(AppointmentEvent event) {
        neo4jClient.query("""
            MERGE (p:Patient {id: $patientId})
            MERGE (a:Appointment {id: $appointmentId})
            SET a.dateTime = $dateTime, a.hospital = $hospital, a.status = $status
            MERGE (p)-[:HAS_APPOINTMENT]->(a)
            """)
                .bind(event.getPatientId()).to("patientId")
                .bind(event.getAppointmentId()).to("appointmentId")
                .bind(event.getDateTime().toString()).to("dateTime")
                .bind(event.getHospital()).to("hospital")
                .bind(event.getStatus()).to("status")
                .run();

        log.info("Appointment {} linked to Patient {}", event.getAppointmentId(), event.getPatientId());
    }
}