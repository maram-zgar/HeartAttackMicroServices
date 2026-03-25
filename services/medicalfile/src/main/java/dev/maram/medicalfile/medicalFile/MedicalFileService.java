package dev.maram.medicalfile.medicalFile;

import dev.maram.medicalfile.kafka.AppointmentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicalFileService {

    private final MedicalFileRepository repository;
    private final MedicalFileMapper mapper;
    private final Neo4jClient neo4jClient;

    @Transactional
    public void createMedicalFileForPatient(Long patientId, String firstName, String lastName, String email) {
        MedicalFile file = MedicalFile.builder()
                .patientId(patientId)
                .creationDate(LocalDate.now())
                .lastUpdateDate(LocalDate.now())
                .riskPercentage(0.0f)
                .riskLevel(RiskLevel.FAIBLE)
                .build();
        repository.save(file);

        log.info("MedicalFile created and linked to Patient {}", patientId);
    }

    public MedicalFileResponse findByPatientId(Long patientId) {
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